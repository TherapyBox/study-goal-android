package com.studygoal.jisc.Fragments;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.studygoal.jisc.Adapters.ActivityTypeAdapter;
import com.studygoal.jisc.Adapters.ChooseActivityAdapter;
import com.studygoal.jisc.Adapters.ModuleAdapter;
import com.studygoal.jisc.Managers.DataManager;
import com.studygoal.jisc.Managers.NetworkManager;
import com.studygoal.jisc.Managers.xApi.LogActivityEvent;
import com.studygoal.jisc.Managers.xApi.XApiManager;
import com.studygoal.jisc.Models.Activity;
import com.studygoal.jisc.Models.Module;
import com.studygoal.jisc.Models.RunningActivity;
import com.studygoal.jisc.NotificationAlarm;
import com.studygoal.jisc.R;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class LogNewActivityFragment extends Fragment implements View.OnClickListener {

    private View mainView;
    private AppCompatTextView mChooseActivity;
    private AppCompatTextView mModule;
    private AppCompatTextView mActivityType;

    private EditText mReminderTextView;
    private TextView mCountdownTextView;

    private AlarmManager mAlarmManager;
    private PendingIntent mPendingIntent;
    private Timer mTimer;
    private TimerTask mTimerTask;
    private Long mTimestamp;
    private Long mPause;

    private SharedPreferences mSaves;

    private RelativeLayout mAddModuleLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mainView = inflater.inflate(R.layout.log_fragment_new_activity, container, false);

        DataManager.getInstance().reload();

        mAddModuleLayout = (RelativeLayout) mainView.findViewById(R.id.add_new_module_layout);
        mAddModuleLayout.setVisibility(View.GONE);

        ((EditText) mainView.findViewById(R.id.add_module_edit_text)).setTypeface(DataManager.getInstance().myriadpro_regular);
        ((TextView) mainView.findViewById(R.id.add_module_button_text)).setTypeface(DataManager.getInstance().myriadpro_regular);
        mainView.findViewById(R.id.add_module_button_text).setOnClickListener(this);

        mCountdownTextView = ((TextView) mainView.findViewById(R.id.new_activity_text_timer_2));
        mCountdownTextView.setTypeface(DataManager.getInstance().myriadpro_regular);

        ((TextView) mainView.findViewById(R.id.new_activity_text_minutes)).setTypeface(DataManager.getInstance().myriadpro_regular);

        mAlarmManager = (AlarmManager) DataManager.getInstance().mainActivity.getSystemService(Context.ALARM_SERVICE);

        mModule = (AppCompatTextView) mainView.findViewById(R.id.new_activity_module_textView);
        mModule.setTypeface(DataManager.getInstance().myriadpro_regular);
        mModule.setSupportBackgroundTintList(ColorStateList.valueOf(0xFF8a63cc));
        mModule.setOnClickListener(this);

        mActivityType = (AppCompatTextView) mainView.findViewById(R.id.new_activity_activitytype_textView);
        mActivityType.setTypeface(DataManager.getInstance().myriadpro_regular);
        mActivityType.setSupportBackgroundTintList(ColorStateList.valueOf(0xFF8a63cc));
        mActivityType.setOnClickListener(this);

        mChooseActivity = (AppCompatTextView) mainView.findViewById(R.id.new_activity_choose_textView);
        mChooseActivity.setTypeface(DataManager.getInstance().myriadpro_regular);
        mChooseActivity.setSupportBackgroundTintList(ColorStateList.valueOf(0xFF8a63cc));
        mChooseActivity.setOnClickListener(this);

        mReminderTextView = ((EditText) mainView.findViewById(R.id.new_activity_text_timer_1));
        mReminderTextView.setTypeface(DataManager.getInstance().myriadpro_regular);
        mReminderTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() != 0) {
                    int value = Integer.parseInt(s.toString());
                    if (value < 0 || value > 60) {
                        mReminderTextView.setText("");
                        mReminderTextView.setSelection(mReminderTextView.getText().length());
                    }
                }
            }
        });

        mCountdownTextView = ((TextView) mainView.findViewById(R.id.new_activity_text_timer_2));
        mCountdownTextView.setTypeface(DataManager.getInstance().myriadpro_regular);

        ((TextView) mainView.findViewById(R.id.new_activity_text_module)).setTypeface(DataManager.getInstance().myriadpro_regular);
        ((TextView) mainView.findViewById(R.id.new_activity_text_choose)).setTypeface(DataManager.getInstance().myriadpro_regular);
        ((TextView) mainView.findViewById(R.id.new_activity_activity_type_text)).setTypeface(DataManager.getInstance().myriadpro_regular);
        ((TextView) mainView.findViewById(R.id.new_activity_btn_pause_text)).setTypeface(DataManager.getInstance().myriadpro_regular);

        ((TextView) mainView.findViewById(R.id.new_activity_btn_start_text)).setTypeface(DataManager.getInstance().myriadpro_regular);
        ((TextView) mainView.findViewById(R.id.new_activity_btn_stop_text)).setTypeface(DataManager.getInstance().myriadpro_regular);

        if (!DataManager.getInstance().mainActivity.isLandscape) {
            ((TextView) mainView.findViewById(R.id.new_activity_text_reminder)).setTypeface(DataManager.getInstance().myriadpro_regular);
        } else {
            ((TextView) mainView.findViewById(R.id.header_1)).setTypeface(DataManager.getInstance().myriadpro_regular);
            ((TextView) mainView.findViewById(R.id.header_2)).setTypeface(DataManager.getInstance().myriadpro_regular);
        }


        mTimer = new Timer();
        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                Long elapsed_time = System.currentTimeMillis() - mTimestamp;
                Long seconds = (elapsed_time / 1000) % 60;
                Long minutes = elapsed_time / 60000;

                String value = "";
                if (minutes < 10)
                    value += "0" + minutes + ":";
                else
                    value += minutes + ":";
                if (seconds < 10)
                    value += "0" + seconds;
                else
                    value += seconds;
                final String f_value = value;
                DataManager.getInstance().mainActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mCountdownTextView.setText(f_value);
                    }
                });

                if (minutes >= 180) {
                    mainView.findViewById(R.id.new_activity_btn_stop).callOnClick();
                }
            }
        };


        mSaves = DataManager.getInstance().mainActivity.getSharedPreferences("jisc", Context.MODE_PRIVATE);
        mTimestamp = mSaves.getLong("timer", 0);


        RunningActivity activity = new Select().from(RunningActivity.class).executeSingle();
        if (activity != null) {
            mModule.setText(((Module) new Select().from(Module.class).where("module_id = ?", activity.module_id).executeSingle()).name);
            mActivityType.setText(activity.activity_type);
            mChooseActivity.setText(activity.activity);
        } else {
            List<Module> list_module = new Select().from(Module.class).execute();
            if (list_module.size() > 0)
                mModule.setText((list_module.get(0)).name);
            else
                mModule.setText(DataManager.getInstance().mainActivity.getString(R.string.no_module));
            mActivityType.setText(DataManager.getInstance().activity_type.get(0));
            mChooseActivity.setText(DataManager.getInstance().choose_activity.get(DataManager.getInstance().activity_type.get(0)).get(0));
        }

        mainView.findViewById(R.id.new_activity_btn_start).setOnClickListener(this);
        mainView.findViewById(R.id.new_activity_btn_pause).setOnClickListener(this);

        if (mSaves.contains("pause")) {
            mPause = mSaves.getLong("pause", 0);
            if (mTimestamp == 0) {
                mPause = (long) 0;
                mSaves.edit().putLong("pause", 0).apply();
            }
            if (mPause > 0) {
                Long elapsed_time = mPause - mTimestamp;
                Long seconds = (elapsed_time / 1000) % 60;
                Long minutes = elapsed_time / 60000;

                String value = "";
                if (minutes < 10)
                    value += "0" + minutes + ":";
                else
                    value += minutes + ":";
                if (seconds < 10)
                    value += "0" + seconds;
                else
                    value += seconds;
                final String f_value = value;
                DataManager.getInstance().mainActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mCountdownTextView.setText(f_value);
                    }
                });

                //mainView.findViewById(R.id.new_activity_btn_pause).callOnClick();
                mainView.findViewById(R.id.new_activity_btn_pause).setVisibility(View.VISIBLE);
                mainView.findViewById(R.id.new_activity_btn_start).setVisibility(View.GONE);
                mainView.findViewById(R.id.new_activity_btn_stop).setOnClickListener(this);
                ((TextView) mainView.findViewById(R.id.new_activity_btn_pause_text)).setText(DataManager.getInstance().mainActivity.getString(R.string.resume));
            } else {
                mainView.findViewById(R.id.new_activity_btn_stop).setOnClickListener(this);
                mainView.findViewById(R.id.new_activity_btn_start).setVisibility(View.VISIBLE);
                mainView.findViewById(R.id.new_activity_btn_pause).setVisibility(View.GONE);
                if (mTimestamp > 0) {
                    mainView.findViewById(R.id.new_activity_btn_start).setVisibility(View.GONE);
                    mainView.findViewById(R.id.new_activity_btn_pause).setVisibility(View.VISIBLE);
                    mTimer.schedule(mTimerTask, 0, 1000);
                }
            }
        } else {
            if (mTimestamp > 0) {
                mainView.findViewById(R.id.new_activity_btn_stop).setOnClickListener(this);
                mainView.findViewById(R.id.new_activity_btn_start).setVisibility(View.GONE);
                mainView.findViewById(R.id.new_activity_btn_pause).setVisibility(View.VISIBLE);
                if (mTimestamp > 0)
                    mTimer.schedule(mTimerTask, 0, 1000);
            }
        }


