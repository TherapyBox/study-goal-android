package com.studygoal.jisc.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;

import com.activeandroid.query.Select;
import com.studygoal.jisc.Adapters.EventsAttendedAdapter;
import com.studygoal.jisc.MainActivity;
import com.studygoal.jisc.Managers.DataManager;
import com.studygoal.jisc.Managers.xApi.entity.LogActivityEvent;
import com.studygoal.jisc.Managers.xApi.XApiManager;
import com.studygoal.jisc.Models.Event;
import com.studygoal.jisc.R;

import java.util.ArrayList;
import java.util.List;

public class StatsEventAttendance extends BaseFragment {
    private static final String TAG = StatsEventAttendance.class.getSimpleName();

    private static final int PAGE_SIZE = 10;

    private ListView mListView;
    private int mPreviousLast;
    private EventsAttendedAdapter mAdapter;
    private ArrayList<Event> mEvents = new ArrayList<>();
    private boolean mIsLoading = false;

    @Override
    public void onResume() {
        super.onResume();
        DataManager.getInstance().mainActivity.setTitle(getString(R.string.events_attended));
        DataManager.getInstance().mainActivity.hideAllButtons();
        DataManager.getInstance().mainActivity.showCertainButtons(5);

        XApiManager.getInstance().sendLogActivityEvent(LogActivityEvent.NavigateEventsAttended);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View mainView = inflater.inflate(R.layout.stats_event_attendance, container, false);
        mAdapter = new EventsAttendedAdapter(getContext());

        mListView = (ListView) mainView.findViewById(R.id.event_attendance_listView);
        LayoutInflater i = getActivity().getLayoutInflater();
        mListView.setAdapter(mAdapter);
        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                final int lastItem = firstVisibleItem + visibleItemCount;

                if (lastItem == totalItemCount) {
                    if (mPreviousLast != lastItem) {
                        //to avoid multiple calls for last item
                        if (!mIsLoading) {
                            new Thread(() -> {
                                if (!mIsLoading) {
                                    mPreviousLast = lastItem;
                                    mIsLoading = true;
                                    loadData(lastItem, PAGE_SIZE, false);
                                    runOnUiThread(() -> {
                                        mAdapter.updateList(mEvents);
                                        mAdapter.notifyDataSetChanged();
                                        ((MainActivity) getActivity()).hideProgressBar();
                                        mIsLoading = false;
                                    });
                                }
                            }).start();
                        }
                    }
                }
            }
        });

        mListView.setOnItemClickListener((adapterView, view, i12, l) -> {
            return;
        });

        ((MainActivity) getActivity()).showProgressBar(null);

        new Thread(() -> {
            if (!mIsLoading) {
                mIsLoading = true;
                loadData(0, PAGE_SIZE * 2, true);
                runOnUiThread(() -> {
                    mAdapter.updateList(mEvents);
                    mAdapter.notifyDataSetChanged();
                    ((MainActivity) getActivity()).hideProgressBar();
                    mIsLoading = false;
                });
            }
        }).start();

        return mainView;
    }

    private void loadData(int skip, int limit, boolean reset) {
        if (XApiManager.getInstance().getAttendance(skip, limit, reset)) {
            mEvents.clear();
            List<Event> events = new Select().from(Event.class).execute();
            mEvents.addAll(events);
        }
    }
}
