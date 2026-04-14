<template>
  <view class="page settings-page healing-shell">
    <view class="healing-header">
      <text class="title">闹钟设置</text>
    </view>

    <view class="glass-card settings-card">
      <view class="setting-row">
        <text class="row-label">开启提醒</text>
        <switch :checked="alarm.enabled" color="#e685b5" @change="onToggleAlarm" />
      </view>
      <view class="setting-row">
        <text class="row-label">每日提醒时间</text>
        <picker mode="time" :value="alarm.time" @change="onTimeChange" :disabled="!alarm.enabled">
          <text :class="['time-text', { disabled: !alarm.enabled }]">{{ alarm.time }}</text>
        </picker>
      </view>
    </view>
  </view>
</template>

<script setup>
import { onMounted, reactive } from "vue";
import useAppStore from "@/store/app";

const appStore = useAppStore();
const alarm = reactive({
  enabled: false,
  time: "21:00",
});

const syncFromStore = () => {
  alarm.enabled = Boolean(appStore.alarmSettings?.enabled);
  alarm.time = appStore.alarmSettings?.time || "21:00";
};

const onToggleAlarm = (e) => {
  alarm.enabled = Boolean(e.detail.value);
  appStore.updateAlarmSettings({
    enabled: alarm.enabled,
    time: alarm.time,
  });
};

const onTimeChange = (e) => {
  alarm.time = e.detail.value;
  appStore.updateAlarmSettings({
    enabled: alarm.enabled,
    time: alarm.time,
  });
};

onMounted(() => {
  appStore.loadLocalState();
  syncFromStore();
});
</script>

<style lang="less">
.settings-page {
  .settings-card {
    padding: 24rpx 28rpx;
  }

  .setting-row {
    .flex(center, space-between);
    min-height: 92rpx;
    color: #2c3446;
    border-bottom: 1rpx solid rgba(228, 233, 241, 0.9);

    &:last-child {
      border-bottom: none;
    }
  }

  .time-text {
    color: #2c3446;
    font-size: 30rpx;
  }

  .row-label {
    color: #2c3446;
    font-size: 30rpx;
  }

  .disabled {
    color: #acb3c2;
  }
}
</style>
