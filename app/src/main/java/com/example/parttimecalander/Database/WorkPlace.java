package com.example.parttimecalander.Database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.time.LocalDate;

@Entity(tableName = "place")
public class WorkPlace {
    @PrimaryKey(autoGenerate = true)
    public int ID;                         //기본키
    public String placeName;               //근무지명
    public String type;                    //업종
    public int usualPay;                   //기본시급
    public boolean isJuhyu;                //주휴수당

    //public Map<DayOfWeek, pair<LocalTime, LocalTime>> workSchedule; //비 상!! 출근요일이랑 날짜가 룸 데이터베이스로는 저장 안 된다, 컨버터 써야 한다고 함

    public String startDate;            //근무시작일자
    public String endDate;              //근무종료일자
    public boolean isExpanded;             //확장 여부 저장
    public String ColorHex;                //색상 값

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getUsualPay() {
        return usualPay;
    }

    public void setUsualPay(int usualPay) {
        this.usualPay = usualPay;
    }

    public boolean isJuhyu() {
        return isJuhyu;
    }

    public void setJuhyu(boolean juhyu) {
        isJuhyu = juhyu;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    public void setExpanded(boolean expanded) {
        isExpanded = expanded;
    }

    public String getColorHex() {
        return ColorHex;
    }

    public void setColorHex(String colorHex) {
        ColorHex = colorHex;
    }
}