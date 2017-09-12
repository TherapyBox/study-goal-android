package com.studygoal.jisc.Fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatTextView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.activeandroid.query.Select;
import com.studygoal.jisc.Adapters.EventsAttendedAdapter;
import com.studygoal.jisc.Adapters.ModuleAdapter2;
import com.studygoal.jisc.MainActivity;
import com.studygoal.jisc.Managers.DataManager;
import com.studygoal.jisc.Managers.NetworkManager;
import com.studygoal.jisc.Managers.xApi.LogActivityEvent;
import com.studygoal.jisc.Managers.xApi.XApiManager;
import com.studygoal.jisc.Models.Courses;
import com.studygoal.jisc.Models.Event;
import com.studygoal.jisc.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class StatsEventAttendance extends Fragment {

    private AppCompatTextView moduleTextView;
    private ListView listView;
    private ArrayAdapter<String> eventListAdapter;
    private int previouseLast;
    private EventsAttendedAdapter adapter;

    //static final String[] EVENTS = new String[]{"Calculate 101", "Calculate 102", "Calculate 103", "Calculate 101"};
    //private ArrayList<String> events = new ArrayList<String>();
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
        //moduleTextView = (AppCompatTextView) mainView.findViewById(R.id.module_list);
        //((TextView) mainView.findViewById(R.id.module)).setTypeface(DataManager.getInstance().myriadpro_regular);
        adapter = new EventsAttendedAdapter(getContext());

        //setUpModule();

        listView = (ListView) mainView.findViewById(R.id.event_attendance_listView);
        LayoutInflater i = getActivity().getLayoutInflater();
        ViewGroup header = (ViewGroup) i.inflate(R.layout.stats_event_attendance_list_view_header, listView, false);
        //listView.addHeaderView(header, null, false);
        //eventListAdapter = new ArrayAdapter<String>(getContext(), R.layout.list_event_attendance, events);
        listView.setAdapter(adapter);
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {
                //firstVisibleItem, visibleItemCount, totalItemCount
                final int lastItem = i + i1;

                if(lastItem == i2)
                {
                    if(previouseLast!=lastItem)
                    {
                        //to avoid multiple calls for last item
                        previouseLast = lastItem;
                        ((MainActivity) getActivity()).showProgressBar(null);
                        hideProgressBar();
                    }
                }
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                return;
            }
        });

        ((MainActivity) getActivity()).showProgressBar(null);
        hideProgressBar();

        return mainView;
    }

    private void setUpModule() {
        moduleTextView.setSupportBackgroundTintList(ColorStateList.valueOf(0xFF8a63cc));
        moduleTextView.setTypeface(DataManager.getInstance().myriadpro_regular);
        moduleTextView.setText(R.string.anymodule);
        moduleTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(DataManager.getInstance().mainActivity);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.custom_spinner_layout);
                dialog.setCancelable(true);
                dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        dialog.dismiss();
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ((MainActivity) getActivity()).hideProgressBar();
                            }
                        });
                    }
                });

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

                final ListView listView = (ListView) dialog.findViewById(R.id.dialog_listview);
                listView.setAdapter(new ModuleAdapter2(DataManager.getInstance().mainActivity, moduleTextView.getText().toString()));
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        String titleText = ((TextView) view.findViewById(R.id.dialog_item_name)).getText().toString();

                        List<Courses> coursesList = new Select().from(Courses.class).execute();

                        for (int j = 0; j < coursesList.size(); j++) {
                            String courseName = coursesList.get(j).name;
                            if (courseName.equals(titleText)) {
                                return;
                            }
                        }

                        dialog.dismiss();
                        moduleTextView.setText(titleText);
                    }
                });
                //((MainActivity) getActivity()).showProgressBar2("");
                dialog.show();
            }
        });
    }

    private void getData(int skip, int limit){
        if(NetworkManager.getInstance().getEventsAttended(skip, limit)) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
            try {
                Log.e(getClass().getCanonicalName(), "events attended: " + preferences.getString(getString(R.string.events_attended), null));
                JSONArray jsonArray = new JSONArray(preferences.getString(getString(R.string.events_attended), null));
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    JSONObject statement = jsonObject.getJSONObject("statement");
                    JSONObject name = statement.getJSONObject("object").getJSONObject("definition").getJSONObject("name");
                    String[] dataInfo = name.getString("en").split(" ");

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

                    Event event = new Event(dataInfo[2], dataInfo[0], moduleInfo[0]);

                    events.add(event);
                }
                adapter.updateList(events);
                Log.d("", "getData: count: " + adapter.list.size());
                adapter.notifyDataSetChanged();
            } catch (Exception je) {
                je.printStackTrace();
            }
        }
    }

    private void hideProgressBar() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(events.size() == 0)
                            getData(0,20);
                        else
                            getData(events.size(),10);
                        ((MainActivity) getActivity()).hideProgressBar();
                    }
                });
            }
        }).start();
    }

}
