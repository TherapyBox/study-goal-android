package com.studygoal.jisc.Managers.xApi.response.attendance;

import com.google.gson.annotations.SerializedName;

public class AttendanceStatementContextExtensionsCourseArea {
    @SerializedName("http://xapi.jisc.ac.uk/uddModInstanceID")
    String uddModInstanceID;

    public String getUddModInstanceID() {
        return uddModInstanceID;
    }
}
