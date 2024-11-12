package com.example.parttimecalander.Database;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import java.time.LocalDate;

@Entity(
        tableName = "daily",
        foreignKeys = @ForeignKey(
                entity = WorkPlace.class,
                parentColumns = "ID",
                childColumns = "placeId",
                onDelete = ForeignKey.CASCADE
        )
)
public class WorkDaily {                   //일일근무
    @PrimaryKey(autoGenerate = true)
    public int ID;                         //기본키
    public LocalDate date;                 //날짜
    public String startTime;               //시작시간
    public String endTime;                 //종료시간
    public int placeId;                    //근무지 id(외래키)
}
