package com.rip.notification_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;


public class SubscriptionRequest {

    @NotBlank
    private String type;

    @Email
    @NotBlank
    private String email;

    private Integer personId;

    private String party;

    public String getType() {
        return type;
    }

    public String getEmail() {
        return email;
    }

    public Integer getPersonId() {
        return personId;
    }

    public String getParty() {
        return party;
    }
}
