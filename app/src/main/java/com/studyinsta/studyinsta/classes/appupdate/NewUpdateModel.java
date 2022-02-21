package com.studyinsta.studyinsta.classes.appupdate;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class NewUpdateModel {


    @PrimaryKey(autoGenerate = true)
    public int setId;

    @ColumnInfo(name = "versionNumber")
    public long version;

    @ColumnInfo(name = "queryRanTime")
    public Long queryRanTime;
}

