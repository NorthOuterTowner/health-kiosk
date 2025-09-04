<template>
  <div class="charts-layout">
    <!-- 柱状图 -->
    <div ref="barChartRef" class="chart-box"></div>

    <!-- 饼状图 -->
    <div ref="pieChartRef" class="chart-box"></div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from "vue";
import * as echarts from "echarts";

const barChartRef = ref<HTMLDivElement | null>(null);
const pieChartRef = ref<HTMLDivElement | null>(null);

const xAxios = ref<string[]>([]);

onMounted(() => {
  if (barChartRef.value) {
    const barChart = echarts.init(barChartRef.value);

    const barOption = {
      title: {
        text: "每日注册人数",
        left: "center"
      },
      tooltip: {},
      grid: {
        top: 60,
        left: "10%",
        right: "10%",
        bottom: 40
      },
      xAxis: {
        data: ["苹果", "香蕉", "橘子", "梨子", "葡萄", "西瓜"]// calculate according to current date
      },
      yAxis: {},
      series: [
        {
          name: "销量",
          type: "bar",
          data: [5, 20, 36, 10, 10, 20],// use axios api to get data
          itemStyle: {
            borderRadius: [6, 6, 0, 0]
          }
        }
      ]
    };

    barChart.setOption(barOption);
    window.addEventListener("resize", () => barChart.resize());
  }

  if (pieChartRef.value) {
    const pieChart = echarts.init(pieChartRef.value);

    const pieOption = {
      title: {
        text: "用户角色分布",
        left: "center"
      },
      tooltip: {
        trigger: "item"
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
          data: [ // use axios api to get data
            { value: 10, name: "游客" },
            { value: 20, name: "访客" },
            { value: 30, name: "用户" },
            { value: 25, name: "管理员" },
            { value: 15, name: "超级管理员" }
          ],
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
    window.addEventListener("resize", () => pieChart.resize());
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
