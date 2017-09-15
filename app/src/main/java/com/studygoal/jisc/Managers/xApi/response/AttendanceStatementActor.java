package com.studygoal.jisc.Managers.xApi.response;

import com.google.gson.annotations.SerializedName;

public class AttendanceStatementActor {
    @SerializedName("account")
    AttendanceStatementActorAccount account;

    public AttendanceStatementActorAccount getAccount() {
        return account;
    }
}
