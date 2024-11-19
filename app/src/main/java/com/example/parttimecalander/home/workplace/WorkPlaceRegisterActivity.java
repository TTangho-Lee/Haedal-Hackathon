package com.example.parttimecalander.home.workplace;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.room.Room;

import com.example.parttimecalander.Database.Dao.WorkDailyDao;
import com.example.parttimecalander.Database.Dao.WorkPlaceDao;
import com.example.parttimecalander.Database.Database.WorkPlaceDatabase;
import com.example.parttimecalander.Database.User;
import com.example.parttimecalander.Database.WorkPlace;
import com.example.parttimecalander.R;
import com.example.parttimecalander.databinding.ActivityWorkplaceRegisterBinding;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;

public class WorkPlaceRegisterActivity extends AppCompatActivity {
    private ActivityWorkplaceRegisterBinding binding;
    private WorkPlaceDatabase placeDatabase;
    private WorkPlaceDao placeDao;

    private String[] startTimes = new String[7]; // 시작 시간 저장 (월~일)
    private String[] endTimes = new String[7];   // 종료 시간 저장 (월~일)

    private char[] day = {'0', '0', '0', '0', '0', '0', '0'};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // View Binding 초기화
        binding = ActivityWorkplaceRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Database 초기화
        placeDatabase = WorkPlaceDatabase.getDatabase(this);
        placeDao = placeDatabase.workPlaceDao();

        // setupDay 호출
        setupDay(binding.checkboxMonday, binding.timeMonday, binding.startTimeMonday, binding.endTimeMonday);
        setupDay(binding.checkboxTuesday, binding.timeTuesday, binding.startTimeTuesday, binding.endTimeTuesday);
        setupDay(binding.checkboxWednesday, binding.timeWednesday, binding.startTimeWednesday, binding.endTimeWednesday);
        setupDay(binding.checkboxThursday, binding.timeThursday, binding.startTimeThursday, binding.endTimeThursday);
        setupDay(binding.checkboxFriday, binding.timeFriday, binding.startTimeFriday, binding.endTimeFriday);
        setupDay(binding.checkboxSaturday, binding.timeSaturday, binding.startTimeSaturday, binding.endTimeSaturday);
        setupDay(binding.checkboxSunday, binding.timeSunday, binding.startTimeSunday, binding.endTimeSunday);

        List<String> circleColor = List.of("#FF0000", "#0000FF");
        SpinnerColorAdapter colorAdapter = new SpinnerColorAdapter(this,circleColor);
        binding.contentColor.setAdapter(colorAdapter);

        List<String> type = List.of("카페","식당");
        SpinnerAdapter typeAdapter = new SpinnerAdapter(this,type);
        binding.contentType.setAdapter(typeAdapter);

        int baseYear = 2024;

// 2014년부터 2034년까지 연도 리스트 생성
        List<String> years = new ArrayList<>();
        for (int i = baseYear - 10; i <= baseYear + 10; i++) {
            years.add(String.valueOf(i));
        }

// ArrayAdapter 생성 및 연결
        SpinnerAdapter startYearAdapter = new SpinnerAdapter(this,years);
        binding.contentWorkstartyear.setAdapter(startYearAdapter);

// 기본 선택값으로 2024년 설정
        binding.contentWorkstartyear.setSelection(years.indexOf(String.valueOf(baseYear)));

// ArrayAdapter 생성 및 연결
        SpinnerAdapter endYearAdapter = new SpinnerAdapter(this,years);
        binding.contentWorkendyear.setAdapter(endYearAdapter);

// 기본 선택값으로 2024년 설정
        binding.contentWorkendyear.setSelection(years.indexOf(String.valueOf(baseYear)));

// 1월부터 12월까지 월 리스트 생성
        List<String> months = Arrays.asList(
                "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"
        );

// ArrayAdapter 생성 및 연결
        SpinnerAdapter startMonthAdapter = new SpinnerAdapter(this, months);
        binding.contentWorkstartmonth.setAdapter(startMonthAdapter);

// 기본 선택값으로 1월 설정
        binding.contentWorkstartmonth.setSelection(0);

// ArrayAdapter 생성 및 연결
        SpinnerAdapter endMonthAdapter = new SpinnerAdapter(this, months);
        binding.contentWorkendmonth.setAdapter(endMonthAdapter);

// 기본 선택값으로 1월 설정
        binding.contentWorkendmonth.setSelection(0);

// 1일부터 31일까지 날짜 리스트 생성
        List<String> dates = new ArrayList<>();
        for (int i = 1; i <= 31; i++) {
            dates.add(String.valueOf(i));
        }

// ArrayAdapter 생성 및 연결
        SpinnerAdapter startDateAdapter = new SpinnerAdapter(this, dates);
        binding.contentWorkstartday.setAdapter(startDateAdapter);

// 기본 선택값으로 1일 설정
        binding.contentWorkstartday.setSelection(0);

// ArrayAdapter 생성 및 연결
        SpinnerAdapter endDateAdapter = new SpinnerAdapter(this, dates);

