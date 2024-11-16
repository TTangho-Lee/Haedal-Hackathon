package com.example.parttimecalander.home.ui.summationmonth;

public class RecyclerItem {
    public String name;
    public int[][] worked_time;
    public boolean juhyu;
    public int pay;

    public RecyclerItem(String name,int[][] worked_time,boolean juhyu,int pay) {
        this.name=name;
        this.worked_time=worked_time;
        this.juhyu=juhyu;
        this.pay=pay;
    }
}
