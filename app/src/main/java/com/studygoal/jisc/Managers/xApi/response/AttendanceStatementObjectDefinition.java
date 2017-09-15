package com.studygoal.jisc.Managers.xApi.response;

import com.google.gson.annotations.SerializedName;

public class AttendanceStatementObjectDefinition {
    @SerializedName("name")
    AttendanceStatementObjectDefinitionName name;

    public AttendanceStatementObjectDefinitionName getName() {
        return name;
    }
}
