package com.rip.notification_service.dto;

import java.util.List;

/**
 * A test subclass of NotificationRequest that adds setter methods for testing purposes.
 */
public class TestNotificationRequest extends NotificationRequest {
    
    public TestNotificationRequest setPlenaryProtocolIds(List<Integer> plenaryProtocolIds) {
        try {
            java.lang.reflect.Field field = NotificationRequest.class.getDeclaredField("plenaryProtocolIds");
            field.setAccessible(true);
            field.set(this, plenaryProtocolIds);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set plenaryProtocolIds field", e);
        }
        return this;
    }
}