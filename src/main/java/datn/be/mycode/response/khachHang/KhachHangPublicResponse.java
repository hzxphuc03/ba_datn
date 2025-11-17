package datn.be.mycode.response.khachHang;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(description = "Thông tin khách hàng công khai (không chứa thông tin nhạy cảm)")
public class KhachHangPublicResponse {
    
    @Schema(description = "ID khách hàng", example = "1")
    private Long id;
    
    @Schema(description = "Tên khách hàng", example = "Nguyễn Văn A")
    private String ten;
    
    @Schema(description = "Trạng thái tài khoản", example = "1")
    private Integer trangThai;
    
    @Schema(description = "Ngày tạo tài khoản")
    private LocalDate ngayTao;
    
    // Ẩn các thông tin nhạy cảm
    @JsonIgnore
    private String email;
    
    @JsonIgnore
    private String soDienThoai;
    
    @JsonIgnore
    private String diaChi;
    
    @JsonIgnore
    private String matKhau;
    
    // Constructors
    public KhachHangPublicResponse() {}
    
    public KhachHangPublicResponse(Long id, String ten, Integer trangThai, LocalDate ngayTao) {
        this.id = id;
        this.ten = ten;
        this.trangThai = trangThai;
        this.ngayTao = ngayTao;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getTen() {
        return ten;
    }
    
    public void setTen(String ten) {
        this.ten = ten;
    }
    
    public Integer getTrangThai() {
        return trangThai;
    }
    
    public void setTrangThai(Integer trangThai) {
        this.trangThai = trangThai;
    }
    
    public LocalDate getNgayTao() {
        return ngayTao;
    }
    
    public void setNgayTao(LocalDate ngayTao) {
        this.ngayTao = ngayTao;
    }
}