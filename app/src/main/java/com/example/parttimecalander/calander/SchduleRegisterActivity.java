package com.example.parttimecalander.calander;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.parttimecalander.Database.Dao.WorkDailyDao;
import com.example.parttimecalander.Database.Dao.WorkPlaceDao;
import com.example.parttimecalander.Database.Database.WorkDailyDatabase;
import com.example.parttimecalander.Database.Database.WorkPlaceDatabase;
import com.example.parttimecalander.Database.WorkDaily;
import com.example.parttimecalander.Database.WorkPlace;
import com.example.parttimecalander.R;
import com.example.parttimecalander.databinding.ActivitySchduleRegisterBinding;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Executors;

public class SchduleRegisterActivity extends AppCompatActivity {
    private ActivitySchduleRegisterBinding binding;
    private WorkPlaceDatabase placeDatabase;
    private WorkPlaceDao placeDao;
    private WorkDailyDatabase dailyDatabase;
    private WorkDailyDao dailyDao;
    private ArrayList<Integer> years;
    private ArrayList<Integer> months;
    private ArrayList<Integer> daysInMonth;
    private List<WorkPlace> workPlaces;
    private WorkDaily new_workDaily;
    private String startTime, endTime;

    private void init(){
        //binding 초기화
        binding = ActivitySchduleRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Database 초기화
        placeDatabase = WorkPlaceDatabase.getDatabase(this);
        placeDao = placeDatabase.workPlaceDao();
        dailyDatabase = WorkDailyDatabase.getDatabase(this);
        dailyDao = dailyDatabase.workDailyDao();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schdule_register);

        init();
        setUpDaySpinner();
        setWorkPlaceSpinner();
        setStartandEndTimeButton();
        setRegisterButton();

    }
    private void setUpDaySpinner(){
        int baseYear = 2024;
        years = new ArrayList<>();
        months = new ArrayList<>();
        daysInMonth = new ArrayList<>();

        for(int i = baseYear -10; i< baseYear + 10;i++){
            years.add(i);
        }
        for (int i = 1; i <= 12; i++) {
            months.add(i);
        }
        for (int i = 1; i <= 31; i++) {
            daysInMonth.add(i);
        }
        ArrayAdapter<Integer> yearAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, years);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.contentYear.setAdapter(yearAdapter);

        ArrayAdapter<Integer> monthAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, months);
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.contentMonth.setAdapter(monthAdapter);

        ArrayAdapter<Integer> dayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, daysInMonth);
        dayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.contentDay.setAdapter(dayAdapter);

        //월에 맞춰서 일의 개수 선택하기
        binding.contentMonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                int month = (int) binding.contentMonth.getSelectedItem();
                int year = (int) binding.contentYear.getSelectedItem();

                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month-1, 1);
                int daysInMonthCount = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
                daysInMonth.clear();
                for (int i = 1; i <= daysInMonthCount; i++) {
                    daysInMonth.add(i);
                }
                dayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing
            }
        });
        //2024선택
        binding.contentYear.setSelection(10);
    }
    private void setWorkPlaceSpinner(){
        List<String> placeNames = new ArrayList<>();
        new Thread(()->{
            workPlaces = new ArrayList<>();
            workPlaces = placeDao.getDataAll();
            for(WorkPlace wp : workPlaces){
                placeNames.add(wp.placeName);
            }

            runOnUiThread(()->{
                ArrayAdapter<String> WPAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, placeNames);
                WPAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                binding.contentWorkplace.setAdapter(WPAdapter);
            });

        }).start();
    }
    private void setStartandEndTimeButton(){
        binding.contentStartTime.setOnClickListener(v -> {
            showMaterialTimePicker(binding.contentStartTime, 9, 0,true);
        });

        binding.contentEndTime.setOnClickListener(v -> {
            showMaterialTimePicker(binding.contentEndTime, 18, 0,false);
        });
    }
    private void showMaterialTimePicker(TextView targetButton, int defaultHour, int defaultMinute, boolean isStartTime) {
        MaterialTimePicker picker = new MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H) // 24시간 형식
                .setHour(defaultHour) // 기본 시간 설정
                .setMinute(defaultMinute) // 기본 분 설정
                .build();

        picker.addOnPositiveButtonClickListener(v -> {
            // 시간과 분을 받아 30분 단위로 조정
            int hour = picker.getHour();
            int minute = (picker.getMinute() < 30) ? 0 : 30;
            String time = String.format("%02d:%02d", hour, minute);
            targetButton.setText(time);
            if(isStartTime){
                startTime = time + ":00";
            }else{
                endTime = time + ":00";
            }

        });

        picker.show(getSupportFragmentManager(), "MATERIAL_TIME_PICKER");
    }
    private void setRegisterButton(){
        binding.registerButton.setOnClickListener(v-> Executors.newSingleThreadExecutor().execute(()->{
            //TODO: 유효값인지 검사하는 로직

            //근무지 id
            String placeName = binding.contentWorkplace.getSelectedItem().toString();
            int id = placeDao.findId(placeName);

            String year = binding.contentYear.getSelectedItem().toString();
            String month = binding.contentMonth.getSelectedItem().toString();
            String day = binding.contentDay.getSelectedItem().toString();
            if(month.length() == 1){month = '0' + month;}
            if(day.length() == 1){day = '0' + day;}
            String workDay = year + '-' + month + '-' + day;

            new_workDaily = new WorkDaily(workDay + " " + startTime, workDay + " " + endTime, id);
            dailyDao.setInsertData(new_workDaily);
            runOnUiThread(() -> {
                Toast.makeText(this, workDay + "날의 일정 추가", Toast.LENGTH_SHORT).show();
            });

        }));
    }
}