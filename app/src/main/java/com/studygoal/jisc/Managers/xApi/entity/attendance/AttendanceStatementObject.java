package com.studygoal.jisc.Managers.xApi.entity.attendance;

import com.google.gson.annotations.SerializedName;

public class AttendanceStatementObject {
    @SerializedName("definition")
    AttendanceStatementObjectDefinition definition;

    public AttendanceStatementObjectDefinition getDefinition() {
        return definition;
    }
}
