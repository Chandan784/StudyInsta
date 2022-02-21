package com.studyinsta.studyinsta.classes.notificaion;


import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {NotificationModel.class}, version =  1)
 public abstract  class LocalDatabase extends RoomDatabase {

 public abstract NotificationDao notificationDao();

 private static LocalDatabase INSTANCE;

 public static LocalDatabase getDbInstance(Context context){
  if (INSTANCE == null){
    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), LocalDatabase.class, "LOCAL_DATABASE")
            .allowMainThreadQueries()
            .build();
  }
  return INSTANCE;
 }
}
