package com.studygoal.jisc.Models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

@Table(name = "ResponseWeeklyAttendance")
public class WeeklyAttendance extends Model {
    @Column(name = "count")
    private int count;

    @Column(name = "date")
    private String date;

    @Column(name = "week")
    private int week;

    @Column(name = "month")
    private int month;

    @Column(name = "year")
    private int year;

    public WeeklyAttendance() {
        super();
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getWeek() {
        return week;
    }

    public void setWeek(int week) {
        this.week = week;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }
}
