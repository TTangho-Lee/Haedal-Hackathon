package com.example.parttimecalander.Database;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import java.time.LocalDate;
import java.util.Map;

@Entity(tableName = "week")
public class WorkInfo {
    @PrimaryKey(autoGenerate = true)
    public int ID;                         //기본키
    public String startDay;             //시작일
    public String endDay;               //종료일
    public int placeId;                    //근무지 id(외래키)
    public int repeat;                     //반복 구간
    public Map<String,String> Monday;
    public Map<String,String> Tuesday;
    public Map<String,String> Wednesday;
    public Map<String,String> Thursday;
    public Map<String,String> Friday;
    public Map<String,String> Saturday;
    public Map<String,String> Sunday;
}
