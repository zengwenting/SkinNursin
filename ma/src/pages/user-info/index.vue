<template>
  <view class="page user-info-page healing-shell">
    <view class="header-row">
      <view class="healing-header">
        <text class="title">我的信息</text>
      </view>
      <button class="header-btn" @click="toggleEdit">{{ editing ? "确认" : "编辑" }}</button>
    </view>

    <view class="glass-card info-card">
      <view class="info-item">
        <text class="label">皮肤属性</text>
        <picker
          v-if="editing"
          mode="selector"
          :range="skinTypeOptions"
          :value="skinTypeIndex"
          @change="onSkinTypeChange"
        >
          <view class="picker-value">{{ form.skinType || "请选择" }}</view>
        </picker>
        <text v-else class="value">{{ form.skinType || "-" }}</text>
      </view>

      <view class="info-item">
        <text class="label">年龄</text>
        <input
          v-if="editing"
          class="field-input"
          type="number"
          v-model="form.age"
          placeholder="请输入年龄"
        />
        <text v-else class="value">{{ form.age || "-" }}</text>
      </view>

      <view class="info-item">
        <text class="label">性别</text>
        <picker
          v-if="editing"
          mode="selector"
          :range="genderOptions"
          :value="genderIndex"
          @change="onGenderChange"
        >
          <view class="picker-value">{{ form.gender || "请选择" }}</view>
        </picker>
        <text v-else class="value">{{ form.gender || "-" }}</text>
      </view>

      <view class="info-item vertical-item">
        <text class="label">是否敏感肌</text>
        <radio-group class="radio-group" @change="onSensitiveChange">
          <label class="radio-item">
            <radio :checked="form.isSensitive === true" :disabled="!editing" value="true" />
            <text>是</text>
          </label>
          <label class="radio-item">
            <radio :checked="form.isSensitive === false" :disabled="!editing" value="false" />
            <text>否</text>
          </label>
        </radio-group>
      </view>

      <view class="info-item vertical-item" v-if="form.isSensitive">
        <text class="label">敏感源</text>
        <view class="tag-wrap">
          <view class="tag" v-for="(item, index) in form.sensitiveSources" :key="`${item}-${index}`">
            <text>{{ item }}</text>
            <text v-if="editing" class="remove" @click="removeSensitiveSource(index)">x</text>
          </view>
        </view>
        <view class="tag-editor" v-if="editing">
          <input class="field-input" v-model="sensitiveDraft" placeholder="输入后点击添加" />
          <button class="mini-btn" @click="addSensitiveSource">添加</button>
        </view>
      </view>

      <view class="info-item vertical-item">
        <text class="label">护理目标</text>
        <checkbox-group class="goal-group" @change="onGoalChange">
          <label class="goal-item" v-for="item in goalOptions" :key="item">
            <checkbox :disabled="!editing" :checked="form.goals.includes(item)" :value="item" />
            <text>{{ item }}</text>
          </label>
        </checkbox-group>
      </view>

      <view class="info-item vertical-item">
        <text class="label">个人简介</text>
        <textarea
          v-if="editing"
          class="textarea-input"
          v-model="form.bio"
          placeholder="请输入个人简介"
          rows="3"
        />
        <text v-else class="value">{{ form.bio || "-" }}</text>
      </view>
    </view>
  </view>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from "vue";
import useAppStore from "@/store/app";
import api from "@/utils/api";

const textSaved = "\u5df2\u4fdd\u5b58";

const appStore = useAppStore();
const editing = ref(false);
const sensitiveDraft = ref("");
const skinTypeOptions = [
  "\u4e2d\u6027",
  "\u6cb9\u6027",
  "\u5e72\u6027",
  "\u6df7\u6cb9",
  "\u6df7\u5e72",
];
const genderOptions = [
  "\u7537",
  "\u5973"
];
const goalOptions = [
  "\u7ef4\u7a33",
  "\u7f8e\u767d",
  "\u6297\u8870",
  "\u5ae9\u80a4",
];

const form = reactive({
  skinType: "",
  age: "",
  gender: "",
  isSensitive: false,
  sensitiveSources: [],
  goals: [],
  bio: "",
});

const syncFromStore = () => {
  const profileInfo = appStore.profileInfo || {};
  form.skinType = profileInfo.skinType || "";
  form.age = profileInfo.age || "";
  form.gender = profileInfo.gender || "";
  form.isSensitive = typeof profileInfo.isSensitive === "boolean" ? profileInfo.isSensitive : false;
  form.sensitiveSources = Array.isArray(profileInfo.sensitiveSources) ? [...profileInfo.sensitiveSources] : [];
  form.goals = Array.isArray(profileInfo.goals) ? [...profileInfo.goals] : [];
  form.bio = profileInfo.bio || "";
};

const skinTypeIndex = computed(() => {
  const idx = skinTypeOptions.findIndex((item) => item === form.skinType);
  return idx >= 0 ? idx : 0;
});

const genderIndex = computed(() => {
  const idx = genderOptions.findIndex((item) => item === form.gender);
  return idx >= 0 ? idx : 0;
});

