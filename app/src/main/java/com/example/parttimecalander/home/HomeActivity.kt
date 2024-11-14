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


    }
}