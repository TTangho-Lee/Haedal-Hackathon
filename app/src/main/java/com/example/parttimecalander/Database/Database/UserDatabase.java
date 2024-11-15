package com.example.parttimecalander.Database.Database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.parttimecalander.Database.Dao.UserDao;
import com.example.parttimecalander.Database.User;

@Database(entities = {User.class},version=1)
public abstract class UserDatabase extends RoomDatabase {
    public abstract UserDao userDao();
}
