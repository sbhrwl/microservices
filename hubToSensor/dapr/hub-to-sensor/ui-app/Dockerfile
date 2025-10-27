# ---------- Build stage ----------
FROM node:20-alpine AS build
WORKDIR /app

# Install dependencies
COPY package*.json ./
RUN npm ci --no-audit --no-fund

# Copy source and build
COPY . .
RUN npm run build

# ---------- Runtime stage ----------
FROM node:20-alpine AS runtime
ENV NODE_ENV=production \
    PORT=4000
WORKDIR /app

# Copy built artifacts only
COPY --from=build /app/dist ./dist

# Expose configurable port
EXPOSE 4000

# Start SSR server (respects PORT env)
CMD ["node", "dist/ui-app/server/server.mjs"]


