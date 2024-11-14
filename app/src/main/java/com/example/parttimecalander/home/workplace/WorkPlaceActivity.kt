package com.example.parttimecalander.home.workplace

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.parttimecalander.R

class WorkPlaceActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_workplace)
        val recyclerView: RecyclerView = findViewById(R.id.workplace_recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        //TODO:DB에서 데이터 읽어오기
        /*
        val workPlaces = //DB에서 읽어오기, workPlaces는 리스트, workplaceAdapter 참고
         */

        // 어댑터 설정
        val adapter = WorkPlaceAdapter(workPlaces)
        recyclerView.adapter = adapter
    }
}
