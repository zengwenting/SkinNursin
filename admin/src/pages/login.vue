<template>
  <div class="page login_page">
    <div class="login_panel">
      <div class="panel_intro">
        <p class="intro_tag">SkinCare Admin</p>
        <h1>{{ appInfoData?.name }}</h1>
        <h2>{{ appInfoData?.subTitle }}</h2>
        <p class="intro_text">{{ appInfoData?.briefs }}</p>
        <div class="intro_badges">
          <span>用户档案</span>
          <span>肤质测试</span>
          <span>护理打卡</span>
          <span>化妆台产品</span>
        </div>
      </div>

      <div class="panel_form">
        <div class="form_header">
          <h3>登录后台</h3>
          <p>使用现有管理员账号进入系统</p>
        </div>
        <div class="form_body">
          <a-input v-model:value="formLogin.username" placeholder="账号" />
          <a-input-password
            v-model:value="formLogin.passwd"
            placeholder="密码"
            @pressEnter="doLogin"
          />
          <a-button type="primary" size="large" @click="doLogin">
            进入管理台
          </a-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive } from "vue";
import { storeToRefs } from "pinia";
import { message } from "ant-design-vue";
import { localCache } from "codingtalk-vue-toolkit";
import { go } from "@/utils/navigator";
import { TOKEN_KEY } from "@/config";
import { OrgStaff } from "@/entity";
import useAppStore from "@/store/app";

// 登录模块：负责管理员登录并写入本地登录态
const appStore = useAppStore();
const { appInfoData } = storeToRefs(appStore);

const formLogin = reactive({
  username: "",
  passwd: "",
});

function doLogin() {
  if (!formLogin.username || !formLogin.passwd) {
    message.warning("请输入账号和密码");
    return;
  }

  OrgStaff.sendApi(
    "staffLogin",
    { params: formLogin, body: {} },
    { errorTip: "账号或密码错误" }
  ).then((res) => {
    const { status, data } = res;
    if (status) {
      localCache.set(TOKEN_KEY, data);
      message.success("登录成功");
      go("/index");
    }
  });
}
</script>

<style lang="less">
.login_page {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  background:
    radial-gradient(circle at 15% 20%, rgba(247, 210, 223, 0.9), transparent 26%),
    radial-gradient(circle at 85% 15%, rgba(237, 227, 233, 0.92), transparent 24%),
    linear-gradient(180deg, #fff9fb 0%, #f5f1f3 100%);

  .login_panel {
    display: grid;
    grid-template-columns: 1.1fr 0.9fr;
    width: 1080px;
    min-height: 620px;
    overflow: hidden;
    border: 1px solid rgba(234, 217, 224, 0.85);
    border-radius: 36px;
    background: rgba(255, 255, 255, 0.92);
    box-shadow: 0 30px 60px rgba(207, 183, 192, 0.24);
  }

  .panel_intro {
    padding: 64px 56px;
    background:
      linear-gradient(160deg, rgba(251, 232, 238, 0.92) 0%, rgba(255, 255, 255, 0.66) 100%);

    .intro_tag {
      margin: 0 0 18px;
      color: #c68aa2;
      font-size: 13px;
      letter-spacing: 0.16em;
      text-transform: uppercase;
    }

    h1 {
      margin: 0;
      font-size: 42px;
      color: #4d3d46;
    }

    h2 {
      margin: 10px 0 0;
      font-size: 24px;
      font-weight: 500;
      color: #8b6f7b;
    }

    .intro_text {
      max-width: 460px;
      margin-top: 28px;
      color: #75626c;
      line-height: 1.9;
      font-size: 15px;
    }

    .intro_badges {
      display: flex;
      flex-wrap: wrap;
      gap: 12px;
      margin-top: 34px;

      span {
        padding: 10px 16px;
        border-radius: 999px;
        background: rgba(255, 255, 255, 0.8);
        color: #956f7f;
      }
    }
  }

  .panel_form {
    display: flex;
    flex-direction: column;
    justify-content: center;
    padding: 64px 56px;

    .form_header {
      h3 {
        margin: 0;
        font-size: 30px;
        color: #4d3d46;
      }

      p {
        margin: 12px 0 0;
        color: #91808a;
      }
    }

    .form_body {
      display: flex;
      flex-direction: column;
      gap: 18px;
      margin-top: 34px;
    }
  }
}
</style>
