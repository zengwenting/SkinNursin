<template>
  <view class="page skin-data-page healing-shell">
    <view class="healing-header">
      <text class="title">皮肤数据</text>
      <view class="desc">查看最近几次分析结果和评分趋势，帮助你观察护理变化。</view>
    </view>

    <view class="glass-card trend-card" v-if="history.length">
      <view class="section-title">评分趋势</view>
      <view class="trend-item" v-for="item in history.slice(0, 4)" :key="item.id">
        <text v-if="!item.testDate || !isImagePath(item.testDate)">{{ item.testDate || '-' }}</text>
        <image v-else class="test-image" :src="item.testDate" mode="aspectFill" />
        <view class="bar-track">
          <view class="bar-fill" :style="{ width: `${item.hydrationScore}%` }"></view>
        </view>
        <text>{{ item.hydrationScore }}</text>
      </view>
    </view>

    <view class="glass-card history-card">
      <view class="section-title">历史报告</view>
      <view class="history-item" v-for="item in history" :key="item.id">
        <view>
          <text v-if="!item.testDate || !isImagePath(item.testDate)" class="history-date">{{ item.testDate || '-' }}</text>
          <image v-else class="test-image" :src="item.testDate" mode="aspectFill" />
          <text class="history-type">{{ item.skinType }}</text>
        </view>
        <text class="history-advice">{{ item.advice }}</text>
      </view>
    </view>
  </view>
</template>

<script setup>
import { onMounted, ref } from "vue";
import { onShow } from "@dcloudio/uni-app";
import api from "@/utils/api";

const history = ref([]);

const isImagePath = (path) => {
  if (!path) return false;
  
  // 检查是否是云存储路径
  if (path.startsWith('cloud://')) {
    return true;
  }
  
  // 检查是否是网络图片链接
  if (path.startsWith('http://') || path.startsWith('https://')) {
    // 检查是否是图片URL（包含常见图片域名或参数）
    const imageDomains = ['qhimgs1.com', 'qpic.cn', 'wx.qlogo.cn', 'mmbiz.qpic.cn', 'img.com'];
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

const loadHistory = async () => {
  history.value = await api.getSkinHistory();
};

onMounted(loadHistory);
onShow(loadHistory);
</script>

<style lang="less">
.skin-data-page {
  .trend-card,
  .history-card {
    padding: 28rpx;
    margin-bottom: 24rpx;
  }

  .trend-item {
    .flex(center, space-between);
    gap: 18rpx;
    margin-bottom: 18rpx;
    color: #40485b;
    font-size: 24rpx;
  }

  .test-image {
    width: 80rpx;
    height: 80rpx;
    border-radius: 8rpx;
    background: #f5f5f5;
  }

  .bar-track {
    flex: 1;
    height: 16rpx;
    border-radius: 999rpx;
    background: rgba(222, 228, 241, 0.8);
    overflow: hidden;
  }

  .bar-fill {
    height: 100%;
    border-radius: 999rpx;
    background: linear-gradient(90deg, #f6bfd7, #bce8ff);
  }

  .history-item {
    padding: 20rpx 0;
    border-bottom: 1rpx solid rgba(227, 232, 241, 0.9);

    &:last-child {
      border-bottom: none;
    }
  }

  .history-date,
  .history-type {
    display: block;
  }

  .history-date {
    color: #253043;
    font-weight: 700;
  }

  .history-type {
    margin-top: 8rpx;
    color: #8790a5;
    font-size: 24rpx;
  }

  .history-advice {
    display: block;
    margin-top: 12rpx;
    color: #656d82;
    line-height: 1.7;
  }
}
</style>
