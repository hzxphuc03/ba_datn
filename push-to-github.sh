#!/bin/bash

# Script để push code lên GitHub
# Sử dụng: ./push-to-github.sh

echo "🚀 Đang push code lên GitHub..."
echo "📦 Repository: https://github.com/hzxphuc03/ba_datn.git"
echo ""

# Kiểm tra branch
CURRENT_BRANCH=$(git branch --show-current)
echo "📍 Branch hiện tại: $CURRENT_BRANCH"

# Đảm bảo đang ở branch main
if [ "$CURRENT_BRANCH" != "main" ]; then
    echo "⚠️  Đang đổi branch sang main..."
    git branch -M main
fi

# Push code
echo ""
echo "📤 Đang push code..."
git push -u origin main

if [ $? -eq 0 ]; then
    echo ""
    echo "✅ Thành công! Code đã được push lên GitHub"
    echo "🔗 Xem tại: https://github.com/hzxphuc03/ba_datn"
else
    echo ""
    echo "❌ Lỗi khi push. Có thể cần:"
    echo "   1. Nhập username: hzxphuc03"
    echo "   2. Nhập Personal Access Token (không phải mật khẩu)"
    echo "   3. Tạo token tại: https://github.com/settings/tokens"
    echo ""
    echo "Hoặc thử push lại với token trong URL:"
    echo "   git remote set-url origin https://YOUR_TOKEN@github.com/hzxphuc03/ba_datn.git"
    echo "   git push -u origin main"
fi

