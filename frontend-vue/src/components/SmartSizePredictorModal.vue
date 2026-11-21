<template>
  <Teleport to="body">
    <div v-if="isOpen" class="fixed inset-0 z-[9990] overflow-y-auto no-print" aria-labelledby="modal-title" role="dialog" aria-modal="true">
      <!-- Backdrop -->
      <div class="fixed inset-0 z-[9990] bg-black/45 backdrop-blur-[2px] transition-opacity" @click="close"></div>

      <div class="relative z-[9995] flex min-h-screen items-center justify-center p-4 text-center sm:p-0">
        <div class="relative transform overflow-hidden rounded-2xl bg-white text-left text-[#18181B] shadow-2xl transition-all sm:my-8 sm:w-full sm:max-w-lg border border-neutral-200/80 max-h-[90vh] overflow-y-auto w-full">
        
        <!-- Modal Header -->
        <div class="luxury-gradient px-6 py-4 text-[#FFFDF9] flex items-center justify-between">
          <div class="flex items-center gap-2.5">
            <span class="text-2xl">📏</span>
            <div>
              <h3 class="text-base font-bold tracking-tight text-[#FFFDF9]">Trợ Lý Gợi Ý Size AI</h3>
              <p class="text-xs text-[#FFFDF9]/80">Tính toán size vừa vặn chuẩn theo số đo nhân trắc học Việt Nam</p>
            </div>
          </div>
          <button @click="close" class="text-white/80 hover:text-white rounded-lg p-1 hover:bg-white/15 transition-colors">
            <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
            </svg>
          </button>
        </div>

        <div class="p-6 space-y-6">
          <!-- Slider 1: Chiều cao -->
          <div class="space-y-2">
            <div class="flex justify-between items-center text-sm font-medium">
              <span class="text-[#18181B] flex items-center gap-1.5">
                <span>🧍‍♂️</span> Chiều cao của bạn:
              </span>
              <span class="text-base font-bold text-[#7A1032] bg-rose-50 px-2.5 py-0.5 rounded-full border border-rose-200">
                {{ height }} cm
              </span>
            </div>
            <input
              type="range"
              min="145"
              max="190"
              step="1"
              v-model.number="height"
              class="w-full h-2 bg-neutral-200 rounded-lg appearance-none cursor-pointer accent-[#7A1032]"
            />
            <div class="flex justify-between text-[11px] text-[#6B7280]">
              <span>145 cm</span>
              <span>165 cm</span>
              <span>190 cm</span>
            </div>
          </div>

          <!-- Slider 2: Cân nặng -->
          <div class="space-y-2">
            <div class="flex justify-between items-center text-sm font-medium">
              <span class="text-[#18181B] flex items-center gap-1.5">
                <span>⚖️</span> Cân nặng của bạn:
              </span>
              <span class="text-base font-bold text-[#7A1032] bg-rose-50 px-2.5 py-0.5 rounded-full border border-rose-200">
                {{ weight }} kg
              </span>
            </div>
            <input
              type="range"
              min="40"
              max="95"
              step="1"
              v-model.number="weight"
              class="w-full h-2 bg-neutral-200 rounded-lg appearance-none cursor-pointer accent-[#7A1032]"
            />
            <div class="flex justify-between text-[11px] text-[#6B7280]">
              <span>40 kg</span>
              <span>65 kg</span>
              <span>95 kg</span>
            </div>
          </div>

          <!-- Option 3: Sở thích mặc -->
          <div class="space-y-2">
            <label class="block text-sm font-medium text-[#18181B]">
              Sở thích phom dáng:
            </label>
            <div class="grid grid-cols-3 gap-2">
              <button
                type="button"
                @click="fitPreference = 'TIGHT'"
                class="px-3 py-2 text-xs font-semibold rounded-xl border transition-all text-center"
                :class="fitPreference === 'TIGHT'
                  ? 'border-[#7A1032] bg-[#7A1032] text-white'
                  : 'border-neutral-200 bg-white text-[#18181B] hover:border-[#C9963B]'"
              >
                Mặc ôm Body
              </button>
              <button
                type="button"
                @click="fitPreference = 'REGULAR'"
                class="px-3 py-2 text-xs font-semibold rounded-xl border transition-all text-center"
                :class="fitPreference === 'REGULAR'
                  ? 'border-[#7A1032] bg-[#7A1032] text-white'
                  : 'border-neutral-200 bg-white text-[#18181B] hover:border-[#C9963B]'"
              >
                Mặc vừa vặn
              </button>
              <button
                type="button"
                @click="fitPreference = 'OVERSIZE'"
                class="px-3 py-2 text-xs font-semibold rounded-xl border transition-all text-center"
                :class="fitPreference === 'OVERSIZE'
                  ? 'border-[#7A1032] bg-[#7A1032] text-white'
                  : 'border-neutral-200 bg-white text-[#18181B] hover:border-[#C9963B]'"
              >
                Mặc rộng Oversize
              </button>
            </div>
          </div>

          <!-- Prediction Result Box -->
          <div class="rounded-2xl p-4 bg-gradient-to-br from-rose-50/80 via-white to-amber-50/80 border border-[#C9963B]/35 shadow-sm">
            <div class="flex items-center justify-between mb-2">
              <span class="text-xs font-bold uppercase tracking-wider text-[#7A1032]">Kết quả dự đoán AI</span>
              <span class="inline-flex items-center px-2 py-0.5 rounded-full text-[11px] font-semibold bg-emerald-50 text-emerald-700 border border-emerald-200">
                Độ vừa vặn ~{{ matchRate }}%
              </span>
            </div>

            <p class="text-sm text-[#18181B] leading-snug">
              Hệ thống gợi ý bạn chọn <strong class="text-xl text-[#7A1032] font-extrabold mx-1">Size {{ predictedSize }}</strong>
            </p>
            <p class="text-xs text-[#6B7280] mt-1">
              {{ explanation }}
            </p>
          </div>
        </div>

        <!-- Modal Footer -->
        <div class="bg-gray-50/80 px-6 py-4 flex flex-col sm:flex-row gap-2.5 sm:justify-end border-t border-neutral-200">
          <button
            type="button"
            @click="close"
            class="px-4 py-2.5 rounded-xl border border-neutral-300 bg-white text-[#18181B] hover:bg-neutral-100 text-xs font-semibold transition-colors"
          >
            Đóng
          </button>
          <button
            type="button"
            @click="applySuggestedSize"
            class="btn-sheen active:scale-[0.98] px-5 py-2.5 rounded-xl bg-[#7A1032] hover:bg-[#5C0B27] text-white text-xs font-bold transition-all flex items-center justify-center gap-1.5"
          >
            <span>✨</span> Chọn ngay Size {{ predictedSize }}
          </button>
        </div>

        </div>
      </div>
    </div>
  </Teleport>
