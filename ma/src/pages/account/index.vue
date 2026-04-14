<template>
  <view class="page account-page healing-shell">
    <view class="healing-header">
      <text class="title">{{ textTitle }}</text>
    </view>

    <view class="glass-card detail-card">
      <view class="avatar-box">
        <image :src="displayAvatar" mode="aspectFill" />
      </view>
      <view class="info-row">
        <text class="label">{{ textName }}</text>
        <text class="value">{{ displayNickname }}</text>
      </view>
    </view>

    <button class="pill-button logout-btn" @click="logout">{{ textLogout }}</button>
  </view>
</template>

<script setup>
import { computed, onMounted } from "vue";
import useAppStore from "@/store/app";
import { STATIC_PATH } from "@/config";

const textTitle = "\u8d26\u53f7\u8be6\u60c5";
const textName = "\u540d\u79f0";
const textUnlogged = "\u672a\u767b\u5f55";
const textLogout = "\u9000\u51fa\u8d26\u53f7";
const textLogoutSuccess = "\u5df2\u9000\u51fa\u8d26\u53f7";

const appStore = useAppStore();
const fallbackAvatar = STATIC_PATH("blank-avatar.png");
const profile = computed(() => appStore.authProfile);
const displayAvatar = computed(() => profile.value?.avatar || fallbackAvatar);
const displayNickname = computed(() => profile.value?.nickname || textUnlogged);

const logout = () => {
  appStore.logout();
  uni.showToast({ title: textLogoutSuccess, icon: "success" });
  uni.navigateBack();
};

onMounted(() => {
  appStore.loadLocalState();
});
</script>

<style lang="less">
.account-page {
  .detail-card {
    padding: 28rpx;
  }

  .avatar-box {
    .flex(center, center, column);
    margin-bottom: 24rpx;

    image {
      width: 140rpx;
      height: 140rpx;
      border-radius: 50%;
      margin-bottom: 12rpx;
    }
  }

  .info-row {
    .flex(center, space-between);
    min-height: 90rpx;
    padding: 0 10rpx;
  }

  .label {
    color: #687189;
    font-size: 28rpx;
  }

  .value {
    color: #1f2430;
    font-size: 30rpx;
    font-weight: 600;
  }

  .logout-btn {
    margin-top: 34rpx;
  }
}
</style>
