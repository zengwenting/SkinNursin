<template>
  <div class="page management_page">
    <section class="list_card">
      <div class="toolbar">
        <div>
          <h3>护理打卡</h3>
          <p>查看每日护理记录，并修订皮肤状态和备注。</p>
        </div>
        <div class="toolbar_actions">
          <a-input v-model:value="query.keyword" placeholder="搜索用户 / 状态 / 备注" />
          <a-button type="primary" @click="search">搜索</a-button>
        </div>
      </div>

      <div class="table_wrapper">
        <table>
          <thead>
            <tr>
              <th>ID</th>
              <th>用户</th>
              <th>日期</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="item in list" :key="item.id">
              <td>{{ item.id }}</td>
              <td>{{ item.nickname || "-" }}</td>
              <td>{{ item.checkinDate || "-" }}</td>
              <td><a-button size="small" @click="openDetail(item.id)">查看详情</a-button></td>
            </tr>
          </tbody>
        </table>
      </div>
    </section>

    <section class="detail_card">
      <div class="detail_head">
        <h3>打卡详情</h3>
      </div>
      <div v-if="detail.id" class="detail_form">
        <label><span>用户</span><div class="readonly_value">{{ detail.nickname || "-" }}</div></label>
        <label><span>打卡时间</span><div class="readonly_value">{{ detail.createTime || "-" }}</div></label>
      </div>
      <div v-if="detail.id" class="detail_section">
        <h4>使用产品列表</h4>
        <div v-if="detail.cosmetics && detail.cosmetics.length > 0" class="cosmetic_list">
          <div v-for="cosmetic in detail.cosmetics" :key="cosmetic.id" class="cosmetic_item">
            <img class="cosmetic_image" :src="cosmetic.cosmeticImage || 'https://via.placeholder.com/80'" alt="{{ cosmetic.cosmeticName }}" />
            <div class="cosmetic_name">{{ cosmetic.cosmeticName }}</div>
          </div>
        </div>
        <div v-else class="empty_state">
          <div class="empty_icon">📦</div>
          <div class="empty_text">未使用任何产品</div>
        </div>
      </div>
      <div v-else class="empty_panel">请选择左侧打卡记录查看详情</div>
    </section>
  </div>
</template>

<script setup>
// 护理打卡模块：展示打卡列表，并支持查看详情和后台修订。
import { onMounted, reactive, ref } from "vue";
import { message } from "ant-design-vue";
import { skinAssistantAdminApi } from "@/utils/skin-assistant-admin-api";

const query = reactive({ keyword: "" });
const list = ref([]);
const detail = reactive({});

async function loadData() {
  const res = await skinAssistantAdminApi.getCheckinPage(query, 1, 20);
  if (res?.status) {
    list.value = res.data || [];
  }
}

async function openDetail(id) {
  const res = await skinAssistantAdminApi.getCheckinInfo(id);
  if (res?.status && res.data) {
    Object.keys(detail).forEach((key) => delete detail[key]);
    Object.assign(detail, res.data);
  }
}

async function saveDetail() {
  const res = await skinAssistantAdminApi.updateCheckin(detail.id, detail);
  if (res?.status) {
    message.success("护理打卡记录已保存");
    loadData();
    openDetail(detail.id);
  }
}

function search() {
  loadData();
}

onMounted(loadData);
</script>

<style lang="less">
@import "./_management.less";

.detail_section {
  margin-top: 20px;
  
  h4 {
    margin-bottom: 15px;
    font-size: 16px;
    font-weight: 600;
  }
  
  .cosmetic_list {
    display: flex;
    flex-direction: column;
    gap: 10px;
  }
  
  .cosmetic_item {
    display: flex;
    align-items: center;
    gap: 15px;
    padding: 15px;
    background-color: #f9f9f9;
    border-radius: 8px;
    
    .cosmetic_image {
      width: 80px;
      height: 80px;
      border-radius: 4px;
      object-fit: cover;
    }
    
    .cosmetic_name {
      font-size: 14px;
      font-weight: 500;
      color: #333;
    }
  }
  
  .empty_state {
    text-align: center;
    padding: 30px;
    color: #999;
  }
}
</style>
