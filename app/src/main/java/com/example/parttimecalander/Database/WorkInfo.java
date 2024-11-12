package com.example.parttimecalander.Database;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import java.time.LocalDate;

@Entity(
        tableName = "week",
        foreignKeys = @ForeignKey(
                entity = WorkPlace.class,
                parentColumns = "ID",
                childColumns = "placeId",
                onDelete = ForeignKey.CASCADE
        )
)
public class WorkInfo {
    @PrimaryKey(autoGenerate = true)
    public int ID;                         //기본키
    public LocalDate startDay;             //시작일
    public LocalDate endDay;               //종료일
    public String startTime;               //시작시간
    public String endTime;                 //종료시간
    public int placeId;                    //근무지 id(외래키)
    public int repeat;                     //반복 구간
    public int[] dayOfWeek;                //요일
}
