package com.example.parttimecalander.Database.Database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.parttimecalander.Database.Dao.WorkDailyDao;
import com.example.parttimecalander.Database.WorkDaily;

@Database(entities = {WorkDaily.class},version=1)
public abstract class WorkDailyDatabase extends RoomDatabase {
    public abstract WorkDailyDao workDailyDao();
}
