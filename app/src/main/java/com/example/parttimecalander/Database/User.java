package com.example.parttimecalander.Database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.time.LocalDateTime;

@Entity(tableName = "user")
public class User {
    @PrimaryKey(autoGenerate = true)
    public int ID;                         //기본키
    public String name;
    public int money;                      //누적금액
    public int goal;                       //목표금액
    public String recentUpdate;     //최근 체크일자

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public int getGoal() {
        return goal;
    }

    public void setGoal(int goal) {
        this.goal = goal;
    }

    public String getRecentUpdate() {
        return recentUpdate;
    }

    public void setRecentUpdate(String recentUpdate) {
        this.recentUpdate = recentUpdate;
    }
}
