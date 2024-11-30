package com.freighthub.core.service;

import com.freighthub.core.dto.NotificationDto;
import com.freighthub.core.entity.Notification;
import com.freighthub.core.entity.User;
import com.freighthub.core.repository.NotificationRepository;
import com.freighthub.core.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired
    private UserRepository userRepository;

    @Transactional
    public void markAsRead(int notificationId) {
        notificationRepository.findById(notificationId).ifPresent(notification -> {
            notification.setRead(true);
            notificationRepository.save(notification);
        });
    }

    @Transactional
    public void markAsUnread(int notificationId) {
        notificationRepository.findById(notificationId).ifPresent(notification -> {
            notification.setRead(false);
            notificationRepository.save(notification);
        });
    }

    @Transactional
    public void addNotification(NotificationDto notificationDto) {
        Notification notification = new Notification();
        notification.setMessage(notificationDto.getMessage());
        notification.setUserId(userRepository.findById((long) notificationDto.getUserId()).orElseThrow());
        notificationRepository.save(notification);
    }

    @Transactional
    public List<Notification> getNotificationsForUser(int userId) {
        User user = userRepository.findById((long) userId).orElseThrow();
        return notificationRepository.findByAllByUserId(user);
    }
}
