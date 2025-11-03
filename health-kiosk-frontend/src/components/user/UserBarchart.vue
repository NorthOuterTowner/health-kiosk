<template>
  <div class="charts-layout">
    <!-- 柱状图 -->
    <div ref="barChartRef" class="chart-box"></div>

    <!-- 饼状图 -->
    <div ref="pieChartRef" class="chart-box"></div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref, reactive } from 'vue';
import * as echarts from "echarts";
import { userChartApi } from "../../api/user/user";
import { useI18n } from 'vue-i18n'

const { t } = useI18n();

const barChartRef = ref<HTMLDivElement | null>(null);
const pieChartRef = ref<HTMLDivElement | null>(null);

onMounted(async() => {
  try{
    const res = await userChartApi();
    const {register_counts, role_rate} = res.data;
    
    const barCounts = register_counts.map((item: any) => item.cnt);

    const xAxisData = register_counts.map((item: any) => {
      const date = new Date(item.date); // item.date = "2025-08-29T16:00:00.000Z"
      const month = (date.getMonth() + 1).toString().padStart(2, "0"); // 月份从0开始
      const day = date.getDate().toString().padStart(2, "0");
      return `${month}-${day}`; // "08-29"
    });
    
    if(barChartRef.value){
      const barChart = echarts.init(barChartRef.value);
      const barOption = {
        title: {
          text: t('user.barChart.title'),
          left:"center"
        },
        tooltip: {},
        grid: {
          top: 60,
          left: "10%",
          right: "10%",
          bottom: 40
        },
        xAxis: {
          type: "category",
          data: xAxisData
        },
        yAxis: {
          type: "value",
          minInterval: 1
        },
        series: [
          {
            name: "每日注册人数",
            type:"bar",
            data: barCounts,
            itemStyle: {
              borderRadius: [6,6,0,0]
            }
          }
        ]
      };
      barChart.setOption(barOption);
      window.addEventListener("resize",() => barChart.resize());
    };

    const pieData = role_rate.map((item:any) => {
      return {
        value: item.rate,
        name: t(`user.role.${item.role}`)
      }
    });

    if(pieChartRef.value) {
      const pieChart = echarts.init(pieChartRef.value);
      const pieOption = {
        title: {
          text: t('user.pieChart.title'),
          left: "center"
        },
        tooltip: {
          trigger: "item",
          formatter: function (params: any){
            return `${params.name}: ${params.percent}%`;
          }
          
        },
        legend: {
          orient: "vertical",
          left: "left"
        },
        series: [
          {
            name: "用户数",
            type: "pie",
            radius: "70%",
            data: pieData,
            emphasis: {
              itemStyle: {
                shadowBlur: 10,
                shadowOffsetX: 0,
                shadowColor: "rgba(0, 0, 0, 0.5)"
              }
            }
          }
        ]
      };
      pieChart.setOption(pieOption);
      window.addEventListener("resize",() => pieChart.resize());
    }
  }catch(err){
    console.log(err)
  }
});
</script>

<style scoped>
.charts-layout {
  display: flex;
  justify-content: center;
  align-items: flex-start;
}

.chart-box {
  width: 500px;
  height: 300px;
  background-color: aliceblue;
  border-radius: 16px;
  margin: 10px;
  
  margin-top: 0px;
}
</style>
