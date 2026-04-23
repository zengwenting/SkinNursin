// 先添加阻止@zyb-data/stats-h5的代码
window.__ZYB_STATS_DISABLED__ = true;

// 重写require和import，阻止加载@zyb-data/stats-h5
if (typeof require === 'function') {
  const originalRequire = require;
  require = function(id) {
    if (id.includes('@zyb-data/stats-h5') || id.includes('@zyb-data/stats-core')) {
      return {};
    }
    return originalRequire.apply(this, arguments);
  };
}

// 重写动态import
if (typeof window !== 'undefined' && window.import) {
  const originalImport = window.import;
  window.import = function(module) {
    if (module.includes('@zyb-data/stats-h5') || module.includes('@zyb-data/stats-core')) {
      return Promise.resolve({ default: {} });
    }
    return originalImport.apply(this, arguments);
  };
}

import { createApp } from "vue";
import App from "./App.vue";
import { createPinia } from "pinia";
import router  from "@/router";
import get from "lodash/get";
import set from "lodash/set";
import cloneDeep from "lodash/cloneDeep";
import { antDesignInstall, codingtalkVueToolkitInstall } from "@/config";
import "codingtalk-vue-toolkit/dist_publish/index.css";

(async () => {
  const app = createApp(App);
  antDesignInstall(app);
  await codingtalkVueToolkitInstall(app);
  app.config.globalProperties.$_ = window.$_ = {
    get,
    set,
    cloneDeep,
  };
  app.use(createPinia());
  app.use(router);
  app.mount("#app");
})();
