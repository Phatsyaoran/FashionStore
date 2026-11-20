<template>
  <div class="min-h-screen flex flex-col bg-gray-50 text-gray-900">
    <!-- Main Header -->
    <Header />

    <!-- Main Router Content -->
    <main class="flex-1">
      <router-view />
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
import Header from '@/components/Header.vue';
import Footer from '@/components/Footer.vue';
import QuickViewModal from '@/components/QuickViewModal.vue';
import ChatWidget from '@/components/ChatWidget.vue';
import SocialProofToast from '@/components/SocialProofToast.vue';
</script>
