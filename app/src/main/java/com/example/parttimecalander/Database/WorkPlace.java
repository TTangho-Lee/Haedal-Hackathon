package com.example.parttimecalander.Database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "place")
public class WorkPlace {
    @PrimaryKey(autoGenerate = true)
    public int ID;                         //기본키
    public String placeName;               //근무지명
    public String type;                    //업종
    public int usualPay;                   //기본시급
    public int holidayPay;                 //주휴수당

}
