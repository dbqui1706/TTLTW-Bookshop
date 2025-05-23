package com.example.bookshopwebapplication.utils.mail;

import com.example.bookshopwebapplication.dto.UserDto;
import com.example.bookshopwebapplication.entities.User;
import org.jetbrains.annotations.NotNull;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.*;

public class EmailUtils {
    private static Set<VerificationToken> verificationTokenSet = instance();
    private static Properties properties = new Properties();
    private static ResourceBundle bundle = ResourceBundle.getBundle("emailConfig");

    private static Set<VerificationToken> instance() {
        return new HashSet<>();
    }

    public static void sendEmail(UserDto user, String code) {
        sendEmail(user, code, EmailUtils::getEmailContent);
    }

    public static void sendEmailCancelKey(UserDto user, String code) {
        sendEmail(user, code, (email, fullName, hashCode) -> {
            String activationLink = "http://localhost:8080/key-cancel?email=" + email + "&code=" + code;
            return "<html><body>" +
                    "<p>Xin chào, " + fullName + "</p>" +
                    "<p>Chúng tôi được thông báo bạn đã hủy khóa đặt hàng. Để xác nhận hủy khóa, vui lòng nhấn vào liên kết bên dưới nếu không sẽ hết hạn sau 5 phút:</p>" +
                    "<p><a href=\"" + activationLink + "\" style=\"text-decoration:none; color: #3498db; font-weight: bold;\">Kích hoạt</a></p>" +
                    "<p>Nếu bạn không thực hiện hành động này, vui lòng bỏ qua thư này.</p>" +
                    "<p>Trân trọng,</p>" +
                    "<p>BookShopWeb Team</p>" +
                    "</body></html>";
        });
    }

    private static void sendEmail(UserDto user, String code, IContent funcContent) {
        // sets SMTP server properties
        properties.put("mail.smtp.host", bundle.getString("HOST"));
        properties.put("mail.smtp.port", bundle.getString("TSL_PORT"));
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        // creates a new session with an authenticator
        Authenticator auth = new Authenticator() {
            public PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(bundle.getString("MY_EMAIL"),
                        bundle.getString("MY_PASSWORD"));
            }
        };

        Session session = Session.getInstance(properties, auth);

        // creates a new e-mail message
        try {
            verificationTokenSet.add(new VerificationToken(
                    user.getEmail(),
                    code,
                    System.currentTimeMillis() + 5 * 60 * 1000 // expirationTime = 5 minutes
            ));

            String emailContent = funcContent.getContent(user.getEmail(), user.getFullName(), code);
            String subject = "Thư xác nhận Email từ BookShopWeb Application";
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(bundle.getString("MY_EMAIL")));
            InternetAddress[] toAddresses = {new InternetAddress(user.getEmail())};
            msg.setRecipients(Message.RecipientType.TO, toAddresses);
            msg.setSubject(subject);
            msg.setSentDate(new Date());
            msg.setContent(emailContent, "text/html; charset=UTF-8");

