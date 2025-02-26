package com.example.parttimecalander.Database.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.List;

@Entity(tableName = "place")
public class WorkPlace implements Serializable {
    @PrimaryKey(autoGenerate = true)
    public int ID;                         //기본키
    public String placeName;               //근무지명
    public String type;                    //업종
    public int usualPay;                   //기본시급
    public boolean isJuhyu;                //주휴수당
    public String startDate;            //근무시작일자
    public String endDate;              //근무종료일자
    public boolean isExpanded;             //확장 여부 저장
    public String ColorHex;                //색상 값
    public String day;                  // 근무하는 요일
    public List<String> startTime;            // 요일별 시작시간
    public List<String> endTime;              // 요일별 끝나는시간
}