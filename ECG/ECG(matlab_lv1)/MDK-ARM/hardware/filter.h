#ifndef _FILTER_H
#define _FILTER_H

#include "stm32f1xx.h"
#include <stdint.h>
#include <stdbool.h>

// ��ѧ���� �궨��
#define PI 3.14159265358979323846f

// 250Hz���������ã�STM32F103�Ż���
#define ECG_SAMPLE_RATE 250     // 250Hz������
#define NOTCH_CENTER_FREQ 50    // 50Hz�ݲ�����

// ��ͨ�˲����ṹ��
typedef struct 
{
    float *b;         // ����ϵ��
    float *a;         // ��ĸϵ��
    float *x_buf;     // ������ʷ������
    float *y_buf;     // �����ʷ������
    uint16_t order;   // �˲�������
    uint16_t index;   // ��ǰ����������
} IIRFilter;          //�ṹ�������

// ��ͨ�˲����ṹ��
typedef struct 
{
    float b[6];         // ����ϵ��
    float a[6];         // ��ĸϵ��
    float x_buf[6];     // ������ʷ������
    float y_buf[6];     // �����ʷ������
    uint8_t order;      // �˲�������
    uint8_t index;      // ����������
} LowPassFilter;

// �����˲����ṹ��
typedef struct 
{
    float b[5];         // ����ϵ��
    float a[4];         // ��ĸϵ��
    float x[4];         // �����ӳ�
    float y[4];         // ����ӳ�
    uint8_t index;      // ѭ������
} NotchFilter;

// ȫ���˲���ʵ���������ⲿ�ļ��е�����
extern IIRFilter hp_filter;//eg:hp_filter����һ���ļ�������ṹ��ʵ�����ӵ����֣�������IIRFilter����ṹ��
extern LowPassFilter lp_filter;
extern NotchFilter powerline_notch;

// �˲�����������
void ECG_Filter_System_Init(void);
int init_highpass_filter(float fp, float fs, float ap, float as, float sample_rate);
float process_highpass_sample(float input);
void lowpass_filter_init(float fp, float fs, float ap, float as, float sample_rate);
float lowpass_filter_process(float input);
int init_notch_filter(float center_freq, float bandwidth, float sample_rate);
float process_notch_sample(float input);

#endif // _FILTER_H
