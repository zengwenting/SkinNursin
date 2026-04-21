<template>
  <div class="page management_page">
    <section class="list_card">
      <div class="toolbar">
        <div>
          <h3>用户管理</h3>
          <p>查看用户档案并维护肤质、目标和个人简介。</p>
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
              <th>测试次数</th>
              <th>打卡次数</th>
              <th>上次登录</th>
              <th>注册时间</th>
              <th>状态</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="item in list" :key="item.id">
              <td>{{ item.id }}</td>
              <td>
                <strong>{{ item.nickname || "-" }}</strong>
                <p>{{ item.account || "-" }}</p>
              </td>
              <td>{{ item.skinTestCount || 0 }}</td>
              <td>{{ item.checkinCount || 0 }}</td>
              <td>{{ item.lastLoginTime || "-" }}</td>
              <td>{{ item.createTime || "-" }}</td>
              <td>{{ item.status === 1 ? "正常" : "禁用" }}</td>
              <td>
                <a-button size="small" @click="openDetail(item.id)">查看详情</a-button>
                <a-button 
                  size="small" 
                  :type="item.status === 1 ? 'default' : 'primary'"
                  style="margin-left: 8px"
                  @click="toggleStatus(item)"
                >
                  {{ item.status === 1 ? "禁用" : "启用" }}
                </a-button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </section>

    <section class="detail_card">
      <div class="detail_head">
        <h3>用户详情</h3>
        <a-button v-if="detail.id" type="primary" @click="saveDetail">保存修改</a-button>
      </div>

      <div v-if="detail.id" class="detail_form">
        <label>
          <span>昵称</span>
          <input v-model="detail.nickname" />
        </label>
        <label>
          <span>账号</span>
          <input v-model="detail.account" />
        </label>
        <label>
          <span>年龄</span>
          <input v-model="detail.age" type="number" />
        </label>
        <label>
          <span>性别</span>
          <input v-model="detail.gender" />
        </label>
        <label>
          <span>肤质</span>
          <input v-model="detail.skinType" />
        </label>
        <label>
          <span>护肤目标</span>
          <input v-model="detail.skinGoal" />
        </label>
        <label class="full">
          <span>个人简介</span>
          <textarea v-model="detail.bio" rows="5"></textarea>
        </label>
      </div>
      <div v-else class="empty_panel">请选择左侧用户查看详情</div>
    </section>
  </div>
</template>

<script setup>
// 用户管理模块：展示用户列表，并支持查看详情和编辑保存。
import { onMounted, reactive, ref } from "vue";
import { message, Modal } from "ant-design-vue";
import { skinAssistantAdminApi } from "@/utils/skin-assistant-admin-api";

const query = reactive({ keyword: "" });
const list = ref([]);
const detail = reactive({});

async function loadData() {
  const res = await skinAssistantAdminApi.getUserPage(query, 1, 20);
  if (res?.status) {
    list.value = res.data || [];
  }
}

async function openDetail(id) {
  const res = await skinAssistantAdminApi.getUserInfo(id);
  if (res?.status && res.data) {
    Object.keys(detail).forEach((key) => delete detail[key]);
    Object.assign(detail, res.data);
  }
}

async function saveDetail() {
  const res = await skinAssistantAdminApi.updateUser(detail.id, {
    nickname: detail.nickname,
    account: detail.account,
    age: detail.age,
    gender: detail.gender,
    skinType: detail.skinType,
    skinGoal: detail.skinGoal,
    bio: detail.bio,
  });
  if (res?.status) {
    message.success("用户信息已保存");
    loadData();
    openDetail(detail.id);
  }
}

async function toggleStatus(item) {
  const newStatus = item.status === 1 ? 0 : 1;
  const actionText = newStatus === 1 ? "启用" : "禁用";
  
  Modal.confirm({
    title: `确认${actionText}用户？`,
    content: `确定要${actionText}用户 ${item.nickname} 吗？`,
    onOk: async () => {
      const res = await skinAssistantAdminApi.updateUser(item.id, {
        status: newStatus
      });
      if (res?.status) {
        message.success(`${actionText}成功`);
        loadData();
      }
    }
  });
}

function search() {
  loadData();
}

onMounted(loadData);
</script>

<style lang="less">
.management_page {
  display: grid;
  grid-template-columns: 1.25fr 0.75fr;
  gap: 20px;

  .list_card,
  .detail_card {
    padding: 24px;
    border: 1px solid rgba(233, 220, 225, 0.95);
    border-radius: 28px;
    background: rgba(255, 255, 255, 0.9);
    box-shadow: 0 18px 36px rgba(217, 196, 203, 0.14);
  }

  .toolbar,
  .detail_head {
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 20px;
    margin-bottom: 18px;
  }

  .toolbar h3,
  .detail_head h3 {
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
    overflow-x: auto;
    border: 1px solid #f0e4e8;
    border-radius: 22px;
  }

  table {
    min-width: 1000px;
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

  .detail_form {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 16px;
  }

  .detail_form label {
    display: flex;
    flex-direction: column;
    gap: 8px;
    color: #6f5d66;
  }

  .detail_form label.full {
    grid-column: span 2;
  }

  .detail_form input,
  .detail_form textarea {
    width: 100%;
    padding: 12px 14px;
    border: 1px solid #eddde2;
    border-radius: 14px;
    outline: none;
    background: #fffafb;
  }

  .empty_panel {
    display: flex;
    align-items: center;
    justify-content: center;
    height: 240px;
    color: #9c8b93;
    background: #fff8fa;
    border-radius: 18px;
  }
}
</style>
