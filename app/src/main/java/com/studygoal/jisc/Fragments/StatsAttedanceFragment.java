package com.studygoal.jisc.Fragments;

import android.annotation.SuppressLint;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;

import com.activeandroid.query.Select;
import com.studygoal.jisc.Activities.MainActivity;
import com.studygoal.jisc.Managers.DataManager;
import com.studygoal.jisc.Managers.xApi.XApiManager;
import com.studygoal.jisc.Models.WeeklyAttendance;
import com.studygoal.jisc.R;
import com.studygoal.jisc.databinding.FragmentStatsAttendanceBinding;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

public class StatsAttedanceFragment extends BaseFragment {

//    private LineChart lineChart;
//    private BarChart barchart;
//    private AppCompatTextView module;
//
//    private RelativeLayout mChartLayout;
//    private List<ED> mList;
//    private String mSelectedPeriod;

//    ArrayList<String> dates = new ArrayList<>();
//    ArrayList<String> count = new ArrayList<>();

    private boolean mIsLoading = false;

    private FragmentStatsAttendanceBinding mBinding = null;

    @Override
    public void onResume() {
        super.onResume();
        DataManager.getInstance().mainActivity.setTitle(getString(R.string.attendance));
        DataManager.getInstance().mainActivity.hideAllButtons();
        DataManager.getInstance().mainActivity.showCertainButtons(5);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_stats_attendance, container, false);

        mBinding.webviewGraph.getSettings().setJavaScriptEnabled(true);
        mBinding.webviewGraph.setOnTouchListener((view, motionEvent) -> true);
        mBinding.container.setVisibility(View.GONE);
        mBinding.emptyMessage.setVisibility(View.GONE);
        ((MainActivity) getActivity()).showProgressBar(null);

        new Thread(() -> {
            if (!mIsLoading) {
                mIsLoading = true;

                // set default start and end date
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                Calendar cal = GregorianCalendar.getInstance();
                cal.setTime(new Date());
                cal.add(Calendar.DAY_OF_YEAR, -34);
                Date daysBeforeDate = cal.getTime();
                String current = sdf.format(new Date());
                String past = sdf.format(daysBeforeDate);

                XApiManager.getInstance().getWeeklyAttendance(past, current);
                runOnUiThread(() -> {
                    List<WeeklyAttendance> items = new Select().from(WeeklyAttendance.class).execute();

                    if (items != null && items.size() > 0) {
                        mBinding.container.setVisibility(View.VISIBLE);
                        mBinding.emptyMessage.setVisibility(View.GONE);
                        loadWebView(items);
                    } else {
                        mBinding.container.setVisibility(View.GONE);
                        mBinding.emptyMessage.setVisibility(View.VISIBLE);
                    }

                    ((MainActivity) getActivity()).hideProgressBar();
                    mIsLoading = false;
                });
            }
        }).start();

        return mBinding.getRoot();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void loadWebView(List<WeeklyAttendance> items) {
        WebSettings settings = mBinding.webviewGraph.getSettings();
        settings.setJavaScriptEnabled(true);

        try {
            InputStream is = getContext().getAssets().open("stats_attendance_high_chart.html");
            int size = is.available();
            final byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            mBinding.webviewGraph.post(() -> {
                double d = getActivity().getResources().getDisplayMetrics().density;
                int h = (int) (mBinding.webviewGraph.getHeight() / d) - 20;
                int w = (int) (mBinding.webviewGraph.getWidth() / d) - 20;

                String dataCount = "";
                String dataDate = "";

                if (items != null && items.size() > 0) {
                    for (WeeklyAttendance item : items) {
                        dataCount += "" + item.getCount() + ", ";
                        dataDate += "'" + item.getDate() + "', \n";
                    }
                }

                String rawhtml = new String(buffer);
                rawhtml = rawhtml.replace("280px", w + "px");
                rawhtml = rawhtml.replace("220px", h + "px");
                rawhtml = rawhtml.replace("DATA", dataCount);
                rawhtml = rawhtml.replace("DATES", dataDate);
                mBinding.webviewGraph.loadDataWithBaseURL("", rawhtml, "text/html", "UTF-8", "");
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
