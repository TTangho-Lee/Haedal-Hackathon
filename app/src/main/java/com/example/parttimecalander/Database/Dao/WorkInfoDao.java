package com.example.parttimecalander.Database.Dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.parttimecalander.Database.User;
import com.example.parttimecalander.Database.WorkInfo;

import java.util.List;

@Dao
public interface WorkInfoDao {
    @Insert
    void setInsertData(WorkInfo data);
    @Update
    void setUpdateData(WorkInfo data);
    @Delete
    void setDeleteData(WorkInfo data);
    @Query("SELECT * FROM WorkInfo")
    List<WorkInfo> getDataAll();
}
