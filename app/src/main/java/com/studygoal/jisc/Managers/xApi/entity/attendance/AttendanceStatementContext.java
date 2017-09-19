package com.studygoal.jisc.Managers.xApi.entity.attendance;

import com.google.gson.annotations.SerializedName;

public class AttendanceStatementContext {
    @SerializedName("extensions")
    AttendanceStatementContextExtensions extensions;

    public AttendanceStatementContextExtensions getExtensions() {
        return extensions;
    }
}
