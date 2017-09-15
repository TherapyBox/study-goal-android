package com.studygoal.jisc.Managers.xApi.response;

import com.google.gson.annotations.SerializedName;

public class AttendanceStatementContextExtensionsCourseArea {
    @SerializedName("http://xapi.jisc.ac.uk/starttime")
    String uddModInstanceID;

    public String getUddModInstanceID() {
        return uddModInstanceID;
    }
}
