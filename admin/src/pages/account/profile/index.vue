<template>
  <div class="page account_profile">
    <div class="profile_header">
      <h1>管理员列表</h1>
      <div class="header_actions">
        <a-input 
          placeholder="按昵称搜索" 
          v-model:value="searchKeyword" 
          style="width: 300px; margin-right: 12px" 
          @keyup.enter="fetchStaffList"
        >
        </a-input>
        <a-button type="primary" @click="openCreateModal">创建账号</a-button>
      </div>
    </div>
    
    <div class="profile_content">
      <a-table :columns="columns" :data-source="staffList" :pagination="pagination" :loading="loading">
        <template #role="{ record }">
          <a-tag :color="record.role === 'admin' ? 'green' : 'blue'">
            {{ record.role === 'admin' ? '超级管理员' : '运营人员' }}
          </a-tag>
        </template>
        <template #status="{ record }">
          <a-switch 
            :checked="record.status === 1"
            @change="(checked) => handleStatusChange(record, checked)"
            :disabled="record.role === 'admin'"
          />
        </template>
        <template #operation="{ record }">
          <a-space size="small">
            <a-button v-if="record.role !== 'admin'" @click="openUpdateModal(record)">修改</a-button>
            <a-button v-if="record.role !== 'admin'" danger @click="confirmDelete(record)">删除</a-button>
          </a-space>
        </template>
      </a-table>
    </div>

    <!-- 创建管理员弹窗 -->
    <a-modal title="创建管理员" v-model:visible="createModalVisible" @ok="handleCreate">
      <a-form :model="createForm" layout="vertical">
        <a-form-item label="昵称">
          <a-input v-model:value="createForm.nickname" placeholder="请输入昵称" />
        </a-form-item>
        <a-form-item label="账号">
          <a-input v-model:value="createForm.username" placeholder="请输入账号" />
        </a-form-item>
        <a-form-item label="密码">
          <a-input-password v-model:value="createForm.passwd" placeholder="请输入密码" />
        </a-form-item>
      </a-form>
    </a-modal>

    <!-- 修改管理员弹窗 -->
    <a-modal title="修改管理员" v-model:visible="updateModalVisible" @ok="handleUpdate">
      <a-form :model="updateForm" layout="vertical">
        <a-form-item label="昵称">
          <a-input v-model:value="updateForm.nickname" placeholder="请输入昵称" />
        </a-form-item>
        <a-form-item label="账号">
          <a-input v-model:value="updateForm.username" placeholder="请输入账号" />
        </a-form-item>
        <a-form-item label="密码">
          <a-input-password v-model:value="updateForm.passwd" placeholder="请输入密码（留空则不修改）" />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { storeToRefs } from 'pinia';
import useAppStore from '@/store/app';
import { OrgStaff } from '@/entity';
import { message, Modal } from 'ant-design-vue';

const appStore = useAppStore();
const { user } = storeToRefs(appStore);

// 表格数据
const staffList = ref([]);
const loading = ref(false);
const searchKeyword = ref('');

// 分页
const pagination = ref({
  current: 1,
  pageSize: 10,
  total: 0,
  onChange: (page, pageSize) => {
    pagination.value.current = page;
    pagination.value.pageSize = pageSize;
    fetchStaffList();
  },
});

// 列定义
const columns = [
  {
    title: 'ID',
    dataIndex: 'id',
    key: 'id',
  },
  {
    title: '昵称',
    dataIndex: 'nickname',
    key: 'nickname',
  },
  {
    title: '账号',
    dataIndex: 'username',
    key: 'username',
  },
  {
    title: '角色',
    dataIndex: 'role',
    key: 'role',
    slots: { customRender: 'role' },
  },
  {
    title: '账号启用',
    dataIndex: 'status',
    key: 'status',
    slots: { customRender: 'status' },
  },
  {
    title: '操作',
    key: 'operation',
    slots: { customRender: 'operation' },
  },
];

