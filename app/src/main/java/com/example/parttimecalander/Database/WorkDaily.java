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
public class WorkDaily {
    @PrimaryKey(autoGenerate = true)
    public int ID;
    public LocalDate date;
    public String startTime;
    public String endTime;
    public int placeId;
}
