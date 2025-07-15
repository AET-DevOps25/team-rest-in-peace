package com.rip.notification_service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rip.notification_service.dto.SubscriptionRequest;
import com.rip.notification_service.dto.TestNotificationRequest;
import com.rip.notification_service.dto.TestSubscriptionRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(Controller.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
public class ControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ApiService apiService;

    private TestSubscriptionRequest validPartySubscription;
    private TestSubscriptionRequest validPersonSubscription;
    private TestSubscriptionRequest validPlenarySubscription;
    private TestNotificationRequest validNotificationRequest;

    @BeforeEach
    public void setup() {
        validPartySubscription = new TestSubscriptionRequest()
                .setEmail("user@example.com")
                .setType("PARTY")
                .setParty("Test Party");

        validPersonSubscription = new TestSubscriptionRequest()
                .setEmail("user@example.com")
                .setType("PERSON")
                .setPersonId(1);

        validPlenarySubscription = new TestSubscriptionRequest()
                .setEmail("user@example.com")
                .setType("PLENARY_PROTOCOL");

        validNotificationRequest = new TestNotificationRequest()
                .setPlenaryProtocolIds(List.of(1, 2, 3));
    }

    @Test
    public void testHealthEndpoint() throws Exception {
        mockMvc.perform(get("/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.healthy").value(true));
    }

    @Test
    public void testSubscribe_Success() throws Exception {
        when(apiService.addSubscription(any(SubscriptionRequest.class)))
                .thenReturn(new ApiService.Result(true, null));

        mockMvc.perform(post("/subscribe")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validPartySubscription)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    public void testSubscribe_Failure() throws Exception {
        when(apiService.addSubscription(any(SubscriptionRequest.class)))
                .thenReturn(new ApiService.Result(false, "Invalid subscription"));

        mockMvc.perform(post("/subscribe")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validPartySubscription)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").value("Invalid subscription"));
    }

    @Test
    public void testUnsubscribe_Success() throws Exception {
        when(apiService.deleteAllSubscriptions("user@example.com"))
                .thenReturn(new ApiService.Result(true, null));

        mockMvc.perform(delete("/unsubscribe")
                        .param("email", "user@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    public void testUnsubscribe_Failure() throws Exception {
        when(apiService.deleteAllSubscriptions("user@example.com"))
                .thenReturn(new ApiService.Result(false, "Error deleting subscriptions"));

        mockMvc.perform(delete("/unsubscribe")
                        .param("email", "user@example.com"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").value("Error deleting subscriptions"));
    }

    @Test
    public void testNotify_Success() throws Exception {
        when(apiService.notifyAll(anyList()))
                .thenReturn(new ApiService.Result(true, null));

        mockMvc.perform(post("/notify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validNotificationRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    public void testNotify_Failure() throws Exception {
        when(apiService.notifyAll(anyList()))
                .thenReturn(new ApiService.Result(false, "Error sending notifications"));

        mockMvc.perform(post("/notify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validNotificationRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").value("Error sending notifications"));
    }
}
