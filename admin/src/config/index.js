import 'ant-design-vue/dist/reset.css';
import axios from "axios";
import _ from "lodash";
import * as echarts from "echarts";
import injectComponents from "@/components";
import entities from "@/entity";
import { MINI_APP_INFO, MINI_APP_MENU } from "@/constants/miniapp";
import {
  Input,
  InputNumber,
  Button,
  Radio,
  Menu,
  Dropdown,
  Switch,
  Select,
  Slider,
  Steps,
  Tag,
  message,
  Tabs,
  ImagePreviewGroup,
  Image,
  Tree,
  Timeline,
  TimelineItem,
} from "ant-design-vue";
import {
  install,
  localCache
} from "codingtalk-vue-toolkit";
import { rep } from "@/utils/navigator";

const LOCAL_APP_CONFIG = {
  infoData: MINI_APP_INFO,
  menu: MINI_APP_MENU,
  api: {
    web: {
      hostname: "http://localhost:4001/",
      modules: {
        org: {
          staffLogin: { path: "org/staffLogin", method: "POST" },
          staffLogout: { path: "org/staffLogout", method: "POST" },
          staffInfo: { path: "org/staffInfo", method: "POST" },
          staffAccessRuleList: { path: "org/staffAccessRuleList", method: "POST" },
        },
      },
    },
  },
  entity: {},
};

export const TOKEN_KEY = "jwt-token";

export const antDesignInstall = (app) => {
  app.use(Input);
  app.use(InputNumber);
  app.use(Button);
  app.use(Radio);
  app.use(Menu);
  app.use(Switch);
  app.use(Select);
  app.use(Slider);
  app.use(Dropdown);
  app.use(Tag);
  app.use(Tabs);
  app.use(Steps);
  app.use(ImagePreviewGroup);
  app.use(Image);
  app.use(Tree);
  app.use(Timeline);
  app.use(TimelineItem);
  app.config.globalProperties.$message = message;
};

export const codingtalkVueToolkitInstall = async (app) => {
  const axiosWrapper = (config) => {
    if (config?.url === "__local_app_config__") {
      return Promise.resolve({
        data: {
          status: true,
          data: LOCAL_APP_CONFIG,
        },
      });
    }
    return axios(config);
  };
  axiosWrapper.create = (...args) => axios.create(...args);
  axiosWrapper.CancelToken = axios.CancelToken;
  axiosWrapper.isCancel = axios.isCancel;
  axiosWrapper.all = axios.all?.bind(axios);
  axiosWrapper.spread = axios.spread?.bind(axios);
  axiosWrapper.defaults = axios.defaults;
  axiosWrapper.interceptors = axios.interceptors;
  await install(
    app,
    {
      libs: {
        _,
        axios: axiosWrapper,
        echarts,
      },
      entities,
      injectComponents,
      cache: {
        tokenKey: TOKEN_KEY,
        jwtKey: "user-access-token",
      },
      hook: {
        httpAfter: (res) => {
          const { data, status } = res;
          if (data?.code === 4200) {
            localCache.remove(TOKEN_KEY);
            rep("/login");
          }
        },
      },
      remote: {
        oss: "//api.yun-kuai.com/open/oss/authorize",
        app: "__local_app_config__",
        entity: {
          url: "",
        }
      }
    }
  );
};

export default {
  historyUrlKey: "local-url-history",
  historyUrlSize: 10,
};
