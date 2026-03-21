package com.example.soen345.service;

import android.util.Log;

import com.example.soen345.BuildConfig;
import com.twilio.type.PhoneNumber;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class NotificationService {
    public static final String ACCOUNT_SID = BuildConfig.TWILIO_ACCOUNT_SID;
    public static final String AUTH_TOKEN = BuildConfig.TWILIO_AUTH_TOKEN;

    public static final String sourcePhoneNumber = BuildConfig.TWILIO_SRC_PHONE;
    private final SmsSender smsSender;

    public NotificationService() {
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
        this.smsSender = (to, from, body) -> {
            Message.creator(
                    new PhoneNumber(to),
                    new PhoneNumber(from),
                    body
            ).create();
        };
    }
    public NotificationService(SmsSender smsSender) {
        this.smsSender = smsSender;
    }

    public void sendSmsMessage(String targetPhone, String messageText, NotificationCallback callback) {
        new Thread(() -> {
            try {
                Log.i("SMS", "Attempting to send SMS to: " + targetPhone);
                smsSender.send(targetPhone, sourcePhoneNumber, messageText);
                callback.onSuccess();
            } catch (Exception e) {
                Log.e("SMS", "SMS failed: " + e.getMessage());
                callback.onFailure(e);
            }
        }).start();
    }

    public void sendEmail(String toEmail, String subject, String body, NotificationCallback callback) {
        new Thread(() -> {
            try {
                Properties props = new Properties();
                props.put("mail.smtp.auth", "true");
                props.put("mail.smtp.ssl.enable", "true");
                props.put("mail.smtp.host", "smtp.gmail.com");
                props.put("mail.smtp.port", "465");
                props.put("mail.smtp.ssl.trust", "smtp.gmail.com");

                Session session = Session.getInstance(props, new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(
                                BuildConfig.GMAIL_EMAIL,
                                BuildConfig.GMAIL_APP_PASSWORD
                        );
                    }
                });

                MimeMessage message = new MimeMessage(session);
                message.setFrom(new InternetAddress(BuildConfig.GMAIL_EMAIL));
                message.setRecipients(javax.mail.Message.RecipientType.TO, InternetAddress.parse(toEmail));
                message.setSubject(subject);
                message.setText(body);

                Transport.send(message);
                callback.onSuccess();
            } catch (MessagingException e) {
                callback.onFailure(e);
            }
        }).start();
    }

    public interface NotificationCallback{
        void onSuccess();
        void onFailure(Exception e);
    }

    public interface SmsSender {
        void send(String to, String from, String body) throws Exception;
    }
}
