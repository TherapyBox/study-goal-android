package com.studygoal.jisc.Fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.activeandroid.query.Select;
import com.studygoal.jisc.Adapters.AttainmentAdapter;
import com.studygoal.jisc.Managers.DataManager;
import com.studygoal.jisc.Managers.NetworkManager;
import com.studygoal.jisc.Managers.SocialManager;
import com.studygoal.jisc.Managers.xApi.LogActivityEvent;
import com.studygoal.jisc.Managers.xApi.XApiManager;
import com.studygoal.jisc.Models.Attainment;
import com.studygoal.jisc.R;

import java.util.Timer;

public class StatsPortraitFragment extends Fragment {
    private Timer mTimer;
    private Timer mTimer2;
    private ListView mList;
    private AttainmentAdapter mAdapter;
    private View mMainView;
    private int mContor;
    private SwipeRefreshLayout mLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mMainView = inflater.inflate(R.layout.stats2, container, false);

        ((TextView) mMainView.findViewById(R.id.graphs)).setTypeface(DataManager.getInstance().myriadpro_regular);

        ((TextView) mMainView.findViewById(R.id.title)).setTypeface(DataManager.getInstance().myriadpro_regular);
        ((TextView) mMainView.findViewById(R.id.activity_points_1_text)).setTypeface(DataManager.getInstance().myriadpro_regular);
        final TextView activity_points_thisweek;
        (activity_points_thisweek = (TextView) mMainView.findViewById(R.id.activity_points_1)).setTypeface(DataManager.getInstance().myriadpro_regular);
        ((TextView) mMainView.findViewById(R.id.activity_points_2_text)).setTypeface(DataManager.getInstance().myriadpro_regular);
        final TextView activity_points_overall;
        (activity_points_overall = (TextView) mMainView.findViewById(R.id.activity_points_2)).setTypeface(DataManager.getInstance().myriadpro_regular);

        NetworkManager.getInstance().getStudentActivityPoint();
        activity_points_thisweek.setText(DataManager.getInstance().user.last_week_activity_points);
        activity_points_overall.setText(DataManager.getInstance().user.overall_activity_points);

        ((TextView) mMainView.findViewById(R.id.this_week)).setTypeface(DataManager.getInstance().myriadpro_regular);
        ((TextView) mMainView.findViewById(R.id.overall)).setTypeface(DataManager.getInstance().myriadpro_regular);
        ((TextView) mMainView.findViewById(R.id.attainment)).setTypeface(DataManager.getInstance().myriadpro_regular);

        mLayout = (SwipeRefreshLayout) mMainView.findViewById(R.id.stats_swipe_refresh);

        mLayout.setColorSchemeResources(R.color.colorPrimary);
        mLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        NetworkManager.getInstance().getStudentActivityPoint();
                        NetworkManager.getInstance().getAssignmentRanking();

                        DataManager.getInstance().mainActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                activity_points_thisweek.setText(DataManager.getInstance().user.last_week_activity_points);
                                activity_points_overall.setText(DataManager.getInstance().user.overall_activity_points);

                                mAdapter.list = new Select().from(Attainment.class).execute();
                                mAdapter.notifyDataSetChanged();
                                mLayout.setRefreshing(false);
                            }
                        });
                    }
                }).start();

            }
        });

        //Lista pt attainment + refresh
        mList = (ListView) mMainView.findViewById(R.id.list);
        mList.setOnTouchListener(new View.OnTouchListener() {
            // Setting on Touch Listener for handling the touch inside ScrollView
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Disallow the touch request for parent scroll on touch of child view
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
        mAdapter = new AttainmentAdapter(DataManager.getInstance().mainActivity);
        mList.setAdapter(mAdapter);
        //

        mMainView.findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataManager.getInstance().mainActivity.onBackPressed();
            }
        });
        mMainView.findViewById(R.id.graph).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataManager.getInstance().mainActivity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main_fragment, new StatsGraphsFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });

        mMainView.findViewById(R.id.share).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SocialManager.getInstance().shareOnIntent(((TextView) mMainView.findViewById(R.id.activity_points_1)).getText().toString() + " " + ((TextView) mMainView.findViewById(R.id.activity_points_1_text)).getText().toString());
            }
        });

        mMainView.findViewById(R.id.share2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SocialManager.getInstance().shareOnIntent(((TextView) mMainView.findViewById(R.id.activity_points_2)).getText().toString() + " " + ((TextView) mMainView.findViewById(R.id.activity_points_2_text)).getText().toString());
            }
        });

        mMainView.findViewById(R.id.next).setVisibility(View.INVISIBLE);

        final SharedPreferences preferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        if (DataManager.getInstance().user.isStaff && preferences.getBoolean("stats_alert", true)) {

            android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(getActivity());
            alertDialogBuilder.setMessage(R.string.statistics_admin_view);
            alertDialogBuilder.setPositiveButton("Don't show again", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putBoolean("stats_alert", false);
                    editor.apply();
                }
            });
            alertDialogBuilder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            android.app.AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }

        return mMainView;
    }

    @Override
    public void onResume() {
        super.onResume();
        DataManager.getInstance().mainActivity.setTitle(DataManager.getInstance().mainActivity.getString(R.string.stats_title));
        DataManager.getInstance().mainActivity.hideAllButtons();
        DataManager.getInstance().mainActivity.showCertainButtons(5);
        new Thread(new Runnable() {
            @Override
            public void run() {
                NetworkManager.getInstance().getAssignmentRanking();
                mAdapter.list = new Select().from(Attainment.class).execute();
                DataManager.getInstance().mainActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.notifyDataSetChanged();
                    }
                });
            }
        }).start();

        XApiManager.getInstance().sendLogActivityEvent(LogActivityEvent.NavigateStatsAllActivity);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mTimer != null)
            mTimer.cancel();
        if (mTimer2 != null)
            mTimer2.cancel();
    }
}
