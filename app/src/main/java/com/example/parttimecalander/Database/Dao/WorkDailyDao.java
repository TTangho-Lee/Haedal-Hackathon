package com.example.parttimecalander.Database.Dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.parttimecalander.Database.WorkDaily;

import java.util.List;

@Dao
public interface WorkDailyDao {
    @Insert
    void setInsertData(WorkDaily data);
    @Update
    void setUpdateData(WorkDaily data);
    @Delete
    void setDeleteData(WorkDaily data);
    @Query("SELECT * FROM daily")
    List<WorkDaily> getDataAll();
    @Query("SELECT * FROM daily WHERE DATE(daily.startTime) = :selectedDate")
    List<WorkDaily> getSchedulesForDate(String selectedDate);
    @Delete
    void delete(WorkDaily workDaily);
}

