#include "filter.h"
#include <math.h>
#include <string.h>



// ȫ���˲���ʵ��
IIRFilter hp_filter;
LowPassFilter lp_filter;
NotchFilter powerline_notch;

// �˲���ϵ���洢
static float b_coeffs[5];
static float a_coeffs[5];
static float x_history[5];
static float y_history[5];

/**
 * @brief ϵͳ��ʼ������
 * @note STM32F103��Ӳ��FPU�����Ƴ�FPU���ô���
 */
void ECG_Filter_System_Init(void)
{
    // ��ʼ�����˲�����250Hzר��ϵ����
    init_highpass_filter(0.7f, 0.3f, 1.0f, 15.0f, ECG_SAMPLE_RATE);
    lowpass_filter_init(25.0f, 40.0f, 1.0f, 15.0f, ECG_SAMPLE_RATE);
    init_notch_filter(50.0f, 5.0f, ECG_SAMPLE_RATE);
}

/**
 * @brief ��ʼ����ͨ�˲�����250Hz 2�װ�����˹��
 */
int init_highpass_filter(float fp, float fs, float ap, float as, float sample_rate)
{
    // ��֧��250Hz������
    if(sample_rate != 250.0f) 
	 {
        return -1;
    }
    
    // 250Hz 2�װ�����˹��ͨϵ����0.7Hz��ֹ��
    b_coeffs[0] = 0.9905f;   // b0
    b_coeffs[1] = -1.9810f;  // b1
    b_coeffs[2] = 0.9905f;   // b2
    a_coeffs[0] = 1.0f;      // a0
    a_coeffs[1] = -1.9810f;  // a1
    a_coeffs[2] = 0.9810f;   // a2
    
    // ��ʼ���˲����ṹ
    hp_filter.b = b_coeffs;
    hp_filter.a = a_coeffs;
    hp_filter.x_buf = x_history;
    hp_filter.y_buf = y_history;
    hp_filter.order = 2;
    hp_filter.index = 0;
    
    // �����ʷ������
    memset(x_history, 0, sizeof(x_history));
    memset(y_history, 0, sizeof(y_history));
    
    return 0;
}

/**
 * @brief ��ͨ�˲�����
 */
float process_highpass_sample(float input)
{
    uint16_t n = hp_filter.order;
    uint16_t idx = hp_filter.index;
    
    hp_filter.x_buf[idx] = input;
    
    float output = hp_filter.b[0] * hp_filter.x_buf[idx];
    for (int i = 1; i <= n; i++) 
	 {
        int j = (idx + n + 1 - i) % (n + 1);
        output += hp_filter.b[i] * hp_filter.x_buf[j];
        output -= hp_filter.a[i] * hp_filter.y_buf[j];
    }
    output /= hp_filter.a[0];//��һ��
    
    hp_filter.y_buf[idx] = output;
    hp_filter.index = (idx + 1) % (n + 1);
    
    return output;
}

/**
 * @brief ��ʼ����ͨ�˲�����250Hz 4�װ�����˹��
 */
void lowpass_filter_init(float fp, float fs, float ap, float as, float sample_rate)
{
    // ��֧��250Hz������
    if(sample_rate != 250.0f) 
		{
        return;//������ֵ���÷���ȷ�����������˳����������ٽ��к�������
    }
    
    // 250Hz 4�װ�����˹��ͨϵ����25Hz��ֹ��
    const float preset_b[] = {0.0186f, 0.0743f, 0.1114f, 0.0743f, 0.0186f};
    const float preset_a[] = {1.0000f, -1.5704f, 1.2756f, -0.4844f, 0.0762f};
    
    // ����ϵ�����˲����ṹ
    memcpy(lp_filter.b, preset_b, sizeof(preset_b));
    memcpy(lp_filter.a, preset_a, sizeof(preset_a));
    
    lp_filter.order = 4;
    lp_filter.index = 0;
    memset(lp_filter.x_buf, 0, sizeof(lp_filter.x_buf));
    memset(lp_filter.y_buf, 0, sizeof(lp_filter.y_buf));
}

/**
 * @brief ��ͨ�˲�����
 */
float lowpass_filter_process(float input)
{
    uint8_t n = lp_filter.order;
    uint8_t idx = lp_filter.index;
    
    lp_filter.x_buf[idx] = input;
    
    float output = lp_filter.b[0] * lp_filter.x_buf[idx];
    for (uint8_t i = 1; i <= n; i++) 
	 {
        uint8_t hist_idx = (idx + 6 - i) % 6;
        output += lp_filter.b[i] * lp_filter.x_buf[hist_idx];
        output -= lp_filter.a[i] * lp_filter.y_buf[hist_idx];
    }
    output /= lp_filter.a[0];
    
    lp_filter.y_buf[idx] = output;
    lp_filter.index = (idx + 1) % 6;
    
    return output;
}

/**
 * @brief ��ʼ�������˲�����250Hz 4��50Hz�ݲ���
 */
int init_notch_filter(float center_freq, float bandwidth, float sample_rate)
{
    // ��֧��250Hz������
    if(sample_rate != 250.0f) 
	 {
        return -1;
    }
    
    // 250Hz 50Hz�ݲ�ϵ����45-55Hz�����
    powerline_notch.b[0] = 0.9340f;   // b0
    powerline_notch.b[1] = -1.5864f;  // b1
    powerline_notch.b[2] = 2.5028f;   // b2
    powerline_notch.b[3] = -1.5864f;  // b3
    powerline_notch.b[4] = 0.9340f;   // b4
    
    powerline_notch.a[0] = -1.5864f;  // a1
    powerline_notch.a[1] = 2.5028f;   // a2
    powerline_notch.a[2] = -1.5864f;  // a3
    powerline_notch.a[3] = 0.8680f;   // a4
    
    memset(powerline_notch.x, 0, sizeof(powerline_notch.x));
    memset(powerline_notch.y, 0, sizeof(powerline_notch.y));
    powerline_notch.index = 0;
    
    return 0;
}

/**
 * @brief �����˲�����
 */
float process_notch_sample(float input)
{
    // ֱ��II��ʵ��
    float w = input - powerline_notch.a[0] * powerline_notch.y[0]
                     - powerline_notch.a[1] * powerline_notch.y[1]
                     - powerline_notch.a[2] * powerline_notch.y[2]
                     - powerline_notch.a[3] * powerline_notch.y[3];
    
    float output = powerline_notch.b[0] * w
                 + powerline_notch.b[1] * powerline_notch.y[0]
                 + powerline_notch.b[2] * powerline_notch.y[1]
                 + powerline_notch.b[3] * powerline_notch.y[2]
                 + powerline_notch.b[4] * powerline_notch.y[3];
    
    // ����״̬����
    powerline_notch.y[3] = powerline_notch.y[2];
    powerline_notch.y[2] = powerline_notch.y[1];
    powerline_notch.y[1] = powerline_notch.y[0];
    powerline_notch.y[0] = w;
    
    powerline_notch.x[3] = powerline_notch.x[2];
    powerline_notch.x[2] = powerline_notch.x[1];
    powerline_notch.x[1] = powerline_notch.x[0];
    powerline_notch.x[0] = input;
    
    return output;
}
