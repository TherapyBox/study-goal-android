package com.studygoal.jisc.Models;

public class ActivityPoints {
    public String activity;
    public String points;
    public String id;
    public String key;

    public ActivityPoints() {}

    public ActivityPoints(String activity, String points, String id, String key){
        this.activity = activity;
        this.points = points;
        this.id = id;
        this.key = key;
    }
}
