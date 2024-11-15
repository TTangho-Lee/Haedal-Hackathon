package com.example.parttimecalander.home.workplace;

import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.example.parttimecalander.Database.Dao.WorkPlaceDao;
import com.example.parttimecalander.Database.Database.WorkPlaceDatabase;
import com.example.parttimecalander.Database.WorkPlace;
import com.example.parttimecalander.R;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.Executor;
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
        WorkPlaceDatabase database = Room.databaseBuilder(
                getApplicationContext(),
                WorkPlaceDatabase.class, "place"
        ).fallbackToDestructiveMigration().allowMainThreadQueries().build();

        WorkPlaceDao workPlaceDao = database.workPlaceDao();





        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                // 데이터 조회 및 어댑터 설정을 위한 스레드 시작
                List<WorkPlace> places = workPlaceDao.getDataAll();
                // 어댑터 설정
                WorkPlaceAdapter adapter = new WorkPlaceAdapter(places);
                recyclerView.setAdapter(adapter);
            }
        });


    }

    private void enableEdgeToEdge() {
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
    }
}
