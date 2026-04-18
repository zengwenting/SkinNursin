<template>
  <div class="page management_page">
    <section class="list_card">
      <div class="toolbar">
        <div>
          <h3>化妆台产品</h3>
          <p>查看产品信息，并维护分类、品牌、功效和使用说明。</p>
        </div>
        <div class="toolbar_actions">
          <a-input v-model:value="query.keyword" placeholder="搜索产品 / 品牌 / 功效" />
          <a-button type="primary" @click="search">搜索</a-button>
        </div>
      </div>

      <div class="table_wrapper">
        <table>
          <thead>
            <tr>
              <th>ID</th>
              <th>产品</th>
              <th>用户</th>
              <th>分类</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="item in list" :key="item.id">
              <td>{{ item.id }}</td>
              <td>
                <strong>{{ item.name || "-" }}</strong>
                <p>{{ item.brand || "-" }}</p>
              </td>
              <td>{{ item.nickname || "-" }}</td>
              <td>{{ item.category || "-" }}</td>
              <td><a-button size="small" @click="openDetail(item.id)">查看详情</a-button></td>
            </tr>
          </tbody>
        </table>
      </div>
    </section>

    <section class="detail_card">
      <div class="detail_head">
        <h3>产品详情</h3>
        <a-button v-if="detail.id" type="primary" @click="saveDetail">保存修改</a-button>
      </div>
      <div v-if="detail.id" class="detail_form">
        <label><span>名称</span><input v-model="detail.name" /></label>
        <label><span>品牌</span><input v-model="detail.brand" /></label>
        <label><span>分类</span><input v-model="detail.category" /></label>
        <label><span>图片地址</span><input v-model="detail.imageUrl" /></label>
        <label><span>功效标签</span><input v-model="detail.effectTag" /></label>
        <label><span>使用时段</span><input v-model="detail.usePeriod" /></label>
        <label class="full"><span>成分</span><textarea v-model="detail.ingredient" rows="4"></textarea></label>
        <label class="full"><span>备注</span><textarea v-model="detail.note" rows="4"></textarea></label>
      </div>
      <div v-else class="empty_panel">请选择左侧产品查看详情</div>
    </section>
  </div>
</template>

<script setup>
// 化妆台产品模块：展示产品列表，并支持查看详情和后台编辑。
import { onMounted, reactive, ref } from "vue";
import { message } from "ant-design-vue";
import { skinAssistantAdminApi } from "@/utils/skin-assistant-admin-api";

const query = reactive({ keyword: "" });
const list = ref([]);
const detail = reactive({});

async function loadData() {
  const res = await skinAssistantAdminApi.getCosmeticPage(query, 1, 20);
  if (res?.status) {
    list.value = res.data || [];
  }
}

async function openDetail(id) {
  const res = await skinAssistantAdminApi.getCosmeticInfo(id);
  if (res?.status && res.data) {
    Object.keys(detail).forEach((key) => delete detail[key]);
    Object.assign(detail, res.data);
  }
}

async function saveDetail() {
  const res = await skinAssistantAdminApi.updateCosmetic(detail.id, detail);
  if (res?.status) {
    message.success("化妆台产品已保存");
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
