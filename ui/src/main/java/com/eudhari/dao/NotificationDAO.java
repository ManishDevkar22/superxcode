package com.eudhari.dao;

import com.eudhari.model.NotificationModel;
import java.util.List;

public interface NotificationDAO {
    void saveNotification(NotificationModel notification);

    void markAsRead(String notificationId);

    void markAllAsRead(String receiverId);

    List<NotificationModel> getNotificationsByReceiver(String receiverId);

    int getNotificationCount();
}
