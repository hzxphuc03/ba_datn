#!/bin/bash

# Script để đẩy dự án lên Git repository mới
# Sử dụng: ./setup-new-repo.sh YOUR_REPO_URL

if [ -z "$1" ]; then
    echo "❌ Lỗi: Chưa cung cấp URL repository mới"
    echo "Cách sử dụng: ./setup-new-repo.sh YOUR_REPO_URL"
    echo "Ví dụ: ./setup-new-repo.sh git@github.com:username/ba_datn.git"
    exit 1
fi

NEW_REPO_URL=$1

echo "🚀 Bắt đầu thiết lập repository mới..."
echo "📦 Repository URL: $NEW_REPO_URL"
echo ""

# Kiểm tra xem có thay đổi chưa commit không
if [ -n "$(git status --porcelain)" ]; then
    echo "📝 Phát hiện thay đổi chưa commit"
    read -p "Bạn có muốn commit tất cả thay đổi? (y/n): " -n 1 -r
    echo
    if [[ $REPLY =~ ^[Yy]$ ]]; then
        git add .
        git commit -m "Update project - prepare for new repository"
        echo "✅ Đã commit thay đổi"
    else
        echo "⚠️  Bỏ qua commit. Tiếp tục với thay đổi hiện tại..."
    fi
fi

# Xóa remote cũ
echo ""
echo "🔄 Xóa remote cũ..."
git remote remove origin 2>/dev/null || echo "   (Không có remote cũ)"

# Thêm remote mới
echo "➕ Thêm remote mới..."
git remote add origin "$NEW_REPO_URL"

# Kiểm tra remote
echo ""
echo "📋 Kiểm tra remote:"
git remote -v

# Xác nhận push
echo ""
read -p "Bạn có muốn push code lên repository mới ngay bây giờ? (y/n): " -n 1 -r
echo
if [[ $REPLY =~ ^[Yy]$ ]]; then
    # Lấy tên branch hiện tại
    CURRENT_BRANCH=$(git branch --show-current)
    echo ""
    echo "📤 Đang push lên branch: $CURRENT_BRANCH"
    
    # Thử push
    if git push -u origin "$CURRENT_BRANCH"; then
        echo ""
        echo "✅ Thành công! Code đã được push lên repository mới"
        echo "🔗 Repository: $NEW_REPO_URL"
    else
        echo ""
        echo "❌ Lỗi khi push. Có thể cần:"
        echo "   1. Kiểm tra quyền truy cập repository"
        echo "   2. Kiểm tra SSH key hoặc credentials"
        echo "   3. Thử push thủ công: git push -u origin $CURRENT_BRANCH"
    fi
else
    echo ""
    echo "ℹ️  Bạn có thể push sau bằng lệnh:"
    echo "   git push -u origin $(git branch --show-current)"
fi

echo ""
echo "✨ Hoàn tất!"

