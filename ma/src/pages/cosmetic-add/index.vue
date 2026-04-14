<template>
  <view class="page cosmetic-add-page healing-shell">
    <view class="healing-header">
      <text class="title">新增产品</text>
    </view>

    <view class="glass-card form-card">
      <view class="section-title">分类</view>
      <radio-group class="category-group" @change="onCategoryChange">
        <label class="category-option">
          <radio value="skincare" :checked="form.category === 'skincare'" />
          <text>护肤品</text>
        </label>
        <label class="category-option">
          <radio value="makeup" :checked="form.category === 'makeup'" />
          <text>化妆品</text>
        </label>
      </radio-group>

      <input class="field" v-model="form.name" placeholder="名称" />
      <input class="field" v-model="form.brand" placeholder="品牌" />
      <picker mode="date" :value="form.productionDate" @change="onDateChange">
        <view class="field picker-field">生产日期：{{ form.productionDate || "请选择" }}</view>
      </picker>
      <input class="field" v-model="shelfLifeYears" type="number" placeholder="保质期（年）" @input="calcExpireDate" />
      <view class="field picker-field">过期日期：{{ form.expireDate || "--" }}</view>
      <input class="field" v-model="form.note" placeholder="备注（可选）" />

      <view class="upload-box" @click="chooseImage">
        <image v-if="form.imageUrl" :src="form.imageUrl" mode="aspectFill" />
        <text v-else>上传图片（可选）</text>
      </view>

      <button class="pill-button save-btn" @click="saveCosmetic">保存产品</button>
    </view>
  </view>
</template>

<script setup>
import { reactive, ref } from "vue";
import dayjs from "dayjs";
import api from "@/utils/api";

const shelfLifeYears = ref("");
const form = reactive({
  category: "skincare",
  name: "",
  brand: "",
  productionDate: "",
  expireDate: "",
  imageUrl: "",
  effectTag: "",
  ingredient: "",
  usePeriod: "day",
  note: "",
});

const onCategoryChange = (e) => {
  form.category = e.detail.value;
};

const onDateChange = (e) => {
  form.productionDate = e.detail.value;
  calcExpireDate();
};

const calcExpireDate = () => {
  if (!form.productionDate || !shelfLifeYears.value) {
    return;
  }
  form.expireDate = dayjs(form.productionDate).add(Number(shelfLifeYears.value || 0), "year").format("YYYY-MM-DD");
};

const chooseImage = () => {
  uni.chooseImage({
    count: 1,
    success: (res) => {
      [form.imageUrl] = res.tempFilePaths;
    },
  });
};

const saveCosmetic = async () => {
  await api.addCosmetic(form);
  uni.showToast({ title: "保存成功", icon: "success" });
  setTimeout(() => {
    uni.navigateBack();
  }, 500);
};
</script>

<style lang="less">
.cosmetic-add-page {
  .form-card {
    padding: 28rpx;
  }

  .category-group {
    .flex(center, flex-start);
    gap: 28rpx;
    margin-bottom: 24rpx;
  }

  .category-option {
    .flex(center, flex-start);
    gap: 10rpx;
  }

  .field {
    height: 88rpx;
    margin-bottom: 18rpx;
    padding: 0 24rpx;
    border-radius: 24rpx;
    background: rgba(255, 255, 255, 0.82);
  }

  .picker-field {
    .flex(center, flex-start);
    color: #555f75;
  }

  .upload-box {
    .flex(center, center);
    height: 220rpx;
    margin-bottom: 26rpx;
    border-radius: 28rpx;
    background: rgba(255, 255, 255, 0.82);
    color: #8f98ac;

    image {
      width: 100%;
      height: 100%;
      border-radius: 28rpx;
    }
  }

  .save-btn {
    margin-top: 6rpx;
  }
}
</style>
