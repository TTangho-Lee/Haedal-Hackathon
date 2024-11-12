package com.example.parttimecalander.Database.Dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.parttimecalander.Database.WorkInfo;
import com.example.parttimecalander.Database.WorkPlace;

import java.util.List;

@Dao
public interface WorkPlaceDao {
    @Insert
    void setInsertData(WorkPlace data);
    @Update
    void setUpdateData(WorkPlace data);
    @Delete
    void setDeleteData(WorkPlace data);
    @Query("SELECT * FROM WorkPlace")
    List<WorkPlace> getDataAll();
}
