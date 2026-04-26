<template>
  <div class="page management_page">
    <section class="list_card">
      <div class="toolbar">
        <div>
          <h3>肤质测试</h3>
          <p>查看测试结果，并修订总结与建议内容。</p>
        </div>
        <div class="toolbar_actions">
          <a-input v-model:value="query.keyword" placeholder="搜索用户 / 总结 / 建议" />
          <a-button type="primary" @click="search">搜索</a-button>
        </div>
      </div>

      <div class="table_wrapper">
        <table>
          <thead>
            <tr>
              <th>ID</th>
              <th>用户</th>
              <th>测试日期</th>
              <th>图片</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="item in list" :key="item.id">
              <td>{{ item.id }}</td>
              <td>{{ item.nickname || "-" }}</td>
              <td>{{ item.createTime ? new Date(item.createTime).toLocaleDateString() : "-" }}</td>
              <td>
                <img v-if="item.testDate && isImagePath(item.testDate)" :src="item.testDate" class="test-image" />
                <span v-else>-</span>
              </td>
              <td><a-button size="small" @click="openDetail(item.id)">查看详情</a-button></td>
            </tr>
          </tbody>
        </table>
      </div>
    </section>

    <section class="detail_card">
      <div class="detail_head">
        <h3>测试详情</h3>
      </div>
      <div v-if="detail.id" class="detail_form">
        <label><span>肤质</span><div class="readonly_value">{{ detail.skinType || "-" }}</div></label>
        <label><span>补水评分</span><div class="readonly_value">{{ detail.hydrationScore || "-" }}</div></label>
        <label><span>出油评分</span><div class="readonly_value">{{ detail.oilinessScore || "-" }}</div></label>
        <label><span>敏感评分</span><div class="readonly_value">{{ detail.sensitivityScore || "-" }}</div></label>
        <label><span>毛孔评分</span><div class="readonly_value">{{ detail.poreScore || "-" }}</div></label>
        <label><span>黑头评分</span><div class="readonly_value">{{ detail.blackheadScore || "-" }}</div></label>
        <label class="full"><span>总结</span><div class="readonly_value">{{ detail.summary || "-" }}</div></label>
        <label class="full"><span>建议</span><div class="readonly_value">{{ detail.advice || "-" }}</div></label>
      </div>
      <div v-else class="empty_panel">请选择左侧测试记录查看详情</div>
    </section>
  </div>
</template>

<script setup>
// 肤质测试模块：展示测试列表，并支持查看详情和后台修订。
import { onMounted, reactive, ref } from "vue";
import { message } from "ant-design-vue";
import { skinAssistantAdminApi } from "@/utils/skin-assistant-admin-api";

const query = reactive({ keyword: "" });
const list = ref([]);
const detail = reactive({});

// 判断是否是图片路径
const isImagePath = (path) => {
  if (!path) return false;
  
  // 检查是否是云存储路径
  if (path.startsWith('cloud://')) {
    return true;
  }
  
  // 检查是否是网络图片链接
  if (path.startsWith('http://') || path.startsWith('https://')) {
    // 检查是否是图片URL（包含常见图片域名或参数）
    const imageDomains = ['qhimgs1.com', 'qpic.cn', 'wx.qlogo.cn', 'mmbiz.qpic.cn', 'img.com', 'file.youlai.cn'];
    const lowercasePath = path.toLowerCase();
    
    // 检查是否包含图片域名
    for (const domain of imageDomains) {
      if (lowercasePath.includes(domain)) {
        return true;
      }
    }
    
    // 检查是否以图片扩展名结尾
    const imageExtensions = ['.jpg', '.jpeg', '.png', '.gif', '.webp'];
    if (imageExtensions.some(ext => lowercasePath.endsWith(ext))) {
      return true;
    }
  }
  
  // 检查是否是本地图片文件路径
  const imageExtensions = ['.jpg', '.jpeg', '.png', '.gif', '.webp'];
  const lowercasePath = path.toLowerCase();
  return imageExtensions.some(ext => lowercasePath.endsWith(ext));
};

async function loadData() {
  const res = await skinAssistantAdminApi.getSkinTestPage(query, 1, 20);
  if (res?.status) {
    list.value = res.data || [];
  }
}

async function openDetail(id) {
  const res = await skinAssistantAdminApi.getSkinTestInfo(id);
  if (res?.status && res.data) {
    Object.keys(detail).forEach((key) => delete detail[key]);
    Object.assign(detail, res.data);
  }
}

async function saveDetail() {
  const res = await skinAssistantAdminApi.updateSkinTest(detail.id, detail);
  if (res?.status) {
    message.success("肤质测试记录已保存");
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

.test-image {
  width: 60px;
  height: 60px;
  object-fit: cover;
  border-radius: 4px;
  background-color: #f5f5f5;
}
</style>
