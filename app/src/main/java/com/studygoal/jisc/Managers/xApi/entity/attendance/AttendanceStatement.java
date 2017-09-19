package com.studygoal.jisc.Managers.xApi.response.attendance;

import com.google.gson.annotations.SerializedName;

public class AttendanceStatement {
    @SerializedName("actor")
    AttendanceStatementActor actor;

    @SerializedName("object")
    AttendanceStatementObject object;

    @SerializedName("context")
    AttendanceStatementContext context;

    public AttendanceStatementActor getActor() {
        return actor;
    }

    public AttendanceStatementObject getObject() {
        return object;
    }

    public AttendanceStatementContext getContext() {
        return context;
    }
}
