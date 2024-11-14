package com.example.parttimecalander.Database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;

@Entity(tableName = "place")
public class WorkPlace {
    @PrimaryKey(autoGenerate = true)
    public int ID;                         //기본키
    public String placeName;               //근무지명
    public String type;                    //업종
    public int usualPay;                   //기본시급
    public boolean isJuhyu;                //주휴수당

    //public Map<DayOfWeek, pair<LocalTime, LocalTime>> workSchedule; //비 상!! 출근요일이랑 날짜가 룸 데이터베이스로는 저장 안 된다, 컨버터 써야 한다고 함

    public LocalDate startDate;            //근무시작일자
    public LocalDate endDate;              //근무종료일자
    public boolean isExpanded;             //확장 여부 저장
    public String ColorHex;                //색상 값
}