package com.example.parttimecalander.Database.Database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.parttimecalander.Database.Dao.WorkPlaceDao;
import com.example.parttimecalander.Database.WorkPlace;

@Database(entities = {WorkPlace.class},version=1)
public abstract class WorkPlaceDatabase extends RoomDatabase {
    public abstract WorkPlaceDao workPlaceDao();
    private static volatile WorkPlaceDatabase INSTANCE;
    public static WorkPlaceDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (WorkPlaceDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    WorkPlaceDatabase.class, "place_database")
                            .fallbackToDestructiveMigration() // 스키마 변경 시 데이터를 삭제하고 새로 생성
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
