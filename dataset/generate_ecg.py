"""
generate_ecg.py

生成 30000 个单导联心电数据点（单位：mV），保存为 CSV。
可调参数：sampling_rate, n_samples, mean_hr, noise_std, amplitude_scale

依赖：numpy, matplotlib
pip install numpy matplotlib
"""

import numpy as np
import matplotlib.pyplot as plt
import csv
import os

# ---------- 参数（可按需修改） ----------
sampling_rate = 512          # 采样率 (Hz)
n_samples = 30000            # 目标样本点数
mean_hr = 70                 # 平均心率 (bpm)
hr_std = 1.5                 # 心率抖动（bpm 的标准差）
noise_std = 0.015            # 噪声标准差 (mV)，典型生理噪声 ~0.01-0.05 mV
amplitude_scale = 1.0        # 总放大缩放（R 波约 0.8-1.2 mV）
seed = 42                    # 随机种子，便于复现
output_csv = "admin.csv"
plot_png = "ecg_preview.png"
# ----------------------------------------

np.random.seed(seed)

# 计算总时长
duration = n_samples / sampling_rate  # 秒
t = np.arange(n_samples) / sampling_rate

# 构造单搏模板（以秒为单位的时间轴，中心在 0）
# 我们用若干个高斯来模拟 P, Q, R, S, T 波（相对时间和振幅参考）
template_fs = sampling_rate
template_length_s = 1.0  # 模板覆盖 1 秒（足够包住 P-QRS-T）
tpl_t = np.linspace(-0.4, 0.8, int((0.4+0.8)*template_fs))  # -0.4 .. +0.8s

def gaussian(x, mu, sigma, amp):
    return amp * np.exp(-0.5 * ((x - mu)/sigma)**2)

# 波的参数（时间位置(s), sigma(s), amplitude(mV)）
# 这些值可根据需要微调以改变形状
P_params = (-0.18, 0.025, 0.12 * amplitude_scale)   # P 波：小幅
Q_params = (-0.05, 0.01, -0.15 * amplitude_scale)   # Q：小向下
R_params = (0.00, 0.006, 1.0 * amplitude_scale)     # R：主尖峰
S_params = (0.04, 0.01, -0.25 * amplitude_scale)    # S：向下
T_params = (0.28, 0.06, 0.35 * amplitude_scale)     # T：较宽的正波

# 构造模板
tpl = np.zeros_like(tpl_t)
for mu, sigma, amp in [P_params, Q_params, R_params, S_params, T_params]:
    tpl += gaussian(tpl_t, mu, sigma, amp)

# 为了保证模板平均值接近 0（去直流）
tpl -= np.mean(tpl) * 0.0  # 保留直流为0（不强制去掉，因为本身偏移小）

# 生成 RR 间期序列（秒），通过心率（bpm）-> RR = 60/HR
# 用正态分布对心率做微抖动（也可以用泊松或其他模型）
# 先估算需要多少搏以覆盖总时长
est_beats = int(duration * (mean_hr/60.0) * 1.5) + 10
hrs = np.random.normal(loc=mean_hr, scale=hr_std, size=est_beats)
hrs = np.clip(hrs, 40, 180)  # 合理区间裁剪
rrs = 60.0 / hrs  # 每搏 RR(s)

# 将模板放到时间轴上，累加每个搏
ecg = np.zeros(n_samples)

current_time = 0.0
beat_idx = 0
tpl_half_len = len(tpl) // 2
while True:
    if beat_idx >= len(rrs):
        break
    # 以 current_time 为该搏的 R 波出现时间（使用模板中心对应的时间）
    r_time = current_time
    r_sample = int(round(r_time * sampling_rate))
    # 模板起止索引（在全局信号中的位置）
    tpl_start = r_sample - int((len(tpl_t) * 1.0) * 0.4 * template_fs)  # safer offset
    # Simpler: align template center to r_sample by offset
    tpl_center_idx = np.argmin(np.abs(tpl_t - 0.0))
    start_idx = r_sample - tpl_center_idx
    end_idx = start_idx + len(tpl)
    if end_idx < 0:
        # 模板完全在信号左侧，跳过
        current_time += rrs[beat_idx]
        beat_idx += 1
        continue
    # overlapping add
    s = max(start_idx, 0)
    e = min(end_idx, n_samples)
    tpl_s = s - start_idx
    tpl_e = tpl_s + (e - s)
    ecg[s:e] += tpl[tpl_s:tpl_e]
    # advance
    current_time += rrs[beat_idx]
    beat_idx += 1
    # stop if beyond signal
    if current_time * sampling_rate > n_samples + sampling_rate:
        break

# 添加高斯白噪声（模拟电极/肌电/基线噪声）
noise = np.random.normal(scale=noise_std, size=n_samples)
ecg += noise

# 小幅基线 wander（低频扰动，可选）
baseline_freq = 0.25  # Hz
baseline = 0.02 * np.sin(2*np.pi*baseline_freq*t + np.random.rand()*2*np.pi)
ecg += baseline

# 最终单位是 mV（我们在模板设计时就用了 mV 级别）
# 如果想标准化或裁剪，可在此处理，例如保证最大绝对幅度不超过某值
# ecg = np.clip(ecg, -5.0, 5.0)

# 保存为 CSV（时间(s), voltage(mV)）
with open(output_csv, 'w', newline='') as f:
    writer = csv.writer(f)
    writer.writerow(["time_s", "voltage_mV"])
    for ti, v in zip(t, ecg):
        writer.writerow([f"{ti:.6f}", f"{v:.6f}"])

print(f"Saved {n_samples} samples to {os.path.abspath(output_csv)}")

# 画图预览：显示前 5 秒和整段的缩略图
plt.figure(figsize=(10,4))
end_plot_samples = min(n_samples, int(5.0 * sampling_rate))  # 前5秒
plt.plot(t[:end_plot_samples], ecg[:end_plot_samples])
plt.title(f"ECG preview (first {end_plot_samples} samples = {end_plot_samples/sampling_rate:.2f} s)")
plt.xlabel("Time (s)")
plt.ylabel("Voltage (mV)")
plt.grid(True)
plt.tight_layout()
plt.savefig(plot_png, dpi=150)
print(f"Saved preview plot to {os.path.abspath(plot_png)}")

# 也可以显示整段（稀疏），但可能太长；这里仅保存前5s的图像
# 若在交互式环境运行，你可以取消注释下一行显示图像：
# plt.show()
