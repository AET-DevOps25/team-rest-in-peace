package com.rip.notification_service.dto;

/**
 * A test subclass of SubscriptionRequest that adds setter methods for testing purposes.
 */
public class TestSubscriptionRequest extends SubscriptionRequest {
    
    public TestSubscriptionRequest setEmail(String email) {
        try {
            java.lang.reflect.Field field = SubscriptionRequest.class.getDeclaredField("email");
            field.setAccessible(true);
            field.set(this, email);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set email field", e);
        }
        return this;
    }
    
    public TestSubscriptionRequest setType(String type) {
        try {
            java.lang.reflect.Field field = SubscriptionRequest.class.getDeclaredField("type");
            field.setAccessible(true);
            field.set(this, type);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set type field", e);
        }
        return this;
    }
    
    public TestSubscriptionRequest setPersonId(Integer personId) {
        try {
            java.lang.reflect.Field field = SubscriptionRequest.class.getDeclaredField("personId");
            field.setAccessible(true);
            field.set(this, personId);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set personId field", e);
        }
        return this;
    }
    
    public TestSubscriptionRequest setParty(String party) {
        try {
            java.lang.reflect.Field field = SubscriptionRequest.class.getDeclaredField("party");
            field.setAccessible(true);
            field.set(this, party);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set party field", e);
        }
        return this;
    }
}