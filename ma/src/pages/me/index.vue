<template>
  <view class="page profile-page healing-shell">
    <view class="top-safe" :style="{ height: `${safeTop}px` }"></view>

    <view class="healing-header">
      <text class="title title-placeholder">我的</text>
    </view>

    <!-- 微信授权登录区域 -->
    <button 
      v-if="!isLoggedIn" 
      class="glass-card profile-card" 
      open-type="chooseAvatar" 
      @chooseavatar="onChooseAvatar"
    >
      <image class="avatar" :src="displayAvatar" mode="aspectFill" />
      <view class="profile-meta">
        <text class="nickname">{{ displayNickname }}</text>
        <text class="account">{{ accountHint }}</text>
      </view>
    </button>
    <view v-else class="glass-card profile-card" @click="handleProfileClick">
      <image class="avatar" :src="displayAvatar" mode="aspectFill" />
      <view class="profile-meta">
        <text class="nickname">{{ displayNickname }}</text>
        <text class="account">{{ accountHint }}</text>
      </view>
    </view>

    <view class="glass-card menu-card">
      <view class="menu-item" @click="goMyInfo">
        <text class="menu-label">我的信息</text>
        <text class="menu-action">进入</text>
      </view>
      <view class="menu-item" @click="goSettings">
        <text class="menu-label">闹钟设置</text>
        <text class="menu-action">进入</text>
      </view>
      <view class="menu-item" @click="goOfficial">
        <text class="menu-label">公众号</text>
        <text class="menu-action">查看</text>
      </view>
    </view>

    <button v-if="isLoggedIn" class="pill-button logout-floating" @click="logout">
      <text class="logout-text">退出登录</text>
    </button>
  </view>
</template>

<script setup>
import { computed, onMounted, ref } from "vue";
import { onShow } from "@dcloudio/uni-app";
import useAppStore from "@/store/app";
import { STATIC_PATH } from "@/config";
import api from "@/utils/api";

const appStore = useAppStore();
const authProfile = computed(() => appStore.authProfile);
const isLoggedIn = computed(() => appStore.isLoggedIn);
const fallbackAvatar = STATIC_PATH("blank-avatar.png");
const safeTop = ref(108);

const textUnlogged = "未登录";
const textHintLogged = "已登录";
const textHintUnlogged = "点击获取微信头像昵称并登录";
const textLoginSuccess = "登录成功";
const textLoginCancel = "已取消登录";
const textProfileDesc = "用于完善用户资料";
const textAlreadyLogged = "已登录";
const textLogoutSuccess = "已退出登录";

const displayAvatar = computed(() => authProfile.value?.avatar || fallbackAvatar);
const displayNickname = computed(() => authProfile.value?.nickname || textUnlogged);
const accountHint = computed(() => (isLoggedIn.value ? textHintLogged : textHintUnlogged));

const updateSafeTop = () => {
  try {
    const sys = uni.getSystemInfoSync();
    const menuRect = uni.getMenuButtonBoundingClientRect?.();
    if (menuRect && menuRect.bottom) {
      safeTop.value = Math.max(40, Math.ceil(menuRect.bottom - 40));
      return;
    }
    safeTop.value = (sys?.statusBarHeight || 24) + 34;
  } catch (err) {
    safeTop.value = 58;
  }
};

const loginWithWechat = () =>
  new Promise((resolve, reject) => {
    uni.getUserProfile({
      desc: textProfileDesc,
      success: (res) => {
        const profile = res?.userInfo || {};
        appStore.setAuthProfile({
          avatar: profile.avatarUrl || "",
          nickname: profile.nickName || "",
        });
        resolve();
      },
      fail: (err) => reject(err),
    });
  });

const goMyInfo = () => uni.navigateTo({ url: "/pages/user-info/index" });
const goSettings = () => uni.navigateTo({ url: "/pages/settings/index" });
const goOfficial = () => uni.navigateTo({ url: "/pages/official/index" });

