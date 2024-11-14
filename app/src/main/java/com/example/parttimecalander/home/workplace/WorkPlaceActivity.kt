package com.example.parttimecalander.home.workplace

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.parttimecalander.Database.Dao.WorkPlaceDao
import com.example.parttimecalander.Database.Database.WorkPlaceDatabase
import com.example.parttimecalander.Database.WorkPlace
import com.example.parttimecalander.R
import java.time.LocalDate
import kotlin.jvm.Throws

class WorkPlaceActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_workplace)
        val recyclerView: RecyclerView = findViewById(R.id.workplace_recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val database = Room.databaseBuilder(
            applicationContext,
            WorkPlaceDatabase::class.java,"place"
        ).build()

        val workPlaceDao=database.workPlaceDao()
        val place: WorkPlace? = null
        if (place != null) {
            place.placeName="aaa"
            place.type="bbb"
            place.isJuhyu= false
            place.usualPay=10000
            place.startDate=LocalDate.now()
            place.endDate=LocalDate.now()
            place.isExpanded=false
            place.ColorHex="red"

        }
        workPlaceDao.setInsertData(place)
        Thread{
            val places=workPlaceDao.dataAll
            // 어댑터 설정
            val adapter = WorkPlaceAdapter(places)
            recyclerView.adapter = adapter
        }

    }
}
