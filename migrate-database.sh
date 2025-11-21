#!/bin/bash

# Script tổng hợp để migrate database từ local lên hosting
# Sử dụng: ./migrate-database.sh

# Màu sắc cho output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${GREEN}╔════════════════════════════════════════════════════════╗${NC}"
echo -e "${GREEN}║   MIGRATE DATABASE TỪ LOCAL LÊN HOSTING              ║${NC}"
echo -e "${GREEN}╚════════════════════════════════════════════════════════╝${NC}"
echo ""

# Menu
echo -e "${YELLOW}Chọn phương thức:${NC}"
echo "  1) Export từ SQL Server bằng Docker (Khuyến nghị cho macOS)"
echo "  2) Export từ SQL Server (BCP - cần cài mssql-tools)"
echo "  3) Export sang file SQL bằng Docker"
echo "  4) Export backup file (.bak)"
echo "  5) Import vào PostgreSQL"
echo "  6) Tự động: Export + Import"
echo "  7) Xem hướng dẫn"
echo ""
read -p "Nhập lựa chọn (1-7): " choice

case $choice in
    1)
        echo -e "\n${BLUE}=== EXPORT BẰNG DOCKER ===${NC}\n"
        ./export-with-docker.sh
        ;;
    2)
        echo -e "\n${BLUE}=== EXPORT BẰNG BCP ===${NC}\n"
        ./export-with-bcp.sh
        ;;
    3)
        echo -e "\n${BLUE}=== EXPORT SANG SQL BẰNG DOCKER ===${NC}\n"
        ./export-sql-with-docker.sh
        ;;
    4)
        echo -e "\n${BLUE}=== EXPORT BACKUP FILE ===${NC}\n"
        ./export-database.sh
        ;;
    5)
        echo -e "\n${BLUE}=== IMPORT VÀO POSTGRESQL ===${NC}\n"
        echo -e "${YELLOW}Cấu hình PostgreSQL:${NC}"
        read -p "Host: " PG_HOST
        read -p "Port [5432]: " PG_PORT
        PG_PORT=${PG_PORT:-5432}
        read -p "Database: " PG_DATABASE
        read -p "User: " PG_USER
        read -sp "Password: " PG_PASSWORD
        echo ""
        
        export PG_HOST PG_PORT PG_DATABASE PG_USER PG_PASSWORD
        ./import-to-postgresql.sh
        ;;
    6)
        echo -e "\n${BLUE}=== TỰ ĐỘNG EXPORT + IMPORT ===${NC}\n"
        
        # Export
        echo -e "${YELLOW}Bước 1: Export từ SQL Server...${NC}"
        if command -v docker &> /dev/null && docker ps | grep -q "datn-sqlserver"; then
            ./export-with-docker.sh
        else
            ./export-with-bcp.sh
        fi
        
        if [ $? -eq 0 ]; then
            echo -e "\n${YELLOW}Bước 2: Cấu hình PostgreSQL...${NC}"
            read -p "Host: " PG_HOST
            read -p "Port [5432]: " PG_PORT
            PG_PORT=${PG_PORT:-5432}
            read -p "Database: " PG_DATABASE
            read -p "User: " PG_USER
            read -sp "Password: " PG_PASSWORD
            echo ""
            
            export PG_HOST PG_PORT PG_DATABASE PG_USER PG_PASSWORD
            
            echo -e "\n${YELLOW}Bước 3: Import vào PostgreSQL...${NC}"
            ./import-to-postgresql.sh
        else
            echo -e "${RED}❌ Export thất bại!${NC}"
            exit 1
        fi
        ;;
    7)
        echo -e "\n${BLUE}=== HƯỚNG DẪN ===${NC}\n"
        cat DATABASE_MIGRATION_GUIDE.md | head -100
        echo ""
        echo -e "${YELLOW}Xem file DATABASE_MIGRATION_GUIDE.md để biết thêm chi tiết${NC}"
        ;;
    *)
        echo -e "${RED}Lựa chọn không hợp lệ!${NC}"
        exit 1
        ;;
esac

echo ""
echo -e "${GREEN}✅ Hoàn thành!${NC}"