            // sends the e-mail
            Transport.send(msg);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    @NotNull
    private static String getEmailContent(String toAddress, String fullName, String code) {
        String activationLink = "http://localhost:8080/api/auth/active-account?email=" + toAddress + "&code=" + code;
        String emailContent = "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "    <meta charset=\"UTF-8\">" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">" +
                "    <title>Kích hoạt tài khoản BookStore</title>" +
                "</head>" +
                "<body style=\"margin: 0; padding: 0; font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #f9f9f9; color: #333;\">" +
                "    <div style=\"max-width: 600px; margin: 0 auto; background-color: #ffffff; border-radius: 8px; overflow: hidden; box-shadow: 0 4px 8px rgba(0, 0, 0, 0.05); margin-top: 20px; margin-bottom: 20px;\">" +
                "        <!-- Header -->" +
                "        <div style=\"background-color: #1a73e8; padding: 30px 20px; text-align: center;\">" +
                "            <h1 style=\"color: #ffffff; margin: 0; font-size: 24px;\">BookStore Application</h1>" +
                "        </div>" +
                "        <!-- Content -->" +
                "        <div style=\"padding: 30px 40px;\">" +
                "            <p style=\"font-size: 16px; line-height: 1.6; margin-bottom: 20px;\">Xin chào, <strong style=\"color: #1a73e8;\">" + fullName + "</strong>!</p>" +
                "            <p style=\"font-size: 16px; line-height: 1.6; margin-bottom: 20px;\">Bạn vừa đăng ký tài khoản tại BookStore Application. Để kích hoạt tài khoản, vui lòng nhấn vào nút bên dưới:</p>" +
                "            <div style=\"text-align: center; margin: 30px 0;\">" +
                "                <a href=\"" + activationLink + "\" style=\"display: inline-block; background-color: #1a73e8; color: #ffffff; font-weight: bold; padding: 12px 30px; text-decoration: none; border-radius: 5px; font-size: 16px; text-transform: uppercase; box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1); transition: background-color 0.2s;\">Kích hoạt tài khoản</a>" +
                "            </div>" +
                "            <p style=\"font-size: 14px; line-height: 1.6; color: #777; margin-bottom: 20px;\">Liên kết kích hoạt sẽ hết hạn sau 5 phút. Nếu bạn không thực hiện đăng ký này, vui lòng bỏ qua thư này.</p>" +
                "            <div style=\"border-top: 1px solid #eee; margin-top: 30px; padding-top: 20px;\">" +
                "                <p style=\"font-size: 15px; margin-bottom: 10px;\">Trân trọng,</p>" +
                "                <p style=\"font-size: 15px; font-weight: bold; color: #1a73e8; margin: 0;\">BookShopWeb Team</p>" +
                "            </div>" +
                "        </div>" +
                "        <!-- Footer -->" +
                "        <div style=\"background-color: #f5f5f5; padding: 20px; text-align: center; font-size: 12px; color: #777;\">" +
                "            <p>Email này được gửi tự động. Vui lòng không trả lời email này.</p>" +
                "            <p>&copy; 2025 BookStore. Tất cả các quyền được bảo lưu.</p>" +
                "        </div>" +
                "    </div>" +
                "</body>" +
                "</html>";
        return emailContent;
    }

    /**
     * Gửi email đặt lại mật khẩu cho người dùng
     * @param user Thông tin người dùng cần đặt lại mật khẩu
     */
    public static void sendPasswordResetEmail(UserDto user) {
        // Tạo mã xác minh ngẫu nhiên
        String code = UUID.randomUUID().toString();

        // Gửi email chứa mã xác minh
        sendEmail(user, code, (email, fullName, hashCode) -> {
            String resetLink = "http://localhost:8080/reset-password?email=" + email + "&code=" + code;
            return "<html><body>" +
                    "<p>Xin chào, " + fullName + "</p>" +
                    "<p>Chúng tôi nhận được yêu cầu đặt lại mật khẩu cho tài khoản của bạn tại BookShopWeb. Để đặt lại mật khẩu, vui lòng nhấn vào liên kết bên dưới:</p>" +
                    "<p><a href=\"" + resetLink + "\" style=\"text-decoration:none; color: #3498db; font-weight: bold;\">Đặt lại mật khẩu</a></p>" +
                    "<p>Liên kết này sẽ hết hạn sau 5 phút.</p>" +
                    "<p>Nếu bạn không yêu cầu đặt lại mật khẩu, vui lòng bỏ qua thư này và đảm bảo rằng bạn vẫn có thể đăng nhập vào tài khoản của mình.</p>" +
                    "<p>Trân trọng,</p>" +
                    "<p>BookShopWeb Team</p>" +
                    "</body></html>";
        });
    }

    public static boolean isValidVerificationCode(String email, String code) {
        for (VerificationToken token : verificationTokenSet) {
            if (token.getEmail().equals(email)
                    && token.getCode().equals(code)
                    && System.currentTimeMillis() <= token.getExpirationTime()) {
                verificationTokenSet.remove(token);
                return true;
            }
        }
        return false;
    }

    // Sử dụng để custom nội dung trong mail
    @FunctionalInterface
    private interface IContent {
        String getContent(String address, String fullName, String code);

    }
}
