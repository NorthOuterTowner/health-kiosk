#include "arm_math.h"

#define NUMSTAGES 3   // 高通 + 陷波 + 低通
#define BLOCKSIZE 1   // 每次处理一个采样点

/* 滤波器系数 */
static float32_t biquad_coeffs[5*NUMSTAGES] = {
    /* 高通 0.5Hz */
    0.9967f, -1.9934f, 0.9967f, -1.9933f, 0.9934f,
    /* 陷波 50Hz */
    0.991673f, -1.604561f, 0.991673f, -1.604561f, 0.983346f,
    /* 低通 100Hz */
    0.2066f, 0.4131f, 0.2066f, -0.3695f, 0.1958f
};

static float32_t biquad_state[4*NUMSTAGES];

static arm_biquad_casd_df1_inst_f32 S;

//初始化
void ECG_Filter_Init(void)
{
    arm_biquad_cascade_df1_init_f32(&S, NUMSTAGES, biquad_coeffs, biquad_state);
}

float32_t ECG_Filter_Process(float32_t input)
{
    float32_t output;
    arm_biquad_cascade_df1_f32(&S, &input, &output, BLOCKSIZE);
    return output;
}

/**调用方法
 * ECG_Filter_Init();
 * ECG_Filter_Process(input);
 */