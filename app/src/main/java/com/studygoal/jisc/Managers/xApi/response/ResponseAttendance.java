package com.studygoal.jisc.Managers.xApi.response;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ResponseAttendance {
    @SerializedName("statement")
    ArrayList<AttendanceStatement> statement;

    public ArrayList<AttendanceStatement> getStatement() {
        return statement;
    }
}
