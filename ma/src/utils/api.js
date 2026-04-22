import { API_PREFIX, DEFAULT_USER_ID } from "@/config";

const buildQueryString = (params = {}) =>
  Object.entries(params)
    .filter(([, value]) => value !== undefined && value !== null && value !== "")
    .map(([key, value]) => `${encodeURIComponent(key)}=${encodeURIComponent(value)}`)
    .join("&");

const request = (url, { method = "GET", data = {}, params = {} } = {}) =>
  new Promise((resolve, reject) => {
    const query = buildQueryString(params);
    const finalUrl = `${API_PREFIX}${url}${query ? `?${query}` : ""}`;
    uni.request({
      url: finalUrl,
      method,
      data,
      success: (res) => {
        const payload = res.data || {};
        if (payload.code === 0) {
          resolve(payload.data);
          return;
        }
        reject(new Error(payload.msg || "Request failed"));
      },
      fail: (err) => reject(err),
    });
  });

export const api = {
  getUserInfo(userId = DEFAULT_USER_ID) {
    return request("/user/info", { params: { userId } });
  },
  updateUser(data) {
    return request("/user/update", { method: "POST", data: { id: DEFAULT_USER_ID, ...data } });
  },
  getCosmetics(category) {
    return request("/cosmetic/list", { params: { userId: DEFAULT_USER_ID, category } });
  },
  addCosmetic(data) {
    return request("/cosmetic/create", { method: "POST", data: { userId: DEFAULT_USER_ID, ...data } });
  },
  deleteCosmetic(id) {
    return request("/cosmetic/delete", { method: "POST", data: { id } });
  },
  getTodayCheckin() {
    return request("/checkin/today", { params: { userId: DEFAULT_USER_ID } });
  },
  createCheckin(data) {
    return request("/checkin/create", { method: "POST", data: { userId: DEFAULT_USER_ID, ...data } });
  },
  getCheckinCalendar() {
    return request("/checkin/calendar", { params: { userId: DEFAULT_USER_ID } });
  },
  getCheckinDetail(date) {
    return request("/checkin/detail", { params: { userId: DEFAULT_USER_ID, date } });
  },
  createSkinTest(data) {
    return request("/skin/test", { method: "POST", data: { userId: DEFAULT_USER_ID, ...data } });
  },
  getSkinHistory() {
    return request("/skin/history", { params: { userId: DEFAULT_USER_ID } });
  },
  aiChat(message) {
    return request("/ai/chat", { method: "POST", data: { message } });
  },
  getIngredient(name) {
    return request("/ai/ingredient", { params: { name } });
  },
  getRecommend(skinType) {
    return request("/ai/recommend", { params: { skinType } });
  },
  login(data) {
    return request("/auth/login", { method: "POST", data });
  },
  getSystemConfig() {
    return request("/system/config");
  },
};

export default api;