//        mainView.findViewById(R.id.new_activity_activityhistory_btn).setOnClickListener(this);
//        mainView.findViewById(R.id.new_activity_logrecent_btn).setOnClickListener(this);

        return mainView;
    }

    @Override
    public void onResume() {
        super.onResume();
        DataManager.getInstance().mainActivity.setTitle(DataManager.getInstance().mainActivity.getString(R.string.report_activity_title));
        DataManager.getInstance().mainActivity.hideAllButtons();
        DataManager.getInstance().mainActivity.showCertainButtons(8);

        String module = (mModule != null && mModule.getText() != null) ? mModule.getText().toString() : null;
        XApiManager.getInstance().sendLogActivityEvent(LogActivityEvent.AddTimedLog, module);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mTimer.cancel();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.new_activity_btn_stop: {
                if (mSaves.getLong("pause", 0) > 0) {
                    Long _pause = System.currentTimeMillis() - mSaves.getLong("pause", 0);
                    mTimestamp -= _pause;
                    mSaves.edit().putLong("pause", 0).apply();
                }

                mTimer.cancel();
//                    timer = new Timer();

                Calendar c = Calendar.getInstance();
                c.setTimeInMillis(System.currentTimeMillis());

                mainView.findViewById(R.id.new_activity_btn_start).setVisibility(View.VISIBLE);
                mainView.findViewById(R.id.new_activity_btn_pause).setVisibility(View.GONE);

                HashMap<String, String> params = new HashMap<>();
                params.put("student_id", DataManager.getInstance().user.id);
                params.put("module_id", ((Module) (new Select().from(Module.class).where("module_name = ?", mModule.getText().toString()).executeSingle())).id);
                params.put("activity_type", DataManager.getInstance().api_values.get(mActivityType.getText().toString()));
                params.put("activity", DataManager.getInstance().api_values.get(mChooseActivity.getText().toString()));
                params.put("activity_date", c.get(Calendar.YEAR) + "-" + ((c.get(Calendar.MONTH) + 1) < 10 ? "0" + (c.get(Calendar.MONTH) + 1) : (c.get(Calendar.MONTH) + 1)) + "-" + ((c.get(Calendar.DAY_OF_MONTH)) < 10 ? "0" + c.get(Calendar.DAY_OF_MONTH) : c.get(Calendar.DAY_OF_MONTH)));
                long duration = ((System.currentTimeMillis() - mTimestamp) / 60000);
                if (duration == 0) {
                    Snackbar.make(mainView.findViewById(R.id.container), R.string.activity_canceled_due_to_short_time, Snackbar.LENGTH_LONG).show();
                    new Delete().from(RunningActivity.class).execute();

                    mSaves.edit().putLong("timer", 0).apply();
                    mSaves.edit().putLong("pause", 0).apply();


                    Intent intent = new Intent(DataManager.getInstance().mainActivity, NotificationAlarm.class);
                    mPendingIntent = PendingIntent.getBroadcast(DataManager.getInstance().mainActivity, 0,
                            intent, PendingIntent.FLAG_ONE_SHOT);
                    mAlarmManager.cancel(mPendingIntent);

                    mCountdownTextView.setText("00:00");

                    ((TextView) mainView.findViewById(R.id.new_activity_btn_pause_text)).setText(DataManager.getInstance().mainActivity.getString(R.string.pause));

                    ((CardView) mainView.findViewById(R.id.new_activity_btn_start)).setCardBackgroundColor(ContextCompat.getColor(DataManager.getInstance().mainActivity, R.color.default_blue));//getResources().getColor(R.color.default_blue));
                    mainView.findViewById(R.id.new_activity_btn_start).setOnClickListener(this);
                    mainView.findViewById(R.id.new_activity_btn_stop).setOnClickListener(null);
                    return;
                }
                //duration = 1;
                params.put("time_spent", duration + "");

                String responseCode = NetworkManager.getInstance().addActivity(params);
                if (responseCode.equals("403")) {
                    Snackbar.make(mainView.findViewById(R.id.container), R.string.already_added_activity, Snackbar.LENGTH_LONG).show();
                } else if (!responseCode.equals("200")) {
                    Activity activity = new Activity();
                    activity.student_id = params.get("student_id");
                    activity.module_id = params.get("module_id");
                    activity.activity_type = params.get("activity_type");
                    activity.activity = params.get("activity");
                    activity.activity_date = params.get("activity_date");
                    activity.time_spent = params.get("time_spent");
                    activity.save();
                } else {
//                    NetworkManager.getInstance().getActivityHistory(DataManager.getInstance().user.id);
                }

                new Delete().from(RunningActivity.class).execute();

                mSaves.edit().putLong("timer", 0).apply();
                mSaves.edit().putLong("pause", 0).apply();
                ((TextView) mainView.findViewById(R.id.new_activity_btn_pause_text)).setText(DataManager.getInstance().mainActivity.getString(R.string.pause));

                Intent intent = new Intent(DataManager.getInstance().mainActivity, NotificationAlarm.class);
                mPendingIntent = PendingIntent.getBroadcast(DataManager.getInstance().mainActivity, 0,
                        intent, PendingIntent.FLAG_ONE_SHOT);
                mAlarmManager.cancel(mPendingIntent);


                Snackbar.make(mainView.findViewById(R.id.container), R.string.activity_stopped, Snackbar.LENGTH_LONG).show();
                mCountdownTextView.setText("00:00");

                ((CardView) mainView.findViewById(R.id.new_activity_btn_start)).setCardBackgroundColor(ContextCompat.getColor(DataManager.getInstance().mainActivity, R.color.default_blue));//getResources().getColor(R.color.default_blue));
                mainView.findViewById(R.id.new_activity_btn_start).setOnClickListener(this);
                mainView.findViewById(R.id.new_activity_btn_stop).setOnClickListener(null);

                DataManager.getInstance().mainActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        DataManager.getInstance().mainActivity.onBackPressed();
                    }
                });
                break;
            }
            case R.id.new_activity_btn_pause: {
                if (((TextView) mainView.findViewById(R.id.new_activity_btn_pause_text)).getText().toString().equals(DataManager.getInstance().mainActivity.getString(R.string.pause))) {
                    Intent intent = new Intent(DataManager.getInstance().mainActivity, NotificationAlarm.class);
                    mPendingIntent = PendingIntent.getBroadcast(DataManager.getInstance().mainActivity, 0,
                            intent, PendingIntent.FLAG_ONE_SHOT);
                    mAlarmManager.cancel(mPendingIntent);

                    mSaves.edit().putLong("pause", System.currentTimeMillis()).apply();

                    mTimer.cancel();

                    Snackbar.make(mainView.findViewById(R.id.container), R.string.activity_paused, Snackbar.LENGTH_LONG).show();

                    ((TextView) mainView.findViewById(R.id.new_activity_btn_pause_text)).setText(DataManager.getInstance().mainActivity.getString(R.string.resume));
                } else {
                    Long _pause = System.currentTimeMillis() - mSaves.getLong("pause", 0);
                    mTimestamp += _pause;
                    mSaves.edit().putLong("pause", 0).apply();
                    int reminder = (Integer.parseInt(mReminderTextView.getText().toString().split(":")[0]) * 60) + Integer.parseInt(mReminderTextView.getText().toString().split(":")[1]);
                    mTimer = new Timer();
                    mTimerTask = new TimerTask() {
                        @Override
                        public void run() {
                            Long elapsed_time = System.currentTimeMillis() - mTimestamp;
                            Long seconds = (elapsed_time / 1000) % 60;
                            Long minutes = elapsed_time / 60000;

                            String value = "";
                            if (minutes < 10)
                                value += "0" + minutes + ":";
                            else
                                value += minutes + ":";
                            if (seconds < 10)
                                value += "0" + seconds;
                            else
                                value += seconds;
                            final String f_value = value;
                            DataManager.getInstance().mainActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mCountdownTextView.setText(f_value);
                                }
                            });

                            if (minutes >= 180) {
                                mainView.findViewById(R.id.new_activity_btn_stop).callOnClick();
                            }
                        }
                    };
                    if (reminder != 0) {
                        Intent intent = new Intent(DataManager.getInstance().mainActivity, NotificationAlarm.class);
                        mPendingIntent = PendingIntent.getBroadcast(DataManager.getInstance().mainActivity, 0,
                                intent, PendingIntent.FLAG_UPDATE_CURRENT);
                        mAlarmManager.set(AlarmManager.RTC_WAKEUP,
                                System.currentTimeMillis() + reminder * 1000, mPendingIntent);
                    }
                    mTimer.schedule(mTimerTask, 0, 1000);
                    mSaves.edit().putLong("timer", mTimestamp).apply();

                    Snackbar.make(mainView.findViewById(R.id.container), R.string.activity_resumed, Snackbar.LENGTH_LONG).show();

                    ((TextView) mainView.findViewById(R.id.new_activity_btn_pause_text)).setText(DataManager.getInstance().mainActivity.getString(R.string.pause));
                }
                break;
            }
            case R.id.new_activity_btn_start: {

                if (DataManager.getInstance().user.isDemo) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(LogNewActivityFragment.this.getActivity());
                    alertDialogBuilder.setTitle(Html.fromHtml("<font color='#3791ee'>" + getString(R.string.demo_mode_addactivitylog) + "</font>"));
                    alertDialogBuilder.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                    return;
                }

                if (mModule.getText().toString().equals(DataManager.getInstance().mainActivity.getString(R.string.no_module))) {
                    Snackbar.make(mainView.findViewById(R.id.container), R.string.no_module_selected, Snackbar.LENGTH_LONG).show();
                    return;
                }
                mainView.findViewById(R.id.new_activity_btn_start).setVisibility(View.GONE);
                mainView.findViewById(R.id.new_activity_btn_pause).setVisibility(View.VISIBLE);
                mainView.findViewById(R.id.new_activity_btn_stop).setOnClickListener(this);

                Snackbar.make(mainView.findViewById(R.id.container), R.string.activity_started, Snackbar.LENGTH_LONG).show();

                mTimestamp = System.currentTimeMillis();
                int reminder = Integer.parseInt(mReminderTextView.getText().toString());
                mTimer = new Timer();
                mTimerTask = new TimerTask() {
                    @Override
                    public void run() {
                        Long elapsed_time = System.currentTimeMillis() - mTimestamp;
                        Long seconds = (elapsed_time / 1000) % 60;
                        Long minutes = elapsed_time / 60000;

                        String value = "";
                        if (minutes < 10)
                            value += "0" + minutes + ":";
                        else
                            value += minutes + ":";
                        if (seconds < 10)
                            value += "0" + seconds;
                        else
                            value += seconds;
                        final String f_value = value;
                        DataManager.getInstance().mainActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mCountdownTextView.setText(f_value);
                            }
                        });

                        if (minutes >= 180) {
                            mainView.findViewById(R.id.new_activity_btn_stop).callOnClick();
                        }
                    }
                };
                mTimer.schedule(mTimerTask, 0, 1000);
                mSaves.edit().putLong("timer", mTimestamp).apply();


                Calendar c = Calendar.getInstance();
                RunningActivity activity = new RunningActivity();
                activity.student_id = DataManager.getInstance().user.id;
                activity.module_id = ((Module) (new Select().from(Module.class).where("module_name = ?", mModule.getText().toString()).executeSingle())).id;
                activity.activity_type = mActivityType.getText().toString();
                activity.activity = mChooseActivity.getText().toString();
                activity.activity_date = c.get(Calendar.YEAR) + "-" + ((c.get(Calendar.MONTH) + 1) < 10 ? "0" + (c.get(Calendar.MONTH) + 1) : (c.get(Calendar.MONTH) + 1)) + "-" + ((c.get(Calendar.DAY_OF_MONTH)) < 10 ? "0" + c.get(Calendar.DAY_OF_MONTH) : c.get(Calendar.DAY_OF_MONTH));
                activity.save();

                ((CardView) mainView.findViewById(R.id.new_activity_btn_start)).setCardBackgroundColor(ContextCompat.getColor(DataManager.getInstance().mainActivity, R.color.light_grey));//getResources().getColor(R.color.light_grey));
                mainView.findViewById(R.id.new_activity_btn_start).setOnClickListener(null);

                if (reminder != 0) {
                    Intent intent = new Intent(DataManager.getInstance().mainActivity, NotificationAlarm.class);
                    //TODO: Bug Samsung devices - old code works on all except samsung
                    mPendingIntent = PendingIntent.getBroadcast(DataManager.getInstance().mainActivity, 0,
                            intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    mAlarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + reminder * 1000, mPendingIntent);

                    //TODO: BUGFIX
                }
                break;
            }
            case R.id.new_activity_text_timer_1: {
                final Dialog dialog = new Dialog(DataManager.getInstance().mainActivity);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.timespent_layout);
                dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

                if (DataManager.getInstance().mainActivity.isLandscape) {
                    DisplayMetrics displaymetrics = new DisplayMetrics();
                    DataManager.getInstance().mainActivity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
                    int width = (int) (displaymetrics.widthPixels * 0.3);

                    WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
                    params.width = width;
                    dialog.getWindow().setAttributes(params);
                }

                int hour = Integer.parseInt(mReminderTextView.getText().toString().split(":")[0]);
                int minute = Integer.parseInt(mReminderTextView.getText().toString().split(":")[1]);

                final NumberPicker hourPicker = (NumberPicker) dialog.findViewById(R.id.hour_picker);
                hourPicker.setMinValue(0);
                hourPicker.setMaxValue(180);
                hourPicker.setValue(hour);
                hourPicker.setFormatter(new NumberPicker.Formatter() {
                    @Override
                    public String format(int value) {
                        if (value < 10)
                            return "0" + value;
                        else
                            return value + "";
                    }
                });
                final NumberPicker minutePicker = (NumberPicker) dialog.findViewById(R.id.minute_picker);
                minutePicker.setMinValue(0);
                minutePicker.setMaxValue(59);
                minutePicker.setValue(minute);
                minutePicker.setFormatter(new NumberPicker.Formatter() {
                    @Override
                    public String format(int value) {
                        if (value < 10)
                            return "0" + value;
                        else
                            return value + "";
                    }
                });

                ((TextView) dialog.findViewById(R.id.timespent_save_text)).setTypeface(DataManager.getInstance().myriadpro_regular);
                dialog.findViewById(R.id.timespent_save_btn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String clock = "";
                        int hour = hourPicker.getValue();
                        if (hour < 10)
                            clock += "0" + hour + ":";
                        else
                            clock += hour + ":";
                        int minute = minutePicker.getValue();
                        if (minute < 10)
                            clock += "0" + minute;
                        else
                            clock += minute;
                        mReminderTextView.setText(clock);
                        dialog.dismiss();
                    }
                });
                ((TextView) dialog.findViewById(R.id.dialog_title)).setTypeface(DataManager.getInstance().oratorstd_typeface);

                dialog.show();
                break;
            }
            case R.id.new_activity_module_textView: {
                final Dialog dialog = new Dialog(DataManager.getInstance().mainActivity);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.custom_spinner_layout);
                dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                if (DataManager.getInstance().mainActivity.isLandscape) {
                    DisplayMetrics displaymetrics = new DisplayMetrics();
                    DataManager.getInstance().mainActivity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
                    int width = (int) (displaymetrics.widthPixels * 0.3);

                    WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
                    params.width = width;
                    dialog.getWindow().setAttributes(params);
                }

                ((TextView) dialog.findViewById(R.id.dialog_title)).setTypeface(DataManager.getInstance().oratorstd_typeface);
                ((TextView) dialog.findViewById(R.id.dialog_title)).setText(R.string.choose_module);

                final ModuleAdapter moduleAdapter = new ModuleAdapter(DataManager.getInstance().mainActivity, mModule.getText().toString());
                final ListView listView = (ListView) dialog.findViewById(R.id.dialog_listview);
                listView.setAdapter(moduleAdapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        if (DataManager.getInstance().user.isSocial
                                && position == moduleAdapter.moduleList.size() - 1) {
                            //add new module
                            EditText add_module_edit_text = (EditText) mAddModuleLayout.findViewById(R.id.add_module_edit_text);
                            add_module_edit_text.setText("");
                            mAddModuleLayout.setVisibility(View.VISIBLE);
                            dialog.dismiss();
                        } else {
                            mModule.setText(((TextView) view.findViewById(R.id.dialog_item_name)).getText().toString());
                            RunningActivity activity = new Select().from(RunningActivity.class).executeSingle();
                            if (activity != null) {
                                activity.student_id = DataManager.getInstance().user.id;
                                activity.module_id = ((Module) (new Select().from(Module.class).where("module_name = ?", mModule.getText().toString()).executeSingle())).id;
                                activity.save();
                            }
                            dialog.dismiss();
                        }
                    }
                });

                dialog.show();
                break;
            }
            case R.id.new_activity_activitytype_textView: {
                final Dialog dialog = new Dialog(DataManager.getInstance().mainActivity);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.custom_spinner_layout);
                dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                if (DataManager.getInstance().mainActivity.isLandscape) {
                    DisplayMetrics displaymetrics = new DisplayMetrics();
                    DataManager.getInstance().mainActivity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
                    int width = (int) (displaymetrics.widthPixels * 0.3);

                    WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
                    params.width = width;
                    dialog.getWindow().setAttributes(params);
                }

                ((TextView) dialog.findViewById(R.id.dialog_title)).setTypeface(DataManager.getInstance().oratorstd_typeface);
                ((TextView) dialog.findViewById(R.id.dialog_title)).setText(R.string.choose_activity_type);

                final ListView listView = (ListView) dialog.findViewById(R.id.dialog_listview);
                listView.setAdapter(new ActivityTypeAdapter(DataManager.getInstance().mainActivity, mActivityType.getText().toString()));
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        mActivityType.setText(((TextView) view.findViewById(R.id.dialog_item_name)).getText().toString());
                        mChooseActivity.setText(DataManager.getInstance().choose_activity.get(mActivityType.getText().toString()).get(0));

                        RunningActivity activity = new Select().from(RunningActivity.class).executeSingle();
                        if (activity != null) {
                            activity.activity_type = mActivityType.getText().toString();
                            activity.activity = mChooseActivity.getText().toString();
                            activity.save();
                        }
                        dialog.dismiss();
                    }
                });

                dialog.show();
                break;
            }
            case R.id.new_activity_choose_textView: {
                final Dialog dialog = new Dialog(DataManager.getInstance().mainActivity);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.custom_spinner_layout);
                dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                if (DataManager.getInstance().mainActivity.isLandscape) {
                    DisplayMetrics displaymetrics = new DisplayMetrics();
                    DataManager.getInstance().mainActivity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
                    int width = (int) (displaymetrics.widthPixels * 0.3);

                    WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
                    params.width = width;
                    dialog.getWindow().setAttributes(params);
                }

                ((TextView) dialog.findViewById(R.id.dialog_title)).setTypeface(DataManager.getInstance().oratorstd_typeface);
                ((TextView) dialog.findViewById(R.id.dialog_title)).setText(R.string.choose_activity);

                final ListView listView = (ListView) dialog.findViewById(R.id.dialog_listview);
                listView.setAdapter(new ChooseActivityAdapter(DataManager.getInstance().mainActivity, mChooseActivity.getText().toString(), mActivityType.getText().toString()));
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        mChooseActivity.setText(((TextView) view.findViewById(R.id.dialog_item_name)).getText().toString());

                        RunningActivity activity = new Select().from(RunningActivity.class).executeSingle();
                        if (activity != null) {
                            activity.activity = mChooseActivity.getText().toString();
                            activity.save();
                        }
                        dialog.dismiss();
                    }
                });

                dialog.show();
                break;
            }
            case R.id.add_module_button_text: {
                EditText add_module_edit_text = (EditText) mAddModuleLayout.findViewById(R.id.add_module_edit_text);
                final String moduleName = add_module_edit_text.getText().toString();
                if (moduleName.length() == 0) {
                    Snackbar.make(DataManager.getInstance().mainActivity.findViewById(R.id.drawer_layout), R.string.module_name_invalid, Snackbar.LENGTH_LONG).show();
                    return;
                }

                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        final HashMap<String, String> params = new HashMap<>();
                        params.put("student_id", DataManager.getInstance().user.id);
                        params.put("module", moduleName);
                        params.put("is_social", "yes");

                        if (NetworkManager.getInstance().addModule(params)) {

                            DataManager.getInstance().mainActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                }
                            });
                        } else {
                            DataManager.getInstance().mainActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    (DataManager.getInstance().mainActivity).hideProgressBar();
                                    Snackbar.make(DataManager.getInstance().mainActivity.findViewById(R.id.drawer_layout), R.string.something_went_wrong, Snackbar.LENGTH_LONG).show();
                                }
                            });
                        }
                    }
                }).start();

                mAddModuleLayout.setVisibility(View.GONE);

                return;
            }
        }
    }
}
