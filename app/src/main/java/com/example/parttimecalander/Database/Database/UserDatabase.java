package com.example.parttimecalander.Database.Database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.parttimecalander.Database.Dao.UserDao;
import com.example.parttimecalander.Database.User;

@Database(entities = {User.class},version=1)
public abstract class UserDatabase extends RoomDatabase {
    public abstract UserDao userDao();
    private static volatile UserDatabase INSTANCE;
    public static UserDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (UserDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    UserDatabase.class, "user_database")
                            .fallbackToDestructiveMigration() // 스키마 변경 시 데이터를 삭제하고 새로 생성
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
