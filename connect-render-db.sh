#!/bin/bash

# Script kết nối PostgreSQL trên Render
# Database: ba_datn_db
# Host: dpg-d4emvkshg0os738ftv4g-a.oregon-postgres.render.com

# Thông tin kết nối
DB_HOST="dpg-d4emvkshg0os738ftv4g-a.oregon-postgres.render.com"
DB_PORT="5432"
DB_NAME="ba_datn_db"
DB_USER="ba_datn_db_user"
DB_PASSWORD="lNxER7uBbNYw60imnHx6coVCKyiUvSDH"

echo "🔌 Đang kết nối đến PostgreSQL trên Render..."
echo "Host: $DB_HOST"
echo "Database: $DB_NAME"
echo ""

# Kết nối database
PGPASSWORD=$DB_PASSWORD psql -h $DB_HOST -p $DB_PORT -U $DB_USER -d $DB_NAME

