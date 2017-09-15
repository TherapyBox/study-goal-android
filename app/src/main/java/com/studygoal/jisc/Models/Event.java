package com.studygoal.jisc.Models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

@Table(name = "Event")
public class Event extends Model {
    @Column(name = "activity")
    private String activity;

    @Column(name = "module")
    private String module;

    @Column(name = "date")
    private String date;

    @Column(name = "time")
    private long time = 0;

    public Event(String activity, String module, String date, long time) {
        this.activity = activity;
        this.module = module;
        this.date = date;
        this.time = time;
    }

    public String getActivity() {
        return activity;
    }

    public String getModule() {
        return module;
    }

    public String getDate() {
        return date;
    }

    public long getTime() {
        return time;
    }
}

