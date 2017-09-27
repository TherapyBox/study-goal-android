package com.studygoal.jisc.Managers.xApi.entity.attendance;

import com.google.gson.annotations.SerializedName;

public class AttendanceStatementActorAccount {
    @SerializedName("name")
    String name;

    public String getName() {
        return name;
    }
}