const onSkinTypeChange = (e) => {
  if (!editing.value) return;
  const index = Number(e.detail.value);
  form.skinType = skinTypeOptions[index] || "";
};

const onGenderChange = (e) => {
  if (!editing.value) return;
  const index = Number(e.detail.value);
  form.gender = genderOptions[index] || "";
};

const onSensitiveChange = (e) => {
  if (!editing.value) return;
  form.isSensitive = e.detail.value === "true";
  if (!form.isSensitive) {
    form.sensitiveSources = [];
    sensitiveDraft.value = "";
  }
};

const addSensitiveSource = () => {
  if (!editing.value) return;
  const value = sensitiveDraft.value.trim();
  if (!value) return;
  if (!form.sensitiveSources.includes(value)) {
    form.sensitiveSources.push(value);
  }
  sensitiveDraft.value = "";
};

const removeSensitiveSource = (index) => {
  if (!editing.value) return;
  form.sensitiveSources.splice(index, 1);
};

const onGoalChange = (e) => {
  if (!editing.value) return;
  form.goals = Array.isArray(e.detail.value) ? e.detail.value : [];
};

const toggleEdit = async () => {
  if (editing.value) {
    try {
      await api.updateUser({
        skinType: form.skinType,
        age: parseInt(form.age) || null,
        gender: form.gender,
        isSensitive: form.isSensitive,
        sensitiveSource: form.isSensitive ? form.sensitiveSources.join(", ") : "",
        skinGoal: form.goals.join(", "),
        bio: form.bio,
      });
      appStore.updateProfileInfo({
        skinType: form.skinType,
        age: form.age,
        gender: form.gender,
        isSensitive: form.isSensitive,
        sensitiveSources: form.sensitiveSources,
        goals: form.goals,
        bio: form.bio,
      });
      uni.showToast({ title: textSaved, icon: "success" });
    } catch (error) {
      uni.showToast({ title: "保存失败，请重试", icon: "none" });
      console.error("保存用户信息失败:", error);
    }
  }
  editing.value = !editing.value;
};

onMounted(() => {
  appStore.loadLocalState();
  syncFromStore();
});
</script>

<style lang="less">
.user-info-page {
  .header-row {
    .flex(center, space-between);
  }

  .header-btn {
    min-width: 132rpx;
    height: 68rpx;
    line-height: 68rpx;
    margin: 0;
    border-radius: 999rpx;
    border: 2rpx solid rgba(224, 132, 178, 0.45);
    background: rgba(255, 255, 255, 0.9);
    color: #2f3749;
    font-size: 26rpx;
    font-weight: 600;
  }

  .header-btn::after {
    border: none;
  }

  .info-card {
    padding: 18rpx 26rpx;
  }

  .info-item {
    .flex(center, space-between);
    min-height: 96rpx;
    border-bottom: 1rpx solid rgba(225, 229, 239, 0.9);
  }

  .vertical-item {
    display: block;
    padding: 24rpx 0;
  }

  .info-item:last-child {
    border-bottom: none;
  }

  .label {
    color: #2f3749;
    font-size: 30rpx;
  }

  .value,
  .picker-value {
    color: #697287;
    font-size: 28rpx;
  }

  .field-input {
    width: 300rpx;
    min-height: 72rpx;
    padding: 0 22rpx;
    border-radius: 18rpx;
    background: rgba(255, 255, 255, 0.9);
    border: 1rpx solid rgba(225, 229, 239, 0.95);
    box-sizing: border-box;
    color: #2f3749;
    font-size: 28rpx;
  }

  .textarea-input {
    width: 100%;
    min-height: 160rpx;
    padding: 20rpx;
    border-radius: 18rpx;
    background: rgba(255, 255, 255, 0.9);
    border: 1rpx solid rgba(225, 229, 239, 0.95);
    box-sizing: border-box;
    color: #2f3749;
    font-size: 28rpx;
    resize: none;
  }

  .radio-group,
  .goal-group {
    margin-top: 16rpx;
    display: flex;
    flex-wrap: wrap;
    gap: 22rpx;
  }

  .radio-item,
  .goal-item {
    .flex(center, flex-start);
    gap: 10rpx;
    color: #4a5266;
    font-size: 28rpx;
  }

  .tag-wrap {
    margin-top: 16rpx;
    display: flex;
    flex-wrap: wrap;
    gap: 12rpx;
  }

  .tag {
    .flex(center, center);
    gap: 10rpx;
    padding: 8rpx 20rpx;
    border-radius: 999rpx;
    background: rgba(255, 238, 246, 0.92);
    color: #8a3f66;
    font-size: 24rpx;
  }

  .remove {
    font-size: 30rpx;
    line-height: 1;
  }

  .tag-editor {
    .flex(center, flex-start);
    gap: 12rpx;
    margin-top: 16rpx;
  }

  .mini-btn {
    margin: 0;
    min-width: 104rpx;
    height: 72rpx;
    line-height: 72rpx;
    border-radius: 18rpx;
    background: rgba(255, 255, 255, 0.9);
    border: 1rpx solid rgba(225, 229, 239, 0.95);
    color: #4a5266;
    font-size: 26rpx;
  }

  .mini-btn::after {
    border: none;
  }
}
</style>
