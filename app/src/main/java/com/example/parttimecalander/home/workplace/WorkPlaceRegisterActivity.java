package com.example.parttimecalander.home.workplace;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.example.parttimecalander.Database.Dao.WorkDailyDao;
import com.example.parttimecalander.Database.Dao.WorkPlaceDao;
import com.example.parttimecalander.Database.Database.PartTimeDatabase;
import com.example.parttimecalander.Database.data.WorkDaily;
import com.example.parttimecalander.Database.data.WorkPlace;
import com.example.parttimecalander.databinding.ActivityWorkplaceRegisterBinding;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Executors;

public class WorkPlaceRegisterActivity extends AppCompatActivity {
    private ActivityWorkplaceRegisterBinding binding;
    private PartTimeDatabase partTimeDatabase;
    private WorkPlaceDao placeDao;
    private WorkDailyDao dailyDao;
    private char[] day = {'0', '0', '0', '0', '0', '0', '0'};
    private LocalTime[] startTimes = new LocalTime[7];
    private LocalTime[] endTimes = new LocalTime[7];

    WorkPlace new_workplace = new WorkPlace();
    @Override
    public void onBackPressed(){
        super.onBackPressed();
        Intent intent = new Intent(WorkPlaceRegisterActivity.this, WorkPlaceActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // View Binding 초기화
        binding = ActivityWorkplaceRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        // 해당 요일에 근무를 선택시 시작날짜와 끝날짜 기록 및 시간 선택
        setCheckBox();
        // 직종 구분 컬러, 업종, 근무 기간 선택 스피너 활성화
        setSpinner();
        // 등록 버튼 클릭 리스너
        binding.registerButton.setOnClickListener(v -> {
            Executors.newSingleThreadExecutor().execute(() -> {

                // Database 초기화
                partTimeDatabase = PartTimeDatabase.getDatabase(this);
                placeDao = partTimeDatabase.workPlaceDao();
                dailyDao = partTimeDatabase.workDailyDao();
                // 직장이름
                new_workplace.placeName = binding.contentWorkplaceName.getText().toString();
                // 색 구분
                new_workplace.ColorHex = binding.contentColor.getSelectedItem().toString();
                // 업종
                new_workplace.type = binding.contentType.getSelectedItem().toString();
                // 시급
                new_workplace.usualPay = Integer.parseInt(binding.contentSalary.getText().toString());

                // 주휴유무
                if (binding.radioButtonYes.isChecked()) {
                    new_workplace.isJuhyu = true;
                } else if (binding.radioButtonNo.isChecked()) {
                    new_workplace.isJuhyu = false;
                }

                // 시작 날짜와 끝 날짜 기록
                String startYear = binding.contentWorkstartyear.getSelectedItem().toString();
                String startMonth = binding.contentWorkstartmonth.getSelectedItem().toString();
                String startDay = binding.contentWorkstartday.getSelectedItem().toString();
                String endYear = binding.contentWorkendyear.getSelectedItem().toString();
                String endMonth = binding.contentWorkendmonth.getSelectedItem().toString();
                String endDay = binding.contentWorkendday.getSelectedItem().toString();

                // 날짜와 시간을 ZonedDateTime으로 변환
                // 왜 zonedDateTime으로 하냐??? -> 하나로 통합하기 위해
                ZoneId zoneId = ZoneId.systemDefault(); // 시스템 기본 시간대 사용

                // 시작 날짜와 시간 결합
                ZonedDateTime startDateTime = ZonedDateTime.of(
                        LocalDate.of(Integer.parseInt(startYear), Integer.parseInt(startMonth), Integer.parseInt(startDay)),
                        LocalTime.MIN, // 시간 정보가 없을 경우 자정으로 설정
                        zoneId
                );

                // 끝 날짜와 시간 결합
                ZonedDateTime endDateTime = ZonedDateTime.of(
                        LocalDate.of(Integer.parseInt(endYear), Integer.parseInt(endMonth), Integer.parseInt(endDay)),
                        LocalTime.MAX, // 시간 정보가 없을 경우 하루의 마지막 시간으로 설정
                        zoneId
                );

                // 아르바이트 시작 날짜
                new_workplace.startDate = startDateTime.toString();
                // 아르바이트 그만두는 날짜
                new_workplace.endDate = endDateTime.toString();
                // 아르바이트 일하는 요일
                new_workplace.day = String.valueOf(day);
                // 특정 요일에 아르바이트 시작하는 시간
                List<String> startTimeList = new ArrayList<>();
                // 특정 요일에 아르바이트 끝나는 시간
                List<String> endTimeList = new ArrayList<>();

                for(int i = 0; i < 7;++i){
                    if(day[i] == '1'){
                        startTimeList.add(startTimes[i].toString());
                        endTimeList.add(endTimes[i].toString());
                    } else {
                        startTimeList.add(null);
                        endTimeList.add(null);
                    }
                }

                new_workplace.startTime = startTimeList;
                new_workplace.endTime = endTimeList;

                placeDao.setInsertData(new_workplace);
                int id = placeDao.findId(new_workplace.placeName);

                // 현제날짜 새롭게 선언 -> 끝날짜까지 순회돌면서 선택한 날짜에 해당되면 스케쥴 넣어줄거
                ZonedDateTime currentDateTime = startDateTime;

                while (!currentDateTime.isAfter(endDateTime)) {
                    int dayOfWeek = currentDateTime.getDayOfWeek().getValue() % 7;
                    // Monday=1 ~ Sunday=7 -> %7로 Sunday=0으로 조정 (day 배열과 일치)

                    if (day[dayOfWeek] == '1' && startTimes[dayOfWeek] != null && endTimes[dayOfWeek] != null) {
                        // 선택된 요일이면 해당 요일의 시작/끝 시간을 ZonedDateTime으로 생성
                        ZonedDateTime startZonedDateTime = currentDateTime.withHour(startTimes[dayOfWeek].getHour())
                                .withMinute(startTimes[dayOfWeek].getMinute())
                                .withSecond(0)
                                .withNano(0);

                        ZonedDateTime endZonedDateTime = currentDateTime.withHour(endTimes[dayOfWeek].getHour())
                                .withMinute(endTimes[dayOfWeek].getMinute())
                                .withSecond(0)
                                .withNano(0);

                        WorkDaily workDaily = new WorkDaily(startZonedDateTime.toString(),
                                                            endZonedDateTime.toString(),
                                                            id);
                        dailyDao.setInsertData(workDaily);
                    }

                    // 하루씩 더하기
                    currentDateTime = currentDateTime.plusDays(1);
                }
            });
            // 다음 액티비티로 이동
            Intent intent = new Intent(WorkPlaceRegisterActivity.this, WorkPlaceActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void setSpinner(){
        // 직장 구분 색 선택
        List<String> circleColor = List.of("#FFB3B3","#FFCC99","#FFFF99","#B3E6B3","#99CCFF","#99B3FF","#D9B3FF");
        SpinnerColorAdapter colorAdapter = new SpinnerColorAdapter(this, circleColor);
        binding.contentColor.setAdapter(colorAdapter);

        List<String> type = List.of("음식점", "카페", "판매", "문화", "서비스", "사무", "교육", "기타");
        SpinnerAdapter typeAdapter = new SpinnerAdapter(this, type);
        binding.contentType.setAdapter(typeAdapter);

        int baseYear = 2025;

// 2014년부터 2034년까지 연도 리스트 생성
        List<String> years = new ArrayList<>();
        for (int i = baseYear - 10; i <= baseYear + 10; i++) {
            years.add(String.valueOf(i));
        }

// ArrayAdapter 생성 및 연결
        SpinnerAdapter startYearAdapter = new SpinnerAdapter(this, years);
        binding.contentWorkstartyear.setAdapter(startYearAdapter);

// 기본 선택값으로 2024년 설정
        binding.contentWorkstartyear.setSelection(years.indexOf(String.valueOf(baseYear)));

// ArrayAdapter 생성 및 연결
        SpinnerAdapter endYearAdapter = new SpinnerAdapter(this, years);
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

    }
    private void setCheckBox(){
        // 월 ~ 일까지 체크박스가 있는데 클릭을 하면 해당 체크박스 밑에 시간을 선택하는 기능이 열림
        setupDay(binding.checkboxMonday, binding.timeMonday, binding.startTimeMonday, binding.endTimeMonday);
        setupDay(binding.checkboxTuesday, binding.timeTuesday, binding.startTimeTuesday, binding.endTimeTuesday);
        setupDay(binding.checkboxWednesday, binding.timeWednesday, binding.startTimeWednesday, binding.endTimeWednesday);
        setupDay(binding.checkboxThursday, binding.timeThursday, binding.startTimeThursday, binding.endTimeThursday);
        setupDay(binding.checkboxFriday, binding.timeFriday, binding.startTimeFriday, binding.endTimeFriday);
        setupDay(binding.checkboxSaturday, binding.timeSaturday, binding.startTimeSaturday, binding.endTimeSaturday);
        setupDay(binding.checkboxSunday, binding.timeSunday, binding.startTimeSunday, binding.endTimeSunday);
    }
    private void setupDay(CheckBox checkBox, LinearLayout timeLayout, Button startTimeButton, Button endTimeButton) {
        int dayPosition = Integer.parseInt(checkBox.getTag().toString());
        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                day[dayPosition] = '1';
                timeLayout.setVisibility(View.VISIBLE);
            } else {
                day[dayPosition] = '0';
                timeLayout.setVisibility(View.GONE);
            }
        });

        startTimeButton.setOnClickListener(v -> {
            showMaterialTimePicker(startTimeButton, 9, 0,dayPosition,true);
        });

        endTimeButton.setOnClickListener(v -> {
            showMaterialTimePicker(endTimeButton, 18, 0,dayPosition,false);
        });
    }

    private void showMaterialTimePicker(Button targetButton, int defaultHour, int defaultMinute,int dayPosition,boolean isStartTime) {
        MaterialTimePicker picker = new MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H) // 24시간 형식
                .setHour(defaultHour) // 기본 시간 설정
                .setMinute(defaultMinute) // 기본 분 설정
                .build();

        picker.addOnPositiveButtonClickListener(v -> {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
            // 시간과 분을 받아 30분 단위로 조정
            int hour = picker.getHour();
            int minute = (picker.getMinute() < 30) ? 0 : 30;
            LocalTime time = LocalTime.of(hour, minute);
            targetButton.setText(formatter.format(time));

            if (isStartTime) {
                startTimes[dayPosition] = time;
            } else {
                endTimes[dayPosition] = time;
            }
        });

        picker.show(getSupportFragmentManager(), "MATERIAL_TIME_PICKER");
    }
}