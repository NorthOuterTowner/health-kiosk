#ifndef _FILTER_H
#define _FILTER_H

#include "stm32f1xx.h"
#include <stdint.h>
#include <stdbool.h>

// 数学常数 宏定义
#define PI 3.14159265358979323846f

// 250Hz采样率配置（STM32F103优化）
#define ECG_SAMPLE_RATE 250     // 250Hz采样率
#define NOTCH_CENTER_FREQ 50    // 50Hz陷波中心

// 高通滤波器结构体
typedef struct 
{
    float *b;         // 分子系数
    float *a;         // 分母系数
    float *x_buf;     // 输入历史缓冲区
    float *y_buf;     // 输出历史缓冲区
    uint16_t order;   // 滤波器阶数
    uint16_t index;   // 当前缓冲区索引
} IIRFilter;          //结构体的名字

// 低通滤波器结构体
typedef struct 
{
    float b[6];         // 分子系数
    float a[6];         // 分母系数
    float x_buf[6];     // 输入历史缓冲区
    float y_buf[6];     // 输出历史缓冲区
    uint8_t order;      // 滤波器阶数
    uint8_t index;      // 缓冲区索引
} LowPassFilter;

// 带阻滤波器结构体
typedef struct 
{
    float b[5];         // 分子系数
    float a[4];         // 分母系数
    float x[4];         // 输入延迟
    float y[4];         // 输出延迟
    uint8_t index;      // 循环索引
} NotchFilter;

// 全局滤波器实例声明：外部文件中的声明
extern IIRFilter hp_filter;//eg:hp_filter在另一个文件中这个结构体实际例子的名字，它属于IIRFilter这个结构体
extern LowPassFilter lp_filter;
extern NotchFilter powerline_notch;

// 滤波器函数声明
void ECG_Filter_System_Init(void);
int init_highpass_filter(float fp, float fs, float ap, float as, float sample_rate);
float process_highpass_sample(float input);
void lowpass_filter_init(float fp, float fs, float ap, float as, float sample_rate);
float lowpass_filter_process(float input);
int init_notch_filter(float center_freq, float bandwidth, float sample_rate);
float process_notch_sample(float input);

#endif // _FILTER_H
