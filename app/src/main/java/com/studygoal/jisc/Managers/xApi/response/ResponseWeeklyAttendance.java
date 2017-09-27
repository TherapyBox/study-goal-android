package com.studygoal.jisc.Managers.xApi.response;

import com.google.gson.annotations.SerializedName;
import com.studygoal.jisc.Managers.xApi.entity.weeklyattendance.WeeklyAttendanceId;

public class ResponseWeeklyAttendance {
    @SerializedName("_id")
    WeeklyAttendanceId id;

    @SerializedName("count")
    int count;

    @SerializedName("date")
    String date;

    public WeeklyAttendanceId getId() {
        return id;
    }

    public int getCount() {
        return count;
    }

    public String getDate() {
        return date;
    }
}
