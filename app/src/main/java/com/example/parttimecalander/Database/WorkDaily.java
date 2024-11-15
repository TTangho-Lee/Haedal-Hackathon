package com.example.parttimecalander.Database;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import java.time.LocalDate;
import java.util.Map;

@Entity(tableName = "daily")
public class WorkDaily {                   //일일근무
    @PrimaryKey(autoGenerate = true)
    public int ID;                         //기본키
    public String date;                 //날짜
    public Map<String,String> time;
    public int placeId;                    //근무지 id(외래키)
}
