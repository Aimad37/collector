#!/bin/bash

PROJECT_DIR=$(pwd)
echo "🚀 Déploiement Collector sur K3s"
echo "================================="

# Build des images
echo "📦 Build des images Docker..."
docker build -t collector-backend:latest ./backend
docker build -t collector-frontend:latest ./frontend

# Import dans K3s
echo "📥 Import des images dans K3s..."
docker save collector-backend:latest | sudo k3s ctr images import -
docker save collector-frontend:latest | sudo k3s ctr images import -

# Remplace le chemin Keycloak et Grafana
sed -i "s|PATH_TO_REPLACE|$PROJECT_DIR|g" k8s/keycloak/deployment.yaml
sed -i "s|PATH_TO_REPLACE|$PROJECT_DIR|g" k8s/grafana/deployment.yaml

# Remplace le chemin Nginx
sed -i "s|PATH_TO_REPLACE|$PROJECT_DIR|g" k8s/nginx/deployment.yaml



# Déploiement
echo "☸️ Déploiement sur K3s..."
sudo kubectl apply -f k8s/namespace.yaml
sudo kubectl apply -f k8s/configmap.yaml
sudo kubectl apply -f k8s/postgres/
sudo kubectl apply -f k8s/rabbitmq/
sudo kubectl apply -f k8s/keycloak/
sudo kubectl apply -f k8s/backend/
sudo kubectl apply -f k8s/frontend/
sudo kubectl apply -f k8s/prometheus/
sudo kubectl apply -f k8s/grafana/
sudo kubectl apply -f k8s/nginx/

# Vérification
echo "✅ Pods en cours de démarrage..."
sudo kubectl get pods -n collector

echo ""
echo "🎉 Application accessible sur :"
echo "Frontend  : http://localhost:30000"
echo "Backend   : http://localhost:30001"
echo "Keycloak  : http://localhost:30002"
echo "RabbitMQ  : http://localhost:30003"
echo "Prometheus: http://localhost:30004"
echo "Grafana   : http://localhost:30005"
echo "Nginx HTTPS : https://localhost:30443"