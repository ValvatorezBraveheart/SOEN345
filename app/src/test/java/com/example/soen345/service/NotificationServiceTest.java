package com.example.soen345.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class NotificationServiceTest {

    @Mock
    private NotificationService.SmsSender mockSmsSender;
    @Mock private NotificationService.NotificationCallback mockCallback;
    private final String fakePhoneNumber = "+1234567890";

    private NotificationService service;

    @BeforeEach
    public void setUp() {
        service = new NotificationService(mockSmsSender);
    }

    @Test
    public void sendSmsMessage_success_callsOnSuccess() throws Exception {
        doNothing().when(mockSmsSender).send(any(), any(), any());

        service.sendSmsMessage(fakePhoneNumber, "Testing", mockCallback);

        // Wait for background thread
        Thread.sleep(500);

        verify(mockSmsSender).send(fakePhoneNumber, NotificationService.sourcePhoneNumber, "Testing");
        verify(mockCallback).onSuccess();
    }

    @Test
    public void sendSmsMessage_failure_callsOnFailure() throws Exception {
        Exception fakeException = new Exception("Twilio error");
        doThrow(fakeException).when(mockSmsSender).send(any(), any(), any());

        service.sendSmsMessage(fakePhoneNumber, "Testing", mockCallback);

        Thread.sleep(500);

        verify(mockCallback).onFailure(fakeException);
    }

    @Test
    public void sendSmsMessage_nullPhone_callsOnFailure() throws Exception {
        Exception fakeException = new Exception("Invalid phone number");
        doThrow(fakeException).when(mockSmsSender).send(isNull(), any(), any());

        service.sendSmsMessage(null, "Testing", mockCallback);

        Thread.sleep(500);

        verify(mockCallback).onFailure(any());
    }

    @Test
    public void sendSmsMessage_emptyMessage_callsOnFailure() throws Exception {
        Exception fakeException = new Exception("Empty message");
        doThrow(fakeException).when(mockSmsSender).send(any(), any(), eq(""));

        service.sendSmsMessage(fakePhoneNumber, "", mockCallback);

        Thread.sleep(500);

        verify(mockCallback).onFailure(any());
    }
}