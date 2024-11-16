package com.example.parttimecalander.Database.Database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.parttimecalander.Database.Dao.WorkDailyDao;
import com.example.parttimecalander.Database.WorkDaily;

@Database(entities = {WorkDaily.class},version=1)
public abstract class WorkDailyDatabase extends RoomDatabase {
    public abstract WorkDailyDao workDailyDao();
    private static volatile WorkDailyDatabase INSTANCE;
    public static WorkDailyDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (WorkDailyDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    WorkDailyDatabase.class, "daily_database")
                            .fallbackToDestructiveMigration() // 스키마 변경 시 데이터를 삭제하고 새로 생성
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
