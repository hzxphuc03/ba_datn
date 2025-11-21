#!/bin/bash

# Script export database sang file SQL sử dụng Docker
# Sử dụng: ./export-sql-with-docker.sh

# Màu sắc cho output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${GREEN}=== EXPORT DATABASE SANG SQL BẰNG DOCKER ===${NC}\n"

# Cấu hình database
DB_SERVER="localhost"
DB_PORT="1433"
DB_NAME="DATN_SD40"
DB_USER="sa"
DB_PASSWORD="Password.1"
OUTPUT_DIR="./database-export"
TIMESTAMP=$(date +"%Y%m%d_%H%M%S")
OUTPUT_FILE="${OUTPUT_DIR}/DATN_SD40_${TIMESTAMP}.sql"

# Tạo thư mục output
mkdir -p "$OUTPUT_DIR"

echo -e "${YELLOW}Thông tin database:${NC}"
echo "  Server: $DB_SERVER:$DB_PORT"
echo "  Database: $DB_NAME"
echo "  User: $DB_USER"
echo "  Output: $OUTPUT_FILE"
echo ""

# Kiểm tra Docker
if ! command -v docker &> /dev/null; then
    echo -e "${RED}❌ Docker không được tìm thấy!${NC}"
    exit 1
fi

# Kiểm tra SQL Server container
if ! docker ps | grep -q "datn-sqlserver"; then
    echo -e "${YELLOW}⚠ SQL Server container chưa chạy. Đang khởi động...${NC}"
    docker-compose up -d sqlserver
    sleep 10
fi

echo -e "${YELLOW}Đang export schema và dữ liệu...${NC}"

# Tạo file SQL
echo "-- Database Export: $DB_NAME" > "$OUTPUT_FILE"
echo "-- Generated: $(date)" >> "$OUTPUT_FILE"
echo "-- Server: $DB_SERVER:$DB_PORT" >> "$OUTPUT_FILE"
echo "" >> "$OUTPUT_FILE"

# Export schema (cấu trúc bảng)
echo -e "${BLUE}Exporting schema...${NC}"
docker exec datn-sqlserver /opt/mssql-tools/bin/sqlcmd \
    -S localhost \
    -U "$DB_USER" \
    -P "$DB_PASSWORD" \
    -d "$DB_NAME" \
    -Q "
SET NOCOUNT ON;
SELECT 
    'CREATE TABLE ' + QUOTENAME(TABLE_SCHEMA) + '.' + QUOTENAME(TABLE_NAME) + ' (' + CHAR(13) + CHAR(10) +
    STUFF((
        SELECT ', ' + CHAR(13) + CHAR(10) + '    ' + 
            QUOTENAME(COLUMN_NAME) + ' ' + 
            DATA_TYPE + 
            CASE 
                WHEN CHARACTER_MAXIMUM_LENGTH IS NOT NULL THEN '(' + 
                    CASE WHEN CHARACTER_MAXIMUM_LENGTH = -1 THEN 'MAX' ELSE CAST(CHARACTER_MAXIMUM_LENGTH AS VARCHAR) END + ')'
                WHEN NUMERIC_PRECISION IS NOT NULL AND NUMERIC_SCALE IS NOT NULL THEN 
                    '(' + CAST(NUMERIC_PRECISION AS VARCHAR) + ',' + CAST(NUMERIC_SCALE AS VARCHAR) + ')'
                WHEN NUMERIC_PRECISION IS NOT NULL THEN '(' + CAST(NUMERIC_PRECISION AS VARCHAR) + ')'
                ELSE ''
            END +
            CASE WHEN IS_NULLABLE = 'NO' THEN ' NOT NULL' ELSE ' NULL' END
        FROM INFORMATION_SCHEMA.COLUMNS
        WHERE TABLE_SCHEMA = t.TABLE_SCHEMA AND TABLE_NAME = t.TABLE_NAME
        ORDER BY ORDINAL_POSITION
        FOR XML PATH(''), TYPE
    ).value('.', 'NVARCHAR(MAX)'), 1, 2, '') + CHAR(13) + CHAR(10) + ');'
FROM INFORMATION_SCHEMA.TABLES t
WHERE TABLE_TYPE = 'BASE TABLE'
ORDER BY TABLE_SCHEMA, TABLE_NAME;
" -h -1 -W >> "$OUTPUT_FILE" 2>&1

echo "" >> "$OUTPUT_FILE"
echo "-- Data Export" >> "$OUTPUT_FILE"
echo "" >> "$OUTPUT_FILE"

# Export data từ từng bảng
echo -e "${BLUE}Exporting data...${NC}"

# Lấy danh sách tables
docker exec datn-sqlserver /opt/mssql-tools/bin/sqlcmd \
    -S localhost \
    -U "$DB_USER" \
    -P "$DB_PASSWORD" \
    -d "$DB_NAME" \
    -Q "SELECT TABLE_SCHEMA + '.' + TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_TYPE = 'BASE TABLE' ORDER BY TABLE_SCHEMA, TABLE_NAME;" \
    -h -1 -W -s "," > "${OUTPUT_DIR}/tables_temp.txt" 2>&1

TABLE_COUNT=0
while IFS= read -r table; do
    if [ ! -z "$table" ] && [[ ! "$table" =~ ^-+$ ]] && [[ ! "$table" =~ ^TABLE_NAME$ ]]; then
        TABLE_COUNT=$((TABLE_COUNT + 1))
        echo -e "  ${BLUE}[$TABLE_COUNT]${NC} $table"
        
        # Export data dạng INSERT statements (đơn giản)
        echo "-- Data for $table" >> "$OUTPUT_FILE"
        docker exec datn-sqlserver /opt/mssql-tools/bin/sqlcmd \
            -S localhost \
            -U "$DB_USER" \
            -P "$DB_PASSWORD" \
            -d "$DB_NAME" \
            -Q "SELECT * FROM $table;" \
            -h -1 -W -s "," >> "$OUTPUT_FILE" 2>&1
        echo "" >> "$OUTPUT_FILE"
    fi
done < "${OUTPUT_DIR}/tables_temp.txt"

rm -f "${OUTPUT_DIR}/tables_temp.txt"

echo ""
echo -e "${GREEN}✅ Export thành công!${NC}"
echo "  File SQL: $OUTPUT_FILE"
echo "  Đã export $TABLE_COUNT bảng"
echo ""
echo -e "${YELLOW}Lưu ý:${NC}"
echo "  - File SQL này chứa schema và dữ liệu"
echo "  - Có thể cần chỉnh sửa để tương thích với PostgreSQL"
echo "  - Xem file DATABASE_MIGRATION_GUIDE.md để biết thêm"

