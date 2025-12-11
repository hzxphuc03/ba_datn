# Hướng dẫn sử dụng Storage Gateway Proxy

## Vấn đề

Frontend đang gửi request upload ảnh đến `fsel-gateway` với `Content-Type: application/json`, nhưng server yêu cầu `multipart/form-data` → Lỗi 415 Unsupported Media Type.

## Giải pháp

Backend đã tạo **Proxy Controller** để chuyển đổi request từ frontend và forward đến fsel-gateway với đúng format.

## API Endpoints

### 1. Upload ảnh với MultipartFile (Khuyến nghị)

```
POST /api/storage-gateway/upload
Content-Type: multipart/form-data
Authorization: Bearer {token}

Body:
- file: (file)

Response:
{
  // Response từ fsel-gateway
}
```

### 2. Upload ảnh với Base64

```
POST /api/storage-gateway/upload-base64
Content-Type: application/json
Authorization: Bearer {token}

Body:
{
  "image": "data:image/jpeg;base64,/9j/4AAQ...",
  "filename": "image.jpg" // optional
}

Response:
{
  // Response từ fsel-gateway
}
```

## Frontend Integration

### Option 1: Sử dụng MultipartFile (Khuyến nghị)

```typescript
async function uploadImage(file: File): Promise<any> {
  const formData = new FormData();
  formData.append('file', file);
  
  const token = localStorage.getItem('accessToken');
  const response = await fetch('https://ba-datn-1.onrender.com/api/storage-gateway/upload', {
    method: 'POST',
    headers: {
      'Authorization': `Bearer ${token}`
      // KHÔNG set Content-Type header - browser sẽ tự động set multipart/form-data
    },
    body: formData
  });
  
  return await response.json();
}
```

### Option 2: Sử dụng Base64 (nếu frontend đang dùng)

```typescript
async function uploadImageBase64(base64Image: string, filename: string = 'image.jpg'): Promise<any> {
  const token = localStorage.getItem('accessToken');
  const response = await fetch('https://ba-datn-1.onrender.com/api/storage-gateway/upload-base64', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    },
    body: JSON.stringify({
      image: base64Image,
      filename: filename
    })
  });
  
  return await response.json();
}
```

### React Example

```jsx
import { useState } from 'react';

function ImageUpload() {
  const [uploading, setUploading] = useState(false);
  
  const handleFileChange = async (e) => {
    const file = e.target.files[0];
    if (!file) return;
    
    setUploading(true);
    try {
      const formData = new FormData();
      formData.append('file', file);
      
      const token = localStorage.getItem('accessToken');
      const response = await fetch('https://ba-datn-1.onrender.com/api/storage-gateway/upload', {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${token}`
        },
        body: formData
      });
      
      const data = await response.json();
      if (response.ok) {
        console.log('Upload successful:', data);
        // Use data.url or data.path from response
      } else {
        alert('Upload failed: ' + (data.message || 'Unknown error'));
      }
    } catch (error) {
      alert('Upload failed: ' + error.message);
    } finally {
      setUploading(false);
    }
  };
  
  return (
    <div>
      <input 
        type="file" 
        accept="image/*" 
        onChange={handleFileChange}
        disabled={uploading}
      />
      {uploading && <p>Uploading...</p>}
    </div>
  );
}
```

## Lưu ý

1. **Authentication**: Tất cả endpoints đều yêu cầu JWT token
2. **Content-Type**: 
   - Option 1: KHÔNG set Content-Type header - browser sẽ tự động set `multipart/form-data` với boundary
   - Option 2: Set `Content-Type: application/json` khi dùng base64
3. **File size**: Tùy theo giới hạn của fsel-gateway
4. **Response format**: Response sẽ giống như response từ fsel-gateway trực tiếp

## Troubleshooting

### Lỗi: 415 Unsupported Media Type
- Đảm bảo không set `Content-Type: application/json` khi dùng FormData
- Browser sẽ tự động set `multipart/form-data` với boundary

### Lỗi: 401 Unauthorized
- Kiểm tra JWT token có hợp lệ không
- Token phải được gửi trong header `Authorization: Bearer {token}`

### Lỗi: Connection timeout
- Kiểm tra fsel-gateway có hoạt động không
- Kiểm tra network connection

## So sánh với Cloudinary

Nếu muốn chuyển sang Cloudinary (đơn giản hơn, không cần proxy):
- Xem file `CLOUDINARY_SETUP.md`
- Endpoint: `/api/upload/image`
- Không cần forward request đến gateway khác

