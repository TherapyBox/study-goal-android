package com.studygoal.jisc.Fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;

import com.studygoal.jisc.Managers.DataManager;
import com.studygoal.jisc.Managers.xApi.LogActivityEvent;
import com.studygoal.jisc.Managers.xApi.XApiManager;
import com.studygoal.jisc.Models.Activity;
import com.studygoal.jisc.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Marjana-Tbox on 07/09/17.
 */

public class AppUsageFragment extends Fragment {

    private TextView startdate;
    private TextView enddate;
    private Calendar pickedDate = Calendar.getInstance();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

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

        startdate = (TextView) mainView.findViewById(R.id.app_usage_start);
        startdate.setText(dateFormat.format(pickedDate.getTime()));
        enddate = (TextView) mainView.findViewById(R.id.app_usage_end);
        enddate.setText(dateFormat.format(pickedDate.getTime()));

        DatePickerDialog.OnDateSetListener datePickerStart = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                pickedDate.set(Calendar.YEAR, year);
                pickedDate.set(Calendar.MONTH, monthOfYear);
                pickedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                startdate.setText(dateFormat.format(pickedDate.getTime()));
                //refresh data
                pickedDate = Calendar.getInstance();
            }
        };

        DatePickerDialog.OnDateSetListener datePickerEnd = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                pickedDate.set(Calendar.YEAR, year);
                pickedDate.set(Calendar.MONTH, monthOfYear);
                pickedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                enddate.setText(dateFormat.format(pickedDate.getTime()));
                //refresh data
                pickedDate = Calendar.getInstance();
            }
        };

        startdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(getActivity(), datePickerStart, pickedDate
                        .get(Calendar.YEAR), pickedDate.get(Calendar.MONTH),
                        pickedDate.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        enddate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(getActivity(), datePickerEnd, pickedDate
                        .get(Calendar.YEAR), pickedDate.get(Calendar.MONTH),
                        pickedDate.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        return mainView;
    }
}
