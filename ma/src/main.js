import { createSSRApp } from "vue";
import App from "./App.vue";
import { createPinia } from "pinia";
import { codingtalkUniToolkitInstall } from "@/config";
import $filter from "./utils/filter";
import uviewPlus from 'uview-plus'

// --- 必须加这段 ---
if (!wx.cloud) {
  console.error('请使用 2.2.3 或以上的基础库以使用云能力')
} else {
  wx.cloud.init({
    // 这里是我当前的环境 ID：cloudbase-d0gxrthsz5100b04b
    env: 'cloudbase-d0gxrthsz5100b04b',
    traceUser: true,
  })
}
// ------------------

export function createApp() {
  const app = createSSRApp(App);
  app.config.globalProperties.$filter = $filter;
  app.use(createPinia());
  app.use(uviewPlus)
  codingtalkUniToolkitInstall(app);
  return {
    app,
  };
}
