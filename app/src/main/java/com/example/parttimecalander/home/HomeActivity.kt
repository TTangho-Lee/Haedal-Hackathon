package com.example.parttimecalander.home

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.room.Room.databaseBuilder
import com.example.parttimecalander.Database.Dao.UserDao
import com.example.parttimecalander.Database.Database.UserDatabase
import com.example.parttimecalander.Database.User
import com.example.parttimecalander.R
import java.time.LocalDateTime

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)

        val userDatabase:UserDatabase = databaseBuilder(
            applicationContext,
            UserDatabase::class.java, "유저정보"
        )
            .fallbackToDestructiveMigration()
            .allowMainThreadQueries()
            .build()
        val userDao = userDatabase.userDao()
        val user1 = User()
        user1.name="이승호"
        user1.money = 10000
        user1.goal = 100000
        user1.recentUpdate = LocalDateTime.now()
        userDao.setInsertData(user1)


        val user_text:TextView=findViewById(R.id.user_text)
        val user = userDao.getDataAll()[0]
        user_text.text=user.name+"님, 열심히 땀 흘려\n"+user.money+"원이나 모았어요!"
    }
}