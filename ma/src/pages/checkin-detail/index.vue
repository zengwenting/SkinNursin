<template>
  <view class="page detail-page healing-shell">
    <view class="glass-card log-card">
      <view class="month-switcher">
        <button class="switch-btn" hover-class="switch-btn--hover" @click="changeMonth(-1)">&#8249;</button>
        <text class="month-label">{{ monthLabel }}</text>
        <button class="switch-btn" hover-class="switch-btn--hover" @click="changeMonth(1)">&#8250;</button>
      </view>

      <view class="weekday-row">
        <text v-for="weekday in weekdays" :key="weekday" class="weekday-item">{{ weekday }}</text>
      </view>

      <view class="calendar-grid">
        <view
          v-for="item in calendarDays"
          :key="item.key"
          class="day-item"
          :class="item.state"
          @click="openDetail(item)"
        >
          <text>{{ item.label }}</text>
        </view>
      </view>

      <view class="metrics-row">
        <view class="metric-item">
          <text class="metric-label">连续打卡</text>
          <text class="metric-value">{{ streakDays }}天</text>
        </view>
        <view class="metric-item">
          <text class="metric-label">累计打卡</text>
          <text class="metric-value">{{ totalDays }}天</text>
        </view>
      </view>
    </view>

    <view class="glass-card record-card">
      <view class="section-title">当日护肤记录</view>
      <view v-if="detail?.items?.length" class="detail-list">
        <view class="detail-item" v-for="item in detail.items" :key="item.id">
          <image class="detail-image" :src="item.cosmeticImage || fallbackImage" mode="aspectFill" />
          <view class="detail-copy">
            <text class="detail-name">{{ item.cosmeticName }}</text>
            <text class="detail-category">{{ item.category === 'skincare' ? '护肤' : '彩妆' }}</text>
          </view>
        </view>
      </view>
      <view v-else class="empty-record">
        <text>{{ selectedDate ? `${selectedDate} 暂无护肤记录` : "请选择已打卡日期查看护肤记录" }}</text>
      </view>
    </view>
  </view>
</template>

<script setup>
import { computed, onMounted, ref } from "vue";
import { onShow } from "@dcloudio/uni-app";
import dayjs from "dayjs";
import api from "@/utils/api";
import { STATIC_PATH } from "@/config";

const weekdays = ["日", "一", "二", "三", "四", "五", "六"];
const fallbackImage = STATIC_PATH("img-default.png");

const checkedDates = ref([]);
const detail = ref(null);
const selectedDate = ref("");
const currentMonth = ref(dayjs().startOf("month"));

const checkedDateSet = computed(() => new Set(checkedDates.value));
const monthLabel = computed(() => currentMonth.value.format("YYYY年M月"));
const totalDays = computed(() => checkedDates.value.length);

const streakDays = computed(() => {
  if (!checkedDates.value.length) {
    return 0;
  }
  const sorted = [...checkedDates.value].sort();
  let cursor = dayjs(sorted[sorted.length - 1]);
  let streak = 0;
  for (let index = sorted.length - 1; index >= 0; index -= 1) {
    const current = dayjs(sorted[index]);
    if (current.isSame(cursor, "day")) {
      streak += 1;
      cursor = cursor.subtract(1, "day");
    } else if (current.isBefore(cursor, "day")) {
      break;
    }
  }
  return streak;
});

const calendarDays = computed(() => {
  const startOfMonth = currentMonth.value.startOf("month");
  const endOfMonth = currentMonth.value.endOf("month");
  const leadingEmpty = startOfMonth.day();
  const days = [];

  for (let index = 0; index < leadingEmpty; index += 1) {
    days.push({
      key: `empty-${index}`,
      label: "",
      date: "",
      state: "empty",
      clickable: false,
    });
  }

  for (let day = 1; day <= endOfMonth.date(); day += 1) {
    const date = startOfMonth.date(day);
    const dateText = date.format("YYYY-MM-DD");
    const isFuture = date.isAfter(dayjs(), "day");
    const isChecked = checkedDateSet.value.has(dateText);
    let state = "missed";

    if (isFuture) {
      state = "locked";
    } else if (isChecked) {
      state = "checked";
    }

    days.push({
      key: dateText,
      label: String(day),
      date: dateText,
      state,
      clickable: !isFuture,
    });
  }

  return days;
});

const loadCalendar = async () => {
  const result = await api.getCheckinCalendar();
  checkedDates.value = (result?.dates || []).slice().sort();

  const today = dayjs().format("YYYY-MM-DD");
  const defaultDate = checkedDateSet.value.has(today)
    ? today
    : checkedDates.value[checkedDates.value.length - 1] || "";

  if (defaultDate) {
    selectedDate.value = defaultDate;
    await loadDetail(defaultDate);
  } else {
    detail.value = null;
    selectedDate.value = "";
  }
};

