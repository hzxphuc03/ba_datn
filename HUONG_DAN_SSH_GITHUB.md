# 🔐 Hướng Dẫn Kiểm Tra và Cấu Hình SSH Key cho GitHub

## Cách 1: Kiểm Tra Nhanh (Khuyến Nghị) ⚡

### Kiểm tra SSH key đã tồn tại chưa:
```bash
ls -la ~/.ssh
```

**Kết quả:**
- ✅ **Đã có SSH key** nếu thấy các file như:
  - `id_rsa` và `id_rsa.pub` (RSA key)
  - `id_ed25519` và `id_ed25519.pub` (Ed25519 key - khuyến nghị)
  - `id_ecdsa` và `id_ecdsa.pub` (ECDSA key)

- ❌ **Chưa có SSH key** nếu chỉ thấy:
  - `known_hosts`
  - Hoặc thư mục rỗng

### Test kết nối GitHub:
```bash
ssh -T git@github.com
```

**Kết quả:**
- ✅ **Đã cấu hình đúng** nếu thấy:
  ```
  Hi username! You've successfully authenticated, but GitHub does not provide shell access.
  ```

- ❌ **Chưa cấu hình** nếu thấy:
  ```
  Permission denied (publickey)
  ```
  hoặc
  ```
  Host key verification failed
  ```

---

## Cách 2: Kiểm Tra SSH Key Đã Thêm Vào GitHub Chưa

### Xem public key hiện có:
```bash
# Nếu dùng Ed25519 (khuyến nghị)
cat ~/.ssh/id_ed25519.pub

# Hoặc nếu dùng RSA
cat ~/.ssh/id_rsa.pub
```

### Kiểm tra trên GitHub:
1. Vào: https://github.com/settings/keys
2. Xem danh sách SSH keys
3. So sánh với key trong terminal

---

## 📝 Cấu Hình SSH Key (Nếu Chưa Có)

### Bước 1: Tạo SSH Key Mới

#### Tạo Ed25519 key (Khuyến nghị - bảo mật cao hơn):
```bash
ssh-keygen -t ed25519 -C "your_email@example.com"
```

**Lưu ý khi tạo:**
- Nhấn Enter để dùng đường dẫn mặc định: `~/.ssh/id_ed25519`
- Nhập passphrase (mật khẩu bảo vệ key) - có thể để trống
- Nhập lại passphrase để xác nhận

#### Hoặc tạo RSA key (nếu hệ thống không hỗ trợ Ed25519):
```bash
ssh-keygen -t rsa -b 4096 -C "your_email@example.com"
```

### Bước 2: Khởi Động SSH Agent

```bash
# Khởi động ssh-agent
eval "$(ssh-agent -s)"

# Thêm SSH key vào agent
ssh-add ~/.ssh/id_ed25519
# Hoặc nếu dùng RSA:
# ssh-add ~/.ssh/id_rsa
```

### Bước 3: Copy Public Key

```bash
# Copy public key (Ed25519)
cat ~/.ssh/id_ed25519.pub | pbcopy

# Hoặc copy thủ công
cat ~/.ssh/id_ed25519.pub
```

**Lưu ý:** Trên macOS, lệnh `pbcopy` sẽ copy vào clipboard. Trên Linux, dùng:
```bash
cat ~/.ssh/id_ed25519.pub | xclip -selection clipboard
# hoặc
cat ~/.ssh/id_ed25519.pub
# rồi copy thủ công
```

### Bước 4: Thêm SSH Key Vào GitHub

1. **Truy cập GitHub Settings:**
   - Vào: https://github.com/settings/keys
   - Hoặc: GitHub → Profile → Settings → SSH and GPG keys

2. **Thêm SSH Key:**
   - Click nút **"New SSH key"** hoặc **"Add SSH key"**
   - **Title**: Đặt tên dễ nhớ (ví dụ: "MacBook Pro", "Work Laptop")
   - **Key**: Paste public key đã copy ở bước 3
   - Click **"Add SSH key"**

