// 皮肤护理小助手后台动态路由配置
const dynamic = [
  {
    name: "layoutAuthDynamic",
    path: "/layout_auth--dynamic",
    component: () => import("@/layout/auth.vue"),
    children: [
      {
        name: "miniUsers",
        path: "/mini/users",
        meta: {
          title: "用户管理",
          requiresAuth: true,
        },
        component: () => import("@/pages/skin-assistant/user-management.vue"),
      },
      {
        name: "miniSkinTests",
        path: "/mini/skin-tests",
        meta: {
          title: "肤质测试管理",
          requiresAuth: true,
        },
        component: () => import("@/pages/skin-assistant/skin-test-management.vue"),
      },
      {
        name: "miniCheckins",
        path: "/mini/checkins",
        meta: {
          title: "护理打卡管理",
          requiresAuth: true,
        },
        component: () => import("@/pages/skin-assistant/checkin-management.vue"),
      },
      {
        name: "miniCosmetics",
        path: "/mini/cosmetics",
        meta: {
          title: "化妆台产品管理",
          requiresAuth: true,
        },
        component: () => import("@/pages/skin-assistant/cosmetic-management.vue"),
      },
    ],
  },
];

export default dynamic;
