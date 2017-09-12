package com.studygoal.jisc.Models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

public class Event {

    public Event(String date, String activity, String module){
        this.activity = activity;
        this.module = module;
        this.date = date;
    }

    public String activity;
    public String module;
    public String date;
}

