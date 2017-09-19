package com.studygoal.jisc.Managers.xApi.response.attendance;

import com.google.gson.annotations.SerializedName;

public class AttendanceStatementObjectDefinition {
    @SerializedName("name")
    AttendanceStatementObjectDefinitionName name;

    public AttendanceStatementObjectDefinitionName getName() {
        return name;
    }
}
