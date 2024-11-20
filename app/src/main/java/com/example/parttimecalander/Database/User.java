package com.example.parttimecalander.Database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.time.LocalDateTime;

@Entity(tableName = "user")
public class User {
    @PrimaryKey(autoGenerate = true)
    public int ID;                         //기본키
    public String phone;
    public String email;
    public String address;
    public String name;
    public int birthYear;
    public int birthMonth;
    public int birthDay;
    public int money;                      //누적금액
    public int goal;                       //목표금액
    public String recentUpdate;     //최근 체크일자
}
