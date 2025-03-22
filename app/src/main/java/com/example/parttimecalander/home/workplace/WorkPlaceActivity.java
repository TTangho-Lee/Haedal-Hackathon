package com.example.parttimecalander.home.workplace;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.parttimecalander.Database.Dao.WorkPlaceDao;
import com.example.parttimecalander.Database.Database.PartTimeDatabase;
import com.example.parttimecalander.Database.data.WorkPlace;
import com.example.parttimecalander.R;
import com.example.parttimecalander.home.HomeActivity;

import java.util.List;
import java.util.concurrent.Executors;

public class WorkPlaceActivity extends AppCompatActivity {
    ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        enableEdgeToEdge();
        setContentView(R.layout.activity_workplace);

        //뒤로가기 버튼 초기화
        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WorkPlaceActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });

        // RecyclerView 초기화
        RecyclerView recyclerView = findViewById(R.id.workplace_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Room 데이터베이스 초기화
        PartTimeDatabase database = PartTimeDatabase.getDatabase(this);

        WorkPlaceDao workPlaceDao = database.workPlaceDao();

        Button register_btn = findViewById(R.id.register_btn);
        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WorkPlaceActivity.this, WorkPlaceRegisterActivity.class);
                intent.putExtra("isRegister", true);
                startActivity(intent);
                finish();
            }
        });

        Executors.newSingleThreadExecutor().execute(() -> {
            // 데이터 조회 및 어댑터 설정을 위한 스레드 시작
            List<WorkPlace> places = workPlaceDao.activeWorkingPlace();
            // 어댑터 설정
            WorkPlaceAdapter adapter = new WorkPlaceAdapter(this, places);
            recyclerView.setAdapter(adapter);
        });
    }

    private void enableEdgeToEdge() {
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
    }
}
