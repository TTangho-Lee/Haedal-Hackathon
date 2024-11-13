package com.example.parttimecalander;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.example.parttimecalander.Database.Dao.UserDao;
import com.example.parttimecalander.Database.Database.UserDatabase;
import com.example.parttimecalander.Database.User;

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
    }
}
