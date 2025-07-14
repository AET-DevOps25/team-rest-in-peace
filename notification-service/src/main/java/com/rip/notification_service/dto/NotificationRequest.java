package com.rip.notification_service.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class NotificationRequest {
    @NotNull
    @NotEmpty
    private List<Integer> plenaryProtocolIds;

    public List<Integer> getPlenaryProtocolIds() {
        return plenaryProtocolIds;
    }
}
