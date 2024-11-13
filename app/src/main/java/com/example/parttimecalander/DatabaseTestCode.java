package com.example.parttimecalander;

import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.example.parttimecalander.Database.Dao.UserDao;
import com.example.parttimecalander.Database.Database.UserDatabase;
import com.example.parttimecalander.Database.User;

import java.time.LocalDateTime;

public class DatabaseTestCode extends AppCompatActivity {
    UserDatabase userDatabase;
    UserDao userDao;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        userDatabase= Room.databaseBuilder(getApplicationContext(), UserDatabase.class,"유저정보")
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build();
        userDao=userDatabase.userDao();
        User user1 = new User();
        user1.money=10000;
        user1.goal=100000;
        user1.recentUpdate= LocalDateTime.now();
        userDao.setInsertData(user1);

        User user2=userDao.getDataAll().get(0);
        user2.money+=1000;
        user2.recentUpdate=LocalDateTime.now();
        userDao.setUpdateData(user2);
    }
}
