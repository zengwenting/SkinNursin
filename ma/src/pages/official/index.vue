<template>
  <view class="page official-page healing-shell">
    <view class="healing-header">
      <text class="title">公众号</text>
      <view class="desc">关注公众号可以接收护理提醒、活动通知和后续 AI 能力升级消息。</view>
    </view>

    <view class="glass-card official-card">
      <image class="poster" :src="poster" mode="aspectFit" />
      <view class="official-name">智能面部护理助手服务号</view>
      <view class="official-tip">可先使用当前小程序完成记录，后续再补公众号联动。</view>
    </view>
  </view>
</template>

<script setup>
import { STATIC_PATH } from "@/config";

const poster = STATIC_PATH("logo-icon.png");
</script>

<style lang="less">
.official-page {
  .official-card {
    padding: 30rpx;
    text-align: center;
  }

  .poster {
    width: 240rpx;
    height: 240rpx;
    margin: 20rpx auto;
  }

  .official-name {
    color: #243045;
    font-size: 32rpx;
    font-weight: 700;
  }

  .official-tip {
    margin-top: 16rpx;
    color: #7f8799;
    line-height: 1.8;
  }
}
</style>
