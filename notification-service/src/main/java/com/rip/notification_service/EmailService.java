package com.rip.notification_service;

import com.rip.notification_service.model.Person;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import jakarta.mail.internet.MimeMessage;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class EmailService {

    @Value("${client.base.url}")
    private String clientBaseUrl;

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

    private static final String HTML_TEMPLATE_NEW_SPEECH_FOR_PARTY = """
            <html>
              <body>
                <p>Dear Subscriber,</p>
                <p>New speeches for the following parties are now available: %s</p>
                <p>If you no longer wish to receive these notifications, you can <a href="%s">unsubscribe here</a>.</p>
                <hr/>
                <p>Best regards,<br/>Policy Watch Team</p>
              </body>
            </html>
            """;

    private static final String HTML_TEMPLATE_NEW_SPEECH_FOR_SPEAKER = """
            <html>
              <body>
                <p>Dear Subscriber,</p>
                <p>New Speeches by the following Speakers are now available:</p>
                <p>%s</p>
                <p>If you no longer wish to receive these notifications, you can <a href="%s">unsubscribe here</a>.</p>
                <hr/>
                <p>Best regards,<br/>Policy Watch Team</p>
              </body>
            </html>
            """;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }


    public void sendPlenaryProtocolNotification(String to, int count) throws Exception {
        String subject = "New Plenary Protocol Available";
        String html = String.format(HTML_TEMPLATE_PLENARY_PROTOCOL, count, clientBaseUrl, buildUnsubscribeUrl(to));

        sendNotification(to, subject, html);
    }


    public void sendPartyNotification(String to, Set<String> parties) throws Exception {
        String subject = "New Speeches for Subscribed Party Available";
        String partiesString = parties.stream()
                .map(party -> {
                    String partyUrl = UriComponentsBuilder
                            .fromUriString(clientBaseUrl)
                            .path("/party")
                            .queryParam("partei", party)
                            .build()
                            .encode()
                            .toUriString();
                    return String.format(
                            "<a href=\"%s\">%s</a>",
                            partyUrl,
                            party
                    );
                })
                .collect(Collectors.joining("<br/>"));
        String html = String.format(HTML_TEMPLATE_NEW_SPEECH_FOR_PARTY, partiesString, buildUnsubscribeUrl(to));
        sendNotification(to, subject, html);
    }


    public void sendPersonNotification(String to, Set<Person> speakers) throws Exception {
        String subject = "New Speeches for Subscribed Speakers Available";
        String speakersString = speakers.stream()
                .map(speaker -> {
                    String speakerUrl = UriComponentsBuilder
                            .fromUriString(clientBaseUrl)
                            .path("/reden/{id}")
                            .buildAndExpand(speaker.getId())
                            .encode()
                            .toUriString();
                    return String.format(
                            "<a href=\"%s\">%s %s</a>",
                            speakerUrl,
                            speaker.getFirstName(),
                            speaker.getLastName()
                    );
                })
                .collect(Collectors.joining("<br/>"));
        String html = String.format(HTML_TEMPLATE_NEW_SPEECH_FOR_SPEAKER, speakersString, buildUnsubscribeUrl(to));

        sendNotification(to, subject, html);
    }

    private void sendNotification(String to, String subject, String html) throws MessagingException {

        MimeMessage msg = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(msg, true, "UTF-8");
        helper.setFrom(FROM);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(html, true);

        mailSender.send(msg);
    }

    private String buildUnsubscribeUrl(String email) {
        return UriComponentsBuilder.fromUriString(clientBaseUrl)
                .path("/unsubscribe")
                .queryParam("email", email)
                .build()
                .encode()
                .toUriString();
    }

}
