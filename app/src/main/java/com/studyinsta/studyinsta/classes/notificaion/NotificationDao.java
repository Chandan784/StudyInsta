package com.studyinsta.studyinsta.classes.notificaion;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface NotificationDao {

    @Query("SELECT * FROM notificationmodel")
    List<NotificationModel> getAllNotificaitons();

    @Insert
    void insertNotification(NotificationModel... notificationModels);

    @Delete
    void deleteNotification(NotificationModel notificationModel);
}
