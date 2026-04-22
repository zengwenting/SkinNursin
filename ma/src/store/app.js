import { defineStore } from "pinia";
import { DEFAULT_USER_ID } from "@/config";
import api from "@/utils/api";

const AUTH_STORAGE_KEY = "ma_auth_profile";
const INFO_STORAGE_KEY = "ma_profile_info";
const ALARM_STORAGE_KEY = "ma_alarm_settings";
const TOKEN_STORAGE_KEY = "ma_token";

const defaultProfileInfo = () => ({
  skinType: "",
  age: "",
  gender: "",
  isSensitive: false,
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
    authProfile: {},
    profileInfo: defaultProfileInfo(),
    alarmSettings: defaultAlarmSettings(),
    token: '',
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
      this.authProfile = readStorage(AUTH_STORAGE_KEY, {}) || {};
      this.token = readStorage(TOKEN_STORAGE_KEY, '');
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
    setToken(token) {
      this.token = token || '';
      writeStorage(TOKEN_STORAGE_KEY, this.token);
    },
    logout() {
      this.authProfile = {};
      this.token = '';
      writeStorage(AUTH_STORAGE_KEY, {});
      writeStorage(TOKEN_STORAGE_KEY, '');
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
        // 更新 authProfile 状态，确保前端显示的头像和昵称与后端保持一致
        if (this.user) {
          this.setAuthProfile({
            avatar: this.user.avatar,
            nickname: this.user.nickname
          });
          // 更新 profileInfo 状态，确保前端显示的用户信息与后端保持一致
          this.updateProfileInfo({
            skinType: this.user.skinType || "",
            age: this.user.age || "",
            gender: this.user.gender || "",
            isSensitive: this.user.isSensitive === 1,
            sensitiveSources: this.user.sensitiveSource ? this.user.sensitiveSource.split(", ") : [],
            goals: this.user.skinGoal ? this.user.skinGoal.split(", ") : [],
          });
        }
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
