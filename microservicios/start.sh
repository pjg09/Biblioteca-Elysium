#!/bin/bash
set -e

echo "================================================"
echo "  SISTEMA DE BIBLIOTECA — MICROSERVICIOS"
echo "================================================"

# Build JARs con Maven
echo "[1/4] Compilando JARs con Maven..."
mvn clean package -DskipTests

# Build todas las imágenes incluyendo cli-service
echo "[2/4] Construyendo imágenes Docker..."
docker compose --profile cli build

# Levantar stack en background (sin cli)
echo "[3/4] Levantando stack..."
docker compose up -d

# Esperar a que circulacion-service esté listo (es el último en arrancar)
echo "[4/4] Esperando que el stack esté listo..."
until curl -sf http://localhost:8081/actuator/health > /dev/null 2>&1; do
    printf "."
    sleep 3
done
echo ""
echo "Stack listo."
echo ""

# Lanzar el CLI interactivo
docker compose --profile cli run --rm cli-service