        binding.contentWorkendday.setAdapter(endDateAdapter);

// 기본 선택값으로 1일 설정
        binding.contentWorkendday.setSelection(0);

        // 등록 버튼 클릭 리스너
        binding.registerButton.setOnClickListener(v -> {
            Executors.newSingleThreadExecutor().execute(() -> {
                WorkPlace new_workplace = new WorkPlace();
                new_workplace.placeName = binding.contentWorkplaceName.getText().toString();
                new_workplace.ColorHex = binding.contentColor.getSelectedItem().toString();
                new_workplace.type = binding.contentType.getSelectedItem().toString();
                new_workplace.usualPay = Integer.parseInt(binding.contentSalary.getText().toString());

                if (binding.radioButtonYes.isChecked()) {
                    new_workplace.isJuhyu = true;
                } else if (binding.radioButtonNo.isChecked()) {
                    new_workplace.isJuhyu = false;
                }

                new_workplace.startDate = binding.contentWorkstartyear.getSelectedItem().toString() + '-'
                        + binding.contentWorkstartmonth.getSelectedItem().toString() + '-'
                        + binding.contentWorkstartday.getSelectedItem().toString() + ' '
                        + "00:00:00";

                new_workplace.endDate = binding.contentWorkendyear.getSelectedItem().toString() + '-'
                        + binding.contentWorkendmonth.getSelectedItem().toString() + '-'
                        + binding.contentWorkendday.getSelectedItem().toString() + ' '
                        + "00:00:00";
                new_workplace.day = String.valueOf(day);
                new_workplace.startTime = combineStartTimes(startTimes);
                new_workplace.endTime = combineStartTimes(endTimes);
                // 데이터베이스에 삽입
//                System.out.println(new_workplace.placeName);
//                System.out.println(new_workplace.type);
//                System.out.println(new_workplace.isJuhyu);
//                System.out.println(new_workplace.startDate);
//                System.out.println(new_workplace.endDate);
//                System.out.println(new_workplace.ColorHex);
//                System.out.println(new_workplace.day);
//                System.out.println(new_workplace.startTime);
                placeDao.setInsertData(new_workplace);
            });

            // 다음 액티비티로 이동
            Intent intent = new Intent(WorkPlaceRegisterActivity.this, WorkPlaceActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void setupDay(CheckBox checkBox, LinearLayout timeLayout, Button startTimeButton, Button endTimeButton) {
        int k = Integer.parseInt(checkBox.getTag().toString());
        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                day[k] = '1';
                timeLayout.setVisibility(View.VISIBLE);
            } else {
                day[k] = '0';
                timeLayout.setVisibility(View.GONE);
                startTimes[k] = null; // 체크 해제 시 시간 초기화
                endTimes[k] = null;
            }
        });

        startTimeButton.setOnClickListener(v -> {
            showMaterialTimePicker(startTimeButton, 9, 0,k,true);
        });

        endTimeButton.setOnClickListener(v -> {
            showMaterialTimePicker(endTimeButton, 18, 0,k,false);
        });
    }

    private void showMaterialTimePicker(Button targetButton, int defaultHour, int defaultMinute,int k,boolean isStartTime) {
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

            if (isStartTime) {
                startTimes[k] = time+":00";
            } else {
                endTimes[k] = time+":00";
            }
        });

        picker.show(getSupportFragmentManager(), "MATERIAL_TIME_PICKER");
    }
    private String combineStartTimes(String[] times) {
        StringBuilder combinedTimes = new StringBuilder();

        for (String time : times) {
            if (time != null) { // null 값은 건너뛰기
                if (combinedTimes.length() > 0) {
                    combinedTimes.append(","); // 이전 값이 있다면 ',' 추가
                }
                combinedTimes.append(time);
            }
        }

        return combinedTimes.toString(); // 결합된 문자열 반환
    }
}