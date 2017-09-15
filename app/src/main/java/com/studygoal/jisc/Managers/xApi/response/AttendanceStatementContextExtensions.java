package com.studygoal.jisc.Managers.xApi.response;

import com.google.gson.annotations.SerializedName;

public class AttendanceStatementContextExtensions {
    @SerializedName("http://xapi.jisc.ac.uk/courseArea")
    AttendanceStatementContextExtensionsCourseArea courseArea;

    @SerializedName("http://xapi.jisc.ac.uk/activity_type_id")
    String activityTypeId;

    @SerializedName("http://xapi.jisc.ac.uk/starttime")
    String startTime;

    @SerializedName("http://xapi.jisc.ac.uk/recipeVersion")
    String recipeVersion;

    public AttendanceStatementContextExtensionsCourseArea getCourseArea() {
        return courseArea;
    }

    public String getActivityTypeId() {
        return activityTypeId;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getRecipeVersion() {
        return recipeVersion;
    }
}
