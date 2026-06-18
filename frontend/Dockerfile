# ============================================
# ÉTAPE 1 : Build de l'application Vue.js
# ============================================
FROM node:22-alpine AS build

WORKDIR /app

# Copie les fichiers de dépendances
COPY package*.json ./
RUN npm install


# Copie le code source
COPY . .

# Compile pour la production
RUN npm run build

# ============================================
# ÉTAPE 2 : Serveur nginx léger
# ============================================
FROM nginx:alpine

# Copie le build dans nginx
COPY --from=build /app/dist /usr/share/nginx/html

# Config nginx pour Vue Router (SPA)
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]