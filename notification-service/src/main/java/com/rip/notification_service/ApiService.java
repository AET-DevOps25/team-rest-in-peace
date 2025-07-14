package com.rip.notification_service;

import com.rip.notification_service.dto.SubscriptionRequest;
import com.rip.notification_service.model.NotificationSetting;
import com.rip.notification_service.model.Person;
import com.rip.notification_service.repository.NotificationSettingRepository;
import com.rip.notification_service.repository.PersonRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

@Service
public class ApiService {
    private static final Logger logger = LoggerFactory.getLogger(ApiService.class);

    @Autowired
    private NotificationSettingRepository notificationSettingRepository;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private EmailService emailService;


    @Value("${client.base.url}")
    private String clientBaseUrl;

    @Value("${client.unsubscribe.url}")
    private String clientUnsubscribeBaseUrl;


    public record Result(boolean success, String errorMessage) {
    }


    public Result addSubscription(SubscriptionRequest subscriptionRequest) {
        switch (subscriptionRequest.getType()) {
            case "PARTY": {
                List<String> allParties = personRepository.findDistinctParties().orElse(new ArrayList<>());
                String party = subscriptionRequest.getParty();
                if (party == null || !allParties.contains(party)) {
                    return new Result(false, "Unknown or missing party: " + party);
                }
                NotificationSetting notificationSetting = new NotificationSetting(subscriptionRequest.getEmail(), subscriptionRequest.getType(), subscriptionRequest.getParty());
                notificationSettingRepository.save(notificationSetting);
                logger.info("Subscription added for email " + subscriptionRequest.getEmail() + "with type " + subscriptionRequest.getType());
                return new Result(true, null);
            }
            case "PERSON": {
                Optional<Person> personOptional = personRepository.findById(subscriptionRequest.getPersonId());
                if (personOptional.isEmpty()) {
                    return new Result(false, "No person found with ID: " + subscriptionRequest.getPersonId());
                }
                NotificationSetting notificationSetting = new NotificationSetting(subscriptionRequest.getEmail(), subscriptionRequest.getType(), personOptional.get());
                notificationSettingRepository.save(notificationSetting);
                logger.info("Subscription added for email " + subscriptionRequest.getEmail() + "with type " + subscriptionRequest.getType());
                return new Result(true, null);
            }
            case "PLENARY_PROTOCOL": {
                NotificationSetting notificationSetting = new NotificationSetting(subscriptionRequest.getEmail(), subscriptionRequest.getType());
                notificationSettingRepository.save(notificationSetting);
                logger.info("Subscription added for email " + subscriptionRequest.getEmail() + "with type " + subscriptionRequest.getType());
                return new Result(true, null);
            }
            default: {
                logger.error("Unknown subscription type: " + subscriptionRequest.getType());
                return new Result(false, "Unsupported subscription type: " + subscriptionRequest.getType());
            }
        }
    }

    public Result deleteAllSubscriptions(String email) {
        try {
            notificationSettingRepository.deleteAllByEmail(email);
        } catch (Exception e) {
            logger.error("Error deleting all subscriptions: " + e.getMessage());
            return new Result(false, e.getMessage());
        }
        logger.info("Deleted all subscriptions for email: " + email);
        return new Result(true, null);
    }


    public Result notifyAll(List<Integer> plenaryProtocolIds) {

        buildAndSendNotificationPlenaryProtocol(plenaryProtocolIds);

        return new Result(true, null);
    }

    public void buildAndSendNotificationPlenaryProtocol(List<Integer> plenaryProtocolIds) {
        List<NotificationSetting> notificationSettings = notificationSettingRepository.findAllByType("PLENARY_PROTOCOL");
        logger.info("Notification Plenary Protocols found: " + notificationSettings.size());
        notificationSettings.stream().map(NotificationSetting::getEmail).distinct().forEach(email ->
        {
            String unsubscribeLink = clientUnsubscribeBaseUrl + "?email=" + email;
            try {
                emailService.sendPlenaryProtocolNotification(email, plenaryProtocolIds.size(), clientBaseUrl, unsubscribeLink);
            } catch (Exception e) {
                logger.error("Email Error: " + e.getMessage());
            }
        });
    }

    public void buildAndSendNotificationParty(List<Integer> plenaryProtocolIds) {
        // TODO: Implement
    }

    public void buildAndSendNotificationPerson(List<Integer> plenaryProtocolIds) {
        // TODO: Implement
    }
}
