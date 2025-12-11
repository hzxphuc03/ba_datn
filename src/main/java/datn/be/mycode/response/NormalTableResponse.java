package datn.be.mycode.response;

import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString

public class NormalTableResponse<T> {

    private List<T> item;
    
    // Thêm field 'data' để tương thích với frontend
    // Sử dụng @JsonGetter để map item sang data khi serialize
    @com.fasterxml.jackson.annotation.JsonGetter("data")
    public List<T> getData() {
        // Đảm bảo không bao giờ trả về null
        return item != null ? item : new java.util.ArrayList<>();
    }
    
    @com.fasterxml.jackson.annotation.JsonGetter("item")
    public List<T> getItem() {
        // Đảm bảo không bao giờ trả về null
        return item != null ? item : new java.util.ArrayList<>();
    }

    private int page;

    private int pageSize;

    private Long totalItem;
}