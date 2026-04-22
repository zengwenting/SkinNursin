<template>
  <div class="page dashboard_home">
    <section class="hero_card">
      <div>
        <p class="hero_tag">Dashboard</p>
        <h2>皮肤护理小助手后台总览</h2>
        <p class="hero_desc">
          统一查看用户、肤质测试、护理打卡和化妆台产品数据，并提供图表化运营视图。
        </p>
      </div>
      <a-button type="primary" @click="loadDashboard">刷新数据</a-button>
    </section>

    <section class="stats_grid">
      <div class="stat_card" v-for="item in stats" :key="item.label">
        <p>{{ item.label }}</p>
        <strong>{{ item.value }}</strong>
      </div>
    </section>

    <section class="chart_grid">
      <div class="chart_card">
        <div class="chart_head">
          <h3>用户肤质分布</h3>
        </div>
        <div ref="skinTypeChartRef" class="chart_canvas"></div>
      </div>
      <div class="chart_card">
          <div class="chart_head">
            <h3>敏感肌占比</h3>
          </div>
          <div ref="sensitiveSkinChartRef" class="chart_canvas"></div>
        </div>
      <div class="chart_card chart_card--wide">
        <div class="chart_head">
          <h3>近期打卡数量趋势</h3>
        </div>
        <div ref="checkinTrendChartRef" class="chart_canvas"></div>
      </div>
      <div class="chart_card chart_card--wide">
        <div class="chart_head">
          <h3>近期测试数量趋势</h3>
        </div>
        <div ref="skinTestTrendChartRef" class="chart_canvas"></div>
      </div>
    </section>
  </div>
</template>

<script setup>
// 首页仪表盘模块：负责统计卡片和图表展示。
import { computed, nextTick, onMounted, reactive, ref } from "vue";
import * as echarts from "echarts";
import { skinAssistantAdminApi } from "@/utils/skin-assistant-admin-api";

const dashboard = reactive({});
const skinTypeChartRef = ref(null);
const sensitiveSkinChartRef = ref(null);
const checkinTrendChartRef = ref(null);
const skinTestTrendChartRef = ref(null);

let skinTypeChart = null;
let sensitiveSkinChart = null;
let checkinTrendChart = null;
let skinTestTrendChart = null;

const stats = computed(() => [
  { label: "小程序用户", value: dashboard.userCount || 0 },
  { label: "肤质测试记录", value: dashboard.skinTestCount || 0 },
  { label: "护理打卡记录", value: dashboard.checkinCount || 0 },
  { label: "活跃用户数量", value: dashboard.activeUserCount || 0 },
]);

async function loadDashboard() {
  const res = await skinAssistantAdminApi.getDashboardSummary();
  if (res?.status) {
    Object.assign(dashboard, res.data || {});
    await nextTick();
    renderCharts();
  }
}

function renderCharts() {
  if (!skinTypeChart && skinTypeChartRef.value) {
    skinTypeChart = echarts.init(skinTypeChartRef.value);
  }
  if (!sensitiveSkinChart && sensitiveSkinChartRef.value) {
    sensitiveSkinChart = echarts.init(sensitiveSkinChartRef.value);
  }
  if (!checkinTrendChart && checkinTrendChartRef.value) {
    checkinTrendChart = echarts.init(checkinTrendChartRef.value);
  }
  if (!skinTestTrendChart && skinTestTrendChartRef.value) {
    skinTestTrendChart = echarts.init(skinTestTrendChartRef.value);
  }

  skinTypeChart?.setOption({
    tooltip: { trigger: "item" },
    series: [
      {
        type: "pie",
        radius: ["45%", "72%"],
        data: (dashboard.skinTypeDistribution || []).map((item) => ({
          name: item.label,
          value: item.value,
        })),
      },
    ],
  });

  sensitiveSkinChart?.setOption({
    tooltip: { trigger: "item" },
    series: [
      {
        type: "pie",
        radius: ["45%", "72%"],
        data: (dashboard.sensitiveSkinDistribution || []).map((item) => ({
          name: item.label,
          value: item.value,
        })),
        itemStyle: {
          color: function(params) {
            const colors = ['#c9839c', '#e4a8be'];
            return colors[params.dataIndex % colors.length];
          }
        }
      },
    ],
  });

  checkinTrendChart?.setOption({
    tooltip: { trigger: "axis" },
    xAxis: {
      type: "category",
      data: (dashboard.recentCheckinTrend || []).map((item) => item.label),
    },
    yAxis: { type: "value" },
    series: [
      {
        type: "line",
        smooth: true,
        data: (dashboard.recentCheckinTrend || []).map((item) => item.value),
        areaStyle: {
          color: "rgba(228, 168, 190, 0.18)",
        },
        lineStyle: {
          color: "#c9839c",
        },
        itemStyle: {
          color: "#c9839c",
        },
      },
    ],
  });

  skinTestTrendChart?.setOption({
    tooltip: { trigger: "axis" },
    xAxis: {
      type: "category",
      data: (dashboard.recentSkinTestTrend || []).map((item) => item.label),
    },
    yAxis: { type: "value" },
    series: [
      {
        type: "line",
        smooth: true,
        data: (dashboard.recentSkinTestTrend || []).map((item) => item.value),
        areaStyle: {
          color: "rgba(228, 168, 190, 0.18)",
        },
        lineStyle: {
          color: "#c9839c",
        },
        itemStyle: {
          color: "#c9839c",
        },
      },
    ],
  });
}

onMounted(loadDashboard);
</script>

<style lang="less">
.dashboard_home {
  .hero_card,
  .chart_card,
  .stat_card {
    border: 1px solid rgba(233, 220, 225, 0.95);
    background: rgba(255, 255, 255, 0.9);
    box-shadow: 0 18px 36px rgba(217, 196, 203, 0.14);
  }

  .hero_card {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 28px 30px;
    border-radius: 28px;
  }

  .hero_tag {
    margin: 0 0 10px;
    color: #c58ba0;
    font-size: 12px;
    letter-spacing: 0.14em;
    text-transform: uppercase;
  }

  .hero_card h2 {
    margin: 0;
    color: #4d3d46;
    font-size: 30px;
  }

  .hero_desc {
    max-width: 680px;
    margin: 12px 0 0;
    color: #7c6a73;
    line-height: 1.8;
  }

  .stats_grid {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 18px;
    margin-top: 20px;
  }

  .stat_card {
    padding: 24px;
    border-radius: 24px;
  }

  .stat_card p {
    margin: 0;
    color: #9e8891;
  }

  .stat_card strong {
    display: block;
    margin-top: 14px;
    color: #4d3d46;
    font-size: 34px;
  }

  .chart_grid {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 20px;
    margin-top: 20px;
  }

  .chart_card {
    padding: 24px;
    border-radius: 24px;
  }

  .chart_card--wide {
    grid-column: span 2;
  }

  .chart_head {
    margin-bottom: 18px;
  }

  .chart_head h3 {
    margin: 0;
    color: #4d3d46;
  }

  .chart_canvas {
    height: 320px;
  }
}
</style>
