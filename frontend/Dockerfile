# ============================================
# QuickPoll Frontend — Multi-stage Build
# Angular 19 / Node 20 / Nginx
# ============================================

# Stage 1: Build
FROM node:20.11-alpine AS build
WORKDIR /app

# Cache dependencies first
COPY package.json package-lock.json ./
RUN npm ci --legacy-peer-deps

# Copy source and build
COPY . .
RUN npm run build -- --configuration=production

# Stage 2: Runtime
FROM nginx:1.25.4-alpine AS runtime

# Security: remove default config
RUN rm -rf /usr/share/nginx/html/*

# Copy built Angular app
COPY --from=build /app/dist/quickpoll-frontend/browser /usr/share/nginx/html

# Copy custom nginx config
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Security: non-root user
RUN chown -R nginx:nginx /usr/share/nginx/html && \
    chown -R nginx:nginx /var/cache/nginx && \
    chown -R nginx:nginx /var/log/nginx && \
    touch /var/run/nginx.pid && \
    chown -R nginx:nginx /var/run/nginx.pid

USER nginx

EXPOSE 80

HEALTHCHECK --interval=30s --timeout=5s --start-period=10s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:80/ || exit 1

CMD ["nginx", "-g", "daemon off;"]
