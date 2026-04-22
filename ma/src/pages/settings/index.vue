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
import api from "@/utils/api";

const appStore = useAppStore();
const alarm = reactive({
  enabled: false,
  time: "08:00",
});



const syncFromStore = () => {
  alarm.enabled = Boolean(appStore.alarmSettings?.enabled);
  alarm.time = appStore.alarmSettings?.time || "08:00";
};

// 订阅消息
const subscribeMessage = async () => {
  try {
    const res = await wx.requestSubscribeMessage({
      tmplIds: ['tgWzJiNGFoA0HQLc4TdjLzf3c9rHCcuSeskHMx2RbFI'],
      success: (res) => {
        console.log('订阅消息成功', res);
      },
      fail: (err) => {
        console.error('订阅消息失败', err);
      }
    });
    return res;
  } catch (err) {
    console.error('订阅消息异常', err);
    return null;
  }
};

// 更新数据库
const updateDatabase = async (time, enabled) => {
  try {
    // 更新本地存储
    appStore.updateAlarmSettings({
      enabled: enabled,
      time: time,
    });
    
    // 更新后端数据库
    await api.updateUser({
      remindtime: time,
      onclock: enabled
    });
  } catch (err) {
    console.error('更新数据库失败', err);
  }
};

const onToggleAlarm = async (e) => {
  const enabled = Boolean(e.detail.value);
  alarm.enabled = enabled;
  
  // 开启闹钟时订阅消息
  if (enabled) {
    await subscribeMessage();
  }
  
  // 更新数据库
  await updateDatabase(alarm.time, enabled);
};

const onTimeChange = async (e) => {
  const time = e.detail.value;
  alarm.time = time;
  
  // 订阅消息
  await subscribeMessage();
  
  // 更新数据库
  await updateDatabase(time, alarm.enabled);
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
