#include "stm32f10x.h"                  // Device header
#include "delay.h"
#include "OLED.h"
#include "OELD_Data.h"
#include "FMQ.h"
#include "Serial.h"
#include "MQ3.h"

u16 MQ3_Value;//酒精浓度


int main(void)
{
	 Adc_Init();
	 OLED_Init();
	 mfq_Init();
	 Serial_Init();
	
	OLED_ShowChinese(0, 47, "酒精浓度：");
	OLED_ShowString(96,47,"mg/L",OLED_8X16);
	
	OLED_Update();

	while(1)
	{

		 MQ3_Value=(Get_Adc_Average(ADC_Channel_1,10)*2.92)/4096;//酒精浓度值
		
		OLED_ShowNum(70,47,MQ3_Value,3,OLED_8X16);
		OLED_Update();
		
		
		fmq(MQ3_Value);//25mg/L

		 printf("酒精浓度= %d ",MQ3_Value);
		 Serial_SendString("mg/L\r\n");

	}
}
