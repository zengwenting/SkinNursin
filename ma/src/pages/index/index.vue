<template>
  <view class="page home-page healing-shell">
    <view class="home-layout">
      <view class="top-zone">
        <view class="home-copy">
          <text class="title">智能面部护理助手</text>
          <text class="desc">把护肤、分析和记录，轻轻收进每天都愿意打开的治愈节奏里。</text>
        </view>
      </view>

      <view class="middle-zone">
        <button class="checkin-card" hover-class="checkin-card--hover" @click="goCheckin">
          <image class="calendar-icon" :src="calendarIcon" mode="aspectFit" />
          <text class="checkin-label">{{ checkedIn ? "查看详情" : "打卡" }}</text>
        </button>
        <text v-if="checkedIn" class="checkin-tip">完成今日打卡，你太棒了！</text>
      </view>

      <view class="bottom-zone">
        <view class="quick-actions">
          <button class="quick-action" hover-class="quick-action--hover" @click="goSkinTest">肤质分析</button>
          <button class="quick-action" hover-class="quick-action--hover" @click="goSkinData">皮肤数据</button>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { computed, onMounted } from "vue";
import { onShow } from "@dcloudio/uni-app";
import useAppStore from "@/store/app";
import { STATIC_PATH } from "@/config";

const appStore = useAppStore();
const checkedIn = computed(() => appStore.todayCheckin?.checkedIn);
const calendarIcon = STATIC_PATH("\u65e5\u5386.png");

const goCheckin = () => {
  uni.navigateTo({
    url: checkedIn.value ? "/pages/checkin-detail/index" : "/pages/checkin/index",
  });
};

const goSkinTest = () => {
  uni.navigateTo({ url: "/pages/skin-test/index" });
};

const goSkinData = () => {
  uni.navigateTo({ url: "/pages/skin-data/index" });
};

const refreshPage = () => {
  appStore.refreshTodayCheckin();
  appStore.refreshUser();
};

onMounted(refreshPage);
onShow(refreshPage);
</script>

<style lang="less">
.home-page {
  overflow: hidden;

  .home-layout {
    min-height: calc(100vh - 84rpx);
    display: flex;
    flex-direction: column;
  }

  .top-zone,
  .middle-zone,
  .bottom-zone {
    flex: 1;
    display: flex;
    align-items: center;
    justify-content: center;
  }

  .top-zone {
    align-items: flex-start;
    padding-top: 220rpx;
  }

  .home-copy {
    width: 100%;
    padding-left: 28rpx;
    text-align: left;
  }

  .title {
    display: block;
    color: #514b7a;
    font-size: 56rpx;
    font-weight: 800;
    letter-spacing: 2rpx;
    text-shadow: 0 10rpx 24rpx rgba(255, 255, 255, 0.72);
  }

  .desc {
    display: block;
    max-width: 520rpx;
    margin: 22rpx 0 0;
    color: #7d8196;
    font-size: 28rpx;
    line-height: 1.8;
  }

  .middle-zone {
    flex-direction: column;
    padding: 12rpx 0 36rpx;
  }

  .checkin-card {
    width: 340rpx;
    height: 340rpx;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    gap: 22rpx;
    border: none;
    border-radius: 56rpx;
    background: linear-gradient(145deg, #fffafd 0%, #ffe9f3 52%, #eef8ff 100%);
    box-shadow:
      0 30rpx 76rpx rgba(230, 178, 208, 0.34),
      inset 0 0 0 3rpx rgba(255, 255, 255, 0.78);
  }

  .checkin-card::after {
    border: none;
  }

  .checkin-card--hover {
    transform: scale(0.97);
    background: linear-gradient(145deg, #fff2f8 0%, #ffdce9 52%, #e2f5ff 100%);
    box-shadow:
      0 20rpx 46rpx rgba(225, 157, 195, 0.4),
      inset 0 0 0 3rpx rgba(255, 255, 255, 0.92);
  }

  .calendar-icon {
    width: 140rpx;
    height: 140rpx;
  }

  .checkin-label {
    color: #252b38;
    font-size: 52rpx;
    font-weight: 700;
    line-height: 1.2;
  }

  .checkin-tip {
    margin-top: 24rpx;
    color: #d1709f;
    font-size: 26rpx;
    font-weight: 600;
    letter-spacing: 1rpx;
  }

  .bottom-zone {
    align-items: flex-end;
    padding-bottom: 54rpx;
  }

  .quick-actions {
    width: 100%;
    display: flex;
    gap: 26rpx;
  }

  .quick-action {
    flex: 1;
    min-height: 100rpx;
    border: none;
    border-radius: 999rpx;
    background: linear-gradient(180deg, rgba(255, 255, 255, 0.98) 0%, rgba(255, 244, 249, 0.96) 100%);
    box-shadow:
      0 18rpx 34rpx rgba(230, 188, 214, 0.26),
      inset 0 0 0 2rpx rgba(255, 255, 255, 0.88);
    color: #252b38;
    font-size: 34rpx;
    font-weight: 600;
  }

  .quick-action--hover {
    transform: translateY(-4rpx);
    background: linear-gradient(180deg, #fff1f7 0%, #ffe0ec 100%);
    box-shadow:
      0 24rpx 42rpx rgba(229, 173, 204, 0.34),
      inset 0 0 0 2rpx rgba(255, 255, 255, 0.94);
  }

  .quick-action::after {
    border: none;
  }
}
</style>
