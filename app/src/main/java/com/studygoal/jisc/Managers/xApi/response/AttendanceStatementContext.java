package com.studygoal.jisc.Managers.xApi.response;

import com.google.gson.annotations.SerializedName;

public class AttendanceStatementContext {
    @SerializedName("extensions")
    AttendanceStatementContextExtensions extensions;

    public AttendanceStatementContextExtensions getExtensions() {
        return extensions;
    }
}
