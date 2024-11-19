package datn.be.mycode.util;

import datn.be.mycode.entity.HoaDon;
import datn.be.mycode.entity.HoaDonChiTiet;
import datn.be.mycode.request.thaoTacHoaDon.ThaoTacHoaDonAddRequest;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
public class EmailSender {

    private static String EMAIL_HOST= "toannhph23989@fpt.edu.vn";
    private final Map<String, String> otpStorage = new HashMap<>(); // Lưu OTP trong bộ nhớ tạm
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    @Autowired
    private JavaMailSender javaMailSender;

    public void sendSimpleMessage(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(EMAIL_HOST);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        try {
        javaMailSender.send(message);
        } catch (Exception e){
            e.printStackTrace();
        }
    }


    public void sendEmailOtp(String to) {
        var optCode = 100000 + new Random().nextInt(999999);
        String otp = String.valueOf(optCode);
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message,true);
            message.setFrom(EMAIL_HOST);
            message.setRecipients(MimeMessage.RecipientType.TO,to);
            message.setSubject("OTP");

            // Đọc tệp HTML
            String htmlTemplate = readFile("src/main/resources/view/otp/otp.html");

            // Thay thế OTP trong HTML
            String htmlContent = htmlTemplate.replace("${otp}", otp);

            // Thêm hình ảnh inline
            helper.addInline("logo", new File("src/main/resources/view/otp/logo.png"));

            // Thiết lập nội dung HTML
            helper.setText(htmlContent, true);

            otpStorage.put(to,otp);
            javaMailSender.send(message);

            // Xóa OTP sau 3 phút
            scheduler.schedule(() -> otpStorage.remove(to), 3, TimeUnit.MINUTES);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public Boolean checkOtp(String email, String otpReq){
        var otp = otpStorage.get(email);
        if(otp != null && otp.equals(otpReq)){
            return true;
        }
        return false;
    }

    public void sendEmailHoaDon(String to, List<HoaDonChiTiet> hoaDonChiTiets, HoaDon hoaDon) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message,true);
            message.setFrom(EMAIL_HOST);
            message.setRecipients(MimeMessage.RecipientType.TO,to);
            message.setSubject("Hóa đơn");

            // Đọc tệp HTML
            String htmlTemplate = readFile("src/main/resources/view/thanh-toan/mail.html");

            // Tạo HTML cho chi tiết sản phẩm
            String productDetailsHtml = generateProductDetails(hoaDonChiTiets);

            // Thay thế các biến trong HTML
            String htmlContent = htmlTemplate
                    .replace("${OrderCode}", hoaDon.getMa())
                    .replace("${FullName}", hoaDon.getKhachHang().getHoTen())
                    .replace("${PaymentMethod}", hoaDon.getPhuongThucThanhToan().getTen())
                    .replace("${CreatedDate}", hoaDon.getNgayTao()+"")
                    .replace("${detail}", productDetailsHtml)
                    .replace("${Price}", hoaDon.getTongTienSanPhamChuaGiam()+"")
                    .replace("${tienGiam}", hoaDon.getTongTienGiam()+"")
                    .replace("${PriceShip}", hoaDon.getPhiShip()+"")
                    .replace("${TotalPrice}", hoaDon.getTongTien()+"");

            // Thêm hình ảnh inline
            helper.addInline("logo", new File("src/main/resources/view/otp/logo.png"));

            // Thiết lập nội dung HTML
            helper.setText(htmlContent, true);

            javaMailSender.send(message);

            // Xóa OTP sau 3 phút
            scheduler.schedule(() -> otpStorage.remove(to), 3, TimeUnit.MINUTES);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void sendEmailHoaDonAddVoucher(String to, List<HoaDonChiTiet> hoaDonChiTiets, HoaDon hoaDon, String maVoucher) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message,true);
            message.setFrom(EMAIL_HOST);
            message.setRecipients(MimeMessage.RecipientType.TO,to);
            message.setSubject("Hóa đơn");

            // Đọc tệp HTML
            String htmlTemplate = readFile("src/main/resources/view/thanh-toan/mail-add-voucher.html");

            // Tạo HTML cho chi tiết sản phẩm
            String productDetailsHtml = generateProductDetails(hoaDonChiTiets);

            // Thay thế các biến trong HTML
            String htmlContent = htmlTemplate
                    .replace("${OrderCode}", hoaDon.getMa())
                    .replace("${FullName}", hoaDon.getKhachHang().getHoTen())
                    .replace("${PaymentMethod}", hoaDon.getPhuongThucThanhToan().getTen())
                    .replace("${CreatedDate}", hoaDon.getNgayTao()+"")
                    .replace("${detail}", productDetailsHtml)
                    .replace("${Price}", hoaDon.getTongTienSanPhamChuaGiam()+"")
                    .replace("${tienGiam}", hoaDon.getTongTienGiam()+"")
                    .replace("${PriceShip}", hoaDon.getPhiShip()+"")
                    .replace("${TotalPrice}", hoaDon.getTongTien()+"")
                    .replace("${maGiamGia}", maVoucher);

            // Thêm hình ảnh inline
            helper.addInline("logo", new File("src/main/resources/view/otp/logo.png"));

            // Thiết lập nội dung HTML
            helper.setText(htmlContent, true);

            javaMailSender.send(message);

            // Xóa OTP sau 3 phút
            scheduler.schedule(() -> otpStorage.remove(to), 3, TimeUnit.MINUTES);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public String generateProductDetails(List<HoaDonChiTiet> hoaDonChiTiets) {
        StringBuilder productDetailsBuilder = new StringBuilder();

        for (HoaDonChiTiet hoaDonChiTiet : hoaDonChiTiets) {
            productDetailsBuilder.append("<tr>")
                    .append("<td style=\"white-space: nowrap;\">").append(hoaDonChiTiet.getSanPhamChiTiet().getSanPham().getTen()).append("</td>")
                    .append("<td style=\"white-space: nowrap;\">").append(hoaDonChiTiet.getSoLuong()).append("</td>")
                    .append("<td style=\"white-space: nowrap;\">").append(hoaDonChiTiet.getGiaHienHanh().setScale(0, RoundingMode.DOWN)).append(" đ</td>")
                    .append("<td style=\"white-space: nowrap;\">").append(hoaDonChiTiet.getGiaDaGiam().setScale(0, RoundingMode.DOWN)).append(" đ</td>")
                    .append("</tr>");
        }

        return productDetailsBuilder.toString();
    }

    public void sendEmailTrangThaiHoaDon(String email, ThaoTacHoaDonAddRequest thaoTacHoaDon) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message,true);
            message.setFrom(EMAIL_HOST);
            message.setRecipients(MimeMessage.RecipientType.TO,email);
            message.setSubject("Hóa đơn");

            // Đọc tệp HTML
            String htmlTemplate = readFile("src/main/resources/view/trang-thai-hoa-don/status.html");


            // Thay thế các biến trong HTML
            String htmlContent = htmlTemplate
                    .replace("${InvoiceStatus}", thaoTacHoaDon.getThaoTac());

            // Thêm hình ảnh inline
            helper.addInline("logo", new File("src/main/resources/view/otp/logo.png"));

            // Thiết lập nội dung HTML
            helper.setText(htmlContent, true);

            javaMailSender.send(message);
        } catch (Exception e){
            e.printStackTrace();
        }
    }


    public String readFile(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        return Files.readString(path, StandardCharsets.UTF_8);
    }

}
