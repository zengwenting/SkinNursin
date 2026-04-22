<template>
  <view class="page skin-test-page healing-shell">
    <view class="healing-header">
      <text class="title">肤质分析</text>
      <view class="desc">点击按钮调起前置摄像头拍摄。当前 AI 为假数据返回，接口已预留真实接入位置。</view>
    </view>

    <view class="glass-card upload-card">
      <image v-if="photo" class="preview" :src="photo" mode="aspectFill" />
      <view v-else class="preview placeholder">等待拍摄照片</view>
      <button class="pill-button" @click="takePhoto">拍摄并分析</button>
    </view>

    <view class="glass-card report-card" v-if="report">
      <view class="section-title">分析报告</view>
      <view class="score-row"><text>肤质类型</text><text>{{ report.skinType }}</text></view>
      <view class="score-row"><text>补水评分</text><text>{{ report.hydrationScore }}</text></view>
      <view class="score-row"><text>出油评分</text><text>{{ report.oilinessScore }}</text></view>
      <view class="score-row"><text>敏感评分</text><text>{{ report.sensitivityScore }}</text></view>
      <view class="report-text">{{ report.advice }}</view>
    </view>
  </view>
</template>

<script setup>
import { ref } from "vue";
import api from "@/utils/api";

const photo = ref("");
const report = ref(null);

const randomScore = (base) => base + Math.floor(Math.random() * 8);

const takePhoto = async () => {
  try {
    // 获取系统配置
    const config = await api.getSystemConfig();
    
    // 检查配置是否有效
    if (!config || !config.ai_api_key || !config.ai_api_url || !config.ai_model) {
      uni.showToast({ title: "大模型连接失败", icon: "none" });
      return;
    }
    
    uni.chooseImage({
      count: 1,
      sourceType: ["camera"],
      camera: "front",
      success: async (res) => {
        photo.value = res.tempFilePaths[0];
        report.value = await api.createSkinTest({
          skinType: "combination",
          hydrationScore: randomScore(80),
          oilinessScore: randomScore(35),
          sensitivityScore: randomScore(20),
          poreScore: randomScore(40),
          blackheadScore: randomScore(30),
        });
      },
    });
  } catch (err) {
    console.error('获取系统配置失败:', err);
    uni.showToast({ title: "大模型连接失败", icon: "none" });
  }
};
</script>

<style lang="less">
.skin-test-page {
  .upload-card,
  .report-card {
    padding: 28rpx;
    margin-bottom: 24rpx;
  }

  .preview {
    width: 100%;
    height: 460rpx;
    margin-bottom: 26rpx;
    border-radius: 34rpx;
    background: rgba(255, 255, 255, 0.8);
  }

  .placeholder {
    .flex(center, center);
    color: #97a0b5;
  }

  .score-row {
    .flex(center, space-between);
    min-height: 70rpx;
    color: #2d3547;
  }

  .report-text {
    margin-top: 18rpx;
    color: #697286;
    line-height: 1.8;
  }
}
</style>
