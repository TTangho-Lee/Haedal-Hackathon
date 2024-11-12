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
    public int ID;
    public LocalDate startDay;
    public LocalDate endDay;
    public String startTime;
    public String endTime;
    public int placeId;
    public int repeat;
    public int[] dayOfWeek;
}