// 创建弹窗
const createModalVisible = ref(false);
const createForm = ref({
  nickname: '',
  username: '',
  passwd: '',
});

// 修改弹窗
const updateModalVisible = ref(false);
const updateForm = ref({
  id: '',
  nickname: '',
  username: '',
  passwd: '',
});

// 获取管理员列表
const fetchStaffList = async () => {
  loading.value = true;
  try {
    const response = await OrgStaff.sendApi('staffPage', {
      params: {
        pageIndex: pagination.value.current,
        pageSize: pagination.value.pageSize,
        query: JSON.stringify({
          keyword: searchKeyword.value,
        }),
      },
    });
    if (response.status) {
      staffList.value = response.data;
      pagination.value.total = response.data.length;
    }
  } catch (error) {
    message.error('获取管理员列表失败');
  } finally {
    loading.value = false;
  }
};

// 打开创建弹窗
const openCreateModal = () => {
  createForm.value = {
    nickname: '',
    username: '',
    passwd: '',
  };
  createModalVisible.value = true;
};

// 打开修改弹窗
const openUpdateModal = (record) => {
  updateForm.value = {
    id: record.id,
    nickname: record.nickname,
    username: record.username,
    passwd: '',
  };
  updateModalVisible.value = true;
};

// 处理创建
const handleCreate = async () => {
  try {
    const response = await OrgStaff.sendApi('staffAdd', {
      params: {
        nickname: createForm.value.nickname,
        username: createForm.value.username,
        passwd: createForm.value.passwd,
      },
      body: {},
    });
    if (response.status) {
      message.success('创建成功');
      createModalVisible.value = false;
      fetchStaffList();
    } else {
      message.error('创建失败');
    }
  } catch (error) {
    message.error('创建失败');
  }
};

// 处理修改
const handleUpdate = async () => {
  try {
    const response = await OrgStaff.sendApi('staffUpdate', {
      params: {
        id: updateForm.value.id,
        nickname: updateForm.value.nickname,
        username: updateForm.value.username,
        passwd: updateForm.value.passwd,
      },
      body: {},
    });
    if (response.status) {
      message.success('修改成功');
      updateModalVisible.value = false;
      fetchStaffList();
    } else {
      message.error('修改失败');
    }
  } catch (error) {
    message.error('修改失败');
  }
};

// 处理状态变更
const handleStatusChange = async (record, checked) => {
  const newStatus = checked ? 1 : 0;
  try {
    const response = await OrgStaff.sendApi('staffUpdate', {
      params: {
        id: record.id,
        nickname: record.nickname,
        username: record.username,
        status: newStatus,
      },
      body: {},
    });
    if (response.status) {
      // 更新本地状态
      record.status = newStatus;
    } else {
      message.error('状态变更失败');
    }
  } catch (error) {
    message.error('状态变更失败');
  }
};

// 确认删除
const confirmDelete = (record) => {
  Modal.confirm({
    title: '确认删除该管理员？',
    okText: '删除',
    cancelText: '取消',
    onOk: async () => {
      try {
        const response = await OrgStaff.sendApi('staffDelete', {
          params: {
            id: record.id,
          },
          body: {},
        });
        if (response.status) {
          message.success('删除成功');
          fetchStaffList();
        } else {
          message.error('删除失败');
        }
      } catch (error) {
        message.error('删除失败');
      }
    },
  });
};

// 初始化
onMounted(() => {
  fetchStaffList();
});
</script>

<style lang="less">
.account_profile {
  display: flex;
  flex-direction: column;
  gap: 20px;

  .profile_header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 20px;
    background: white;
    border-radius: 8px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);

    h1 {
      margin: 0;
      font-size: 20px;
      color: #333;
    }

    .header_actions {
      display: flex;
      align-items: center;
    }
  }

  .profile_content {
    background: white;
    border-radius: 8px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
    padding: 20px;
  }
}
</style>