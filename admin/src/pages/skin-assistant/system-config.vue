<template>
  <div class="page config_page">
    <section class="config_card">
      <div class="config_header">
        <h3>系统配置</h3>
        <p>配置 AI 大模型的各项参数和业务 Prompt</p>
      </div>

      <div class="config_form">
        <!-- 基础连接配置 -->
        <div class="config_group">
          <h4>基础连接配置</h4>
          <div class="form_row">
            <label class="form_item">
              <span>AI API Key</span>
              <a-input v-model:value="form.ai_api_key" placeholder="请输入 AI API Key" />
            </label>
          </div>
          <div class="form_row">
            <label class="form_item">
              <span>AI Base URL</span>
              <a-input v-model:value="form.ai_api_url" placeholder="请输入 AI Base URL" />
            </label>
          </div>
          <div class="form_row">
            <label class="form_item">
              <span>模型名称</span>
              <a-input v-model:value="form.ai_model" placeholder="请输入模型名称" />
            </label>
          </div>
        </div>

        <!-- AI 参数配置 -->
        <div class="config_group">
          <h4>AI 参数配置</h4>
          <div class="form_row">
            <label class="form_item">
              <span>Temperature (温度)</span>
              <div class="slider_wrapper">
                <a-slider 
                  v-model:value="temperatureValue" 
                  :min="0" 
                  :max="1.5" 
                  :step="0.1"
                  style="width: 300px"
                  @change="handleTemperatureChange"
                />
                <span class="slider_value">{{ temperatureValue }}</span>
              </div>
            </label>
          </div>
          <div class="form_row">
            <label class="form_item">
              <span>Max Token</span>
              <a-input-number 
                v-model:value="maxTokenValue" 
                :min="1" 
                :max="10000"
                @change="handleMaxTokenChange"
              />
            </label>
          </div>
        </div>

        <!-- Prompt 配置 -->
        <div class="config_group">
          <h4>Prompt 配置</h4>
          <div class="form_row">
            <label class="form_item">
              <span>护肤建议 Prompt</span>
              <a-textarea 
                v-model:value="form.prompt_care" 
                placeholder="请输入护肤建议 Prompt" 
                :rows="4"
              />
            </label>
          </div>
          <div class="form_row">
            <label class="form_item">
              <span>成分分析 Prompt</span>
              <a-textarea 
                v-model:value="form.prompt_ingredient" 
                placeholder="请输入成分分析 Prompt" 
                :rows="4"
              />
            </label>
          </div>
          <div class="form_row">
            <label class="form_item">
              <span>问答助手 Prompt</span>
              <a-textarea 
                v-model:value="form.prompt_chat" 
                placeholder="请输入问答助手 Prompt" 
                :rows="4"
              />
            </label>
          </div>
          <div class="form_row">
            <label class="form_item">
              <span>皮肤测试 Prompt</span>
              <a-textarea 
                v-model:value="form.prompt_test" 
                placeholder="请输入皮肤测试 Prompt" 
                :rows="4"
              />
            </label>
          </div>
        </div>

        <!-- 保存按钮 -->
        <div class="form_actions">
          <a-button type="primary" @click="saveConfig">保存并更新</a-button>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from "vue";
import { message } from "ant-design-vue";
import { skinAssistantAdminApi } from "@/utils/skin-assistant-admin-api";

// 表单数据
const form = reactive({
  ai_api_key: "",
  ai_api_url: "",
  ai_model: "",
  ai_temperature: "0.7",
  ai_max_token: "2048",
  prompt_care: "",
  prompt_ingredient: "",
  prompt_chat: "",
  prompt_test: "",
});

// 滑块和数字输入框的双向绑定
const temperatureValue = ref(0.7);
const maxTokenValue = ref(2048);

// 处理温度滑块变化
const handleTemperatureChange = (value) => {
  form.ai_temperature = value.toString();
};

// 处理最大 token 变化
const handleMaxTokenChange = (value) => {
  form.ai_max_token = value.toString();
};

// 加载配置数据
async function loadConfig() {
  try {
    const res = await skinAssistantAdminApi.getConfigList();
    if (res?.status && res.data) {
      Object.assign(form, res.data);
      // 更新滑块和数字输入框的值
      temperatureValue.value = parseFloat(form.ai_temperature || "0.7");
      maxTokenValue.value = parseInt(form.ai_max_token || "2048");
    }
  } catch (error) {
    console.error("加载配置失败:", error);
  }
}

// 保存配置
async function saveConfig() {
  try {
    const res = await skinAssistantAdminApi.updateConfig(form);
    if (res?.status) {
      message.success("配置保存成功");
    }
  } catch (error) {
    console.error("保存配置失败:", error);
    message.error("保存配置失败");
  }
}

// 页面加载时获取配置
onMounted(loadConfig);
</script>

<style lang="less">
.config_page {
  .config_card {
    padding: 24px;
    border: 1px solid rgba(233, 220, 225, 0.95);
    border-radius: 28px;
    background: rgba(255, 255, 255, 0.9);
    box-shadow: 0 18px 36px rgba(217, 196, 203, 0.14);
  }

  .config_header {
    margin-bottom: 24px;

    h3 {
      margin: 0 0 8px;
      color: #4d3d46;
    }

    p {
      margin: 0;
      color: #8f7b84;
    }
  }

  .config_group {
    margin-bottom: 32px;

    h4 {
      margin: 0 0 16px;
      color: #6f5d66;
      font-size: 16px;
      font-weight: 600;
    }
  }

  .form_row {
    margin-bottom: 16px;
  }

  .form_item {
    display: flex;
    flex-direction: column;
    gap: 8px;
    color: #6f5d66;

    span {
      font-size: 14px;
      font-weight: 500;
    }
  }

  .slider_wrapper {
    display: flex;
    align-items: center;
    gap: 16px;

    .slider_value {
      min-width: 40px;
      text-align: right;
      color: #8f7b84;
    }
  }

  .form_actions {
    margin-top: 32px;
    display: flex;
    justify-content: flex-end;
  }
}
</style>
