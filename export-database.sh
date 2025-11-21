#!/bin/bash

# Script export database từ SQL Server local
# Sử dụng: ./export-database.sh

# Màu sắc cho output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

echo -e "${GREEN}=== EXPORT DATABASE TỪ SQL SERVER LOCAL ===${NC}\n"

# Cấu hình database
DB_SERVER="localhost"
DB_PORT="1433"
DB_NAME="DATN_SD40"
DB_USER="sa"
DB_PASSWORD="Password.1"
OUTPUT_DIR="./database-export"
TIMESTAMP=$(date +"%Y%m%d_%H%M%S")
OUTPUT_FILE="${OUTPUT_DIR}/DATN_SD40_${TIMESTAMP}.bak"

# Tạo thư mục output
mkdir -p "$OUTPUT_DIR"

echo -e "${YELLOW}Thông tin database:${NC}"
echo "  Server: $DB_SERVER:$DB_PORT"
echo "  Database: $DB_NAME"
echo "  User: $DB_USER"
echo "  Output: $OUTPUT_FILE"
echo ""

# Kiểm tra sqlcmd có sẵn không
if ! command -v sqlcmd &> /dev/null; then
    echo -e "${RED}❌ sqlcmd không được tìm thấy!${NC}"
    echo ""
    echo "Cài đặt sqlcmd:"
    echo "  macOS: brew install mssql-tools"
    echo "  Hoặc tải từ: https://learn.microsoft.com/en-us/sql/tools/sqlcmd-utility"
    exit 1
fi

echo -e "${YELLOW}Đang export database...${NC}"

# Export database sang file .bak (backup file)
sqlcmd -S "$DB_SERVER,$DB_PORT" -U "$DB_USER" -P "$DB_PASSWORD" -Q "BACKUP DATABASE [$DB_NAME] TO DISK = '$OUTPUT_FILE' WITH FORMAT, INIT, NAME = 'Full Backup of $DB_NAME', SKIP, NOREWIND, NOUNLOAD, STATS = 10" 2>&1

if [ $? -eq 0 ]; then
    echo -e "${GREEN}✅ Export thành công!${NC}"
    echo "  File backup: $OUTPUT_FILE"
    echo ""
    echo -e "${YELLOW}Lưu ý:${NC}"
    echo "  - File .bak chỉ dùng để restore vào SQL Server khác"
    echo "  - Nếu hosting dùng PostgreSQL, cần chuyển đổi dữ liệu"
    echo "  - Xem file migrate-to-postgresql.sh để chuyển đổi"
else
    echo -e "${RED}❌ Export thất bại!${NC}"
    exit 1
fi