const loadDetail = async (date) => {
  detail.value = await api.getCheckinDetail(date);
};

const openDetail = async (item) => {
  if (!item.clickable || item.state === "empty") {
    return;
  }
  if (item.state !== "checked") {
    uni.showToast({ title: "当天未打卡", icon: "none" });
    return;
  }
  selectedDate.value = item.date;
  await loadDetail(item.date);
};

const changeMonth = (step) => {
  currentMonth.value = currentMonth.value.add(step, "month").startOf("month");
};

onMounted(loadCalendar);
onShow(loadCalendar);
</script>

<style lang="less">
.detail-page {
  .log-card,
  .record-card {
    padding: 28rpx;
  }

  .record-card {
    margin-top: 24rpx;
  }

  .month-switcher {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 24rpx;
  }

  .switch-btn {
    width: 72rpx;
    height: 72rpx;
    border: none;
    border-radius: 50%;
    background: linear-gradient(180deg, rgba(255, 255, 255, 0.96) 0%, rgba(255, 242, 248, 0.96) 100%);
    box-shadow: 0 14rpx 24rpx rgba(233, 187, 214, 0.24);
    color: #616785;
    font-size: 40rpx;
    line-height: 72rpx;
    text-align: center;
  }

  .switch-btn::after {
    border: none;
  }

  .switch-btn--hover {
    transform: scale(0.95);
    background: linear-gradient(180deg, rgba(255, 239, 247, 1) 0%, rgba(255, 228, 239, 1) 100%);
  }

  .month-label {
    color: #40465b;
    font-size: 34rpx;
    font-weight: 700;
  }

  .weekday-row {
    display: grid;
    grid-template-columns: repeat(7, 1fr);
    gap: 12rpx;
    margin-bottom: 14rpx;
  }

  .weekday-item {
    text-align: center;
    color: #8d94a8;
    font-size: 22rpx;
  }

  .calendar-grid {
    display: grid;
    grid-template-columns: repeat(7, 1fr);
    gap: 12rpx;
  }

  .day-item {
    display: flex;
    align-items: center;
    justify-content: center;
    height: 76rpx;
    border-radius: 22rpx;
    font-size: 24rpx;
    font-weight: 600;
    transition: all 0.2s ease;

    &.checked {
      background: rgba(206, 245, 220, 0.96);
      color: #24985a;
      box-shadow: inset 0 0 0 2rpx rgba(113, 202, 149, 0.18);
    }

    &.missed {
      background: rgba(255, 232, 236, 0.96);
      color: #d66074;
      box-shadow: inset 0 0 0 2rpx rgba(238, 167, 180, 0.16);
    }

    &.locked {
      background: rgba(236, 239, 245, 0.96);
      color: #b3b9c8;
    }

    &.empty {
      background: transparent;
      color: transparent;
      box-shadow: none;
    }
  }

  .metrics-row {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 18rpx;
    margin-top: 28rpx;
  }

  .metric-item {
    padding: 20rpx 22rpx;
    border-radius: 24rpx;
    background: linear-gradient(180deg, rgba(255, 255, 255, 0.96) 0%, rgba(253, 245, 249, 0.96) 100%);
  }

  .metric-label {
    display: block;
    color: #8d94a8;
    font-size: 22rpx;
  }

  .metric-value {
    display: block;
    margin-top: 10rpx;
    color: #343b4f;
    font-size: 30rpx;
    font-weight: 700;
  }

  .detail-list {
    display: flex;
    flex-direction: column;
    gap: 16rpx;
  }

  .detail-item {
    display: flex;
    align-items: center;
    gap: 16rpx;
    padding: 18rpx;
    border-radius: 24rpx;
    background: rgba(255, 255, 255, 0.84);
  }

  .detail-image {
    width: 88rpx;
    height: 88rpx;
    border-radius: 22rpx;
    background: #f2f4fa;
  }

  .detail-copy {
    display: flex;
    flex-direction: column;
    gap: 8rpx;
  }

  .detail-name {
    color: #2c3345;
    font-size: 28rpx;
    font-weight: 700;
  }

  .detail-category {
    color: #8d94a8;
    font-size: 22rpx;
  }

  .empty-record {
    padding: 30rpx 12rpx 10rpx;
    text-align: center;
    color: #8d94a8;
    font-size: 26rpx;
    line-height: 1.8;
  }
}
</style>
