<template>
  <div class="layout">
    <Sidebar />
    <div class="page">
      <div class="chat-container">
        <div class="chat-window">
          <div class="messages" ref="messagesRef">
            <div
              v-for="(m, i) in messages"
              :key="m.id ?? i"
              :class="['message-row', m.role === 'user' ? 'msg-user' : 'msg-ai']"
            >
              <div class="message-bubble">
                <span v-html="formatMessage(m.content)"></span>
                <!-- 添加流式输入指示器 -->
                <span v-if="m.streaming" class="cursor-blink">▋</span>
              </div>
            </div>
          </div>

          <div class="composer">
            <textarea
              v-model="input"
              :placeholder="t('selfLLM.ui.inputPlaceholder')"
              @keydown.enter.prevent="onEnter"
              @keydown.shift.enter.stop
              :disabled="sending"
              rows="2"
            ></textarea>
            <div class="controls">
              <!-- 根据状态切换按钮 -->
              <button v-if="sending" @click="onStop" class="stop-btn">
                {{ t('selfLLM.ui.stop') || '停止' }}
              </button>
              <button v-else @click="onSend" :disabled="sending">
                {{ t('selfLLM.ui.send') }}
              </button>

              <div class="small">{{ t('selfLLM.ui.modelLabel') }}
                <select v-model="model" :disabled="sending">
                  <option>Qwen/Qwen3-8B</option>
                  <option>QwQ-32B</option>
                </select>
              </div>
            </div>
          </div>
        </div>

        <div class="corpus-panel">
          <div class="corpus-list">
            <h3>{{ t('selfLLM.ui.corpusTitle') }}</h3>
            <div
              v-for="item in corpus"
              :key="item.id"
              class="corpus-item"
              @click="useCorpus(item)"
            >
              <div class="corpus-request">{{ item.request }}</div>
            </div>
          </div>

          <div class="pager">
            <button @click="prevPage" :disabled="page <= 1">{{ t('selfLLM.ui.pagePrev') }}</button>
            <span>{{ pageText }}</span>
            <button @click="nextPage">{{ t('selfLLM.ui.pageNext') }}</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, nextTick, computed } from "vue";
import Sidebar from "../../components/Sidebar.vue";
import {
  getCorpusApi,
  sendMessageStreamApi,
  sendMessageApi,
  type CorpusItem,
} from "../../api/self/chat";
import { useI18n } from 'vue-i18n'

const { t } = useI18n();

type Message = {
  id?: number | string;
  role: "user" | "assistant";
  content: string;
  streaming?: boolean;
};

const pageText = computed(() => t("selfLLM.ui.pageText", { page: page.value }));

const messages = ref<Message[]>([]);
const input = ref("");
const model = ref("Qwen/Qwen3-8B");

const page = ref(1);
const limit = 4;
const corpus = ref<CorpusItem[]>([]);
const showResponseFor = ref<number | null>(null);

const sending = ref(false);
const messagesRef = ref<HTMLElement | null>(null);

let streamAbortController: AbortController | null = null;

const formatMessage = (text: string) => {
  if (!text) return "";
  const esc = String(text)
    .replace(/&/g, "&amp;")
    .replace(/</g, "&lt;")
    .replace(/>/g, "&gt;");
  return esc.replace(/\n/g, "<br/>");
};

const scrollToBottom = async () => {
  await nextTick();
  const el = messagesRef.value;
  if (el) {
    el.scrollTop = el.scrollHeight;
  }
};

const loadCorpus = async () => {
  try {
    const res = await getCorpusApi(page.value, limit);
    const rows = res?.data?.data ?? res?.data ?? [];
    corpus.value = rows;
  } catch (e) {
    console.error("getCorpus error:", e);
  }
};

const prevPage = async () => {
  if (page.value <= 1) return;
  page.value--;
  await loadCorpus();
};

const nextPage = async () => {
  page.value++;
  await loadCorpus();
};

const useCorpus = (item: CorpusItem) => {
  messages.value.push({ role: "user", content: item.request });
  messages.value.push({ role: "assistant", content: item.response });
  showResponseFor.value = item.id;
  scrollToBottom();
};

const onSend = async () => {
  const text = input.value?.trim();
  if (!text || sending.value) return;

  // 用户消息
  messages.value.push({ role: "user", content: text });
  input.value = "";
  await scrollToBottom();

  // 助手消息
  const assistantMsg: Message = {
    role: "assistant",
    content: "",
    streaming: true,
  };
  messages.value.push(assistantMsg);
  await scrollToBottom();

  sending.value = true;
  streamAbortController = new AbortController();

  try {
    await sendMessageStreamApi(
      text,
      (chunk: string, done: boolean) => {
        if (done) {
          assistantMsg.streaming = false;
          sending.value = false;
          streamAbortController = null;
          scrollToBottom();
        } else {
          nextTick(() => {
            assistantMsg.content += chunk;
            messages.value.pop();
            messages.value.push(assistantMsg);
             scrollToBottom();
          });
        }
      },
    )}catch(err){

    }
};

