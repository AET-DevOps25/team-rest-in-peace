package com.rip.notification_service;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import jakarta.mail.internet.MimeMessage;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    private static final String FROM = "PolicyWatch <policywatch.rip@gmail.com>";

    private static final String HTML_TEMPLATE_PLENARY_PROTOCOL = """
            <html>
              <body>
                <p>Dear Subscriber,</p>
                <p>There have been %d new plenary protocol(s) published.</p>
                <p>You can view them <a href="%s/plenary-protocols">here</a>.</p>
                <p>If you no longer wish to receive these notifications, you can <a href="%s">unsubscribe here</a>.</p>
                <hr/>
                <p>Best regards,<br/>Policy Watch Team</p>
              </body>
            </html>
            """;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }


    public void sendPlenaryProtocolNotification(String to, int count, String clientBaseUrl, String unsubscribeLink) throws Exception {
        String subject = "New Plenary Protocol Available";
        String html = String.format(HTML_TEMPLATE_PLENARY_PROTOCOL, count, clientBaseUrl, unsubscribeLink);

        MimeMessage msg = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(msg, true, "UTF-8");
        helper.setFrom(FROM);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(html, true);

        mailSender.send(msg);
    }
}
