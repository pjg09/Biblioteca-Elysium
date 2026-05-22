#!/bin/bash
set -e

echo "================================================"
echo "  SISTEMA DE BIBLIOTECA — MICROSERVICIOS"
echo "================================================"

# Verificar Docker
if ! docker info > /dev/null 2>&1; then
    echo "ERROR: Docker no está corriendo o no tienes permisos."
    echo ""
    echo "  Solución:"
    echo "    sudo usermod -aG docker \$USER && newgrp docker"
    echo ""
    echo "  Luego cierra sesión y vuelve a entrar, o ejecuta 'newgrp docker'."
    exit 1
fi

# Build todas las imágenes (Maven corre dentro del contenedor)
echo "[1/3] Construyendo imágenes Docker..."
docker compose --profile cli build

# Levantar stack en background (sin cli)
echo "[2/3] Levantando stack..."
docker compose up -d

# Esperar a que circulacion-service esté listo (es el último en arrancar)
echo "[3/3] Esperando que el stack esté listo..."

# Usar curl o wget según disponibilidad
if command -v curl &> /dev/null; then
    HEALTH_CMD="curl -sf http://localhost:8081/actuator/health"
elif command -v wget &> /dev/null; then
    HEALTH_CMD="wget -qO- http://localhost:8081/actuator/health"
else
    echo "Advertencia: ni curl ni wget están instalados. Esperando 60s..."
    sleep 60
    echo "Continuando..."
    docker compose --profile cli run --rm cli-service
    exit 0
fi

counter=0
until $HEALTH_CMD > /dev/null 2>&1; do
    printf "."
    sleep 3
    counter=$((counter + 1))
    if [ $counter -gt 60 ]; then
        echo ""
        echo "Time out: circulacion-service no respondió después de 3 minutos."
        echo "Revisa los logs con: docker compose logs circulacion-service"
        exit 1
    fi
done
echo ""
echo "Stack listo."
echo ""

# Lanzar el CLI interactivo
docker compose --profile cli run --rm cli-service
