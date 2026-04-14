<template>
  <div class="page mini_table_page">
    <section class="table_card">
      <div class="toolbar">
        <div>
          <h3>护理打卡</h3>
          <p>查看用户每日皮肤状态和实际使用的护肤产品数量。</p>
        </div>
        <div class="toolbar_actions">
          <a-input v-model:value="query.keyword" placeholder="搜索用户 / 肌肤状态 / 备注" />
          <a-button type="primary" @click="search">搜索</a-button>
        </div>
      </div>

      <div class="table_wrapper">
        <table>
          <thead>
            <tr>
              <th>ID</th>
              <th>用户</th>
              <th>打卡日期</th>
              <th>肌肤状态</th>
              <th>评分</th>
              <th>产品数</th>
              <th>备注</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="item in list" :key="item.id">
              <td>{{ item.id }}</td>
              <td>{{ item.nickname || "-" }}</td>
              <td>{{ item.checkinDate || "-" }}</td>
              <td>{{ item.skinStatus || "-" }}</td>
              <td>
                <strong>补水 {{ item.hydrationScore || 0 }}</strong>
                <p>出油 {{ item.oilinessScore || 0 }} / 敏感 {{ item.sensitivityScore || 0 }}</p>
              </td>
              <td>{{ item.cosmeticCount || 0 }}</td>
              <td>{{ item.note || "-" }}</td>
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
    "checkinPage",
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
  }

  .toolbar h3 {
    margin: 0;
    color: #4d3d46;
  }

  .toolbar p {
    margin: 8px 0 0;
    color: #8f7b84;
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
