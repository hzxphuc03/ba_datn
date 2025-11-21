#!/bin/bash

# Script import database vào PostgreSQL
# Sử dụng: ./import-to-postgresql.sh

# Màu sắc cho output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${GREEN}=== IMPORT DATABASE VÀO POSTGRESQL ===${NC}\n"

# Cấu hình PostgreSQL (thay đổi theo hosting của bạn)
PG_HOST="${PG_HOST:-localhost}"
PG_PORT="${PG_PORT:-5432}"
PG_DATABASE="${PG_DATABASE:-ba_datn_db_demo}"
PG_USER="${PG_USER:-ba_datn_db_demo_user}"
PG_PASSWORD="${PG_PASSWORD:-}"

# File CSV từ export
CSV_DIR="./database-export/bcp-export"

echo -e "${YELLOW}Cấu hình PostgreSQL:${NC}"
echo "  Host: $PG_HOST"
echo "  Port: $PG_PORT"
echo "  Database: $PG_DATABASE"
echo "  User: $PG_USER"
echo "  CSV Directory: $CSV_DIR"
echo ""

# Kiểm tra psql có sẵn không
if ! command -v psql &> /dev/null; then
    echo -e "${RED}❌ psql không được tìm thấy!${NC}"
    echo ""
    echo "Cài đặt PostgreSQL client:"
    echo "  macOS: brew install postgresql"
    exit 1
fi

# Kiểm tra thư mục CSV
if [ ! -d "$CSV_DIR" ]; then
    echo -e "${RED}❌ Thư mục CSV không tồn tại: $CSV_DIR${NC}"
    echo "  Chạy ./export-with-bcp.sh trước để export dữ liệu"
    exit 1
fi

# Set password
export PGPASSWORD="$PG_PASSWORD"

# Test connection
echo -e "${YELLOW}Đang kiểm tra kết nối...${NC}"
psql -h "$PG_HOST" -p "$PG_PORT" -U "$PG_USER" -d "$PG_DATABASE" -c "SELECT version();" > /dev/null 2>&1

if [ $? -ne 0 ]; then
    echo -e "${RED}❌ Không thể kết nối đến PostgreSQL!${NC}"
    echo "  Kiểm tra lại thông tin kết nối"
    exit 1
fi

echo -e "${GREEN}✓ Kết nối thành công!${NC}\n"

# Import từng file CSV
echo -e "${YELLOW}Đang import dữ liệu...${NC}\n"

IMPORT_COUNT=0
for csv_file in "$CSV_DIR"/*.csv; do
    if [ -f "$csv_file" ]; then
        IMPORT_COUNT=$((IMPORT_COUNT + 1))
        # Lấy tên bảng từ tên file (bỏ .csv)
        TABLE_NAME=$(basename "$csv_file" .csv)
        # Chuyển đổi tên bảng từ dbo_table thành table (bỏ schema prefix)
        TABLE_NAME=$(echo "$TABLE_NAME" | sed 's/^dbo_//')
        
        echo -e "${BLUE}[$IMPORT_COUNT] Importing: $TABLE_NAME${NC}"
        
        # Import CSV vào PostgreSQL
        psql -h "$PG_HOST" -p "$PG_PORT" -U "$PG_USER" -d "$PG_DATABASE" <<EOF
\copy "$TABLE_NAME" FROM '$csv_file' WITH (FORMAT csv, DELIMITER ',', HEADER false, ENCODING 'UTF8');
EOF
        
        if [ $? -eq 0 ]; then
            echo -e "  ${GREEN}✓${NC} Đã import: $TABLE_NAME"
        else
            echo -e "  ${YELLOW}⚠${NC} Có thể bảng $TABLE_NAME chưa tồn tại hoặc có lỗi"
        fi
    fi
done

echo ""
echo -e "${GREEN}✅ Hoàn thành!${NC}"
echo "  Đã import $IMPORT_COUNT bảng"
echo ""
echo -e "${YELLOW}Lưu ý:${NC}"
echo "  - Đảm bảo schema đã được tạo trước (Hibernate sẽ tự tạo nếu ddl-auto=update)"
echo "  - Kiểm tra lại dữ liệu sau khi import"
echo "  - Một số kiểu dữ liệu có thể cần chuyển đổi thủ công"

