package com.example.parttimecalander.Database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.time.LocalDateTime;

@Entity(tableName = "user")
public class User {
    @PrimaryKey(autoGenerate = true)
    public int ID;
    public int money;
    public int goal;
    public LocalDateTime recentUpdate;
}
