package com.example.parttimecalander.Database.Dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.parttimecalander.Database.data.WorkDaily;

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
    @Query("SELECT * FROM daily WHERE daily.startTime = :selectedDate")
    List<WorkDaily> getSchedulesForDate(String selectedDate);
    @Query("DELETE FROM daily WHERE startTime > :selectedDate AND placeId = :placeId")
    void deleteSchedulesAfterDateForPlace(String selectedDate, int placeId);
    @Query("SELECT * FROM daily WHERE startTime BETWEEN :firstDay AND :lastDay")
    List<WorkDaily> getSchedulesBetweenDays(String firstDay, String lastDay);
    @Query("SELECT * FROM daily WHERE startTime < :selectedDate AND endTime > :selectedDate")
    WorkDaily getScheduleForDate(String selectedDate);
    @Query("DELETE FROM daily WHERE placeId = :placeId")
    void setDeleteDataById(int placeId);
    @Delete
    void delete(WorkDaily workDaily);
}

