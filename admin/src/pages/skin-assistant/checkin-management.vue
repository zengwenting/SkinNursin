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
              <th>状态</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="item in list" :key="item.id">
              <td>{{ item.id }}</td>
              <td>{{ item.nickname || "-" }}</td>
              <td>{{ item.checkinDate || "-" }}</td>
              <td>{{ item.skinStatus || "-" }}</td>
              <td><a-button size="small" @click="openDetail(item.id)">查看详情</a-button></td>
            </tr>
          </tbody>
        </table>
      </div>
    </section>

    <section class="detail_card">
      <div class="detail_head">
        <h3>打卡详情</h3>
        <a-button v-if="detail.id" type="primary" @click="saveDetail">保存修改</a-button>
      </div>
      <div v-if="detail.id" class="detail_form">
        <label><span>皮肤状态</span><input v-model="detail.skinStatus" /></label>
        <label><span>补水评分</span><input v-model="detail.hydrationScore" type="number" /></label>
        <label><span>出油评分</span><input v-model="detail.oilinessScore" type="number" /></label>
        <label><span>敏感评分</span><input v-model="detail.sensitivityScore" type="number" /></label>
        <label class="full"><span>备注</span><textarea v-model="detail.note" rows="5"></textarea></label>
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
</style>