// 处理头像点击事件（已登录状态）
const handleProfileClick = async () => {
  if (isLoggedIn.value) {
    // 跳转到账户详情页面
    uni.navigateTo({ url: "/pages/account/index" });
    return;
  }
};

// 处理微信头像选择事件
const onChooseAvatar = async (e) => {
  try {
    // 获取用户选择的头像信息
    const avatarUrl = e.detail.avatarUrl;
    
    // 调用微信登录获取 code
    const loginRes = await new Promise((resolve, reject) => {
      uni.login({
        success: resolve,
        fail: reject
      });
    });
    
    if (!loginRes.code) {
      throw new Error('获取微信登录码失败');
    }
    
    // 模拟获取用户昵称（实际项目中可能需要用户输入）
    const nickname = '微信用户';
    
    // 模拟 openid（实际项目中应该由后端通过 code 换取）
    const openid = 'mock_openid_' + Date.now();
    
    // 调用登录接口
    const loginApiRes = await api.login({
      code: loginRes.code,
      nickname: nickname,
      avatar: avatarUrl,
      openid: openid
    });
    
    // 更新用户信息
    appStore.setAuthProfile({
      avatar: loginApiRes.avatar,
      nickname: loginApiRes.nickname
    });
    
    // 模拟存储 token
    appStore.setToken('mock_token_' + Date.now());
    
    uni.showToast({ title: textLoginSuccess, icon: "success" });
  } catch (err) {
    console.error('登录失败:', err);
    uni.showToast({ title: textLoginCancel, icon: "none" });
  }
};

const logout = () => {
  appStore.logout();
  uni.showToast({ title: textLogoutSuccess, icon: "success" });
};

onMounted(() => {
  updateSafeTop();
  appStore.loadLocalState();
  appStore.refreshUser();
});

onShow(() => {
  updateSafeTop();
  appStore.loadLocalState();
  appStore.refreshUser();
});
</script>

<style lang="less">
.profile-page {
  .top-safe {
    width: 100%;
  }

  .title-placeholder {
    visibility: hidden;
  }

  .profile-card {
    .flex(center, flex-start);
    padding: 30rpx;
    margin-top: 10px;
    margin-bottom: 24rpx;
  }

  /* 微信授权登录按钮样式 */
  button.profile-card {
    background: transparent;
    border: none;
    outline: none;
    padding: 0;
    margin: 0;
    width: 100%;
    margin-top: 10px;
    margin-bottom: 24rpx;
    padding: 30rpx;
  }

  button.profile-card::after {
    border: none;
  }

  .avatar {
    width: 126rpx;
    height: 126rpx;
    border-radius: 50%;
    margin-right: 22rpx;
    background: #f1f4fa;
  }

  .profile-meta {
    display: flex;
    flex-direction: column;
    gap: 12rpx;
  }

  .nickname {
    color: #222938;
    font-size: 36rpx;
    font-weight: 700;
  }

  .account {
    color: #7a8093;
    font-size: 26rpx;
  }

  .menu-card {
    padding: 18rpx 26rpx;
  }

  .menu-item {
    .flex(center, space-between);
    min-height: 98rpx;
    border-bottom: 1rpx solid rgba(225, 229, 239, 0.9);

    &:last-child {
      border-bottom: none;
    }
  }

  .menu-label {
    color: #2f3749;
    font-size: 30rpx;
    font-weight: 500;
  }

  .menu-label,
  .menu-action,
  .menu-label text,
  .menu-action text {
    color: inherit;
  }

  .menu-action {
    color: #7a8093;
    font-size: 28rpx;
  }

  .logout-floating {
    position: fixed;
    left: 28rpx;
    right: 28rpx;
    bottom: calc(env(safe-area-inset-bottom) + 126rpx);
    z-index: 20;
    min-height: 92rpx;
    text-align: center;
  }

  .logout-text {
    color: #1f2430;
    font-size: 32rpx;
    font-weight: 700;
    line-height: 1.2;
  }
}
</style>
