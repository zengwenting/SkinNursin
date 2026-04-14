<template>
  <view class="page dresser-page healing-shell">
    <view class="toolbar">
      <button class="manage-btn" @click="toggleManage">{{ managing ? "完成" : "管理" }}</button>
    </view>

    <view class="glass-card section-card">
      <view class="section-head" @click="toggleSection('skincare')">
        <text class="section-name">护肤品</text>
        <text class="section-arrow">{{ openSections.skincare ? "收起" : "展开" }}</text>
      </view>
      <view class="cosmetic-list" v-if="openSections.skincare">
        <view class="cosmetic-card" v-for="item in skincareItems" :key="item.id">
          <view class="cosmetic-main">
            <image class="cosmetic-image" :src="item.imageUrl || fallbackImage" mode="aspectFill" />
            <view class="cosmetic-meta">
              <text class="cosmetic-name">{{ item.name }}</text>
              <text class="cosmetic-expire">到期：{{ item.expireDate || "--" }}</text>
            </view>
          </view>
          <checkbox v-if="managing" :checked="selectedIds.includes(item.id)" @click="toggleSelect(item.id)" />
        </view>
      </view>
    </view>

    <view class="glass-card section-card">
      <view class="section-head" @click="toggleSection('makeup')">
        <text class="section-name">化妆品</text>
        <text class="section-arrow">{{ openSections.makeup ? "收起" : "展开" }}</text>
      </view>
      <view class="cosmetic-list" v-if="openSections.makeup">
        <view class="cosmetic-card" v-for="item in makeupItems" :key="item.id">
          <view class="cosmetic-main">
            <image class="cosmetic-image" :src="item.imageUrl || fallbackImage" mode="aspectFill" />
            <view class="cosmetic-meta">
              <text class="cosmetic-name">{{ item.name }}</text>
              <text class="cosmetic-expire">到期：{{ item.expireDate || "--" }}</text>
            </view>
          </view>
          <checkbox v-if="managing" :checked="selectedIds.includes(item.id)" @click="toggleSelect(item.id)" />
        </view>
      </view>
    </view>

    <view class="footer-actions">
      <button class="pill-button" @click="deleteSelected" v-if="managing && selectedIds.length">删除所选</button>
      <button class="pill-button" @click="goAdd" v-if="!managing">添加产品</button>
    </view>
  </view>
</template>

<script setup>
import { onMounted, ref } from "vue";
import { onShow } from "@dcloudio/uni-app";
import api from "@/utils/api";
import { STATIC_PATH } from "@/config";

const fallbackImage = STATIC_PATH("img-default.png");
const skincareItems = ref([]);
const makeupItems = ref([]);
const managing = ref(false);
const selectedIds = ref([]);
const openSections = ref({
  skincare: true,
  makeup: true,
});

const loadCosmetics = async () => {
  skincareItems.value = await api.getCosmetics("skincare");
  makeupItems.value = await api.getCosmetics("makeup");
};

const toggleManage = () => {
  managing.value = !managing.value;
  if (!managing.value) {
    selectedIds.value = [];
  }
};

const toggleSelect = (id) => {
  selectedIds.value = selectedIds.value.includes(id)
    ? selectedIds.value.filter((item) => item !== id)
    : [...selectedIds.value, id];
};

const toggleSection = (key) => {
  openSections.value[key] = !openSections.value[key];
};

const deleteSelected = async () => {
  await Promise.all(selectedIds.value.map((id) => api.deleteCosmetic(id)));
  uni.showToast({ title: "已删除", icon: "success" });
  selectedIds.value = [];
  await loadCosmetics();
};

const goAdd = () => {
  uni.navigateTo({ url: "/pages/cosmetic-add/index" });
};

onMounted(loadCosmetics);
onShow(loadCosmetics);
</script>

<style lang="less">
.dresser-page {
  padding-top: 60px;

  .toolbar {
    display: flex;
    justify-content: flex-end;
    width: 100%;
    margin-bottom: 22rpx;
  }

  .manage-btn {
    margin-left: auto;
    min-width: 180rpx;
    height: 76rpx;
    padding: 0 28rpx;
    border: none;
    border-radius: 25px;
    background: linear-gradient(180deg, rgba(255, 255, 255, 0.97) 0%, rgba(255, 241, 247, 0.96) 100%);
    box-shadow: 0 14rpx 30rpx rgba(228, 187, 213, 0.24);
    color: #455063;
    font-size: 28rpx;
    font-weight: 600;
  }

  .manage-btn::after {
    border: none;
  }

  .section-card {
    margin-bottom: 24rpx;
    padding: 26rpx;
  }

  .section-head {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 18rpx;
  }

  .section-name {
    font-size: 32rpx;
    font-weight: 700;
    color: #222938;
  }

  .section-arrow {
    color: #8b93a6;
    font-size: 24rpx;
  }

  .cosmetic-list {
    display: flex;
    flex-direction: column;
    gap: 18rpx;
  }

  .cosmetic-card {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 18rpx;
    border-radius: 26rpx;
    background: rgba(255, 255, 255, 0.85);
  }

  .cosmetic-main {
    display: flex;
    align-items: center;
    flex: 1;
  }

  .cosmetic-image {
    width: 110rpx;
    height: 110rpx;
    border-radius: 24rpx;
    margin-right: 18rpx;
    background: #f2f4fa;
  }

  .cosmetic-meta {
    display: flex;
    flex-direction: column;
    gap: 10rpx;
  }

  .cosmetic-name {
    color: #212737;
    font-size: 30rpx;
    font-weight: 700;
  }

  .cosmetic-expire {
    color: #7b8397;
    font-size: 24rpx;
  }

  .footer-actions {
    display: flex;
    flex-direction: column;
    gap: 16rpx;
    margin-top: 10rpx;
  }
}
</style>
