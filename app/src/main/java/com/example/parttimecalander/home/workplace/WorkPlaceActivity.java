package com.example.parttimecalander.home.workplace;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.example.parttimecalander.Database.Dao.WorkPlaceDao;
import com.example.parttimecalander.Database.Database.WorkPlaceDatabase;
import com.example.parttimecalander.Database.WorkPlace;
import com.example.parttimecalander.R;

import java.util.List;
import java.util.concurrent.Executors;

public class WorkPlaceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        enableEdgeToEdge();
        setContentView(R.layout.activity_workplace);

        // RecyclerView 초기화
        RecyclerView recyclerView = findViewById(R.id.workplace_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Room 데이터베이스 초기화
        WorkPlaceDatabase database=WorkPlaceDatabase.getDatabase(this);

        WorkPlaceDao workPlaceDao = database.workPlaceDao();
        Button register_btn=(Button)findViewById(R.id.register_btn);

        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WorkPlaceActivity.this, WorkPlaceRegisterActivity.class);
                startActivity(intent);
            }
        });




        Executors.newSingleThreadExecutor().execute(() -> {
            // 데이터 조회 및 어댑터 설정을 위한 스레드 시작
            List<WorkPlace> places = workPlaceDao.getDataAll();
            // 어댑터 설정
            WorkPlaceAdapter adapter = new WorkPlaceAdapter(places);
            recyclerView.setAdapter(adapter);
        });



    }

    private void enableEdgeToEdge() {
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
    }
}
