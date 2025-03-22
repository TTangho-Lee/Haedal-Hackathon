package com.example.parttimecalander.Database.Database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.parttimecalander.Database.Converters;
import com.example.parttimecalander.Database.Dao.UserDao;
import com.example.parttimecalander.Database.Dao.WorkDailyDao;
import com.example.parttimecalander.Database.Dao.WorkPlaceDao;
import com.example.parttimecalander.Database.data.User;
import com.example.parttimecalander.Database.data.WorkDaily;
import com.example.parttimecalander.Database.data.WorkPlace;

@Database(entities = {User.class, WorkDaily.class, WorkPlace.class},version=2)
@TypeConverters({Converters.class})
public abstract class PartTimeDatabase extends RoomDatabase {
    public abstract UserDao userDao();
    public abstract WorkDailyDao workDailyDao();
    public abstract WorkPlaceDao workPlaceDao();
    private static volatile PartTimeDatabase INSTANCE;
    public static PartTimeDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (PartTimeDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    PartTimeDatabase.class, "user_database")
                            .fallbackToDestructiveMigration() // 스키마 변경 시 데이터를 삭제하고 새로 생성
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
