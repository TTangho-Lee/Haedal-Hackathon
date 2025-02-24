package com.example.parttimecalander.Database.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

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
    public String day;
    public String startTime;
    public String endTime;
}