package com.studygoal.jisc.Managers.xApi.entity.attendance;

import com.google.gson.annotations.SerializedName;

public class AttendanceStatementActor {
    @SerializedName("account")
    AttendanceStatementActorAccount account;

    public AttendanceStatementActorAccount getAccount() {
        return account;
    }
}