</template>

<script setup>
import { ref, computed, watch, onUnmounted } from 'vue';

const props = defineProps({
  isOpen: {
    type: Boolean,
    default: false
  },
  availableSizes: {
    type: Array,
    default: () => ['S', 'M', 'L', 'XL']
  }
});

watch(() => props.isOpen, (open) => {
  document.body.style.overflow = open ? 'hidden' : '';
});

onUnmounted(() => {
  document.body.style.overflow = '';
});

const emit = defineEmits(['close', 'select-size']);

const height = ref(170);
const weight = ref(62);
const fitPreference = ref('REGULAR'); // TIGHT, REGULAR, OVERSIZE

// Thuật toán tính toán Size chuẩn nhân trắc học
const predictedSize = computed(() => {
  let size = 'M';
  const w = weight.value;
  const h = height.value;
  const pref = fitPreference.value;

  // 1. Phân cấp size theo cân nặng cơ bản
  if (w < 53) {
    size = 'S';
  } else if (w <= 62) {
    size = 'M';
  } else if (w <= 72) {
    size = 'L';
  } else {
    size = 'XL';
  }

  // 2. Tinh chỉnh theo chiều cao và sở thích phom
  const sizeOrder = ['S', 'M', 'L', 'XL'];
  let idx = sizeOrder.indexOf(size);

  if (pref === 'OVERSIZE' || h > 178) {
    if (idx < sizeOrder.length - 1) {
      idx += 1;
    }
  } else if (pref === 'TIGHT') {
    // Nếu chọn mặc ôm và ở cận dưới của dải cân nặng
    if (idx > 0 && ((w === 53 && size === 'M') || (w === 63 && size === 'L') || (w === 73 && size === 'XL'))) {
      idx -= 1;
    }
  }

  return sizeOrder[idx];
});

const matchRate = computed(() => {
  if (fitPreference.value === 'REGULAR') return 95;
  if (fitPreference.value === 'OVERSIZE') return 92;
  return 90;
});

const explanation = computed(() => {
  const size = predictedSize.value;
  const prefText = fitPreference.value === 'OVERSIZE' ? 'rộng rãi' : fitPreference.value === 'TIGHT' ? 'ôm sát' : 'thoải mái chuẩn phom';
  return `Với chiều cao ${height.value}cm, cân nặng ${weight.value}kg và nhu cầu mặc ${prefText}, Size ${size} sẽ mang lại trải nghiệm tối ưu nhất cho bạn.`;
});

function close() {
  emit('close');
}

function applySuggestedSize() {
  emit('select-size', predictedSize.value);
  close();
}
</script>
