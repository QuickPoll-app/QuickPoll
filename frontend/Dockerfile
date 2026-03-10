# ============================================
# QuickPoll Frontend — Multi-stage Build
# Angular 19 / Node 20 / Nginx
# ============================================
# syntax=docker/dockerfile:1

# Build Args
ARG NODE_VERSION=20.11-alpine
ARG NGINX_VERSION=1.25.4-alpine

# Stage 1: Dependencies (cached layer)
FROM node:${NODE_VERSION} AS deps
WORKDIR /app
COPY package.json package-lock.json ./
RUN --mount=type=cache,target=/root/.npm \
    npm ci --legacy-peer-deps

# Stage 2: Build
FROM deps AS build
WORKDIR /app
COPY . .
RUN npm run build -- --configuration=production

# Stage 3: Runtime
FROM nginx:${NGINX_VERSION} AS runtime

# OCI image labels
LABEL org.opencontainers.image.title="QuickPoll Frontend" \
    org.opencontainers.image.description="Real-Time Polling Platform UI — Angular 19" \
    org.opencontainers.image.source="https://github.com/QuickPoll-app/QuickPoll"

# Remove default nginx content
RUN rm -rf /usr/share/nginx/html/*

# Copy built Angular app
COPY --from=build /app/dist/quickpoll-frontend/browser /usr/share/nginx/html

# Copy ECS-optimized nginx config (no /api/ proxy — ALB routes by path)
COPY nginx.ecs.conf /etc/nginx/conf.d/default.conf

# Security: non-root user with writable dirs (single layer)
RUN chown -R nginx:nginx /usr/share/nginx/html && \
    chown -R nginx:nginx /var/cache/nginx && \
    chown -R nginx:nginx /var/log/nginx && \
    touch /var/run/nginx.pid && \
    chown -R nginx:nginx /var/run/nginx.pid

# Drop to non-root
USER nginx

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=5s --start-period=10s --retries=3 \
    CMD wget -qO- http://localhost:8080/health || exit 1

# Nginx with optimized worker shutdown
STOPSIGNAL SIGQUIT
CMD ["nginx", "-g", "daemon off;"]
