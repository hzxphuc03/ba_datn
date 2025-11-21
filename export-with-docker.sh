#!/bin/bash

# Script export database từ SQL Server sử dụng Docker
# Không cần cài đặt mssql-tools trên máy
# Sử dụng: ./export-with-docker.sh

# Màu sắc cho output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${GREEN}=== EXPORT DATABASE BẰNG DOCKER ===${NC}\n"

# Cấu hình database
DB_SERVER="localhost"
DB_PORT="1433"
DB_NAME="DATN_SD40"
DB_USER="sa"
DB_PASSWORD="Password.1"
OUTPUT_DIR="./database-export/bcp-export"
TIMESTAMP=$(date +"%Y%m%d_%H%M%S")

# Tạo thư mục output
mkdir -p "$OUTPUT_DIR"

echo -e "${YELLOW}Thông tin database:${NC}"
echo "  Server: $DB_SERVER:$DB_PORT"
echo "  Database: $DB_NAME"
echo "  User: $DB_USER"
echo "  Output: $OUTPUT_DIR"
echo ""

# Kiểm tra Docker
if ! command -v docker &> /dev/null; then
    echo -e "${RED}❌ Docker không được tìm thấy!${NC}"
    echo ""
    echo "Cài đặt Docker:"
    echo "  macOS: https://www.docker.com/products/docker-desktop"
    exit 1
fi

# Kiểm tra SQL Server container có đang chạy không
if ! docker ps | grep -q "datn-sqlserver"; then
    echo -e "${YELLOW}⚠ SQL Server container chưa chạy. Đang khởi động...${NC}"
    docker-compose up -d sqlserver
    
    # Đợi SQL Server sẵn sàng
    echo -e "${YELLOW}Đang đợi SQL Server khởi động...${NC}"
    sleep 10
    
    # Kiểm tra lại
    if ! docker ps | grep -q "datn-sqlserver"; then
        echo -e "${RED}❌ Không thể khởi động SQL Server container!${NC}"
        exit 1
    fi
fi

echo -e "${GREEN}✓ SQL Server container đang chạy${NC}\n"

# Lấy danh sách tables bằng Docker
echo -e "${YELLOW}Đang lấy danh sách bảng...${NC}"

docker exec datn-sqlserver /opt/mssql-tools/bin/sqlcmd \
    -S localhost \
    -U "$DB_USER" \
    -P "$DB_PASSWORD" \
    -d "$DB_NAME" \
    -Q "SELECT TABLE_SCHEMA + '.' + TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_TYPE = 'BASE TABLE' ORDER BY TABLE_SCHEMA, TABLE_NAME;" \
    -h -1 -W -s "," > "${OUTPUT_DIR}/tables.txt" 2>&1

if [ $? -ne 0 ]; then
    echo -e "${RED}❌ Không thể lấy danh sách bảng!${NC}"
    echo "Kiểm tra lại thông tin kết nối database"
    exit 1
fi

# Export từng bảng
echo -e "${YELLOW}Đang export dữ liệu từ các bảng...${NC}\n"

TABLE_COUNT=0
while IFS= read -r table; do
    if [ ! -z "$table" ] && [[ ! "$table" =~ ^-+$ ]] && [[ ! "$table" =~ ^TABLE_NAME$ ]]; then
        TABLE_COUNT=$((TABLE_COUNT + 1))
        # Tạo tên file an toàn
        SAFE_TABLE_NAME=$(echo "$table" | tr '.' '_' | tr ' ' '_' | tr '-' '_')
        OUTPUT_FILE="${OUTPUT_DIR}/${SAFE_TABLE_NAME}.csv"
        
        echo -e "${BLUE}[$TABLE_COUNT] Exporting: $table${NC}"
        
        # Export bằng BCP qua Docker
        docker exec datn-sqlserver /opt/mssql-tools/bin/bcp \
            "$table" out "/tmp/${SAFE_TABLE_NAME}.csv" \
            -c -t"," \
            -S localhost \
            -U "$DB_USER" \
            -P "$DB_PASSWORD" \
            -d "$DB_NAME" 2>&1
        
        if [ $? -eq 0 ]; then
            # Copy file từ container ra host
            docker cp "datn-sqlserver:/tmp/${SAFE_TABLE_NAME}.csv" "$OUTPUT_FILE"
            
            # Xóa file trong container
            docker exec datn-sqlserver rm -f "/tmp/${SAFE_TABLE_NAME}.csv"
            
            if [ -f "$OUTPUT_FILE" ]; then
                echo -e "  ${GREEN}✓${NC} Đã export: $OUTPUT_FILE ($(wc -l < "$OUTPUT_FILE" | tr -d ' ') dòng)"
            else
                echo -e "  ${RED}✗${NC} Lỗi khi copy file: $table"
            fi
        else
            echo -e "  ${RED}✗${NC} Lỗi khi export: $table"
        fi
    fi
done < "${OUTPUT_DIR}/tables.txt"

echo ""
echo -e "${GREEN}✅ Hoàn thành!${NC}"
echo "  Đã export $TABLE_COUNT bảng"
echo "  Thư mục: $OUTPUT_DIR"
echo ""
echo -e "${YELLOW}Lưu ý:${NC}"
echo "  - Mỗi bảng được export thành file CSV riêng"
echo "  - Có thể import vào PostgreSQL bằng COPY hoặc pgAdmin"
echo "  - Chạy ./import-to-postgresql.sh để import tự động"

