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
    uni.chooseImage({
      count: 1,
      sourceType: ["camera"],
      camera: "front",
      sizeType: ['compressed'], // 使用微信自动压缩
      success: async (res) => {
        let tempImagePath = res.tempFilePaths[0];
        
        // 检查图片大小，大于5MB则二次压缩
        const fileInfo = await new Promise((resolve) => {
          uni.getFileInfo({
            filePath: tempImagePath,
            success: resolve,
            fail: (err) => {
              console.error('获取文件信息失败:', err);
              resolve({ size: 0 });
            }
          });
        });
        
        // 如果文件大于5MB，进行二次压缩
        if (fileInfo.size > 5 * 1024 * 1024) {
          const compressResult = await new Promise((resolve) => {
            uni.compressImage({
              src: tempImagePath,
              quality: 80,
              success: resolve,
              fail: (err) => {
                console.error('压缩图片失败:', err);
                resolve({ tempFilePath: tempImagePath });
              }
            });
          });
          tempImagePath = compressResult.tempFilePath;
        }
        
        // 上传图片到云存储
        let fileID = null;
        try {
          const uploadResult = await new Promise((resolve) => {
            wx.cloud.uploadFile({
              cloudPath: 'skin-test/' + Date.now() + '.jpg',
              filePath: tempImagePath,
              success: resolve,
              fail: (err) => {
                console.error('上传图片失败:', err);
                resolve(null);
              }
            });
          });
          if (uploadResult) {
            fileID = uploadResult.fileID;
          }
        } catch (err) {
          console.error('云存储上传失败:', err);
        }
        
        // 显示图片预览
        photo.value = tempImagePath;
        
        // 无论系统配置如何，都返回假数据
        uni.showToast({ title: "大模型连接失败，使用模拟数据", icon: "none" });
        
        // 生成随机评分
        const hydrationScore = Math.floor(Math.random() * 101);
        const oilinessScore = Math.floor(Math.random() * 101);
        const sensitivityScore = Math.floor(Math.random() * 101);
        const poreScore = Math.floor(Math.random() * 101);
        const blackheadScore = Math.floor(Math.random() * 101);
        
        // 计算测试总分（平均值）
        const score = Math.floor((hydrationScore + oilinessScore + sensitivityScore + poreScore + blackheadScore) / 5);
        
        // 固定的总结和建议
        const summary = "本次检测显示屏障状态整体平稳，建议继续保持规律作息。";
        const advice = "建议加强补水护理，减少过度清洁，并持续观察鼻翼与面颊区域。";
        
        // 调用后端API保存假数据
        report.value = await api.createSkinTest({
          testDate: fileID || tempImagePath, // 存储fileID或临时路径
          skinType: "combination",
          hydrationScore: hydrationScore,
          oilinessScore: oilinessScore,
          sensitivityScore: sensitivityScore,
          poreScore: poreScore,
          blackheadScore: blackheadScore,
          score: score,
          summary: summary,
          advice: advice
        });
      },
      fail: (err) => {
        console.error('选择图片失败:', err);
        uni.showToast({ title: "选择图片失败", icon: "none" });
      }
    });
  } catch (err) {
    console.error('拍摄照片失败:', err);
    uni.showToast({ title: "大模型连接失败，使用模拟数据", icon: "none" });
    
    // 即使出错也返回假数据
    const hydrationScore = Math.floor(Math.random() * 101);
    const oilinessScore = Math.floor(Math.random() * 101);
    const sensitivityScore = Math.floor(Math.random() * 101);
    const poreScore = Math.floor(Math.random() * 101);
    const blackheadScore = Math.floor(Math.random() * 101);
    const score = Math.floor((hydrationScore + oilinessScore + sensitivityScore + poreScore + blackheadScore) / 5);
    const summary = "本次检测显示屏障状态整体平稳，建议继续保持规律作息。";
    const advice = "建议加强补水护理，减少过度清洁，并持续观察鼻翼与面颊区域。";
    
    report.value = await api.createSkinTest({
      testDate: "",
      skinType: "combination",
      hydrationScore: hydrationScore,
      oilinessScore: oilinessScore,
      sensitivityScore: sensitivityScore,
      poreScore: poreScore,
      blackheadScore: blackheadScore,
      score: score,
      summary: summary,
      advice: advice
    });
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
