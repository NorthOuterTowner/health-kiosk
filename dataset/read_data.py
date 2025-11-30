import wfdb
import matplotlib.pyplot as plt
import numpy as np
import os

# 1. 定义记录名称和文件夹路径
# record_name 只需要文件名主体
record_name = 'A00005'

# 文件夹路径：根据您的要求，文件在 'training2017' 目录下
# 注意：wfdb.rdrecord 可以直接通过 record_name='training2017/A00001' 来指定路径
# 这样更简单直接。
full_record_path = os.path.join('dataset/training2017', record_name)

print(f"正在尝试读取记录: {full_record_path}.hea 和 {full_record_path}.mat")

# 2. 读取 WFDB 记录
# rdrecord 函数会自动找到并解析 .hea 和 .mat 文件
record = wfdb.rdrecord(full_record_path)

print("\n--- 记录元数据 (从 A00001.hea 解析) ---")

# 从头文件内容分析得出以下信息：
# A00001 1 300 9000 05:05:15 1/05/2000 
# A00001.mat 16+24 1000/mV 16 0 -127 0 0 ECG
print(f"记录名称: {record.record_name}")
print(f"信号数量: {record.n_sig}")
print(f"采样频率 (Fs): {record.fs} Hz")
print(f"总样本数: {record.sig_len} (对应 {record.sig_len / record.fs} 秒)")
print(f"信号名称: {record.sig_name}")
print(f"信号单位: {record.units}")
print(f"记录基准时间: {record.base_time} {record.base_date}")

print("\n--- 信号数据样本 ---")
# 信号数据（已转换为物理单位，例如 mV）存储在 record.p_signal 中
signals = record.p_signal 
print(f"信号数据形状: {signals.shape} (样本数 x 信号通道数)")

# 打印前5个时间点的信号值
print(f"前5个时间点的信号值:\n{signals[:5, 0]} {record.units[0]}")


# 3. 可视化信号
print("\n--- 正在绘制波形图 ---")
# 计算时间轴（秒）
time = np.arange(record.sig_len) / record.fs 

plt.figure(figsize=(12, 5))
plt.plot(time, signals[:, 0], linewidth=1.0)

plt.title(f'PhysioNet Challenge 2017 ECG Signal: {record.sig_name[0]}')
plt.xlabel('Time (s)')
plt.ylabel(f'Amplitude ({record.units[0]})')
plt.grid(True)
plt.show()
print("绘图完成。")
