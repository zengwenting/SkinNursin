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
        component: () => import("@/pages/mini/users.vue"),
      },
      {
        name: "miniSkinTests",
        path: "/mini/skin-tests",
        meta: {
          title: "肤质测试",
          requiresAuth: true,
        },
        component: () => import("@/pages/mini/skin-tests.vue"),
      },
      {
        name: "miniCheckins",
        path: "/mini/checkins",
        meta: {
          title: "护理打卡",
          requiresAuth: true,
        },
        component: () => import("@/pages/mini/checkins.vue"),
      },
      {
        name: "miniCosmetics",
        path: "/mini/cosmetics",
        meta: {
          title: "化妆台产品",
          requiresAuth: true,
        },
        component: () => import("@/pages/mini/cosmetics.vue"),
      },
    ],
  },
];

export default dynamic;
