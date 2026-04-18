import axios from "axios";
import { localCache } from "codingtalk-vue-toolkit";
import { TOKEN_KEY } from "@/config";

const BASE_URL =
  process.env.NODE_ENV === "production"
    ? "/skin-assistant/"
    : "http://localhost:4001/skin-assistant/";

// 皮肤护理后台接口模块：统一封装后台管理系统业务接口。
async function request(path, body = {}, params = {}) {
  const token = localCache.get(TOKEN_KEY);
  const response = await axios.post(`${BASE_URL}${path}`, body, {
    params,
    headers: token
      ? {
          "user-access-token": token,
        }
      : {},
  });
  return response.data;
}

export const skinAssistantAdminApi = {
  getDashboardSummary() {
    return request("dashboard/summary");
  },
  getUserPage(query, pageIndex = 1, pageSize = 10) {
    return request("users/page", { query }, { pageIndex, pageSize });
  },
  getUserInfo(id) {
    return request("users/info", {}, { id });
  },
  updateUser(id, form) {
    return request("users/update", form, { id });
  },
  getSkinTestPage(query, pageIndex = 1, pageSize = 10) {
    return request("skin-tests/page", { query }, { pageIndex, pageSize });
  },
  getSkinTestInfo(id) {
    return request("skin-tests/info", {}, { id });
  },
  updateSkinTest(id, form) {
    return request("skin-tests/update", form, { id });
  },
  getCheckinPage(query, pageIndex = 1, pageSize = 10) {
    return request("checkins/page", { query }, { pageIndex, pageSize });
  },
  getCheckinInfo(id) {
    return request("checkins/info", {}, { id });
  },
  updateCheckin(id, form) {
    return request("checkins/update", form, { id });
  },
  getCosmeticPage(query, pageIndex = 1, pageSize = 10) {
    return request("cosmetics/page", { query }, { pageIndex, pageSize });
  },
  getCosmeticInfo(id) {
    return request("cosmetics/info", {}, { id });
  },
  updateCosmetic(id, form) {
    return request("cosmetics/update", form, { id });
  },
};
