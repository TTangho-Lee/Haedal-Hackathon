package com.example.parttimecalander.Database.Database;

import androidx.room.Database;

import com.example.parttimecalander.Database.Dao.UserDao;
import com.example.parttimecalander.Database.Dao.WorkDailyDao;
import com.example.parttimecalander.Database.User;
import com.example.parttimecalander.Database.WorkDaily;

@Database(entities = {User.class},version=1)
public abstract class UserDatabase {
    public abstract UserDao userDao();
}
