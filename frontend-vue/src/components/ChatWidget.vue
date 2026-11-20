<template>
  <div class="fixed bottom-4 right-4 sm:bottom-6 sm:right-6 z-50 flex flex-col items-end">
    
    <!-- CHAT POPUP WINDOW -->
    <transition
      enter-active-class="transition duration-300 ease-out transform"
      enter-from-class="opacity-0 scale-90 translate-y-4"
      enter-to-class="opacity-100 scale-100 translate-y-0"
      leave-active-class="transition duration-200 ease-in transform"
      leave-from-class="opacity-100 scale-100 translate-y-0"
      leave-to-class="opacity-0 scale-90 translate-y-4"
    >
      <div
        v-if="isOpen"
        class="w-[calc(100vw-2rem)] sm:w-[410px] max-w-[410px] h-[580px] max-h-[85vh] bg-white rounded-3xl shadow-2xl border border-gray-100 flex flex-col overflow-hidden mb-4"
      >
        <!-- Header -->
        <div class="bg-gradient-to-r from-rose-900 via-rose-800 to-gray-900 text-white p-4 flex items-center justify-between shadow-md">
          <div class="flex items-center gap-3">
            <div class="relative">
              <div class="w-10 h-10 rounded-2xl bg-white/10 backdrop-blur-md flex items-center justify-center text-xl border border-white/20 shadow-inner">
                🤖
              </div>
              <span class="absolute -bottom-0.5 -right-0.5 w-3 h-3 bg-emerald-500 border-2 border-rose-900 rounded-full animate-pulse"></span>
            </div>
            <div>
              <div class="flex items-center gap-2">
                <h3 class="font-extrabold text-sm tracking-tight">Trợ Lý Fashion AI</h3>
                <span class="px-1.5 py-0.5 text-[9px] font-black bg-rose-500/40 text-rose-200 border border-rose-400/30 rounded">
                  SMART
                </span>
              </div>
              <p class="text-[11px] text-rose-200 font-medium">Trực tuyến 24/7 • Tư vấn tức thì</p>
            </div>
          </div>

          <div class="flex items-center gap-1">
            <button
              type="button"
              @click="clearChat"
              title="Làm mới đoạn chat"
              class="w-8 h-8 rounded-xl bg-white/10 hover:bg-white/20 text-white flex items-center justify-center text-xs transition-colors"
            >
              🔄
            </button>
            <button
              type="button"
              @click="isOpen = false"
              title="Đóng cửa sổ"
              class="w-8 h-8 rounded-xl bg-white/10 hover:bg-white/20 text-white flex items-center justify-center text-xs transition-colors"
            >
              ✕
            </button>
          </div>
        </div>

        <!-- Chat Messages Container -->
        <div
          ref="messagesContainer"
          class="flex-1 overflow-y-auto p-4 space-y-4 bg-gray-50/60 scroll-smooth"
        >
          <div
            v-for="(msg, index) in messages"
            :key="index"
            class="flex flex-col space-y-2"
          >
            <!-- BOT MESSAGE -->
            <div
              v-if="msg.sender === 'bot'"
              class="flex items-start gap-2.5 max-w-[92%]"
            >
              <div class="w-7 h-7 rounded-xl bg-gradient-to-br from-rose-500 to-amber-500 text-white flex items-center justify-center text-xs shrink-0 shadow-sm mt-0.5">
                AI
              </div>
              <div class="space-y-2.5 flex-1">
                <!-- Text Bubble -->
                <div class="bg-white p-3.5 rounded-2xl rounded-tl-sm border border-gray-100 shadow-sm text-xs text-gray-800 leading-relaxed space-y-2">
                  <div
                    class="prose prose-xs max-w-none whitespace-pre-line"
                    v-html="formatMessageText(msg.text)"
                  ></div>

                  <!-- Suggested Size Badge if any -->
                  <div
                    v-if="msg.suggestedSize"
                    class="mt-2 inline-flex items-center gap-2 px-3 py-1.5 rounded-xl bg-amber-50 border border-amber-200 text-amber-900 font-bold text-xs"
                  >
                    <span>📏</span> Phom chuẩn cho bạn: <span class="text-sm font-extrabold text-rose-600">Size {{ msg.suggestedSize }}</span>
                  </div>
                </div>

                <!-- Mini Product Cards Carousel/List if available -->
                <div
                  v-if="msg.suggestedProducts && msg.suggestedProducts.length > 0"
                  class="space-y-2 pt-1"
                >
                  <div class="text-[11px] font-bold text-gray-500 uppercase tracking-wider flex items-center gap-1">
                    <span>✨</span> Sản phẩm đề xuất dành riêng cho bạn:
                  </div>

                  <div class="grid grid-cols-1 gap-2">
                    <div
                      v-for="p in msg.suggestedProducts"
                      :key="p.id"
                      class="bg-white p-2.5 rounded-2xl border border-rose-100/70 shadow-sm hover:shadow-md transition-all flex items-center gap-3"
                    >
                      <img
                        :src="p.thumbnailUrl"
                        :alt="p.name"
                        @error="onImageError"
                        class="w-14 h-16 object-cover rounded-xl shrink-0 bg-gray-100"
                        loading="lazy"
                      />
                      <div class="flex-1 min-w-0">
                        <span class="text-[10px] font-semibold text-rose-600 uppercase tracking-wider">
                          {{ p.categoryName || 'Thời Trang' }}
                        </span>
                        <h4 class="text-xs font-bold text-gray-900 truncate">
                          {{ p.name }}
                        </h4>
                        <div class="flex items-center gap-2 mt-0.5">
                          <span class="text-xs font-extrabold text-rose-600">
                            {{ formatVND(p.salePrice || p.basePrice) }}
                          </span>
                          <span
                            v-if="p.salePrice && p.basePrice > p.salePrice"
                            class="text-[10px] text-gray-400 line-through"
                          >
                            {{ formatVND(p.basePrice) }}
                          </span>
                        </div>
                      </div>
                      <button
                        type="button"
                        @click="openProduct(p)"
                        class="px-2.5 py-1.5 rounded-xl bg-rose-50 hover:bg-rose-600 text-rose-700 hover:text-white text-[11px] font-bold shrink-0 transition-colors"
                      >
                        Xem ngay
                      </button>
                    </div>
                  </div>
                </div>

                <!-- Active Coupons Card if available -->
                <div
                  v-if="msg.activeCoupons && msg.activeCoupons.length > 0"
                  class="grid grid-cols-1 gap-1.5 pt-1"
                >
                  <div
                    v-for="c in msg.activeCoupons"
                    :key="c.id"
                    class="bg-gradient-to-r from-rose-50 to-amber-50 p-2.5 rounded-2xl border border-rose-200/60 flex items-center justify-between"
                  >
                    <div>
                      <div class="flex items-center gap-2">
                        <span class="font-mono font-black text-rose-700 text-xs px-2 py-0.5 bg-white rounded-lg border border-rose-200">
                          {{ c.code }}
                        </span>
                        <span class="text-xs font-bold text-amber-800">
                          Giảm {{ c.discountPercent }}%
                        </span>
                      </div>
                      <p class="text-[10px] text-gray-500 mt-0.5">
                        Tối đa {{ formatVND(c.maxDiscountAmount) }} (Đơn từ {{ formatVND(c.minOrderValue) }})
                      </p>
                    </div>
                    <button
                      type="button"
                      @click="copyCouponCode(c.code)"
                      class="px-2.5 py-1 rounded-xl bg-white hover:bg-rose-600 text-rose-700 hover:text-white border border-rose-200 font-bold text-[10px] transition-colors shadow-sm"
                    >
                      {{ copiedCode === c.code ? '✓ Đã chép' : 'Sao chép' }}
                    </button>
                  </div>
                </div>

                <!-- Order Info Tracking Card if available -->
                <div
                  v-if="msg.orderInfo"
                  class="bg-white p-3 rounded-2xl border border-emerald-200 shadow-sm space-y-2"
                >
                  <div class="flex items-center justify-between">
                    <span class="font-mono font-extrabold text-xs text-emerald-800">
                      #{{ msg.orderInfo.orderCode }}
                    </span>
                    <span class="text-[10px] font-bold px-2 py-0.5 rounded-full bg-emerald-50 text-emerald-700 border border-emerald-200">
                      {{ msg.orderInfo.orderStatus }}
                    </span>
                  </div>
                  <router-link
                    to="/order-tracking"
                    class="block text-center py-1.5 bg-emerald-600 hover:bg-emerald-700 text-white rounded-xl text-[11px] font-bold transition-colors"
                  >
                    Mở Trang Tra Cứu Chi Tiết →
                  </router-link>
                </div>

              </div>
            </div>

            <!-- USER MESSAGE -->
            <div
              v-else
              class="flex justify-end"
            >
              <div class="bg-gradient-to-r from-rose-600 to-rose-700 text-white px-4 py-2.5 rounded-2xl rounded-tr-sm text-xs shadow-sm max-w-[85%] leading-relaxed font-medium">
                {{ msg.text }}
              </div>
            </div>
          </div>

          <!-- Loading Indicator -->
          <div
            v-if="isLoading"
            class="flex items-center gap-2 text-gray-400 text-xs italic py-1"
          >
            <div class="w-6 h-6 rounded-xl bg-rose-100 text-rose-600 flex items-center justify-center text-[10px] font-bold">
              AI
            </div>
            <div class="flex items-center gap-1 bg-white px-3 py-2 rounded-2xl border border-gray-100 shadow-sm">
              <span class="w-2 h-2 bg-rose-500 rounded-full animate-bounce [animation-delay:-0.3s]"></span>
              <span class="w-2 h-2 bg-rose-500 rounded-full animate-bounce [animation-delay:-0.15s]"></span>
              <span class="w-2 h-2 bg-rose-500 rounded-full animate-bounce"></span>
            </div>
            <span>Đang tra cứu kho hàng...</span>
          </div>
        </div>

        <!-- Quick Replies Pills -->
        <div class="p-2.5 bg-white border-t border-gray-100 flex items-center gap-1.5 overflow-x-auto no-scrollbar">
          <button
            v-for="(reply, idx) in currentQuickReplies"
            :key="idx"
            type="button"
            @click="sendQuickReply(reply)"
            class="whitespace-nowrap px-3 py-1 rounded-full bg-gray-100 hover:bg-rose-50 hover:text-rose-600 text-gray-700 font-semibold text-[11px] transition-colors border border-gray-200/60 shrink-0"
          >
            {{ reply }}
          </button>
        </div>

        <!-- Input Footer -->
        <form
          @submit.prevent="handleSend"
          class="p-3 bg-white border-t border-gray-100 flex items-center gap-2"
        >
          <input
            v-model="inputQuery"
            type="text"
            placeholder="Hỏi size (VD: 1m75 65kg), tìm đồ hoặc mã đơn..."
            class="flex-1 px-3.5 py-2.5 bg-gray-50 border border-gray-200 rounded-2xl text-xs text-gray-900 placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-rose-500/20 focus:border-rose-500 transition-all"
            :disabled="isLoading"
          />
          <button
            type="submit"
            :disabled="!inputQuery.trim() || isLoading"
            class="w-10 h-10 rounded-2xl bg-gradient-to-r from-rose-600 to-rose-700 hover:from-rose-700 hover:to-rose-800 disabled:opacity-50 disabled:cursor-not-allowed text-white flex items-center justify-center shadow-md shadow-rose-600/30 transition-all shrink-0"
          >
            <svg class="w-4 h-4 transform rotate-90" fill="currentColor" viewBox="0 0 20 20">
              <path d="M10.894 2.553a1 1 0 00-1.788 0l-7 14a1 1 0 001.169 1.409l5-1.429A1 1 0 009 15.571V11a1 1 0 112 0v4.571a1 1 0 00.725.962l5 1.428a1 1 0 001.17-1.408l-7-14z" />
            </svg>
          </button>
        </form>
      </div>
    </transition>

    <!-- FLOATING TOGGLE BUTTON -->
    <button
      type="button"
      @click="toggleChat"
      class="group relative flex items-center gap-2.5 px-4 py-3 rounded-full bg-gradient-to-r from-rose-600 via-rose-500 to-amber-500 text-white shadow-xl hover:shadow-2xl hover:scale-105 active:scale-95 transition-all duration-300"
    >
      <!-- Pulse Ring -->
      <span class="absolute -inset-1 rounded-full bg-rose-500 opacity-30 group-hover:opacity-60 blur-sm transition-opacity"></span>

      <span class="relative text-xl animate-bounce">💬</span>
      <div class="relative text-left hidden sm:block">
        <div class="text-[10px] font-bold text-rose-100 uppercase tracking-widest leading-none">Hỏi Trợ Lý</div>
        <div class="text-xs font-black leading-tight">Fashion AI</div>
      </div>
      <span class="relative w-2.5 h-2.5 bg-emerald-400 rounded-full border-2 border-white"></span>
    </button>

  </div>