3. **Xác nhận:**
   - GitHub có thể yêu cầu nhập mật khẩu để xác nhận

### Bước 5: Test Kết Nối

```bash
ssh -T git@github.com
```

**Kết quả mong đợi:**
```
Hi username! You've successfully authenticated, but GitHub does not provide shell access.
```

✅ **Thành công!** Bây giờ bạn có thể dùng SSH URL khi clone/push.

---

## 🔄 Sử Dụng SSH URL

Sau khi cấu hình SSH, bạn có thể dùng SSH URL thay vì HTTPS:

```bash
# Thay đổi remote URL sang SSH
git remote set-url origin git@github.com:YOUR_USERNAME/REPO_NAME.git

# Kiểm tra
git remote -v
```

**Lợi ích của SSH:**
- ✅ Không cần nhập username/password mỗi lần push
- ✅ Bảo mật cao hơn
- ✅ Tiện lợi hơn khi làm việc thường xuyên

---

## ❌ Xử Lý Lỗi

### Lỗi: "Permission denied (publickey)"

**Nguyên nhân:** SSH key chưa được thêm vào GitHub hoặc sai key

**Giải pháp:**
1. Kiểm tra key đã thêm vào GitHub chưa
2. Đảm bảo đang dùng đúng key:
   ```bash
   ssh-add -l  # Xem danh sách keys đã thêm vào agent
   ```
3. Thử lại:
   ```bash
   ssh -T git@github.com
   ```

### Lỗi: "Host key verification failed"

**Nguyên nhân:** Chưa có SSH key hoặc chưa thêm vào known_hosts

**Giải pháp:**
```bash
# Thêm GitHub vào known_hosts
ssh-keyscan github.com >> ~/.ssh/known_hosts

# Sau đó tạo SSH key nếu chưa có
```

### Lỗi: "Could not open a connection to your authentication agent"

**Nguyên nhân:** SSH agent chưa chạy

**Giải pháp:**
```bash
eval "$(ssh-agent -s)"
ssh-add ~/.ssh/id_ed25519
```

---

## 📋 Checklist

- [ ] Đã kiểm tra SSH key tồn tại (`ls -la ~/.ssh`)
- [ ] Đã tạo SSH key mới (nếu chưa có)
- [ ] Đã copy public key
- [ ] Đã thêm SSH key vào GitHub
- [ ] Đã test kết nối (`ssh -T git@github.com`)
- [ ] Đã đổi remote URL sang SSH (nếu muốn)

---

## 💡 Mẹo Hữu Ích

### Xem tất cả SSH keys:
```bash
ls -la ~/.ssh/*.pub
```

### Xóa SSH key khỏi agent:
```bash
ssh-add -d ~/.ssh/id_ed25519
```

### Xem thông tin SSH key:
```bash
ssh-keygen -l -f ~/.ssh/id_ed25519.pub
```

### Tạo nhiều SSH keys cho nhiều tài khoản:
```bash
# Tạo key với tên khác
ssh-keygen -t ed25519 -C "work_email@example.com" -f ~/.ssh/id_ed25519_work

# Cấu hình trong ~/.ssh/config
```

---

## 🔗 Tài Liệu Tham Khảo

- [GitHub SSH Documentation](https://docs.github.com/en/authentication/connecting-to-github-with-ssh)
- [Generating a new SSH key](https://docs.github.com/en/authentication/connecting-to-github-with-ssh/generating-a-new-ssh-key-and-adding-it-to-the-ssh-agent)
- [Adding SSH key to GitHub](https://docs.github.com/en/authentication/connecting-to-github-with-ssh/adding-a-new-ssh-key-to-your-github-account)

---

**Sau khi cấu hình SSH xong, bạn có thể dùng SSH URL khi push code lên GitHub! 🚀**

