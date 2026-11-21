<template>
  <!-- Global Toast Notifications -->
  <ToastContainer />

  <!-- Admin SaaS Layout -->
  <AdminLayout v-if="isAdminRoute">
    <router-view v-slot="{ Component }">
      <Transition name="page-fade" mode="out-in">
        <component :is="Component" />
      </Transition>
    </router-view>
  </AdminLayout>

  <!-- Customer Storefront Layout (Clean White Luxury) -->
  <div v-else class="min-h-screen flex flex-col bg-[#FAFAFA] text-[#18181B] overflow-x-hidden selection:bg-[#7A1032] selection:text-white">
    <!-- Main Header -->
    <Header />

    <!-- Main Router Content -->
    <main class="flex-1">
      <router-view v-slot="{ Component }">
        <Transition name="page-fade" mode="out-in">
          <component :is="Component" />
        </Transition>
      </router-view>
    </main>

    <!-- Global Quick View Modal (Phase 6) -->
    <QuickViewModal />

    <!-- Floating Chatbot Assistant Widget (Phase 9) -->
    <ChatWidget />

    <!-- Floating Social Proof Toast (Phase 12) -->
    <SocialProofToast />

    <!-- Main Footer -->
    <Footer />
  </div>
</template>

<script setup>
import { computed } from 'vue';
import { useRoute } from 'vue-router';
import Header from '@/components/Header.vue';
import Footer from '@/components/Footer.vue';
import QuickViewModal from '@/components/QuickViewModal.vue';
import ChatWidget from '@/components/ChatWidget.vue';
import SocialProofToast from '@/components/SocialProofToast.vue';
import ToastContainer from '@/components/ToastContainer.vue';
import AdminLayout from '@/components/AdminLayout.vue';

const route = useRoute();
const isAdminRoute = computed(() => route.path.startsWith('/admin'));
</script>
