package com.studygoal.jisc.Managers.xApi.response.attendance;

import com.google.gson.annotations.SerializedName;

public class AttendanceStatementContext {
    @SerializedName("extensions")
    AttendanceStatementContextExtensions extensions;

    public AttendanceStatementContextExtensions getExtensions() {
        return extensions;
    }
}
