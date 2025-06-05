package com.p2p.transport.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.p2p.transport.model.Notification;
import com.p2p.transport.model.User;
import com.p2p.transport.repository.NotificationRepository;
import com.p2p.transport.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final FirebaseMessaging firebaseMessaging;

    @Value("${kafka.topic.notifications}")
    private String notificationTopic;

    public NotificationService(NotificationRepository notificationRepository,
                               UserRepository userRepository,
                               KafkaTemplate<String, String> kafkaTemplate,
                               FirebaseMessaging firebaseMessaging) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.firebaseMessaging = firebaseMessaging;
    }

    @Transactional
    public void sendNotification(UUID userId, String message) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Notification notification = Notification.builder()
                .user(user)
                .message(message)
                .build();
        notificationRepository.save(notification);

        kafkaTemplate.send(notificationTopic, userId.toString(), message);

        if (user.getFcmToken() != null && !user.getFcmToken().isEmpty()) {
            try {
                Message fcmMessage = Message.builder()
                        .setToken(user.getFcmToken())
                        .putData("message", message)
                        .build();
                firebaseMessaging.send(fcmMessage);
            } catch (Exception e) {
                System.err.println("Failed to send FCM notification: " + e.getMessage());
            }
        }
    }

    public List<Notification> getNotifications(UUID userId) {
        return notificationRepository.findByUserId(userId);
    }
}