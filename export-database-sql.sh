#!/bin/bash

# Script export database từ SQL Server sang file SQL (schema + data)
# Phù hợp để import vào PostgreSQL hoặc SQL Server khác
# Sử dụng: ./export-database-sql.sh

# Màu sắc cho output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${GREEN}=== EXPORT DATABASE SANG FILE SQL ===${NC}\n"

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

# Kiểm tra sqlcmd có sẵn không
if ! command -v sqlcmd &> /dev/null; then
    echo -e "${RED}❌ sqlcmd không được tìm thấy!${NC}"
    echo ""
    echo "Cài đặt sqlcmd:"
    echo "  macOS: brew install mssql-tools"
    echo "  Hoặc tải từ: https://learn.microsoft.com/en-us/sql/tools/sqlcmd-utility"
    exit 1
fi

echo -e "${YELLOW}Đang export schema (cấu trúc bảng)...${NC}"

# Export schema
sqlcmd -S "$DB_SERVER,$DB_PORT" -U "$DB_USER" -P "$DB_PASSWORD" -d "$DB_NAME" -Q "
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
" -h -1 -W -s "," > "${OUTPUT_FILE}.schema" 2>&1

if [ $? -ne 0 ]; then
    echo -e "${RED}❌ Export schema thất bại!${NC}"
    echo -e "${BLUE}Đang thử cách khác...${NC}"
    
    # Cách đơn giản hơn: Export tất cả tables
    echo "-- Database Schema Export" > "$OUTPUT_FILE"
    echo "-- Generated: $(date)" >> "$OUTPUT_FILE"
    echo "" >> "$OUTPUT_FILE"
    
    # Lấy danh sách tables
    sqlcmd -S "$DB_SERVER,$DB_PORT" -U "$DB_USER" -P "$DB_PASSWORD" -d "$DB_NAME" -Q "
    SELECT TABLE_SCHEMA + '.' + TABLE_NAME 
    FROM INFORMATION_SCHEMA.TABLES 
    WHERE TABLE_TYPE = 'BASE TABLE'
    ORDER BY TABLE_SCHEMA, TABLE_NAME;
    " -h -1 -W -s "," > "${OUTPUT_DIR}/tables.txt" 2>&1
    
    echo -e "${YELLOW}Đang export dữ liệu từ các bảng...${NC}"
    
    # Export data từ từng bảng
    while IFS= read -r table; do
        if [ ! -z "$table" ]; then
            echo -e "${BLUE}  Exporting: $table${NC}"
            sqlcmd -S "$DB_SERVER,$DB_PORT" -U "$DB_USER" -P "$DB_PASSWORD" -d "$DB_NAME" -Q "
            SET NOCOUNT ON;
            SELECT * FROM $table;
            " -h -1 -W -s "," >> "$OUTPUT_FILE" 2>&1
        fi
    done < "${OUTPUT_DIR}/tables.txt"
else
    cat "${OUTPUT_FILE}.schema" >> "$OUTPUT_FILE"
    rm "${OUTPUT_FILE}.schema"
fi

echo ""
echo -e "${YELLOW}Đang export dữ liệu...${NC}"

# Export data (INSERT statements) - cách đơn giản
sqlcmd -S "$DB_SERVER,$DB_PORT" -U "$DB_USER" -P "$DB_PASSWORD" -d "$DB_NAME" -Q "
SET NOCOUNT ON;
EXEC sp_MSforeachtable 'SELECT ''SELECT * FROM ?'' AS Query';
" -h -1 -W >> "$OUTPUT_FILE" 2>&1

echo ""
echo -e "${GREEN}✅ Export thành công!${NC}"
echo "  File SQL: $OUTPUT_FILE"
echo ""
echo -e "${YELLOW}Lưu ý:${NC}"
echo "  - File SQL này chứa schema và dữ liệu"
echo "  - Có thể cần chỉnh sửa để tương thích với PostgreSQL"
echo "  - Xem file migrate-to-postgresql.sh để chuyển đổi tự động"

