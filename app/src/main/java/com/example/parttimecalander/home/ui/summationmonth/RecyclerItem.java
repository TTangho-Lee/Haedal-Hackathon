package com.example.parttimecalander.home.ui.summationmonth;

import java.io.Serializable;


public class RecyclerItem implements Serializable{
    public int year;
    public int month;
    public String name;
    public int[][] worked_time;
    public boolean juhyu;
    public int pay;
    public String ColorHex;

    public RecyclerItem(int year,int month,String name,int[][] worked_time,boolean juhyu,int pay,String ColorHex) {
        this.year=year;
        this.month=month;
        this.name=name;
        this.worked_time=worked_time;
        this.juhyu=juhyu;
        this.pay=pay;
        this.ColorHex=ColorHex;
    }
}