</template>

<script setup>
import { ref, nextTick, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import api from '@/services/api';
import { useProductStore } from '@/stores/productStore';
import { formatVND, onImageError } from '@/services/formatters';

const router = useRouter();
const productStore = useProductStore();

const isOpen = ref(false);
const isLoading = ref(false);
const inputQuery = ref('');
const messagesContainer = ref(null);
const copiedCode = ref(null);

const DEFAULT_QUICK_REPLIES = [
  '📏 Tư vấn chọn Size cho tôi',
  '🎁 Xin mã giảm giá hôm nay',
  '👕 Gợi ý đồ dưới 400k',
  '📦 Tra cứu đơn hàng'
];

const currentQuickReplies = ref([...DEFAULT_QUICK_REPLIES]);

const messages = ref([
  {
    sender: 'bot',
    text: 'Dạ xin chào bạn! Em là **Trợ Lý Ảo Thông Minh** của FashionStore ✨\n\nEm luôn sẵn sàng hỗ trợ bạn:\n• 📏 **Tư vấn chọn Size chuẩn** theo chiều cao & cân nặng\n• 🎁 **Tặng mã giảm giá & freeship** cực hot hôm nay\n• 👕 **Gợi ý phối đồ** theo ngân sách (VD: đồ dưới 400k)\n• 📦 **Tra cứu đơn hàng** tự động theo mã vận đơn\n\nBạn cần em trợ giúp điều gì cứ chọn nhanh bên dưới hoặc nhắn tin cho em nhé! 👇',
    suggestedProducts: [],
    activeCoupons: [],
    orderInfo: null,
    suggestedSize: null
  }
]);

function toggleChat() {
  isOpen.value = !isOpen.value;
  if (isOpen.value) {
    scrollToBottom();
  }
}

function clearChat() {
  messages.value = [
    {
      sender: 'bot',
      text: 'Cuộc trò chuyện đã được làm mới! Bạn cần em hỗ trợ chọn Size, tìm đồ hay tra cứu đơn hàng nào ạ? ✨',
      suggestedProducts: [],
      activeCoupons: [],
      orderInfo: null,
      suggestedSize: null
    }
  ];
  currentQuickReplies.value = [...DEFAULT_QUICK_REPLIES];
}

function sendQuickReply(reply) {
  inputQuery.value = reply;
  handleSend();
}

async function handleSend() {
  const query = inputQuery.value.trim();
  if (!query || isLoading.value) return;

  // Append user message
  messages.value.push({
    sender: 'user',
    text: query
  });

  inputQuery.value = '';
  isLoading.value = true;
  scrollToBottom();

  try {
    const res = await api.post('/chat-assistant', {
      message: query
    });

    if (res.success && res.data) {
      const data = res.data;
      messages.value.push({
        sender: 'bot',
        text: data.reply || 'Dạ shop đã ghi nhận câu hỏi của bạn!',
        suggestedSize: data.suggestedSize || null,
        suggestedProducts: data.suggestedProducts || [],
        activeCoupons: data.activeCoupons || [],
        orderInfo: data.orderInfo || null
      });

      if (data.quickReplies && data.quickReplies.length > 0) {
        currentQuickReplies.value = data.quickReplies;
      }
    } else {
      messages.value.push({
        sender: 'bot',
        text: 'Dạ hệ thống đang bận một chút, bạn thử lại sau giây lát nhé!'
      });
    }
  } catch (err) {
    console.error('Lỗi gọi trợ lý AI:', err);
    messages.value.push({
      sender: 'bot',
      text: 'Dạ tạm thời em chưa kết nối được máy chủ, bạn vui lòng thử lại sau nhé!'
    });
  } finally {
    isLoading.value = false;
    scrollToBottom();
  }
}

function openProduct(product) {
  if (productStore.openQuickView) {
    productStore.openQuickView(product);
  } else if (product.slug) {
    router.push(`/product/${product.slug}`);
    isOpen.value = false;
  }
}

function copyCouponCode(code) {
  if (!code) return;
  navigator.clipboard.writeText(code);
  copiedCode.value = code;
  setTimeout(() => {
    copiedCode.value = null;
  }, 2000);
}

function formatMessageText(text) {
  if (!text) return '';
  // Convert markdown **bold** to <strong>bold</strong>
  let formatted = text.replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>');
  // Convert markdown `code` to <code>
  formatted = formatted.replace(/`([^`]+)`/g, '<code class="px-1.5 py-0.5 rounded bg-gray-100 text-rose-600 font-mono text-[11px]">$1</code>');
  return formatted;
}

function scrollToBottom() {
  nextTick(() => {
    if (messagesContainer.value) {
      messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight;
    }
  });
}
</script>

<style scoped>
.no-scrollbar::-webkit-scrollbar {
  display: none;
}
.no-scrollbar {
  -ms-overflow-style: none;
  scrollbar-width: none;
}
</style>
