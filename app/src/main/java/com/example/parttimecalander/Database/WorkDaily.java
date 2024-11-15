package com.example.parttimecalander.Database;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import java.time.LocalDate;

@Entity(tableName = "daily")
public class WorkDaily {                   //일일근무
    @PrimaryKey(autoGenerate = true)
    public int ID;                         //기본키
    public String date;                 //날짜

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public int getPlaceId() {
        return placeId;
    }

    public void setPlaceId(int placeId) {
        this.placeId = placeId;
    }

    public String startTime;               //시작시간
    public String endTime;                 //종료시간
    public int placeId;                    //근무지 id(외래키)
}
