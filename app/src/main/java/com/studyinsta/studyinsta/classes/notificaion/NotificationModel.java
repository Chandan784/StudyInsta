package com.studyinsta.studyinsta.classes.notificaion;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.text.SimpleDateFormat;
import java.util.Calendar;

@Entity
public class NotificationModel {

    @PrimaryKey(autoGenerate = true)
    public int notificationId;

    @ColumnInfo(name = "notificationTitle")
    public String title;

    @ColumnInfo(name = "notificationBody")
    public String body;

    @ColumnInfo(name = "notificationArrivalTime")
    public String dateValue = getTime();


    private String getTime (){
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("dd/mm/yyy 'at' h:mm a");
        return String.valueOf(format.format(calendar.getTime()));
    }
}

