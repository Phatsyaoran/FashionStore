<template>
  <div class="fixed bottom-4 right-4 md:bottom-6 md:right-6 z-50 flex flex-col items-end no-print">
    
    <!-- CHAT POPUP WINDOW (Clean White + Luxury Gradient Header) -->
    <Transition name="chat-pop">
      <div
        v-if="isOpen"
        class="w-[calc(100vw-2rem)] sm:w-96 h-[540px] max-h-[82vh] bg-white rounded-2xl shadow-2xl border border-neutral-200/80 flex flex-col overflow-hidden mb-3 text-[#18181B]"
      >
        <!-- Header -->
        <div class="relative overflow-hidden luxury-gradient text-[#FFFDF9] px-4 py-3.5 flex items-center justify-between">
          <StarfieldOverlay :min-stars="35" />
          <div class="relative z-10 flex items-center gap-3">
            <div class="relative">
              <div class="w-9 h-9 rounded-lg bg-white/15 flex items-center justify-center text-base border border-white/20">
                🤖
              </div>
              <span class="absolute -bottom-0.5 -right-0.5 w-2.5 h-2.5 bg-emerald-400 border-2 border-[#5C0B27] rounded-full"></span>
            </div>
            <div>
              <div class="flex items-center gap-2">
                <h3 class="font-bold text-sm tracking-tight text-[#FFFDF9]">Trợ Lý Fashion AI</h3>
                <span class="px-1.5 py-0.5 text-[9px] font-bold bg-white/20 text-[#F3D078] rounded uppercase tracking-wider">
                  AI
                </span>
              </div>
              <p class="text-[11px] text-[#FFFDF9]/80">Trực tuyến 24/7 • Tư vấn tức thì</p>
            </div>
          </div>

          <div class="relative z-10 flex items-center gap-1">
            <button
              type="button"
              @click="clearChat"
              title="Làm mới đoạn chat"
              class="w-8 h-8 rounded-xl bg-white/15 hover:bg-white/25 text-white flex items-center justify-center text-xs transition-colors"
            >
              🔄
            </button>
            <button
              type="button"
              @click="isOpen = false"
              title="Đóng cửa sổ"
              class="w-8 h-8 rounded-xl bg-white/15 hover:bg-white/25 text-white flex items-center justify-center text-xs transition-colors"
            >
              ✕
            </button>
          </div>
        </div>

        <!-- Chat Messages Container -->
        <div
          ref="messagesContainer"
          class="flex-1 overflow-y-auto p-4 space-y-4 bg-[#FAFAFA] scroll-smooth"
        >
          <div
            v-for="(msg, index) in messages"
            :key="index"
            class="flex flex-col space-y-2 animate-msg-in"
          >
            <!-- BOT MESSAGE -->
            <div
              v-if="msg.sender === 'bot'"
              class="flex items-start gap-2.5 max-w-[92%]"
            >
              <div class="w-7 h-7 rounded-xl luxury-gradient text-[#F3D078] flex items-center justify-center text-[10px] font-bold shrink-0 shadow-sm mt-0.5">
                AI
              </div>
              <div class="space-y-2.5 flex-1">
                <!-- Text Bubble -->
                <div class="bg-white p-3.5 rounded-2xl rounded-tl-sm border border-neutral-200/80 shadow-sm text-xs text-[#18181B] leading-relaxed space-y-2">
                  <div
                    class="prose prose-xs max-w-none whitespace-pre-line"
                    v-html="formatMessageText(msg.text)"
                  ></div>

                  <!-- Suggested Size Badge if any -->
                  <div
                    v-if="msg.suggestedSize"
                    class="mt-2 inline-flex items-center gap-2 px-3 py-1.5 rounded-xl bg-amber-50 border border-amber-200 text-amber-900 font-bold text-xs"
                  >
                    <span>📏</span> Phom chuẩn cho bạn: <span class="text-sm font-extrabold text-[#7A1032]">Size {{ msg.suggestedSize }}</span>
                  </div>
                </div>

                <!-- Mini Product Cards -->
                <div
                  v-if="msg.suggestedProducts && msg.suggestedProducts.length > 0"
                  class="space-y-2 pt-1"
                >
                  <div class="text-[11px] font-bold text-[#6B7280] uppercase tracking-wider flex items-center gap-1">
                    <span>✨</span> Sản phẩm đề xuất dành riêng cho bạn:
                  </div>

                  <div class="grid grid-cols-1 gap-2">
                    <div
                      v-for="p in msg.suggestedProducts"
                      :key="p.id"
                      class="bg-white p-2.5 rounded-xl border border-neutral-200/80 hover:border-[#C9963B]/50 shadow-sm transition-all flex items-center gap-3"
                    >
                      <img
                        :src="p.thumbnailUrl"
                        :alt="p.name"
                        @error="onImageError"
                        class="w-14 h-16 object-cover rounded-lg shrink-0 bg-neutral-100"
                        loading="lazy"
                      />
                      <div class="flex-1 min-w-0">
                        <span class="text-[10px] font-semibold text-[#6B7280] uppercase tracking-wider">
                          {{ p.categoryName || 'Thời Trang' }}
                        </span>
                        <h4 class="text-xs font-bold text-[#18181B] truncate">
                          {{ p.name }}
                        </h4>
                        <div class="flex items-center gap-2 mt-0.5">
                          <span class="text-xs font-extrabold text-[#7A1032]">
                            {{ formatVND(p.salePrice || p.basePrice) }}
                          </span>
                          <span
                            v-if="p.salePrice && p.basePrice > p.salePrice"
                            class="text-[10px] text-[#6B7280]/70 line-through"
                          >
                            {{ formatVND(p.basePrice) }}
                          </span>
                        </div>
                      </div>
                      <button
                        type="button"
                        @click="openProduct(p)"
                        class="px-2.5 py-1.5 rounded-lg bg-[#7A1032] hover:bg-[#5C0B27] text-white text-[11px] font-bold shrink-0 transition-colors"
                      >
                        Xem ngay
                      </button>
                    </div>
                  </div>
                </div>

                <!-- Active Coupons Card -->
                <div
                  v-if="msg.activeCoupons && msg.activeCoupons.length > 0"
                  class="grid grid-cols-1 gap-1.5 pt-1"
                >
                  <div
                    v-for="c in msg.activeCoupons"
                    :key="c.id"
                    class="bg-amber-50/70 p-2.5 rounded-xl border border-[#C9963B]/30 flex items-center justify-between"
                  >
                    <div>
                      <div class="flex items-center gap-2">
                        <span class="font-mono font-black text-[#7A1032] text-xs px-2 py-0.5 bg-white rounded-md border border-neutral-200">
                          {{ c.code }}
                        </span>
                        <span class="text-xs font-bold text-[#18181B]">
                          Giảm {{ c.discountPercent }}%
                        </span>
                      </div>
                      <p class="text-[10px] text-[#6B7280] mt-0.5">
                        Tối đa {{ formatVND(c.maxDiscountAmount) }} (Đơn từ {{ formatVND(c.minOrderValue) }})
                      </p>
                    </div>
                    <button
                      type="button"
                      @click="copyCouponCode(c.code)"
                      class="px-2.5 py-1 rounded-lg bg-white hover:bg-[#7A1032] text-[#7A1032] hover:text-white border border-neutral-200 font-bold text-[10px] transition-colors"
                    >
                      {{ copiedCode === c.code ? '✓ Đã chép' : 'Sao chép' }}
                    </button>
                  </div>
                </div>

                <!-- Order Info Tracking Card -->
                <div
                  v-if="msg.orderInfo"
                  class="bg-white p-3 rounded-xl border border-emerald-200 shadow-sm space-y-2"
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
                    class="block text-center py-1.5 bg-emerald-600 hover:bg-emerald-700 text-white rounded-lg text-[11px] font-bold transition-colors"
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
              <div class="bg-[#7A1032] text-white px-4 py-2.5 rounded-2xl rounded-tr-sm text-xs shadow-sm max-w-[85%] leading-relaxed font-medium">
                {{ msg.text }}
              </div>
            </div>
          </div>

          <!-- Loading Indicator -->
          <div
            v-if="isLoading"
            class="flex items-center gap-2 text-[#6B7280] text-xs italic py-1"
          >
            <div class="w-6 h-6 rounded-xl luxury-gradient text-[#F3D078] flex items-center justify-center text-[10px] font-bold">
              AI
            </div>
            <div class="flex items-center gap-1 bg-white px-3 py-2 rounded-2xl border border-neutral-200 shadow-sm">
              <span class="w-2 h-2 bg-[#7A1032] rounded-full animate-bounce [animation-delay:-0.3s]"></span>
              <span class="w-2 h-2 bg-[#C9963B] rounded-full animate-bounce [animation-delay:-0.15s]"></span>
              <span class="w-2 h-2 bg-[#5E1B55] rounded-full animate-bounce"></span>
            </div>
            <span>Trợ lý đang tư vấn...</span>
          </div>
        </div>

        <!-- Quick Replies Pills -->
        <div class="p-2.5 bg-white border-t border-neutral-200/80 flex items-center gap-1.5 overflow-x-auto no-scrollbar">
          <button
            v-for="(reply, idx) in currentQuickReplies"
            :key="idx"
            type="button"
            @click="sendQuickReply(reply)"
            class="whitespace-nowrap px-3 py-1 rounded-full bg-neutral-100 hover:bg-rose-50 hover:text-[#7A1032] text-[#18181B] font-semibold text-[11px] transition-colors border border-neutral-200/60 shrink-0"
          >
            {{ reply }}
          </button>
        </div>

        <!-- Input Footer -->
        <form
          @submit.prevent="handleSend"
          class="p-3 bg-white border-t border-neutral-200/80 flex items-center gap-2"
        >
          <input
            v-model="inputQuery"
            type="text"
            placeholder="Hỏi size (VD: 1m75 65kg), tìm đồ hoặc mã đơn..."
            class="flex-1 px-3.5 py-2.5 bg-white border border-neutral-200 rounded-xl text-xs text-[#18181B] placeholder-[#6B7280] focus:outline-none focus:ring-1 focus:ring-[#7A1032] focus:border-[#7A1032] transition-all"
            :disabled="isLoading"
          />
          <button
            type="submit"
            :disabled="!inputQuery.trim() || isLoading"
            class="w-10 h-10 rounded-xl bg-[#7A1032] hover:bg-[#5C0B27] disabled:opacity-50 disabled:cursor-not-allowed text-white flex items-center justify-center shadow-sm transition-all shrink-0"
          >
            <svg class="w-4 h-4 transform rotate-90" fill="currentColor" viewBox="0 0 20 20">
              <path d="M10.894 2.553a1 1 0 00-1.788 0l-7 14a1 1 0 001.169 1.409l5-1.429A1 1 0 009 15.571V11a1 1 0 112 0v4.571a1 1 0 00.725.962l5 1.428a1 1 0 001.17-1.408l-7-14z" />
            </svg>
          </button>
        </form>
      </div>
    </Transition>

    <!-- FLOATING TOGGLE BUTTON (Luxury Gradient + Interactive Starfield) -->
    <button
      type="button"
      @click="toggleChat"
      class="silky-hover group relative overflow-hidden flex items-center justify-center w-12 h-12 md:w-auto md:h-auto p-0 md:px-4 md:py-3 rounded-full luxury-gradient text-[#FFFDF9] shadow-[0_12px_30px_-6px_rgba(74,8,32,0.5)] hover:scale-[1.03] active:scale-[0.98] border border-white/25"
      aria-label="Mở trợ lý Fashion AI"
    >
      <StarfieldOverlay :min-stars="18" />
      <span class="relative z-10 flex items-center justify-center md:gap-2.5">
        <span class="text-lg leading-none">✨</span>
        <div class="text-left hidden md:block">
          <div class="text-[9px] font-semibold text-[#F3D078] uppercase tracking-[0.14em] leading-none">Tư Vấn</div>
          <div class="text-xs font-bold leading-tight mt-0.5 text-[#FFFDF9]">Fashion AI</div>
        </div>
        <span class="hidden md:inline-block w-2 h-2 bg-emerald-400 rounded-full"></span>
      </span>
      <span class="md:hidden absolute top-2 right-2 z-20 w-2 h-2 bg-emerald-400 rounded-full ring-2 ring-[#5C0B27]"></span>
    </button>

  </div>
</template>

<script setup>
import { ref, nextTick, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import api from '@/services/api';
import { useProductStore } from '@/stores/productStore';
import { formatVND, onImageError } from '@/services/formatters';
import StarfieldOverlay from '@/components/StarfieldOverlay.vue';

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
  if (product?.slug) {
    router.push(`/product/${product.slug}`);
    if (window.innerWidth < 768) {
      isOpen.value = false;
    }
  } else if (productStore.openQuickView) {
    productStore.openQuickView(product);
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

function escapeHtml(raw) {
  return String(raw)
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')
    .replace(/'/g, '&#39;');
}

function formatMessageText(text) {
  if (!text) return '';
  // Escape HTML trước để chặn XSS
  let formatted = escapeHtml(text);
  // Convert markdown **bold** to <strong>bold</strong>
  formatted = formatted.replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>');
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
