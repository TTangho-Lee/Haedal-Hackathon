package com.example.parttimecalander.Database.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "daily")
public class WorkDaily {                   //일일근무
    @PrimaryKey(autoGenerate = true)
    public int ID;                         //기본키
    public String startTime;
    public String endTime;
    public int placeId;                    //근무지 id(외래키)

    public WorkDaily() {

    }
    public WorkDaily(String startTime, String endTime, int placeId) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.placeId = placeId;
    }
}
