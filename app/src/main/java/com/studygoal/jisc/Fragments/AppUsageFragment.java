package com.studygoal.jisc.Fragments;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;

import com.studygoal.jisc.Managers.DataManager;
import com.studygoal.jisc.Managers.NetworkManager;
import com.studygoal.jisc.Managers.xApi.LogActivityEvent;
import com.studygoal.jisc.Managers.xApi.XApiManager;
import com.studygoal.jisc.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Marjana-Tbox on 07/09/17.
 */

public class AppUsageFragment extends Fragment {

    private TextView startDate;
    private TextView endDate;
    private Calendar pickedDate = Calendar.getInstance();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
    private DatePickerDialog.OnDateSetListener datePickerEnd;
    private DatePickerDialog.OnDateSetListener datePickerStart;

    private TextView sessions;
    private TextView activities;
    private TextView setTargets;
    private TextView metTargets;
    private TextView failedTargets;

    public void onResume() {
        super.onResume();
        DataManager.getInstance().mainActivity.setTitle(getString(R.string.app_usage));
        DataManager.getInstance().mainActivity.hideAllButtons();
        DataManager.getInstance().mainActivity.showCertainButtons(5);

        XApiManager.getInstance().sendLogActivityEvent(LogActivityEvent.NavigateAppUsage);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View mainView = inflater.inflate(R.layout.app_usage, container, false);

        setUpDatePicker();

        startDate = (TextView) mainView.findViewById(R.id.app_usage_start);
        startDate.setText(dateFormat.format(pickedDate.getTime()));
        startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(getActivity(), datePickerStart, pickedDate
                        .get(Calendar.YEAR), pickedDate.get(Calendar.MONTH),
                        pickedDate.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        endDate = (TextView) mainView.findViewById(R.id.app_usage_end);
        endDate.setText(dateFormat.format(pickedDate.getTime()));
        endDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(getActivity(), datePickerEnd, pickedDate
                        .get(Calendar.YEAR), pickedDate.get(Calendar.MONTH),
                        pickedDate.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        sessions = (TextView) mainView.findViewById(R.id.app_usage_sessions);
        activities = (TextView) mainView.findViewById(R.id.app_usage_activities);
        setTargets = (TextView) mainView.findViewById(R.id.app_usage_set_targets);
        metTargets = (TextView) mainView.findViewById(R.id.app_usage_met_targets);
        failedTargets = (TextView) mainView.findViewById(R.id.app_usage_failed_targets);

        loadData();
        displayData();

        return mainView;
    }

    private void loadData(){
        NetworkManager.getInstance().getAppUsage(startDate.getText().toString(), endDate.getText().toString());
    }

    private void displayData(){
        SharedPreferences prefs = getContext().getSharedPreferences("jisc", getContext().MODE_PRIVATE);
        sessions.setText(prefs.getString("app_usage_sessions", "0"));
        activities.setText(prefs.getString("app_usage_activities", "0"));
        setTargets.setText(prefs.getString("app_usage_set_targets", "0"));
        metTargets.setText(prefs.getString("app_usage_met_targets", "0"));
        failedTargets.setText(prefs.getString("app_usage_failed_targets", "0"));
    }

    private void setUpDatePicker(){
        datePickerStart = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                pickedDate.set(Calendar.YEAR, year);
                pickedDate.set(Calendar.MONTH, monthOfYear);
                pickedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                startDate.setText(dateFormat.format(pickedDate.getTime()));
                //refresh picker data
                pickedDate = Calendar.getInstance();
            }
        };

        datePickerEnd = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                pickedDate.set(Calendar.YEAR, year);
                pickedDate.set(Calendar.MONTH, monthOfYear);
                pickedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                endDate.setText(dateFormat.format(pickedDate.getTime()));
                //refresh picker data
                pickedDate = Calendar.getInstance();
            }
        };
    }
}
