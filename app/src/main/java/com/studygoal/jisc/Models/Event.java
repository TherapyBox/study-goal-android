package com.studygoal.jisc.Models;

public class Event {
    private String mActivity;
    private String mModule;
    private String mDate;
    private long mTime = 0;

    public Event(String date, String activity, String module, long time) {
        mActivity = activity;
        mModule = module;
        mDate = date;
        mTime = time;
    }

    public String getActivity() {
        return mActivity;
    }

    public String getModule() {
        return mModule;
    }

    public String getDate() {
        return mDate;
    }

    public long getTime() {
        return mTime;
    }
}

