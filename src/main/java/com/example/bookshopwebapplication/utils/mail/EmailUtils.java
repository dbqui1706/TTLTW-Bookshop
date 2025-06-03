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
            // THÊM LOGGING
            System.out.println("=== ADDING TOKEN ===");
            System.out.println("Email: " + user.getEmail());
            System.out.println("Code: " + code);
            System.out.println("Token count after add: " + verificationTokenSet.size());
            System.out.println("All tokens:");
            for (VerificationToken token : verificationTokenSet) {
                System.out.println("  - " + token.getEmail() + " : " + token.getCode() + " (expires: " +
                        new Date(token.getExpirationTime()) + ")");
            }
            System.out.println("==================");

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
            return "<!DOCTYPE html>" +
                    "<html>" +
                    "<head>" +
                    "    <meta charset=\"UTF-8\">" +
                    "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">" +
                    "    <title>Đặt lại mật khẩu BookStore</title>" +
                    "</head>" +
                    "<body style=\"margin: 0; padding: 0; font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #f9f9f9; color: #333;\">" +
                    "    <div style=\"max-width: 600px; margin: 0 auto; background-color: #ffffff; border-radius: 8px; overflow: hidden; box-shadow: 0 4px 8px rgba(0, 0, 0, 0.05); margin-top: 20px; margin-bottom: 20px;\">" +
                    "        <div style=\"background-color: #dc3545; padding: 30px 20px; text-align: center;\">" +
                    "            <h1 style=\"color: #ffffff; margin: 0; font-size: 24px;\">📧 Đặt lại mật khẩu</h1>" +
                    "            <p style=\"color: #ffffff; margin: 10px 0 0 0; font-size: 14px; opacity: 0.9;\">BookStore Application</p>" +
                    "        </div>" +
                    "        <div style=\"padding: 30px 40px;\">" +
                    "            <p style=\"font-size: 16px; line-height: 1.6; margin-bottom: 20px;\">Xin chào, <strong style=\"color: #dc3545;\">" + fullName + "</strong>!</p>" +
                    "            <p style=\"font-size: 16px; line-height: 1.6; margin-bottom: 20px;\">Chúng tôi nhận được yêu cầu đặt lại mật khẩu cho tài khoản của bạn tại BookStore Application.</p>" +
                    "            <p style=\"font-size: 16px; line-height: 1.6; margin-bottom: 25px;\">Để đặt lại mật khẩu, vui lòng nhấn vào nút bên dưới:</p>" +
                    "            <div style=\"text-align: center; margin: 30px 0;\">" +
                    "                <a href=\"" + resetLink + "\" style=\"display: inline-block; background-color: #dc3545; color: #ffffff; font-weight: bold; padding: 15px 35px; text-decoration: none; border-radius: 25px; font-size: 16px; text-transform: uppercase; box-shadow: 0 4px 10px rgba(220, 53, 69, 0.3); letter-spacing: 0.5px;\">🔑 Đặt lại mật khẩu</a>" +
                    "            </div>" +
                    "            <div style=\"background-color: #fff3cd; border: 1px solid #ffeaa7; border-radius: 5px; padding: 15px; margin: 25px 0;\">" +
                    "                <h3 style=\"color: #856404; margin: 0 0 10px 0; font-size: 16px;\">⚠️ Lưu ý bảo mật:</h3>" +
                    "                <ul style=\"color: #856404; margin: 0; padding-left: 20px; font-size: 14px; line-height: 1.5;\">" +
                    "                    <li>Liên kết này sẽ hết hạn sau <strong>5 phút</strong></li>" +
                    "                    <li>Chỉ sử dụng liên kết này nếu bạn đã yêu cầu đặt lại mật khẩu</li>" +
                    "                    <li>Không chia sẻ liên kết này với bất kỳ ai</li>" +
                    "                </ul>" +
                    "            </div>" +
                    "            <p style=\"font-size: 14px; line-height: 1.6; color: #777; margin-bottom: 20px;\">Nếu bạn không yêu cầu đặt lại mật khẩu, vui lòng <strong>bỏ qua email này</strong> và mật khẩu của bạn sẽ không thay đổi.</p>" +
                    "            <div style=\"border-top: 1px solid #eee; margin-top: 30px; padding-top: 20px;\">" +
                    "                <p style=\"font-size: 15px; margin-bottom: 10px;\">Trân trọng,</p>" +
                    "                <p style=\"font-size: 15px; font-weight: bold; color: #dc3545; margin: 0;\">🏪 BookShopWeb Team</p>" +
                    "            </div>" +
                    "        </div>" +
                    "        <div style=\"background-color: #f5f5f5; padding: 20px; text-align: center; font-size: 12px; color: #777;\">" +
                    "            <p style=\"margin: 0 0 5px 0;\">🔐 Email này được gửi tự động từ hệ thống bảo mật. Vui lòng không trả lời email này.</p>" +
                    "            <p style=\"margin: 0;\">&copy; 2025 BookStore. Tất cả các quyền được bảo lưu.</p>" +
                    "        </div>" +
                    "    </div>" +
                    "</body>" +
                    "</html>";
        });
    }

    public static boolean isValidVerificationCode(String email, String code) {
        System.out.println("=== VALIDATING TOKEN ===");
        System.out.println("Looking for - Email: " + email + ", Code: " + code);
        System.out.println("Current token count: " + verificationTokenSet.size());
        System.out.println("All tokens:");

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
