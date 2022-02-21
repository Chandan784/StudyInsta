package com.studyinsta.studyinsta.classes.appupdate;


import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {NewUpdateModel.class}, version =  1)
public abstract  class NewUpdateDatabase extends RoomDatabase {

    public abstract NewUpdateDao newUpdateDao();

    private static NewUpdateDatabase INSTANCE;

    public static NewUpdateDatabase getDbInstance(Context context){
        if (INSTANCE == null){
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), NewUpdateDatabase.class, "UPDATE_INFO")
                    .allowMainThreadQueries()
                    .build();
        }
        return INSTANCE;
    }
}