package com.example.parttimecalander.Database.Database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.parttimecalander.Database.Dao.WorkInfoDao;
import com.example.parttimecalander.Database.WorkInfo;

@Database(entities = {WorkInfo.class},version=1)
public abstract class WorkInfoDatabase extends RoomDatabase {
    public abstract WorkInfoDao workInfoDao();
}
