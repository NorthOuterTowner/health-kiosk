#ifndef __MQ3_H
#define __MQ3_H	 
#include "stm32f10x.h"                  // Device header
#include "Delay.h"
void Adc_Init(void);
u16  Get_Adc(u8 ch); 
u16 Get_Adc_Average(u8 ch,u8 times); 


#endif