const onStop = () => {
  if (streamAbortController) {
    streamAbortController.abort();
  }
};

const onEnter = () => {
  if (!sending.value) {
    onSend();
  }
};

onMounted(async () => {
  await loadCorpus();
  messages.value.push({
    role: "assistant",
    content: "你好，我是健康检测系统后台的 AI 助手，欢迎进行提问",
  });
  scrollToBottom();
});
</script>

<style scoped>
.layout {
  display: flex;
}
.page {
  margin-left: 16rem;
  padding: 20px;
  width: 100%;
}
.chat-container {
  display: flex;
  gap: 16px;
}
.chat-window {
  flex: 1;
  display: flex;
  flex-direction: column;
  height: 80vh;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  overflow: hidden;
  background: #ffffff;
}
.messages {
  flex: 1;
  padding: 16px;
  overflow-y: auto;
  /* 优化滚动性能 */
  scroll-behavior: smooth;
}
.message-row {
  display: flex;
  margin: 8px 0;
}
.msg-user {
  justify-content: flex-end;
}
.msg-ai {
  justify-content: flex-start;
}
.message-bubble {
  max-width: 65%;
  padding: 10px 14px;
  border-radius: 10px;
  line-height: 1.5;
  white-space: pre-wrap;
  word-break: break-word;
  position: relative;
}
.msg-user .message-bubble {
  background: #2563eb;
  color: white;
  border-bottom-right-radius: 2px;
}
.msg-ai .message-bubble {
  background: #f3f4f6;
  color: #111827;
  border-bottom-left-radius: 2px;
}

/* 流式输入光标动画 */
.cursor-blink {
  display: inline-block;
  animation: blink 1s step-end infinite;
  margin-left: 2px;
  color: #2563eb;
  font-weight: bold;
}

@keyframes blink {
  0%, 50% {
    opacity: 1;
  }
  51%, 100% {
    opacity: 0;
  }
}

.composer {
  border-top: 1px solid #e5e7eb;
  padding: 10px;
  display: flex;
  gap: 12px;
  align-items: center;
}
.composer textarea {
  flex: 1;
  border: 1px solid #d1d5db;
  padding: 8px;
  border-radius: 6px;
  resize: none;
  font-family: inherit;
}
.composer textarea:disabled {
  background: #f9fafb;
  cursor: not-allowed;
}
.controls {
  display: flex;
  flex-direction: column;
  gap: 8px;
  align-items: flex-end;
}
.controls button {
  padding: 6px 12px;
  border-radius: 6px;
  border: none;
  background: #111827;
  color: white;
  cursor: pointer;
  transition: all 0.2s;
  min-width: 60px;
}
.controls button:hover:not(:disabled) {
  background: #1f2937;
  transform: translateY(-1px);
}
.controls button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

/* 停止按钮样式 */
.stop-btn {
  background: #ef4444 !important;
}
.stop-btn:hover {
  background: #dc2626 !important;
}

.controls select {
  margin-left: 8px;
  padding: 4px 8px;
  border: 1px solid #d1d5db;
  border-radius: 4px;
}
.controls select:disabled {
  background: #f9fafb;
  cursor: not-allowed;
}

.corpus-panel {
  width: 320px;
  min-width: 280px;
  max-height: 80vh;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  padding: 12px;
  overflow: auto;
  background: #ffffff;
}
.corpus-panel h3 {
  color: #111827;
  margin-bottom: 12px;
}
.corpus-item {
  padding: 8px;
  border-radius: 6px;
  cursor: pointer;
  border: 1px solid transparent;
  margin-bottom: 8px;
  color: #111827;
  transition: all 0.2s;
}
.corpus-item:hover {
  border-color: #e2e8f0;
  background: #fbfbfb;
}
.corpus-request {
  font-weight: 600;
  color: #0047b3;
}
.corpus-response {
  margin-top: 6px;
  font-size: 0.9rem;
  color: #374151;
}
.pager {
  display: flex;
  gap: 8px;
  align-items: center;
  justify-content: center;
  margin-top: 12px;
  padding-top: 8px;
  color: black;
  border-top: 1px dashed #e5e7eb;
}
.pager button {
  padding: 6px 10px;
  border-radius: 6px;
  border: 1px solid #d1d5db;
  background: white;
  cursor: pointer;
  transition: all 0.2s;
}
.pager button:hover:not(:disabled) {
  background: #f9fafb;
  border-color: #9ca3af;
}
.pager button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}
</style>