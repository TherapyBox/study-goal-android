package com.studygoal.jisc.Managers.xApi.response;

import com.google.gson.annotations.SerializedName;
import com.studygoal.jisc.Managers.xApi.entity.attendance.AttendanceStatement;

public class ResponseAttendance {
    @SerializedName("statement")
    AttendanceStatement statement;

    public AttendanceStatement getStatement() {
        return statement;
    }
}
