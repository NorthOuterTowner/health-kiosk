import os
import wfdb
import numpy as np
import pandas as pd
import joblib

from sklearn.model_selection import train_test_split
from sklearn.ensemble import RandomForestClassifier
from sklearn.preprocessing import LabelEncoder
from sklearn.metrics import accuracy_score

DATA_DIR = 'dataset/training2017'
LABEL_FILE = 'dataset/REFERENCE-v3.csv'
FEATURE_CACHE = 'cache/ecg_features.npz'

labels_df = pd.read_csv(LABEL_FILE)
record_to_label = dict(zip(labels_df['record'], labels_df['label']))

def extract_features(signal):
    """
    从一段 ECG 信号中提取简单统计特征
    signal: (N, 1)
    return: feature vector
    """
    signal = signal.squeeze()

    features = [
        np.mean(signal),
        np.std(signal),
        np.max(signal),
        np.min(signal),
        np.ptp(signal),              # 峰峰值
        np.percentile(signal, 25),
        np.percentile(signal, 50),
        np.percentile(signal, 75),
    ]
    return np.array(features)

X = []
y = []

os.makedirs('cache', exist_ok=True)

if os.path.exists(FEATURE_CACHE):
    print("发现特征缓存")
    cache = np.load(FEATURE_CACHE, allow_pickle=True)
    X = cache['X']
    y = cache['y']
else:
    print("未发现缓存")

    for record_name in record_to_label.keys():
        record_path = os.path.join(DATA_DIR, record_name)

        try:
            record = wfdb.rdrecord(record_path)
            signal = np.nan_to_num(record.p_signal[:, 0])

            features = extract_features(signal)
            X.append(features)
            y.append(record_to_label[record_name])

        except Exception as e:
            print(f"{record_name} 读取失败: {e}")

    X = np.array(X)
    y = np.array(y)

    np.savez(FEATURE_CACHE, X=X, y=y)
    print("特征已缓存到:", FEATURE_CACHE)

print(f"成功加载样本数: {len(X)}")
print(f"特征维度: {X.shape[1]}")

le = LabelEncoder()
y_encoded = le.fit_transform(y)

X_train, X_test, y_train, y_test = train_test_split(
    X,
    y_encoded,
    test_size=0.3,
    random_state=42,
    stratify=y_encoded
)

model = RandomForestClassifier(
    n_estimators=300,
    max_depth=10,
    min_samples_leaf=10,
    n_jobs=-1
)

model.fit(X_train, y_train)

train_pred = model.predict(X_train)
test_pred = model.predict(X_test)

train_acc = accuracy_score(y_train, train_pred)
test_acc = accuracy_score(y_test, test_pred)

print("\n====== 训练结果 ======")
print(f"训练集准确率: {train_acc:.4f}")
print(f"测试集准确率: {test_acc:.4f}")

os.makedirs('checkpoints', exist_ok=True)

joblib.dump(
    {
        'model': model,
        'label_encoder': le,
        'feature_type': 'stat_v1'
    },
    'checkpoints/ecg_rf_model.pkl'
)

print("模型已保存到 checkpoints/ecg_rf_model.pkl")