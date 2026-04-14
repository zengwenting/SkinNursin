<template>
  <view class="page assistant-page healing-shell">
    <view class="healing-header">
      <text class="title">智慧助手</text>
      <view class="desc">AI 接口当前已留好注释和对接位，现阶段返回固定建议数据，方便前后端联调。</view>
    </view>

    <view class="glass-card chat-card">
      <view class="message-list">
        <view class="message-item" v-for="(item, index) in messages" :key="index" :class="item.role">
          <text>{{ item.content }}</text>
        </view>
      </view>

      <view class="quick-actions">
        <button class="quick-btn" @click="askIngredient">成分查询</button>
        <button class="quick-btn" @click="askRecommend">智能推荐</button>
      </view>

      <view class="composer">
        <input v-model="inputValue" placeholder="输入你的护肤问题，例如：今天脸颊有点干怎么办？" />
        <button class="send-btn" @click="sendChat">发送</button>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref } from "vue";
import api from "@/utils/api";

const inputValue = ref("");
const messages = ref([
  { role: "assistant", content: "你好，我是你的智慧护理助手。今天想聊肤质、成分，还是护理节奏？" },
]);

const pushReply = (content) => {
  messages.value.push({ role: "assistant", content });
};

const sendChat = async () => {
  if (!inputValue.value.trim()) {
    return;
  }
  messages.value.push({ role: "user", content: inputValue.value });
  const current = inputValue.value;
  inputValue.value = "";
  const result = await api.aiChat(current);
  pushReply(result.reply);
};

const askIngredient = async () => {
  const result = await api.getIngredient("Niacinamide");
  pushReply(`${result.name}：${result.effect}。${result.caution}。`);
};

const askRecommend = async () => {
  const result = await api.getRecommend("combination");
  pushReply(`${result.reply} 推荐流程：${result.routine.join(" / ")}`);
};
</script>

<style lang="less">
.assistant-page {
  .chat-card {
    padding: 28rpx;
  }

  .message-list {
    display: flex;
    flex-direction: column;
    gap: 18rpx;
    min-height: 720rpx;
  }

  .message-item {
    max-width: 82%;
    padding: 22rpx 24rpx;
    border-radius: 28rpx;
    font-size: 28rpx;
    line-height: 1.7;

    &.assistant {
      align-self: flex-start;
      background: rgba(255, 255, 255, 0.86);
      color: #283043;
    }

    &.user {
      align-self: flex-end;
      background: linear-gradient(135deg, #ffd9ea, #dff4ff);
      color: #1f2430;
    }
  }

  .quick-actions {
    .flex(center, space-between);
    gap: 18rpx;
    margin: 28rpx 0;
  }

  .quick-btn {
    flex: 1;
    min-height: 80rpx;
    border: none;
    border-radius: 999rpx;
    background: #fff;
    color: #384156;
    font-size: 28rpx;
    box-shadow: 0 12rpx 28rpx rgba(215, 200, 229, 0.22);
  }

  .composer {
    .flex(center, space-between);
    gap: 18rpx;

    input {
      flex: 1;
      height: 88rpx;
      padding: 0 24rpx;
      border-radius: 999rpx;
      background: rgba(255, 255, 255, 0.88);
    }
  }

  .send-btn {
    min-width: 140rpx;
    height: 88rpx;
    border: none;
    border-radius: 999rpx;
    background: linear-gradient(135deg, #f5bfd8, #bde8ff);
    color: #263043;
    font-size: 28rpx;
    font-weight: 700;
  }
}
</style>
