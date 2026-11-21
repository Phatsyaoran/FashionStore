<template>
  <Teleport to="body">
    <div
      class="fixed top-20 right-4 z-[10000] flex flex-col gap-2.5 w-[calc(100vw-2rem)] max-w-sm pointer-events-none no-print"
      aria-live="polite"
    >
      <TransitionGroup
        enter-active-class="transform transition ease-out duration-300"
        enter-from-class="translate-x-8 opacity-0 scale-95"
        enter-to-class="translate-x-0 opacity-100 scale-100"
        leave-active-class="transform transition ease-in duration-200"
        leave-from-class="translate-x-0 opacity-100 scale-100"
        leave-to-class="translate-x-8 opacity-0 scale-95"
      >
        <div
          v-for="toast in toastStore.toasts"
          :key="toast.id"
          class="pointer-events-auto flex items-start gap-3 px-4 py-3.5 rounded-xl shadow-md border backdrop-blur-md text-xs font-semibold"
          :class="toastClasses(toast.type)"
        >
          <span class="text-base leading-none shrink-0 mt-0.5">{{ toastIcon(toast.type) }}</span>
          <p class="flex-1 leading-relaxed break-words">{{ toast.message }}</p>
          <button
            type="button"
            @click="toastStore.removeToast(toast.id)"
            class="opacity-60 hover:opacity-100 transition-opacity p-0.5 leading-none shrink-0"
            aria-label="Đóng thông báo"
          >
            ✕
          </button>
        </div>
      </TransitionGroup>
    </div>
  </Teleport>
</template>

<script setup>
import { useToastStore } from '@/stores/toastStore';

const toastStore = useToastStore();

function toastClasses(type) {
  if (type === 'success') {
    return 'bg-emerald-50/95 border-emerald-200 text-emerald-900';
  }
  if (type === 'error') {
    return 'bg-rose-50/95 border-rose-200 text-rose-900';
  }
  return 'bg-white/95 border-gray-200 text-gray-800';
}

function toastIcon(type) {
  if (type === 'success') return '✅';
  if (type === 'error') return '⚠️';
  return 'ℹ️';
}
</script>
