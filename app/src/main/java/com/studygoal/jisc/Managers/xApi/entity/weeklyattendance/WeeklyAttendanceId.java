package com.studygoal.jisc.Managers.xApi.entity.weeklyattendance;

import com.google.gson.annotations.SerializedName;

public class WeeklyAttendanceId {
    @SerializedName("week")
    int week;

    @SerializedName("month")
    int month;

    @SerializedName("year")
    int year;

    public int getWeek() {
        return week;
    }

    public int getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }
}
