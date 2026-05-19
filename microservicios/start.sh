#!/bin/bash
set -e

echo "================================================"
echo "  SISTEMA DE BIBLIOTECA — MICROSERVICIOS"
echo "================================================"

# Build todas las imágenes incluyendo cli-service
echo "[1/3] Construyendo imágenes..."
docker compose --profile cli build

# Levantar stack en background (sin cli)
echo "[2/3] Levantando stack..."
docker compose up -d

# Esperar a que circulacion-service esté listo (es el último en arrancar)
echo "[3/3] Esperando que el stack esté listo..."
until curl -sf http://localhost:8081/actuator/health > /dev/null 2>&1; do
    printf "."
    sleep 3
done
echo ""
echo "Stack listo."
echo ""

# Lanzar el CLI interactivo
docker compose --profile cli run --rm cli-service
