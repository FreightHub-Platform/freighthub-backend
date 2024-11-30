package com.freighthub.core.repository;

import com.freighthub.core.entity.Notification;
import com.freighthub.core.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Integer> {

    @Transactional
    void findByUserId(User userId);

    @Transactional
    @Query("SELECT n FROM Notification n WHERE n.userId = :user")
    List<Notification> findByAllByUserId(User user);
}
