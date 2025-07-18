package com.rip.notification_service;

import com.rip.notification_service.dto.SubscriptionRequest;
import com.rip.notification_service.model.NotificationSetting;
import com.rip.notification_service.model.Person;
import com.rip.notification_service.repository.NotificationSettingRepository;
import com.rip.notification_service.repository.PersonRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.scheduling.annotation.Async;
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

    @Async
    public void notifyAllAsync(List<Integer> plenaryProtocolIds) {
        buildAndSendNotificationPlenaryProtocol(plenaryProtocolIds);

        List<Person> distinctSpeakers = personRepository.findDistinctPersonsByPlenaryProtocolIds(plenaryProtocolIds);

        buildAndSendNotificationParty(distinctSpeakers.stream().map(Person::getParty).distinct().collect(Collectors.toList()));

        buildAndSendNotificationPerson(distinctSpeakers);

    }

    public void buildAndSendNotificationPlenaryProtocol(List<Integer> plenaryProtocolIds) {
        List<NotificationSetting> notificationSettings = notificationSettingRepository.findAllByType("PLENARY_PROTOCOL");
        notificationSettings.stream().map(NotificationSetting::getEmail).distinct().forEach(email ->
        {
            try {
                emailService.sendPlenaryProtocolNotification(email, plenaryProtocolIds.size());
                logger.info("Plenary protocol notification sent to " + email);
            } catch (Exception e) {
                logger.error("Email Error for Plenary protocol notification: " + e.getMessage());
            }
        });
    }

    public void buildAndSendNotificationParty(List<String> partiesToNotify) {
        List<NotificationSetting> notificationSettings = notificationSettingRepository.findAllByType("PARTY");
        notificationSettings
                .stream()
                .filter(notificationSetting -> partiesToNotify.contains(notificationSetting.getParty()))
                .collect(Collectors.groupingBy(NotificationSetting::getEmail))
                .forEach((email, groupedNotificationSettings) -> {
                            Set<String> distinctParties = groupedNotificationSettings.stream().map(NotificationSetting::getParty).collect(Collectors.toSet());
                            try {
                                emailService.sendPartyNotification(email, distinctParties);
                                logger.info("Party notification sent to " + email);
                            } catch (Exception e) {
                                logger.error("Email Error for Party notification: " + e.getMessage());
                            }
                        }
                );
    }

    public void buildAndSendNotificationPerson(List<Person> speakers) {
        List<NotificationSetting> notificationSettings = notificationSettingRepository.findAllByType("PERSON");
        Map<String, Set<Person>> byEmail = notificationSettings.stream()
                .filter(ns ->
                        speakers.stream()
                                .anyMatch(p -> p.getId().equals(ns.getPerson().getId()))
                )
                .collect(Collectors.groupingBy(
                        NotificationSetting::getEmail,
                        Collectors.mapping(NotificationSetting::getPerson, Collectors.toSet())
                ));

        byEmail.forEach((email, persons) -> {
            try {
                emailService.sendPersonNotification(email, persons);
                logger.info("Person notification sent to " + email);
            } catch (Exception e) {
                logger.error("Email Error for Person notification: " + e.getMessage());
            }
        });
    }

}
