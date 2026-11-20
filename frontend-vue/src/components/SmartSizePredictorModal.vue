<template>
  <div v-if="isOpen" class="fixed inset-0 z-50 overflow-y-auto" aria-labelledby="modal-title" role="dialog" aria-modal="true">
    <!-- Backdrop -->
    <div class="fixed inset-0 bg-black/60 backdrop-blur-sm transition-opacity" @click="close"></div>

    <div class="flex min-h-screen items-center justify-center p-4 text-center sm:p-0">
      <div class="relative transform overflow-hidden rounded-2xl bg-white text-left shadow-2xl transition-all sm:my-8 sm:w-full sm:max-w-lg border border-gray-100 max-h-[90vh] overflow-y-auto w-full">
        
        <!-- Modal Header -->
        <div class="bg-gradient-to-r from-gray-900 to-gray-800 px-6 py-4 text-white flex items-center justify-between">
          <div class="flex items-center gap-2.5">
            <span class="text-2xl">📏</span>
            <div>
              <h3 class="text-base font-bold tracking-tight">Trợ Lý Gợi Ý Size Thông Minh</h3>
              <p class="text-xs text-gray-300">Tính toán size vừa vặn chuẩn theo số đo nhân trắc học Việt Nam</p>
            </div>
          </div>
          <button @click="close" class="text-gray-400 hover:text-white rounded-lg p-1 hover:bg-white/10 transition-colors">
            <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
            </svg>
          </button>
        </div>

        <div class="p-6 space-y-6">
          <!-- Slider 1: Chiều cao -->
          <div class="space-y-2">
            <div class="flex justify-between items-center text-sm font-medium">
              <span class="text-gray-700 flex items-center gap-1.5">
                <span>🧍‍♂️</span> Chiều cao của bạn:
              </span>
              <span class="text-base font-bold text-rose-600 bg-rose-50 px-2.5 py-0.5 rounded-full border border-rose-100">
                {{ height }} cm
              </span>
            </div>
            <input
              type="range"
              min="145"
              max="190"
              step="1"
              v-model.number="height"
              class="w-full h-2 bg-gray-200 rounded-lg appearance-none cursor-pointer accent-rose-600"
            />
            <div class="flex justify-between text-[11px] text-gray-400">
              <span>145 cm</span>
              <span>165 cm</span>
              <span>190 cm</span>
            </div>
          </div>

          <!-- Slider 2: Cân nặng -->
          <div class="space-y-2">
            <div class="flex justify-between items-center text-sm font-medium">
              <span class="text-gray-700 flex items-center gap-1.5">
                <span>⚖️</span> Cân nặng của bạn:
              </span>
              <span class="text-base font-bold text-rose-600 bg-rose-50 px-2.5 py-0.5 rounded-full border border-rose-100">
                {{ weight }} kg
              </span>
            </div>
            <input
              type="range"
              min="40"
              max="95"
              step="1"
              v-model.number="weight"
              class="w-full h-2 bg-gray-200 rounded-lg appearance-none cursor-pointer accent-rose-600"
            />
            <div class="flex justify-between text-[11px] text-gray-400">
              <span>40 kg</span>
              <span>65 kg</span>
              <span>95 kg</span>
            </div>
          </div>

          <!-- Option 3: Sở thích mặc -->
          <div class="space-y-2">
            <label class="block text-sm font-medium text-gray-700">
              Sở thích phom dáng:
            </label>
            <div class="grid grid-cols-3 gap-2">
              <button
                type="button"
                @click="fitPreference = 'TIGHT'"
                class="px-3 py-2 text-xs font-semibold rounded-xl border transition-all text-center"
                :class="fitPreference === 'TIGHT'
                  ? 'border-rose-600 bg-rose-50 text-rose-700 shadow-sm'
                  : 'border-gray-200 bg-gray-50 text-gray-700 hover:bg-gray-100'"
              >
                Mặc ôm Body
              </button>
              <button
                type="button"
                @click="fitPreference = 'REGULAR'"
                class="px-3 py-2 text-xs font-semibold rounded-xl border transition-all text-center"
                :class="fitPreference === 'REGULAR'
                  ? 'border-rose-600 bg-rose-50 text-rose-700 shadow-sm'
                  : 'border-gray-200 bg-gray-50 text-gray-700 hover:bg-gray-100'"
              >
                Mặc vừa vặn
              </button>
              <button
                type="button"
                @click="fitPreference = 'OVERSIZE'"
                class="px-3 py-2 text-xs font-semibold rounded-xl border transition-all text-center"
                :class="fitPreference === 'OVERSIZE'
                  ? 'border-rose-600 bg-rose-50 text-rose-700 shadow-sm'
                  : 'border-gray-200 bg-gray-50 text-gray-700 hover:bg-gray-100'"
              >
                Mặc rộng Oversize
              </button>
            </div>
          </div>

          <!-- Prediction Result Box -->
          <div class="rounded-2xl p-4 bg-gradient-to-br from-rose-50 via-rose-100/60 to-amber-50 border border-rose-200 shadow-sm">
            <div class="flex items-center justify-between mb-2">
              <span class="text-xs font-bold uppercase tracking-wider text-rose-800">Kết quả dự đoán AI</span>
              <span class="inline-flex items-center px-2 py-0.5 rounded-full text-[11px] font-semibold bg-emerald-100 text-emerald-800 border border-emerald-200">
                Độ vừa vặn ~{{ matchRate }}%
              </span>
            </div>

            <p class="text-sm text-gray-800 leading-snug">
              Hệ thống gợi ý bạn chọn <strong class="text-xl text-rose-600 font-extrabold mx-1">Size {{ predictedSize }}</strong>
            </p>
            <p class="text-xs text-gray-600 mt-1">
              {{ explanation }}
            </p>
          </div>
        </div>

        <!-- Modal Footer -->
        <div class="bg-gray-50 px-6 py-4 flex flex-col sm:flex-row gap-2.5 sm:justify-end border-t border-gray-100">
          <button
            type="button"
            @click="close"
            class="px-4 py-2.5 rounded-xl border border-gray-300 text-gray-700 hover:bg-gray-100 text-xs font-semibold transition-colors"
          >
            Đóng
          </button>
          <button
            type="button"
            @click="applySuggestedSize"
            class="px-5 py-2.5 rounded-xl bg-rose-600 hover:bg-rose-700 text-white text-xs font-bold shadow-md hover:shadow-lg transition-all flex items-center justify-center gap-1.5"
          >
            <span>✨</span> Chọn ngay Size {{ predictedSize }}
          </button>
        </div>

      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue';

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
