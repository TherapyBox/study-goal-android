package com.studygoal.jisc;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Base64;

import com.studygoal.jisc.General.TLog;

public class Preferences {
    private static final String TAG = Preferences.class.getSimpleName();


    private static final String s_preference_attendance_data = "attendanceData";
    private static final boolean s_preference_attendance_data_default = false;

    private static final String s_preference_attainment_data = "attainmentData";
    private static final boolean s_preference_attainment_data_default = false;

    private static final String s_preference_study_goal_attendance = "studyGoalAttendance";
    private static final boolean s_preference_study_goal_attendance_default = false;


    private final SharedPreferences m_prefs;

    public Preferences(Context context) {
        m_prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public boolean getAttendanceData() {
        boolean value = s_preference_attendance_data_default;

        try {
            value = m_prefs.getBoolean(s_preference_attendance_data, s_preference_attendance_data_default);
        } catch (Exception e) {
            TLog.e(TAG, "Unable get preferences.", e);
        }

        return value;
    }

    public void setAttendanceData(boolean value) {
        setBooleanPreference(s_preference_attendance_data, value);
    }

    public boolean getAttainmentData() {
        boolean value = s_preference_attainment_data_default;

        try {
            value = m_prefs.getBoolean(s_preference_attainment_data, s_preference_attainment_data_default);
        } catch (Exception e) {
            TLog.e(TAG, "Unable get preferences.", e);
        }

        return value;
    }

    public void setAttainmentData(boolean value) {
        setBooleanPreference(s_preference_attainment_data, value);
    }

    public boolean getStudyGoalAttendance() {
        boolean value = s_preference_study_goal_attendance_default;

        try {
            value = m_prefs.getBoolean(s_preference_study_goal_attendance, s_preference_study_goal_attendance_default);
        } catch (Exception e) {
            TLog.e(TAG, "Unable get preferences.", e);
        }

        return value;
    }

    public void setStudyGoalAttendance(boolean value) {
        setBooleanPreference(s_preference_study_goal_attendance, value);
    }

    private void setBooleanPreference(String key, Boolean value) {
        try {
            SharedPreferences.Editor editor = m_prefs.edit();
            editor.putBoolean(key, value);
            editor.commit();
        } catch (Exception e) {
            TLog.e(TAG, "Unable set Boolean preferences: " + key, e);
        }
    }

    private void setStringPreference(String key, String value) {
        try {
            SharedPreferences.Editor editor = m_prefs.edit();
            editor.putString(key, value);
            editor.commit();
        } catch (Exception e) {
            TLog.e(TAG, "Unable set Boolean preferences: " + key, e);
        }
    }

    private void setIntPreference(String key, int value) {
        try {
            SharedPreferences.Editor editor = m_prefs.edit();
            editor.putInt(key, value);
            editor.commit();
        } catch (Exception e) {
            TLog.e(TAG, "Unable set Integer preferences: " + key, e);
        }
    }

    private void setLongPreference(String key, long value) {
        try {
            SharedPreferences.Editor editor = m_prefs.edit();
            editor.putLong(key, value);
            editor.commit();
        } catch (Exception e) {
            TLog.e(TAG, "Unable set Integer preferences: " + key, e);
        }
    }

    private void setFloatPreference(String key, float value) {
        try {
            SharedPreferences.Editor editor = m_prefs.edit();
            editor.putFloat(key, value);
            editor.commit();
        } catch (Exception e) {
            TLog.e(TAG, "Unable set Float preferences: " + key, e);
        }
    }

    private void setBytesPreference(String key, byte[] value) {
        try {
            SharedPreferences.Editor editor = m_prefs.edit();
            String base64Value = Base64.encodeToString(value, Base64.DEFAULT);
            editor.putString(key, base64Value);
            editor.commit();
        } catch (Exception e) {
            TLog.e(TAG, "Unable set byte[] preferences: " + key, e);
        }
    }

    private byte[] getBytesPreference(String key) {
        byte[] value = null;

        try {
            String base64Value = m_prefs.getString(key, null);

            if (base64Value != null) {
                value = Base64.decode(base64Value, Base64.DEFAULT);
            }
        } catch (Exception e) {
            TLog.e(TAG, "Unable get preferences.", e);
        }

        return value;
    }
}
