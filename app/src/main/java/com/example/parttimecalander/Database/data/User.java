package com.example.parttimecalander.Database.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "user")
public class User {
    @PrimaryKey(autoGenerate = true)
    public int ID;                         //기본키
    public byte[] image;
    public String phone;
    public String email;
    public String address;
    public String name;
    public int birthYear;
    public int birthMonth;
    public int birthDay;
    public int money;                      //누적금액
    public byte[] goalImage;               //목표이미지
    public String goalName;
    public int goal;                       //목표금액
    public int goalSaveMoney;               // 목표금액까지 도달금액
    public String recentUpdate;     //최근 체크일자
    public String schoolList;
    public String certList;
    public String selfIntroduce;
    public User() {
    }
}
