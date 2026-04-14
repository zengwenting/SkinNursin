import { defineStore } from "pinia";
import { DEFAULT_USER_ID } from "@/config";
import api from "@/utils/api";

const AUTH_STORAGE_KEY = "ma_auth_profile";
const INFO_STORAGE_KEY = "ma_profile_info";
const ALARM_STORAGE_KEY = "ma_alarm_settings";

const defaultProfileInfo = () => ({
  skinType: "",
  age: "",
  isSensitive: "",
  sensitiveSources: [],
  goals: [],
});

const defaultAlarmSettings = () => ({
  enabled: false,
  time: "21:00",
});

const readStorage = (key, fallback) => {
  try {
    const value = uni.getStorageSync(key);
    return value === undefined || value === null || value === "" ? fallback : value;
  } catch (err) {
    return fallback;
  }
};

const writeStorage = (key, value) => {
  try {
    uni.setStorageSync(key, value);
  } catch (err) {
    // Ignore storage errors and keep in-memory state.
  }
};

export default defineStore({
  id: "app",
  state: () => ({
    userId: DEFAULT_USER_ID,
    user: null,
    todayCheckin: null,
    authProfile: null,
    profileInfo: defaultProfileInfo(),
    alarmSettings: defaultAlarmSettings(),
  }),
  getters: {
    isCheckedIn: (state) => Boolean(state.todayCheckin?.checkedIn),
    isLoggedIn: (state) => Boolean(state.authProfile?.nickname),
  },
  actions: {
    async init() {
      this.loadLocalState();
      await Promise.allSettled([this.refreshUser(), this.refreshTodayCheckin()]);
    },
    loadLocalState() {
      this.authProfile = readStorage(AUTH_STORAGE_KEY, null);
      this.profileInfo = {
        ...defaultProfileInfo(),
        ...readStorage(INFO_STORAGE_KEY, {}),
      };
      this.alarmSettings = {
        ...defaultAlarmSettings(),
        ...readStorage(ALARM_STORAGE_KEY, {}),
      };
    },
    setAuthProfile(profile) {
      this.authProfile = {
        avatar: profile?.avatar || "",
        nickname: profile?.nickname || "",
      };
      writeStorage(AUTH_STORAGE_KEY, this.authProfile);
    },
    logout() {
      this.authProfile = null;
      writeStorage(AUTH_STORAGE_KEY, null);
    },
    updateProfileInfo(payload) {
      this.profileInfo = {
        ...this.profileInfo,
        ...payload,
      };
      writeStorage(INFO_STORAGE_KEY, this.profileInfo);
    },
    updateAlarmSettings(payload) {
      this.alarmSettings = {
        ...this.alarmSettings,
        ...payload,
      };
      writeStorage(ALARM_STORAGE_KEY, this.alarmSettings);
    },
    async refreshUser() {
      try {
        this.user = await api.getUserInfo(this.userId);
        return this.user;
      } catch (err) {
        this.user = this.user || null;
        return this.user;
      }
    },
    async refreshTodayCheckin() {
      try {
        this.todayCheckin = await api.getTodayCheckin();
      } catch (err) {
        this.todayCheckin = this.todayCheckin || null;
      }
      return this.todayCheckin;
    },
  },
});
