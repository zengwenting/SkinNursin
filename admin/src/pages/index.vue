<template>
  <div class="page mini_dashboard">
    <section class="hero_card">
      <div>
        <p class="hero_tag">Dashboard</p>
        <h2>皮肤护理小程序后台总览</h2>
        <p class="hero_desc">
          统一查看用户档案、肤质分析、打卡行为与化妆台产品沉淀，和当前微信小程序数据保持同步。
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

    <section class="panel_grid">
      <div class="panel_card">
        <div class="panel_head">
          <h3>最新用户</h3>
          <span>{{ dashboard.recentUsers?.length || 0 }} 条</span>
        </div>
        <div class="simple_list">
          <div v-for="item in dashboard.recentUsers || []" :key="item.id" class="list_item">
            <div>
              <strong>{{ item.nickname || "-" }}</strong>
              <p>{{ item.skinType || "未填写肤质" }} / {{ item.skinGoal || "未填写目标" }}</p>
            </div>
            <span>{{ item.account || "-" }}</span>
          </div>
        </div>
      </div>

      <div class="panel_card">
        <div class="panel_head">
          <h3>最新肤质测试</h3>
          <span>{{ dashboard.recentSkinTests?.length || 0 }} 条</span>
        </div>
        <div class="simple_list">
          <div v-for="item in dashboard.recentSkinTests || []" :key="item.id" class="list_item">
            <div>
              <strong>{{ item.nickname }}</strong>
              <p>{{ item.skinType || "-" }} / {{ item.testDate || "-" }}</p>
            </div>
            <span>{{ item.hydrationScore || 0 }} 分</span>
          </div>
        </div>
      </div>

      <div class="panel_card">
        <div class="panel_head">
          <h3>最新护理打卡</h3>
          <span>{{ dashboard.recentCheckins?.length || 0 }} 条</span>
        </div>
        <div class="simple_list">
          <div v-for="item in dashboard.recentCheckins || []" :key="item.id" class="list_item">
            <div>
              <strong>{{ item.nickname }}</strong>
              <p>{{ item.skinStatus || "未填写状态" }} / {{ item.checkinDate || "-" }}</p>
            </div>
            <span>{{ item.cosmeticCount || 0 }} 件产品</span>
          </div>
        </div>
      </div>

      <div class="panel_card">
        <div class="panel_head">
          <h3>最新化妆台产品</h3>
          <span>{{ dashboard.recentCosmetics?.length || 0 }} 条</span>
        </div>
        <div class="simple_list">
          <div v-for="item in dashboard.recentCosmetics || []" :key="item.id" class="list_item">
            <div>
              <strong>{{ item.name }}</strong>
              <p>{{ item.nickname }} / {{ item.category || "-" }}</p>
            </div>
            <span>{{ item.brand || "-" }}</span>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive } from "vue";
import { miniAdminPost } from "@/utils/mini-admin";

const dashboard = reactive({});

const stats = computed(() => [
  { label: "小程序用户", value: dashboard.userCount || 0 },
  { label: "肤质测试记录", value: dashboard.skinTestCount || 0 },
  { label: "护理打卡记录", value: dashboard.checkinCount || 0 },
  { label: "化妆台产品", value: dashboard.cosmeticCount || 0 },
]);

async function loadDashboard() {
  const res = await miniAdminPost("dashboard");
  if (res?.status) {
    Object.assign(dashboard, res.data || {});
  }
}

onMounted(loadDashboard);
</script>

<style lang="less">
.mini_dashboard {
  .hero_card,
  .panel_card,
  .stat_card {
    border: 1px solid rgba(233, 220, 225, 0.95);
    background: rgba(255, 255, 255, 0.88);
    box-shadow: 0 18px 36px rgba(217, 196, 203, 0.14);
  }

  .hero_card {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 28px 30px;
    border-radius: 28px;

    .hero_tag {
      margin: 0 0 10px;
      color: #c58ba0;
      font-size: 12px;
      letter-spacing: 0.14em;
      text-transform: uppercase;
    }

    h2 {
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

    p {
      margin: 0;
      color: #9e8891;
    }

    strong {
      display: block;
      margin-top: 14px;
      color: #4d3d46;
      font-size: 34px;
    }
  }

  .panel_grid {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 20px;
    margin-top: 20px;
  }

  .panel_card {
    padding: 24px;
    border-radius: 24px;
  }

  .panel_head {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 14px;

    h3 {
      margin: 0;
      color: #4d3d46;
      font-size: 18px;
    }

    span {
      color: #a18b94;
      font-size: 12px;
    }
  }

  .simple_list {
    display: flex;
    flex-direction: column;
    gap: 12px;
  }

  .list_item {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 14px 16px;
    border-radius: 18px;
    background: #fff7f9;

    strong {
      color: #4d3d46;
    }

    p {
      margin: 6px 0 0;
      color: #8f7b84;
      font-size: 12px;
    }

    span {
      color: #906979;
      font-size: 13px;
    }
  }
}
</style>
