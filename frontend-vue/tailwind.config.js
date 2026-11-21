/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{vue,js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      fontFamily: {
        sans: ['"Plus Jakarta Sans"', '"Be Vietnam Pro"', 'system-ui', '-apple-system', 'sans-serif'],
      },
      colors: {
        ink: '#111111',
        accent: '#DC2626',
        brand: {
          50: '#fef2f2',
          100: '#fee2e2',
          200: '#fecaca',
          500: '#ef4444',
          600: '#dc2626',
          700: '#b91c1c',
          800: '#991b1b',
          900: '#7f1d1d',
        }
      },
      boxShadow: {
        'soft': '0 2px 12px -2px rgba(17, 17, 17, 0.05), 0 4px 16px -4px rgba(17, 17, 17, 0.03)',
        'card': '0 4px 20px -4px rgba(17, 17, 17, 0.06)',
        'float': '0 16px 36px -12px rgba(17, 17, 17, 0.18)',
      }
    },
  },
  plugins: [],
}
