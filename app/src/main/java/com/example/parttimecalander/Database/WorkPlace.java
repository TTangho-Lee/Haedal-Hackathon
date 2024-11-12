package com.example.parttimecalander.Database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "place")
public class WorkPlace {
    @PrimaryKey(autoGenerate = true)
    public int ID;
    public String placeName;
    public String type;
    public int usualPay;
    public int holidayPay;

}
