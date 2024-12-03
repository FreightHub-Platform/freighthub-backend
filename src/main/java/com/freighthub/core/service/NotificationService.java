package com.freighthub.core.service;

import com.freighthub.core.dto.NotificationDto;
import com.freighthub.core.entity.Notification;
import com.freighthub.core.entity.Order;
import com.freighthub.core.entity.User;
import com.freighthub.core.repository.NotificationRepository;
import com.freighthub.core.repository.OrderRepository;
import com.freighthub.core.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired
    private OrderRepository orderRepository;
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
        notification.setNotificationTime(notificationDto.getNotificationTime());
        notificationRepository.save(notification);
    }

    @Transactional
    public List<Notification> getNotificationsForUser(int userId) {
        User user = userRepository.findById((long) userId).orElseThrow();
        return notificationRepository.findByAllByUserId(user);
    }

    @Transactional
    public void addNotificationRoute(String s, int OrderId) {
        Order order = orderRepository.findByid(OrderId);

        LocalDateTime now = LocalDateTime.now();
        String formattedDateTime = now.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        Notification notification = new Notification();
        notification.setMessage(s);
        notification.setUserId(order.getUserId());
        notification.setNotificationTime(formattedDateTime);
        notificationRepository.save(notification);
    }

    @Transactional
    public void markAllAsRead(int userId) {
        User user = userRepository.findById((long) userId).orElseThrow();
        List<Notification> notifications = notificationRepository.findByAllByUserId(user);
        notifications.forEach(notification -> {
            notification.setRead(true);
            notificationRepository.save(notification);
        });
    }
}
