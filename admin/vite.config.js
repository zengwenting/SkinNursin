import { fileURLToPath, URL } from "url";
import path from "path";

import { defineConfig } from "vite";
import vue from "@vitejs/plugin-vue";
import vueJsx from "@vitejs/plugin-vue-jsx";
import topLevelAwait from "vite-plugin-top-level-await";
import postcsspxtoviewport8plugin from "postcss-px-to-viewport-8-plugin";

// 自定义插件，阻止@zyb-data/stats-h5的加载
const blockZybStatsPlugin = {
  name: 'block-zyb-stats',
  resolveId(id) {
    if (id.includes('@zyb-data/stats-h5') || id.includes('@zyb-data/stats-core')) {
      return { id: 'blocked-zyb-stats', external: true };
    }
  },
  load(id) {
    if (id === 'blocked-zyb-stats') {
      return 'export default {}';
    }
  },
  transform(code, id) {
    // 替换代码中的@zyb-data/stats-h5和@zyb-data/stats-core引用
    if (id.includes('node_modules') || id.includes('dist')) {
      code = code
        .replace(/@zyb-data\/stats-h5/g, 'blocked-zyb-stats')
        .replace(/@zyb-data\/stats-core/g, 'blocked-zyb-stats');
      
      // 替换可能导致v[w] is not a function错误的代码
      code = code.replace(/v\[w\]\s*\(\)/g, '');
      
      // 替换与zyb stats相关的初始化代码
      code = code.replace(/new\s+.*zyb.*stats.*\(/g, '');
      code = code.replace(/\.load\(.*zyb.*\)/g, '');
      code = code.replace(/\.init\(.*zyb.*\)/g, '');
    }
    return code;
  }
};

// Web 管理后台的 Vite 配置
export default defineConfig({
  plugins: [
    blockZybStatsPlugin,
    vue(),
    vueJsx(),
    topLevelAwait({
      promiseExportName: "__tla",
      promiseImportName: (i) => `__tla_${i}`,
    }),
  ],
  server: {
    host: "0.0.0.0",
    port: 3050,
    open: false,
  },
  resolve: {
    alias: {
      "@": fileURLToPath(new URL("./src", import.meta.url)),
      axios: path.resolve(__dirname, "node_modules/axios"),
    },
  },
  css: {
    preprocessorOptions: {
      less: {
        charset: false,
        additionalData:
          '@import "@/assets/style/var.less"; @import "@/assets/style/mixins.less";',
      },
    },
    postcss: {
      plugins: [
        postcsspxtoviewport8plugin({
          unitToConvert: "px",
          viewportWidth: (file) => {
            let num = 1920;
            if (file.indexOf("m_") !== -1) {
              num = 375;
            }
            return num;
          },
          unitPrecision: 5,
          propList: ["*"],
          viewportUnit: "vw",
          fontViewportUnit: "vw",
          selectorBlackList: [],
          minPixelValue: 1,
          mediaQuery: true,
          replace: true,
          exclude: [/node_modules\/ant-design-vue/],
          include: [],
          landscape: false,
          landscapeUnit: "vw",
          landscapeWidth: 1024,
        }),
      ],
    },
  },
});
