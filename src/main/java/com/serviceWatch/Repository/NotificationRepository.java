package com.serviceWatch.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.serviceWatch.Entity.Notification;
import com.serviceWatch.Entity.User;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByUserOrderByCreatedAtDesc(User user);

}