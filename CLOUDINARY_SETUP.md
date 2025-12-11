# Hướng dẫn Setup Cloudinary cho Upload Ảnh

## Vấn đề

Backend deploy trên Render không có persistent storage, nên không thể lưu file local. Giải pháp là dùng **Cloudinary** - cloud storage miễn phí cho ảnh.

## Bước 1: Tạo tài khoản Cloudinary

1. Truy cập: https://cloudinary.com/
2. Đăng ký tài khoản miễn phí (Free tier rất tốt)
3. Sau khi đăng ký, vào **Dashboard**

## Bước 2: Lấy thông tin API

Trong Dashboard, bạn sẽ thấy:
- **Cloud Name**: Tên cloud của bạn
- **API Key**: Key để upload
- **API Secret**: Secret key (giữ bí mật!)

## Bước 3: Cấu hình Environment Variables

### Trên Render Dashboard:

1. Vào service backend của bạn trên Render
2. Vào tab **Environment**
3. Thêm 3 biến sau:

```
CLOUDINARY_CLOUD_NAME=your-cloud-name
CLOUDINARY_API_KEY=your-api-key
CLOUDINARY_API_SECRET=your-api-secret
```

### Local Development:

Thêm vào `src/main/resources/application.properties`:

```properties
cloudinary.cloud-name=your-cloud-name
cloudinary.api-key=your-api-key
cloudinary.api-secret=your-api-secret
```

## Bước 4: API Endpoints

### Upload 1 ảnh
```
POST /api/upload/image
Content-Type: multipart/form-data

Body:
- file: (file)

Response:
{
  "success": true,
  "url": "https://res.cloudinary.com/...",
  "message": "Image uploaded successfully"
}
```

### Upload nhiều ảnh
```
POST /api/upload/images
Content-Type: multipart/form-data

Body:
- files: (file[]) - Array of files

Response:
{
  "success": true,
  "urls": ["https://...", "https://..."],
  "count": 2,
  "message": "Images uploaded successfully"
}
```

### Xóa ảnh
```
DELETE /api/upload/image?url={imageUrl}

Response:
{
  "success": true,
  "message": "Image deleted successfully"
}
```

## Bước 5: Frontend Integration

### JavaScript/TypeScript Example:

```typescript
// Upload single image
async function uploadImage(file: File): Promise<string> {
  const formData = new FormData();
  formData.append('file', file);
  
  const response = await fetch('https://your-backend.onrender.com/api/upload/image', {
    method: 'POST',
    headers: {
      'Authorization': `Bearer ${token}` // JWT token required
    },
    body: formData
  });
  
  const data = await response.json();
  if (data.success) {
    return data.url; // Use this URL to save in database
  } else {
    throw new Error(data.message);
  }
}

// Upload multiple images
async function uploadImages(files: File[]): Promise<string[]> {
  const formData = new FormData();
  files.forEach(file => {
    formData.append('files', file);
  });
  
  const response = await fetch('https://your-backend.onrender.com/api/upload/images', {
    method: 'POST',
    headers: {
      'Authorization': `Bearer ${token}`
    },
    body: formData
  });
  
  const data = await response.json();
  if (data.success) {
    return data.urls;
  } else {
    throw new Error(data.message);
  }
}
```

### React Example:

```jsx
import { useState } from 'react';

function ImageUpload() {
  const [imageUrl, setImageUrl] = useState('');
  const [uploading, setUploading] = useState(false);
  
  const handleFileChange = async (e) => {
    const file = e.target.files[0];
    if (!file) return;
    
    setUploading(true);
    try {
      const formData = new FormData();
      formData.append('file', file);
      
      const token = localStorage.getItem('accessToken');
      const response = await fetch('https://your-backend.onrender.com/api/upload/image', {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${token}`
        },
        body: formData
      });
      
      const data = await response.json();
      if (data.success) {
        setImageUrl(data.url);
        // Save data.url to your database
      } else {
        alert(data.message);
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
      {imageUrl && <img src={imageUrl} alt="Uploaded" />}
    </div>
  );
}
```

## Lưu ý

1. **Authentication**: Tất cả endpoints upload đều yêu cầu JWT token
2. **File size**: Giới hạn 10MB mỗi file
3. **File type**: Chỉ chấp nhận file ảnh (image/*)
4. **URL format**: Cloudinary trả về HTTPS URL, có thể dùng trực tiếp trong frontend
5. **Free tier**: Cloudinary free tier rất tốt, đủ cho project nhỏ

## Troubleshooting

### Lỗi: "File is empty"
- Kiểm tra file có được gửi đúng không
- Kiểm tra Content-Type header

### Lỗi: "File must be an image"
- Chỉ chấp nhận file ảnh (jpg, png, gif, webp, etc.)

### Lỗi: "File size must be less than 10MB"
- Giảm kích thước file hoặc compress ảnh

### Lỗi: "Failed to upload image to Cloudinary"
- Kiểm tra Cloudinary credentials trong environment variables
- Kiểm tra kết nối internet
- Kiểm tra Cloudinary dashboard có bị giới hạn không

## Alternative: Nếu không muốn dùng Cloudinary

Có thể dùng các dịch vụ khác:
- **Firebase Storage** (free tier tốt)
- **AWS S3** (có free tier)
- **ImgBB API** (đơn giản, không cần account)
- **Base64** (lưu trực tiếp trong database - không khuyến nghị cho file lớn)

