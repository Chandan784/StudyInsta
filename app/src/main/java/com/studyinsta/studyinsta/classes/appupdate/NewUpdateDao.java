package com.studyinsta.studyinsta.classes.appupdate;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface NewUpdateDao {

    @Query("SELECT * FROM newupdatemodel")
    List<NewUpdateModel> getUpdateList();

    @Insert
    void insertUpdateInfo(NewUpdateModel... newUpdateModels);

    @Delete
    void deleteUpdateInfo(NewUpdateModel newUpdateModel);
}
