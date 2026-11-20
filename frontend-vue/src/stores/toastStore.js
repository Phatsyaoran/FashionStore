import { defineStore } from 'pinia';
import { ref } from 'vue';

let nextId = 1;

export const useToastStore = defineStore('toast', () => {
  const toasts = ref([]);

  function removeToast(id) {
    toasts.value = toasts.value.filter((t) => t.id !== id);
  }

  function addToast(message, type = 'info', duration = 3000) {
    if (!message) return;
    const id = nextId++;
    toasts.value.push({ id, message: String(message), type });

    if (duration > 0) {
      setTimeout(() => {
        removeToast(id);
      }, duration);
    }
    return id;
  }

  function success(message, duration = 3000) {
    return addToast(message, 'success', duration);
  }

  function error(message, duration = 3500) {
    return addToast(message, 'error', duration);
  }

  function info(message, duration = 3000) {
    return addToast(message, 'info', duration);
  }

  return {
    toasts,
    addToast,
    removeToast,
    success,
    error,
    info
  };
});
