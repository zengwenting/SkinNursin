<template>
  <view class="page account-page healing-shell">
    <view class="top-safe" :style="{ height: `${safeTop}px` }"></view>

    <view class="healing-header">
      <text class="title">{{ textTitle }}</text>
    </view>

    <view class="content">
      <!-- 头像模块 -->
      <view class="avatar-section">
        <button 
          class="avatar-button" 
          open-type="chooseAvatar" 
          @chooseavatar="onChooseAvatar"
        >
          <image class="avatar" :src="avatarUrl" mode="aspectFill" />
        </button>
        <text class="avatar-hint">{{ textAvatarHint }}</text>
      </view>

      <!-- 昵称模块 -->
      <view class="nickname-section">
        <view class="nickname-label">{{ textNicknameLabel }}</view>
        <input 
          class="nickname-input" 
          v-model="nickname" 
          placeholder="请输入昵称" 
          placeholder-class="placeholder"
        />
      </view>

      <!-- 保存按钮 -->
      <button class="save-button pill-button" @click="saveProfile">
        <text class="save-text">{{ textSaveButton }}</text>
      </button>
    </view>
  </view>
</template>

<script setup>
import { computed, onMounted, ref } from "vue";
import useAppStore from "@/store/app";
import { STATIC_PATH } from "@/config";
import api from "@/utils/api";

const appStore = useAppStore();
const authProfile = computed(() => appStore.authProfile);
const fallbackAvatar = STATIC_PATH("blank-avatar.png");
const safeTop = ref(108);

const textTitle = "账户详情";
const textAvatarHint = "点击更换头像";
const textNicknameLabel = "昵称：";
const textSaveButton = "保存";
const textSaveSuccess = "保存成功";
const textSaveFailed = "保存失败，请重试";

// 头像和昵称数据
const avatarUrl = ref(authProfile.value?.avatar || fallbackAvatar);
const nickname = ref(authProfile.value?.nickname || "");

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

// 处理头像选择事件
const onChooseAvatar = (e) => {
  const avatar = e.detail.avatarUrl;
  if (avatar) {
    avatarUrl.value = avatar;
  }
};

// 保存个人资料
const saveProfile = async () => {
  try {
    // 调用后端 API 更新 user 表
    await api.updateUser({
      avatar: avatarUrl.value,
      nickname: nickname.value
    });
    
    // 更新 appStore 中的用户信息
    appStore.setAuthProfile({
      avatar: avatarUrl.value,
      nickname: nickname.value
    });
    
    uni.showToast({ title: textSaveSuccess, icon: "success" });
  } catch (err) {
    console.error('保存失败:', err);
    uni.showToast({ title: textSaveFailed, icon: "none" });
  }
};

onMounted(() => {
  updateSafeTop();
  // 加载用户信息
  avatarUrl.value = authProfile.value?.avatar || fallbackAvatar;
  nickname.value = authProfile.value?.nickname || "";
});
</script>

<style lang="less">
.account-page {
  .top-safe {
    width: 100%;
  }

  .content {
    padding: 40rpx 30rpx;
    display: flex;
    flex-direction: column;
    align-items: center;
  }

  /* 头像模块 */
  .avatar-section {
    display: flex;
    flex-direction: column;
    align-items: center;
    margin-bottom: 60rpx;

    .avatar-button {
      background: transparent;
      border: none;
      outline: none;
      padding: 0;
      margin: 0;
      width: 160rpx;
      height: 160rpx;
      border-radius: 50%;
      overflow: hidden;
    }

    .avatar-button::after {
      border: none;
    }

    .avatar {
      width: 100%;
      height: 100%;
      border-radius: 50%;
      background: #f1f4fa;
    }

    .avatar-hint {
      margin-top: 16rpx;
      color: #7a8093;
      font-size: 26rpx;
    }
  }

  /* 昵称模块 */
  .nickname-section {
    width: 100%;
    background: transparent;
    padding: 20rpx 0;
    border-top: 1rpx solid rgba(225, 229, 239, 0.9);
    border-bottom: 1rpx solid rgba(225, 229, 239, 0.9);
    margin-bottom: 60rpx;

    .nickname-label {
      color: #222938;
      font-size: 30rpx;
      margin-bottom: 12rpx;
      text-align: center;
    }

    .nickname-input {
      width: 100%;
      background: transparent;
      border: none;
      outline: none;
      font-size: 32rpx;
      color: #222938;
      text-align: center;
      padding: 10rpx 0;
    }

    .placeholder {
      color: #c0c4cc;
    }
  }

  /* 保存按钮 */
  .save-button {
    width: 150px;
    min-height: 92rpx;
    background: #1f2430;
    border-radius: 8rpx;
    display: flex;
    align-items: center;
    justify-content: center;
  }

  .save-text {
    color: #ffffff;
    font-size: 32rpx;
    font-weight: 700;
    line-height: 1.2;
  }
}
</style>