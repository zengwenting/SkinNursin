<template>
  <div class="page mini_table_page">
    <section class="table_card">
      <div class="toolbar">
        <div>
          <h3>用户管理</h3>
          <p>查看小程序用户基础信息、护肤目标与活跃沉淀数据。</p>
        </div>
        <div class="toolbar_actions">
          <a-input v-model:value="query.keyword" placeholder="搜索昵称 / 账号 / 护肤目标" />
          <a-button type="primary" @click="search">搜索</a-button>
        </div>
      </div>

      <div class="table_wrapper">
        <table>
          <thead>
            <tr>
              <th>ID</th>
              <th>用户</th>
              <th>肤质 / 目标</th>
              <th>测试记录</th>
              <th>打卡记录</th>
              <th>产品数</th>
              <th>最近活跃</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="item in list" :key="item.id">
              <td>{{ item.id }}</td>
              <td>
                <strong>{{ item.nickname || "-" }}</strong>
                <p>{{ item.account || "-" }}</p>
              </td>
              <td>
                <strong>{{ item.skinType || "-" }}</strong>
                <p>{{ item.skinGoal || "未填写目标" }}</p>
              </td>
              <td>{{ item.skinTestCount || 0 }}</td>
              <td>{{ item.checkinCount || 0 }}</td>
              <td>{{ item.cosmeticCount || 0 }}</td>
              <td>
                <strong>{{ item.lastCheckinDate || "-" }}</strong>
                <p>测试：{{ item.lastSkinTestDate || "-" }}</p>
              </td>
            </tr>
            <tr v-if="!list.length">
              <td colspan="7" class="empty">暂无数据</td>
            </tr>
          </tbody>
        </table>
      </div>

      <div class="pagination_bar">
        <span>共 {{ total }} 条</span>
        <div class="page_actions">
          <a-button :disabled="pageIndex <= 1" @click="changePage(pageIndex - 1)">上一页</a-button>
          <span>第 {{ pageIndex }} / {{ totalPage }} 页</span>
          <a-button :disabled="pageIndex >= totalPage" @click="changePage(pageIndex + 1)">下一页</a-button>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from "vue";
import { miniAdminPost } from "@/utils/mini-admin";

const query = reactive({ keyword: "" });
const list = ref([]);
const pageIndex = ref(1);
const pageSize = ref(10);
const total = ref(0);

const totalPage = computed(() => Math.max(1, Math.ceil(total.value / pageSize.value)));

async function loadData() {
  const res = await miniAdminPost(
    "userPage",
    { query: { keyword: query.keyword } },
    { pageIndex: pageIndex.value, pageSize: pageSize.value }
  );
  if (res?.status) {
    list.value = res.data || [];
    total.value = res.page?.total || 0;
  }
}

function search() {
  pageIndex.value = 1;
  loadData();
}

function changePage(page) {
  pageIndex.value = page;
  loadData();
}

onMounted(loadData);
</script>

<style lang="less">
.mini_table_page {
  .table_card {
    padding: 24px;
    border: 1px solid rgba(233, 220, 225, 0.95);
    border-radius: 28px;
    background: rgba(255, 255, 255, 0.9);
    box-shadow: 0 18px 36px rgba(217, 196, 203, 0.14);
  }

  .toolbar {
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 20px;
    margin-bottom: 18px;

    h3 {
      margin: 0;
      color: #4d3d46;
    }

    p {
      margin: 8px 0 0;
      color: #8f7b84;
    }
  }

  .toolbar_actions {
    display: flex;
    gap: 12px;
    width: 420px;
  }

  .table_wrapper {
    overflow: auto;
    border: 1px solid #f0e4e8;
    border-radius: 22px;
  }

  table {
    width: 100%;
    border-collapse: collapse;
    background: white;
  }

  th,
  td {
    padding: 16px;
    border-bottom: 1px solid #f4e8ec;
    text-align: left;
    vertical-align: top;
    color: #5a4d54;
  }

  thead th {
    background: #fff5f8;
    color: #8d7180;
    font-weight: 600;
  }

  tbody p {
    margin: 6px 0 0;
    color: #97838c;
    font-size: 12px;
  }

  .empty {
    text-align: center;
    color: #9c8b93;
  }

  .pagination_bar {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-top: 18px;
    color: #8f7b84;
  }

  .page_actions {
    display: flex;
    align-items: center;
    gap: 12px;
  }
}
</style>
