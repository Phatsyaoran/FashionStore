# ==============================================================================
# STAGE 1: Build Vue 3 + Vite Production Bundle
# ==============================================================================
FROM node:20-alpine AS builder
WORKDIR /app

COPY package.json package-lock.json ./
RUN npm ci

COPY . .
ENV VITE_API_BASE_URL=/api
RUN npm run build

# ==============================================================================
# STAGE 2: Serve SPA & Reverse Proxy with Nginx
# ==============================================================================
FROM nginx:alpine

COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=builder /app/dist /usr/share/nginx/html

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
