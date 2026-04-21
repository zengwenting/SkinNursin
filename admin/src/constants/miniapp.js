// 皮肤护理小助手后台基础信息与菜单配置
export const MINI_APP_INFO = {
  name: "皮肤护理小助手后台管理系统",
  nameEN: "SkinCare Admin",
  subTitle: "后台管理系统",
  briefs: "围绕微信小程序的用户档案、肤质测试、护理打卡与化妆台产品进行统一管理。",
};

// 皮肤护理小助手后台左侧导航菜单
export const MINI_APP_MENU = [
  {
    title: "工作台",
    path: "/index",
    icon: "仪表",
  },
  {
    title: "用户管理",
    path: "/mini/users",
    icon: "用户",
  },
  {
    title: "肤质测试管理",
    path: "/mini/skin-tests",
    icon: "测试",
  },
  {
    title: "护理打卡管理",
    path: "/mini/checkins",
    icon: "打卡",
  },
  {
    title: "系统配置",
    path: "/mini/config",
    icon: "设置",
  },
  {
    title: "账号中心",
    path: "/account/profile",
    icon: "账号",
  },
];
