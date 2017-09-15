package com.studygoal.jisc.Fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;

import com.studygoal.jisc.Adapters.EventsAttendedAdapter;
import com.studygoal.jisc.MainActivity;
import com.studygoal.jisc.Managers.DataManager;
import com.studygoal.jisc.Managers.NetworkManager;
import com.studygoal.jisc.Managers.xApi.LogActivityEvent;
import com.studygoal.jisc.Managers.xApi.XApiManager;
import com.studygoal.jisc.Models.Event;
import com.studygoal.jisc.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class StatsEventAttendance extends BaseFragment {
    private static final String TAG = StatsEventAttendance.class.getSimpleName();

    private static final int PAGE_SIZE = 10;

    private static final SimpleDateFormat sDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    private ListView mListView;
    private int mPreviousLast;
    private EventsAttendedAdapter mAdapter;
    private ArrayList<Event> events = new ArrayList<>();

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

                // TODO: implement paging
                if (lastItem == totalItemCount) {
                    if (mPreviousLast != lastItem) {
                        //to avoid multiple calls for last item
                        mPreviousLast = lastItem;
//                        ((MainActivity) getActivity()).showProgressBar(null);
//                        hideProgressBar();
                    }
                }
            }
        });

        mListView.setOnItemClickListener((adapterView, view, i12, l) -> {
            return;
        });

        ((MainActivity) getActivity()).showProgressBar(null);

        new Thread(() -> {
            loadData(0, PAGE_SIZE * 2);
            runOnUiThread(() -> {
                mAdapter.updateList(events);
                mAdapter.notifyDataSetChanged();
                ((MainActivity) getActivity()).hideProgressBar();
            });
        }).start();

        return mainView;
    }

    private void loadData(int skip, int limit) {
        if (NetworkManager.getInstance().getEventsAttended(skip, limit)) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());

            try {
                Log.e(getClass().getCanonicalName(), "events attended: " + preferences.getString(getString(R.string.events_attended), null));
                JSONArray jsonArray = new JSONArray(preferences.getString(getString(R.string.events_attended), null));

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    JSONObject statement = jsonObject.getJSONObject("statement");
                    JSONObject name = statement.getJSONObject("object").getJSONObject("definition").getJSONObject("name");
                    String[] dataInfo = name.getString("en").split(" ");
                    String activityInfo = statement.getJSONObject("context").getJSONObject("extensions").getString("http://xapi.jisc.ac.uk/activity_type_id");
                    JSONObject courseArea = statement.getJSONObject("context").getJSONObject("extensions").getJSONObject("http://xapi.jisc.ac.uk/courseArea");
                    String[] moduleInfo = courseArea.getString("http://xapi.jisc.ac.uk/uddModInstanceID").split("-");

                    /*String data = "";
                    if (dataInfo.length >= 3 && dataInfo[2] != null)
                        data += dataInfo[2] + " ";
                    if (dataInfo.length >= 2 && dataInfo[1] != null)
                        data += dataInfo[1] + " ";
                    if (dataInfo.length >= 1 && dataInfo[0] != null)
                        data += dataInfo[0] + " ";
                    if (moduleInfo.length >= 1 && moduleInfo[0] != null)
                        data += moduleInfo[0];*/

                    long time = 0;
                    String dateString = dataInfo[2] + " " + dataInfo[1];

                    try {
                        time = sDateFormat.parse(dateString).getTime();
                    } catch (Exception e) {
                        Log.d(TAG, e.getMessage());
                    }

                    Event event = new Event(dateString, activityInfo, moduleInfo[0], time);
                    events.add(event);
                }
            } catch (Exception je) {
                je.printStackTrace();
            }
        }
    }
}
