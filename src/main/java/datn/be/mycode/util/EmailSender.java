package datn.be.mycode.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class EmailSender {

    private static String EMAIL_HOST= "toannhph23989@fpt.edu.vn";

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
}
