import { defineStore } from "pinia";
import { localCache } from "codingtalk-vue-toolkit";
import difference from "lodash/difference";
import cloneDeep from "lodash/cloneDeep";
import routesDynamic from "@/router/routes/dynamic";
import router from "@/router";
import { TOKEN_KEY } from "@/config";
import { OrgStaff } from "@/entity";
import { MINI_APP_INFO, MINI_APP_MENU } from "@/constants/miniapp";

export default defineStore({
  id: "app",
  state: () => ({
    isLogin: false,
    isAdmin: false,
    isLoadedRoute: false,
    user: null,
    menu: [],
    history: [],
    accessRuleIds: [],
    appInfoData: null,
  }),
  getters: {},
  actions: {
    init() {
      this.appInfoData = MINI_APP_INFO;
      localCache.get(TOKEN_KEY) && this.accessInit();
    },
    setHistory(a) {
      this.history = a;
    },
    getUserInfo() {
      return new Promise((resolve) => {
        OrgStaff.sendApi("staffInfo", {}, { parse4Entity: true }).then(
          (res) => {
            const { status, data } = res;
            if (status) {
              const { isAdmin, role } = data;
              this.isLogin = true;
              this.isAdmin = isAdmin || role === 'admin';
              this.user = data;
              resolve(data);
            }
          }
        );
      });
    },
    logout() {
      return new Promise((resolve, reject) => {
        OrgStaff.sendApi("staffLogout").then((res) => {
          const { status } = res;
          if (status) {
            this.isLogin = false;
            this.isAdmin = false;
            this.user = new OrgStaff();
            localCache.remove(TOKEN_KEY);
            resolve();
          } else {
            reject(new Error("staffLogout"));
          }
        });
      });
    },
    async accessInit() {
      const userInfo = await this.getUserInfo();
      const menuJson = MINI_APP_MENU;
      const { isAdmin, role } = userInfo;
      const isAdminRole = isAdmin || role === 'admin';
      return new Promise((resolve) => {
        OrgStaff.sendApi("staffAccessRuleList").then((res) => {
          const { status, data } = res;
          if (status) {
            const accessRuleIds = data.map((i) => i.id);
            this.accessRuleIds = accessRuleIds;
            if (isAdminRole) {
              this.menu = cloneDeep(menuJson);
            } else {
              this.menu = cloneDeep(menuJson).filter((i) => {
                // 普通运营看不到"账号中心"菜单
                if (i.path === '/account') {
                  return false;
                }
                if (i.accessRuleId) {
                  if (Array.isArray(i.accessRuleId)) {
                    return (
                      difference(accessRuleIds, i.accessRuleId).length !==
                      accessRuleIds.length
                    );
                  }
                  if (!accessRuleIds.includes(i.accessRuleId)) {
                    return false;
                  }
                }
                if (i.sub && i.sub.length) {
                  i.sub = i.sub.filter((j) => {
                    // 普通运营看不到"账号中心"菜单
                    if (j.path === '/account/profile') {
                      return false;
                    }
                    if (!j.accessRuleId) {
                      return true;
                    }
                    if (j.accessRuleId) {
                      if (Array.isArray(j.accessRuleId)) {
                        return (
                          difference(accessRuleIds, j.accessRuleId).length !==
                          accessRuleIds.length
                        );
                      }
                      return accessRuleIds.includes(j.accessRuleId);
                    }
                    return false;
                  });
                  if (!i.sub.length) {
                    return false;
                  }
                }
                return true;
              });
            }
            let routes = cloneDeep(routesDynamic);
            if (!isAdminRole) {
              routes.forEach((i) => {
                i.children = i.children.filter((j) => {
                  // 普通运营看不到"账号中心"路由
                  if (j.path === 'account/profile') {
                    return false;
                  }
                  if (j.meta && j.meta.accessRuleId) {
                    if (Array.isArray(j.meta.accessRuleId)) {
                      return (
                        difference(accessRuleIds, j.meta.accessRuleId)
                          .length !== accessRuleIds.length
                      );
                    }
                    return accessRuleIds.includes(j.meta.accessRuleId);
                  }
                  return true;
                });
              });
            }
            for (let i in routes) {
              router.addRoute(routes[i]);
            }
            this.isLoadedRoute = true;
            resolve();
          }
        });
      });
    },
  },
});
