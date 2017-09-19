package com.studygoal.jisc.Managers.xApi.response;

import com.google.gson.annotations.SerializedName;

public class ResponseSetting {
    @SerializedName("value")
    String statement;

    public String getValue() {
        return statement;
    }
}
