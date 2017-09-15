package com.studygoal.jisc.Managers.xApi.response;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ResponseAttendance {
    @SerializedName("statement")
    AttendanceStatement statement;

    public AttendanceStatement getStatement() {
        return statement;
    }
}
