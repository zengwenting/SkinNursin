<template>
  <view class="page checkin-page healing-shell">
    <view class="healing-header">
      <text class="title">今日打卡</text>
      <view class="desc">选择本次使用的护肤品，完成后会生成当日护理记录。</view>
    </view>

    <view class="cosmetic-grid">
      <view
        class="glass-card cosmetic-tile"
        v-for="item in cosmetics"
        :key="item.id"
        :class="{ active: selectedIds.includes(item.id) }"
        @click="toggleProduct(item.id)"
      >
        <image class="tile-image" :src="item.imageUrl || fallbackImage" mode="aspectFill" />
        <text class="tile-name">{{ item.name }}</text>
        <text class="tile-expire">{{ item.expireDate || "--" }}</text>
      </view>
    </view>

    <button v-if="selectedIds.length" class="pill-button submit-btn" @click="submitCheckin">完成打卡</button>
  </view>
</template>

<script setup>
import { onMounted, ref } from "vue";
import api from "@/utils/api";
import useAppStore from "@/store/app";
import { STATIC_PATH } from "@/config";

const appStore = useAppStore();
const cosmetics = ref([]);
const selectedIds = ref([]);
const fallbackImage = STATIC_PATH("img-default.png");

const loadCosmetics = async () => {
  cosmetics.value = await api.getCosmetics("skincare");
};

const toggleProduct = (id) => {
  selectedIds.value = selectedIds.value.includes(id)
    ? selectedIds.value.filter((item) => item !== id)
    : [...selectedIds.value, id];
};

const submitCheckin = async () => {
  await api.createCheckin({
    skinStatus: "stable",
    hydrationScore: 86,
    oilinessScore: 42,
    sensitivityScore: 25,
    note: "完成了今日温和护理与补水修护。",
    cosmeticIds: selectedIds.value,
  });
  await appStore.refreshTodayCheckin();
  uni.showToast({ title: "打卡成功", icon: "success" });
  setTimeout(() => {
    uni.redirectTo({ url: "/pages/checkin-detail/index" });
  }, 500);
};

onMounted(loadCosmetics);
</script>

<style lang="less">
.checkin-page {
  .cosmetic-grid {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 22rpx;
  }

  .cosmetic-tile {
    padding: 20rpx;
    text-align: center;
    border: 2rpx solid transparent;

    &.active {
      border-color: rgba(230, 133, 181, 0.8);
      box-shadow: 0 24rpx 56rpx rgba(237, 170, 205, 0.32);
    }
  }

  .tile-image {
    width: 100%;
    height: 220rpx;
    border-radius: 24rpx;
    background: #f3f5fb;
  }

  .tile-name {
    display: block;
    margin-top: 18rpx;
    color: #273043;
    font-size: 28rpx;
    font-weight: 700;
  }

  .tile-expire {
    display: block;
    margin-top: 10rpx;
    color: #81889b;
    font-size: 22rpx;
  }

  .submit-btn {
    margin-top: 30rpx;
  }
}
</style>
