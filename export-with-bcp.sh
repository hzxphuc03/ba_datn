#!/bin/bash

# Script export database từ SQL Server sử dụng BCP (Bulk Copy Program)
# Export từng bảng riêng biệt, dễ import vào PostgreSQL
# Sử dụng: ./export-with-bcp.sh

# Màu sắc cho output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${GREEN}=== EXPORT DATABASE BẰNG BCP ===${NC}\n"

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

# Kiểm tra bcp có sẵn không
if ! command -v bcp &> /dev/null; then
    echo -e "${RED}❌ bcp không được tìm thấy!${NC}"
    echo ""
    echo "Cài đặt bcp (thường đi kèm với sqlcmd):"
    echo "  macOS: brew install mssql-tools"
    exit 1
fi

# Lấy danh sách tables
echo -e "${YELLOW}Đang lấy danh sách bảng...${NC}"
sqlcmd -S "$DB_SERVER,$DB_PORT" -U "$DB_USER" -P "$DB_PASSWORD" -d "$DB_NAME" -Q "
SELECT TABLE_SCHEMA + '.' + TABLE_NAME 
FROM INFORMATION_SCHEMA.TABLES 
WHERE TABLE_TYPE = 'BASE TABLE'
ORDER BY TABLE_SCHEMA, TABLE_NAME;
" -h -1 -W -s "," > "${OUTPUT_DIR}/tables.txt" 2>&1

if [ $? -ne 0 ]; then
    echo -e "${RED}❌ Không thể lấy danh sách bảng!${NC}"
    exit 1
fi

# Export từng bảng
echo -e "${YELLOW}Đang export dữ liệu từ các bảng...${NC}\n"

TABLE_COUNT=0
while IFS= read -r table; do
    if [ ! -z "$table" ] && [[ ! "$table" =~ ^-+$ ]]; then
        TABLE_COUNT=$((TABLE_COUNT + 1))
        # Tạo tên file an toàn
        SAFE_TABLE_NAME=$(echo "$table" | tr '.' '_' | tr ' ' '_')
        OUTPUT_FILE="${OUTPUT_DIR}/${SAFE_TABLE_NAME}.csv"
        
        echo -e "${BLUE}[$TABLE_COUNT] Exporting: $table${NC}"
        
        # Export bằng BCP
        bcp "$table" out "$OUTPUT_FILE" -c -t"," -S "$DB_SERVER,$DB_PORT" -U "$DB_USER" -P "$DB_PASSWORD" -d "$DB_NAME" 2>&1
        
        if [ $? -eq 0 ]; then
            echo -e "  ${GREEN}✓${NC} Đã export: $OUTPUT_FILE"
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
echo "  - Xem file import-to-postgresql.sh để import tự động"

