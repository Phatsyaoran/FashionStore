import { createRouter, createWebHistory } from 'vue-router';
import HomeView from '@/views/HomeView.vue';
import CatalogView from '@/views/CatalogView.vue';
import ProductDetailView from '@/views/ProductDetailView.vue';
import CartView from '@/views/CartView.vue';
import CheckoutView from '@/views/CheckoutView.vue';
import OrderTrackingView from '@/views/OrderTrackingView.vue';
import LoginView from '@/views/LoginView.vue';
import RegisterView from '@/views/RegisterView.vue';
import ProfileView from '@/views/ProfileView.vue';
import AdminDashboardView from '@/views/admin/AdminDashboardView.vue';
import AdminProductsView from '@/views/admin/AdminProductsView.vue';
import AdminOrdersView from '@/views/admin/AdminOrdersView.vue';
import AdminCouponsView from '@/views/admin/AdminCouponsView.vue';
import AdminCategoriesView from '@/views/admin/AdminCategoriesView.vue';
import { useAuthStore } from '@/stores/authStore';
import { useToastStore } from '@/stores/toastStore';

const routes = [
  {
    path: '/',
    name: 'home',
    component: HomeView
  },
  {
    path: '/catalog',
    name: 'catalog',
    component: CatalogView
  },
  {
    path: '/product/:slug',
    name: 'product-detail',
    component: ProductDetailView
  },
  {
    path: '/cart',
    name: 'cart',
    component: CartView
  },
  {
    path: '/checkout',
    name: 'checkout',
    component: CheckoutView
  },
  {
    path: '/order-tracking',
    name: 'order-tracking',
    component: OrderTrackingView
  },
  {
    path: '/login',
    name: 'login',
    component: LoginView
  },
  {
    path: '/register',
    name: 'register',
    component: RegisterView
  },
  {
    path: '/profile',
    name: 'profile',
    component: ProfileView,
    meta: { requiresAuth: true }
  },
  {
    path: '/admin',
    name: 'admin-dashboard',
    component: AdminDashboardView,
    meta: { requiresAdmin: true }
  },
  {
    path: '/admin/products',
    name: 'admin-products',
    component: AdminProductsView,
    meta: { requiresAdmin: true }
  },
  {
    path: '/admin/orders',
    name: 'admin-orders',
    component: AdminOrdersView,
    meta: { requiresAdmin: true }
  },
  {
    path: '/admin/coupons',
    name: 'admin-coupons',
    component: AdminCouponsView,
    meta: { requiresAdmin: true }
  },
  {
    path: '/admin/categories',
    name: 'admin-categories',
    component: AdminCategoriesView,
    meta: { requiresAdmin: true }
  },
  {
    path: '/:pathMatch(.*)*',
    redirect: '/'
  }
];

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior(to, from, savedPosition) {
    if (savedPosition) {
      return savedPosition;
    } else {
      return { top: 0, behavior: 'smooth' };
    }
  }
});

// Route Guard (Auth & Admin)
router.beforeEach((to, from, next) => {
  const authStore = useAuthStore();
  if (to.meta.requiresAdmin) {
    if (!authStore.isAuthenticated || !authStore.isAdmin) {
      useToastStore().error('Vui lòng đăng nhập bằng tài khoản Quản Trị Viên (Admin) để truy cập khu vực này.');
      next({ name: 'login' });
      return;
    }
  } else if (to.meta.requiresAuth) {
    if (!authStore.isAuthenticated) {
      useToastStore().info('Vui lòng đăng nhập để xem thông tin tài khoản và đơn hàng của bạn.');
      next({ name: 'login' });
      return;
    }
  }
  next();
});

export default router;
