import { defineConfig } from "vite";
import uniPlugin from "@dcloudio/vite-plugin-uni";
import { resolve } from "path";

const uni = uniPlugin.default || uniPlugin;

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [uni()],
  resolve: {
    alias: {
      "@": resolve(__dirname, "./src"),
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
  },
});
