#!/bin/bash

# Script để chạy Docker local testing
# Usage: ./run-docker-local.sh [command]
# Commands: up, down, restart, logs, rebuild

set -e

COMPOSE_FILE="docker-compose.local.yml"

case "$1" in
  up)
    echo "🚀 Starting Docker containers..."
    docker-compose -f $COMPOSE_FILE up -d --build
    echo "✅ Containers started!"
    echo "📊 Checking status..."
    docker-compose -f $COMPOSE_FILE ps
    echo ""
    echo "📝 View logs with: ./run-docker-local.sh logs"
    echo "🌐 API available at: http://localhost:8080"
    echo "📚 Swagger UI: http://localhost:8080/swagger-ui.html"
    ;;
  down)
    echo "🛑 Stopping Docker containers..."
    docker-compose -f $COMPOSE_FILE down
    echo "✅ Containers stopped!"
    ;;
  restart)
    echo "🔄 Restarting Docker containers..."
    docker-compose -f $COMPOSE_FILE restart
    echo "✅ Containers restarted!"
    ;;
  logs)
    echo "📝 Showing logs (Ctrl+C to exit)..."
    docker-compose -f $COMPOSE_FILE logs -f
    ;;
  logs-app)
    echo "📝 Showing app logs (Ctrl+C to exit)..."
    docker-compose -f $COMPOSE_FILE logs -f app
    ;;
  logs-db)
    echo "📝 Showing database logs (Ctrl+C to exit)..."
    docker-compose -f $COMPOSE_FILE logs -f sqlserver
    ;;
  rebuild)
    echo "🔨 Rebuilding containers..."
    docker-compose -f $COMPOSE_FILE down
    docker-compose -f $COMPOSE_FILE build --no-cache
    docker-compose -f $COMPOSE_FILE up -d
    echo "✅ Containers rebuilt and started!"
    ;;
  status)
    echo "📊 Container status:"
    docker-compose -f $COMPOSE_FILE ps
    ;;
  clean)
    echo "🧹 Cleaning up (removing containers and volumes)..."
    read -p "⚠️  This will delete all data. Continue? (y/N) " -n 1 -r
    echo
    if [[ $REPLY =~ ^[Yy]$ ]]; then
      docker-compose -f $COMPOSE_FILE down -v
      echo "✅ Cleaned up!"
    else
      echo "❌ Cancelled."
    fi
    ;;
  test)
    echo "🧪 Testing API endpoints..."
    echo ""
    echo "1. Health check:"
    curl -s http://localhost:8080/actuator/health | jq . || echo "❌ Health check failed"
    echo ""
    echo "2. Swagger UI available at: http://localhost:8080/swagger-ui.html"
    echo ""
    echo "3. Test login (replace username/password):"
    echo "curl -X POST http://localhost:8080/api/auth/login/nhan-vien \\"
    echo "  -H 'Content-Type: application/json' \\"
    echo "  -d '{\"username\":\"your_username\",\"pass\":\"your_password\"}'"
    ;;
  *)
    echo "🐳 Docker Local Testing Helper"
    echo ""
    echo "Usage: ./run-docker-local.sh [command]"
    echo ""
    echo "Commands:"
    echo "  up        - Start containers (build if needed)"
    echo "  down      - Stop containers"
    echo "  restart   - Restart containers"
    echo "  logs      - Show all logs (follow mode)"
    echo "  logs-app  - Show app logs only"
    echo "  logs-db   - Show database logs only"
    echo "  rebuild   - Rebuild containers from scratch"
    echo "  status    - Show container status"
    echo "  clean     - Remove containers and volumes (⚠️ deletes data)"
    echo "  test      - Test API endpoints"
    echo ""
    exit 1
    ;;
esac

