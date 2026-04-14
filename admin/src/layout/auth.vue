<template>
  <div class="layout_auth">
    <aside class="auth_sidebar">
      <div class="sidebar_brand">
        <div class="brand_mark">SA</div>
        <div class="brand_text">
          <h2>{{ appInfoData?.name }}</h2>
          <p>{{ appInfoData?.subTitle }}</p>
        </div>
      </div>
      <nav class="sidebar_nav">
        <button
          v-for="item in menu"
          :key="item.path"
          class="nav_item"
          :class="{ 'nav_item--active': route.path === item.path }"
          @click="go(item.path)"
        >
          <span class="item_icon">{{ item.icon }}</span>
          <span class="item_label">{{ item.title }}</span>
        </button>
      </nav>
      <div class="sidebar_footer">
        <p>统一查看小程序护理数据</p>
      </div>
    </aside>

    <main class="auth_main">
      <header class="main_header">
        <div>
          <p class="header_caption">SkinCare Admin</p>
          <h1>{{ route.meta?.title || "工作台" }}</h1>
        </div>
        <div class="header_user">
          <div class="user_meta">
            <strong>{{ user?.nickname || "管理员" }}</strong>
            <span>{{ user?.username || "staff" }}</span>
          </div>
          <a-button type="default" @click="doLogout">退出登录</a-button>
        </div>
      </header>

      <section class="main_content">
        <router-view></router-view>
      </section>
    </main>
  </div>
</template>

<script setup>
import { storeToRefs } from "pinia";
import { useRoute } from "vue-router";
import { Modal } from "ant-design-vue";
import useAppStore from "@/store/app";
import { go, rep } from "@/utils/navigator";

const route = useRoute();
const appStore = useAppStore();
const { menu, user, appInfoData } = storeToRefs(appStore);

function doLogout() {
  Modal.confirm({
    title: "确认退出当前账号？",
    okText: "退出",
    cancelText: "取消",
    onOk: async () => {
      await appStore.logout();
      rep("/login");
      window.location.reload();
    },
  });
}
</script>

<style lang="less">
.layout_auth {
  display: flex;
  min-height: 100vh;
  background:
    radial-gradient(circle at top left, rgba(246, 214, 224, 0.7), transparent 30%),
    linear-gradient(180deg, #fff8fb 0%, #f7f4f6 100%);

  .auth_sidebar {
    width: 280px;
    min-height: 100vh;
    padding: 28px 22px;
    background: rgba(255, 255, 255, 0.88);
    border-right: 1px solid rgba(225, 210, 216, 0.9);
    backdrop-filter: blur(18px);

    .sidebar_brand {
      display: flex;
      align-items: center;
      gap: 14px;
      padding-bottom: 24px;

      .brand_mark {
        display: flex;
        align-items: center;
        justify-content: center;
        width: 52px;
        height: 52px;
        border-radius: 18px;
        background: linear-gradient(135deg, #f6d4de 0%, #eadde7 100%);
        color: #8f5d72;
        font-weight: 700;
      }

      .brand_text {
        h2 {
          margin: 0;
          font-size: 20px;
          color: #4e3f47;
        }

        p {
          margin: 4px 0 0;
          color: #9d8b93;
          font-size: 12px;
        }
      }
    }

    .sidebar_nav {
      display: flex;
      flex-direction: column;
      gap: 10px;
    }

    .nav_item {
      display: flex;
      align-items: center;
      gap: 12px;
      width: 100%;
      padding: 14px 16px;
      border: 1px solid transparent;
      border-radius: 18px;
      background: transparent;
      color: #65545d;
      text-align: left;
      transition: all 0.2s ease;

      .item_icon {
        min-width: 28px;
        font-size: 13px;
        color: #b57e92;
      }

      &:hover {
        cursor: pointer;
        background: rgba(248, 228, 235, 0.72);
      }

      &--active {
        background: linear-gradient(135deg, #f7dce5 0%, #f6eef2 100%);
        border-color: #efcad6;
        box-shadow: 0 12px 24px rgba(223, 192, 203, 0.25);
      }
    }

    .sidebar_footer {
      margin-top: 24px;
      padding: 16px;
      border-radius: 18px;
      background: #fff6f8;
      color: #a18b94;
      font-size: 13px;
      line-height: 1.6;
    }
  }

  .auth_main {
    flex: 1;
    padding: 28px;

    .main_header {
      display: flex;
      align-items: center;
      justify-content: space-between;
      margin-bottom: 24px;
      padding: 24px 28px;
      border: 1px solid rgba(231, 217, 222, 0.9);
      border-radius: 28px;
      background: rgba(255, 255, 255, 0.85);
      box-shadow: 0 18px 40px rgba(214, 193, 200, 0.16);

      .header_caption {
        margin: 0 0 8px;
        color: #c08aa0;
        font-size: 12px;
        letter-spacing: 0.12em;
        text-transform: uppercase;
      }

      h1 {
        margin: 0;
        font-size: 30px;
        color: #4c3c45;
      }

      .header_user {
        display: flex;
        align-items: center;
        gap: 16px;
      }

      .user_meta {
        display: flex;
        flex-direction: column;
        align-items: flex-end;
        color: #7f6a73;

        strong {
          color: #4c3c45;
        }
      }
    }

    .main_content {
      min-height: calc(100vh - 164px);
    }
  }
}
</style>
