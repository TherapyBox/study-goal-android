package com.studygoal.jisc.Fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.activeandroid.query.Select;
import com.studygoal.jisc.Adapters.GenericAdapter;
import com.studygoal.jisc.Adapters.ModuleAdapter2;
import com.studygoal.jisc.MainActivity;
import com.studygoal.jisc.Managers.DataManager;
import com.studygoal.jisc.Managers.NetworkManager;
import com.studygoal.jisc.Models.Courses;
import com.studygoal.jisc.Models.ED;
import com.studygoal.jisc.Models.Friend;
import com.studygoal.jisc.Models.Module;
import com.studygoal.jisc.R;
import com.studygoal.jisc.Utils.Utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class Stats3 extends Fragment {

    AppCompatTextView module;
    AppCompatTextView period;
    AppCompatTextView compareTo;
    RelativeLayout chart_layout;
    List<ED> list;
    boolean isBar;
    WebView webView;
    float webviewHeight;

    @Override
    public void onResume() {
        super.onResume();
        DataManager.getInstance().mainActivity.setTitle(getString(R.string.engagement_graph));
        DataManager.getInstance().mainActivity.hideAllButtons();
        DataManager.getInstance().mainActivity.showCertainButtons(8);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View mainView = inflater .inflate(R.layout.stats3, container, false);

        isBar = false;
        webView = (WebView) mainView.findViewById(R.id.chart_web);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.setPadding(0, 0, 0, 0);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                webviewHeight = Utils.pxToDp(webView.getHeight()-40);
            }
        });

        webView.loadDataWithBaseURL("", "<html><head></head><body><div style=\"height:100%;width:100%;background:white;\"></div></body></html>", "text/html", "UTF-8", "");

        chart_layout = (RelativeLayout) mainView.findViewById(R.id.chart_layout);

        module = (AppCompatTextView) mainView.findViewById(R.id.module_list);
        module.setSupportBackgroundTintList(ColorStateList.valueOf(0xFF8a63cc));
        module.setTypeface(DataManager.getInstance().myriadpro_regular);
        module.setText(R.string.anymodule);

        period = (AppCompatTextView) mainView.findViewById(R.id.period_list);
        period.setSupportBackgroundTintList(ColorStateList.valueOf(0xFF8a63cc));
        period.setTypeface(DataManager.getInstance().myriadpro_regular);
        period.setText(R.string.last_7_days);

        compareTo = (AppCompatTextView) mainView.findViewById(R.id.compareto);
        compareTo.setSupportBackgroundTintList(ColorStateList.valueOf(0xFF8a63cc));
        compareTo.setTypeface(DataManager.getInstance().myriadpro_regular);
        compareTo.setText(R.string.no_one);
        compareTo.setAlpha(0.5f);

        final View.OnClickListener compareToListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!module.getText().toString().equals(DataManager.getInstance().mainActivity.getString(R.string.anymodule))) {
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
                    ((TextView) dialog.findViewById(R.id.dialog_title)).setText(R.string.choose_student);

                    ArrayList<String> items = new ArrayList<>();
                    items.add(getString(R.string.no_one));
//                    items.add(getString(R.string.top10));
                    items.add(getString(R.string.average));
                    List<Friend> friendList;
                    friendList = new Select().from(Friend.class).execute();
                    for (int i = 0; i < friendList.size(); i++)
                        items.add(friendList.get(i).name);
                    final ListView listView = (ListView) dialog.findViewById(R.id.dialog_listview);
                    listView.setAdapter(new GenericAdapter(DataManager.getInstance().mainActivity, compareTo.getText().toString(), items));
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            compareTo.setText(((TextView) view.findViewById(R.id.dialog_item_name)).getText().toString());
                            dialog.dismiss();
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            getData();
                                            ((MainActivity) getActivity()).hideProgressBar();
                                        }
                                    });
                                }
                            }).start();

                        }
                    });
                    ((MainActivity) getActivity()).showProgressBar2("");
                    dialog.show();
                } else {
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
                    ((TextView) dialog.findViewById(R.id.dialog_title)).setText("");

                    ArrayList<String> items = new ArrayList<>();
                    items.add(getString(R.string.no_one));
//                    items.add(getString(R.string.top10));
//                    items.add(getString(R.string.average));
                    List<Friend> friendList;
                    friendList = new Select().from(Friend.class).execute();
                    for (int i = 0; i < friendList.size(); i++)
                        items.add(friendList.get(i).name);
                    final ListView listView = (ListView) dialog.findViewById(R.id.dialog_listview);
                    listView.setAdapter(new GenericAdapter(DataManager.getInstance().mainActivity, compareTo.getText().toString(), items));
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            compareTo.setText(((TextView) view.findViewById(R.id.dialog_item_name)).getText().toString());
                            dialog.dismiss();
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            getData();
                                            ((MainActivity) getActivity()).hideProgressBar();
                                        }
                                    });
                                }
                            }).start();

                        }
                    });
                    ((MainActivity) getActivity()).showProgressBar2("");
                    dialog.show();
                }
            }
        };

        final TextView description = (TextView) mainView.findViewById(R.id.description);
        description.setTypeface(DataManager.getInstance().myriadpro_regular);
        description.setText(R.string.last_week);

        ((TextView) mainView.findViewById(R.id.module)).setTypeface(DataManager.getInstance().myriadpro_regular);
        ((TextView) mainView.findViewById(R.id.period)).setTypeface(DataManager.getInstance().myriadpro_regular);
        ((TextView) mainView.findViewById(R.id.compare_to)).setTypeface(DataManager.getInstance().myriadpro_regular);

        //Setting the module
        module.setOnClickListener(new View.OnClickListener() {
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
                listView.setAdapter(new ModuleAdapter2(DataManager.getInstance().mainActivity, module.getText().toString()));
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        String titleText = ((TextView) view.findViewById(R.id.dialog_item_name)).getText().toString();

                        List<Courses> coursesList = new Select().from(Courses.class).execute();

                        for (int j = 0; j < coursesList.size(); j++) {
                            String courseName = coursesList.get(j).name;
                            if(courseName.equals(titleText)) {
                                return;
                            }
                        }

                        dialog.dismiss();
                        module.setText(titleText);

                        if(!module.getText().toString().equals(getString(R.string.anymodule))) {
                            compareTo.setOnClickListener(compareToListener);
                            compareTo.setAlpha(1.0f);
                        } else {
                            compareTo.setOnClickListener(null);
                            compareTo.setAlpha(0.5f);
                            compareTo.setText(getString(R.string.no_one));
                        }

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (module.getText().toString().replace(" -", "").equals(getString(R.string.anymodule)) && (compareTo.getText().toString().equals(getString(R.string.average)) || compareTo.getText().toString().equals(getString(R.string.top10))) ){
                                            compareTo.setText(R.string.no_one);
                                        }
                                        getData();
                                        ((MainActivity) getActivity()).hideProgressBar();
                                    }
                                });
                            }
                        }).start();

                    }
                });
                ((MainActivity) getActivity()).showProgressBar2("");
                dialog.show();
            }
        });

        period.setOnClickListener(new View.OnClickListener() {
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
                ((TextView) dialog.findViewById(R.id.dialog_title)).setText(R.string.time_period);

                ArrayList<String> items = new ArrayList<>();
//                items.add(getString(R.string.last_24_hours));
                items.add(getString(R.string.last_7_days));
                items.add(getString(R.string.last_30_days));
                //items.add(getString(R.string.Overall));
                final ListView listView = (ListView) dialog.findViewById(R.id.dialog_listview);
                listView.setAdapter(new GenericAdapter(DataManager.getInstance().mainActivity, period.getText().toString(), items));
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        period.setText(((TextView) view.findViewById(R.id.dialog_item_name)).getText().toString());
//                        if(period.getText().toString().equals(getString(R.string.last_24_hours))) {
//                            description.setText(R.string.last_day);
//                        } else
                        if (period.getText().toString().equals(getString(R.string.last_7_days))) {
                            description.setText(R.string.last_week_engagement);
                        } else if (period.getText().toString().equals(getString(R.string.last_30_days))) {
                            description.setText(R.string.last_month_engagement);
                        } else if (period.getText().toString().equals(getString(R.string.overall))) {
                            description.setText(R.string.Overall_engagement);
                        }
                        dialog.dismiss();
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        getData();
                                        ((MainActivity) getActivity()).hideProgressBar();
                                    }
                                });
                            }
                        }).start();

                    }
                });
                ((MainActivity) getActivity()).showProgressBar2("");
                dialog.show();
            }
        });

        ((ImageView)mainView.findViewById(R.id.change_graph_btn)).setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.bar_graph));
        mainView.findViewById(R.id.change_graph_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //switch between bar / graph
                if (isBar){
                    isBar = false;
                    ((ImageView)v).setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.line_graph));

                } else {
                    isBar = true;
                    ((ImageView)v).setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.bar_graph));
                }

                setData();
            }
        });

        mainView.findViewById(R.id.change_graph_btn).performClick();

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getData();
            }
        }, 100);

        return mainView;
    }


    private void getData() {

        if(DataManager.getInstance().user.isStaff) {
            list = new ArrayList<>();

            if (compareTo.getText().toString().equals(getString(R.string.no_one))) {
                if (period.getText().toString().equals(getString(R.string.last_7_days))) {
                    for (int i = 0; i < 7; i++) {
                        ED item = new ED();
                        item.day = "" + (i+1);
                        item.activity_points = Math.abs(new Random().nextInt()) % 100;

                        list.add(item);
                    }

                    Collections.sort(list, new Comparator<ED>() {
                        @Override
                        public int compare(ED s1, ED s2) {
                            return s1.day.compareToIgnoreCase(s2.day);
                        }
                    });
                } else if (period.getText().toString().equals(getString(R.string.last_30_days))) {

                    for (int i = 0; i < 30; i++) {
                        ED item = new ED();
                        item.day = "" + (i+1);
                        item.activity_points = Math.abs(new Random().nextInt()) % 100;

                        list.add(item);
                    }

                    Collections.sort(list, new Comparator<ED>() {
                        @Override
                        public int compare(ED s1, ED s2) {
                            return s1.day.compareToIgnoreCase(s2.day);
                        }
                    });
                }  else if (period.getText().toString().equals(getString(R.string.overall))) {

                    try {

                        Calendar calendar = Calendar.getInstance();

                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                        Date startDate = dateFormat.parse("01/01/2017");
                        Date now = new Date();

                        while (now.after(startDate)) {
                            int numberOfDays = Math.abs(new Random().nextInt()) % 5 + 1;
                            calendar.setTime(startDate);
                            calendar.add(Calendar.DATE, numberOfDays);

                            startDate = calendar.getTime();

                            ED item = new ED();
                            item.day = dateFormat.format(startDate);
                            item.realDate = startDate;
                            item.activity_points = Math.abs(new Random().nextInt()) % 100;

                            list.add(item);
                        }

                        Collections.sort(list, new Comparator<ED>() {
                            @Override
                            public int compare(ED s1, ED s2) {
                                return s1.realDate.compareTo(s2.realDate);
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else {
                if (period.getText().toString().equals(getString(R.string.last_7_days))) {
                    for (int i = 0; i < 7; i++) {
                        ED item = new ED();
                        item.day = "" + (i+1);
                        item.activity_points = Math.abs(new Random().nextInt()) % 100;
                        item.student_id = DataManager.getInstance().user.jisc_student_id;
                        list.add(item);

                        ED item1 = new ED();
                        item1.day = "" + (i+1);
                        item1.activity_points = Math.abs(new Random().nextInt()) % 100;
                        item1.student_id = "";
                        list.add(item1);
                    }

                    Collections.sort(list, new Comparator<ED>() {
                        @Override
                        public int compare(ED s1, ED s2) {
                            return s1.day.compareToIgnoreCase(s2.day);
                        }
                    });
                } else if (period.getText().toString().equals(getString(R.string.last_30_days))) {

                    for (int i = 0; i < 30; i++) {
                        ED item = new ED();
                        item.day = "" + (i+1);
                        item.activity_points = Math.abs(new Random().nextInt()) % 100;
                        item.student_id = DataManager.getInstance().user.jisc_student_id;
                        list.add(item);

                        ED item1 = new ED();
                        item1.day = "" + (i+1);
                        item1.activity_points = Math.abs(new Random().nextInt()) % 100;
                        item1.student_id = "";
                        list.add(item1);
                    }

                    Collections.sort(list, new Comparator<ED>() {
                        @Override
                        public int compare(ED s1, ED s2) {
                            return s1.day.compareToIgnoreCase(s2.day);
                        }
                    });
                }  else if (period.getText().toString().equals(getString(R.string.overall))) {

                    try {

                        Calendar calendar = Calendar.getInstance();

                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                        Date startDate = dateFormat.parse("01/01/2017");
                        Date now = new Date();

                        while (now.after(startDate)) {
                            int numberOfDays = Math.abs(new Random().nextInt()) % 5 + 1;
                            calendar.setTime(startDate);
                            calendar.add(Calendar.DATE, numberOfDays);

                            startDate = calendar.getTime();

                            ED item = new ED();
                            item.day = dateFormat.format(startDate);
                            item.realDate = startDate;
                            item.activity_points = Math.abs(new Random().nextInt()) % 100;
                            item.student_id = DataManager.getInstance().user.jisc_student_id;
                            list.add(item);

                            ED item1 = new ED();
                            item1.day = dateFormat.format(startDate);
                            item1.realDate = startDate;
                            item1.activity_points = Math.abs(new Random().nextInt()) % 100;
                            item1.student_id = "";
                            list.add(item1);
                        }

                        Collections.sort(list, new Comparator<ED>() {
                            @Override
                            public int compare(ED s1, ED s2) {
                                return s1.realDate.compareTo(s2.realDate);
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            setData();

            DataManager.getInstance().mainActivity.hideProgressBar();
            return;
        }

        String filterType;
        String filterValue;
        boolean isCourse = false;

        String moduleTitleName = module.getText().toString().replace(" -", "");
        if (new Select().from(Module.class).where("module_name LIKE ?", "%" + moduleTitleName + "%").exists()) {
            filterType = "module";
            filterValue = ((Module) new Select().from(Module.class).where("module_name = ?", moduleTitleName).executeSingle()).id;
        } else {
            filterType = "course";
            if (new Select().from(Courses.class).where("course_name LIKE ?", "%" + moduleTitleName + "%").exists()) {
                filterValue = ((Courses) new Select().from(Courses.class).where("course_name = ?", moduleTitleName).executeSingle()).id;
                isCourse = true;
            } else {
                filterValue = "";
            }
        }

        String compareValue;
        String compareType;
//        if (compareTo.getText().toString().contains("Top")) {
//            compareValue = "10";
//            compareType = "top";
//        } else
        if (!compareTo.getText().toString().equals(getString(R.string.no_one))
//                && !compareTo.getText().toString().equals(getString(R.string.top10))
                && !compareTo.getText().toString().equals(getString(R.string.average))) {
            compareValue = ((Friend) new Select().from(Friend.class).where("name = ?", compareTo.getText().toString()).executeSingle()).jisc_student_id.replace("[", "").replace("]", "").replace("\"", "");
            compareType = "friend";
        }
        else if (compareTo.getText().toString().equals(getString(R.string.average))){
            compareValue = "";
            compareType = "average";
        }
        else {
            compareType = "";
            compareValue = "";
        }

        String scope = DataManager.getInstance().api_values.get(period.getText().toString().toLowerCase()).replace(" ", "_").toLowerCase();

        list = NetworkManager.getInstance().getEngagementGraph(
                scope,
                compareType,
                compareValue,
                filterType,
                filterValue,
                isCourse
        );

        setData();

        DataManager.getInstance().mainActivity.hideProgressBar();
    }

    //BAR DATA
    /*
    xAxis: {
		min: 0,
        title: {
            text: null
        }
    },
    series: [{
        name: 'Me',
        data: [20, 31, 50, 10, 25]
    }, {
        name: 'Average',
        data: [25, 10, 50, 31, 20]
    }]
     */

    private void setData() {

        if(list == null) {
            list = new ArrayList<>();
        }

        ArrayList<ED> tempList = new ArrayList<>();
        tempList.addAll(list);

        if (compareTo.getText().toString().equals(getString(R.string.no_one))) {
            if (period.getText().toString().equals(getString(R.string.last_7_days))) {

                final ArrayList<String> xVals = new ArrayList<>();
                ArrayList<String> vals1 = new ArrayList<>();

                String name = getString(R.string.me);

                Date date = new Date();
                date.setTime(date.getTime() - 6*86400000);

                Collections.reverse(tempList);

                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM");
                for (int i = 0; i < tempList.size(); i++) {
                    String day = dateFormat.format(date);
                    date.setTime(date.getTime() + 86400000);
                    vals1.add(""+tempList.get(i).activity_points+"");
                    xVals.add("\'"+day+"\'");
                }

                String webData = "xAxis: { title: {text:null}, categories:[";
                webData += TextUtils.join(",",xVals);
                webData += "]}, series:[{name:\'"+name+"\',data: ["+TextUtils.join(",",vals1)+"]}]";

                String html = getHighhartsString();
                html = html.replace("<<<REPLACE_DATA_HERE>>>",webData);
                html = html.replace("height:1000px","height:"+webviewHeight+"px");

                webView.loadDataWithBaseURL("", html, "text/html", "UTF-8", "");


            } else if (period.getText().toString().equals(getString(R.string.last_30_days))) {

                ArrayList<String> vals1 = new ArrayList<>();
                ArrayList<String> xVals = new ArrayList<>();

                Integer val1 = 0;

                Collections.reverse(tempList);

                Calendar calendar = Calendar.getInstance();
                calendar.setTime(new Date());
                calendar.add(Calendar.DATE, -27);

                String day;

                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

                for (int i = 0; i < tempList.size(); i++) {
                    val1 = val1 + tempList.get(i).activity_points;
                    if (i == 6 || i == 13 || i == 20 || i == 27){
                        vals1.add(""+val1);

                        calendar.add(Calendar.DATE, 6);
                        day = dateFormat.format(calendar.getTime());
                        calendar.add(Calendar.DATE, 1);
                        xVals.add("\'"+day+"\'");
                        val1 = 0;
                    }
                }

                String name = getString(R.string.me);

                String webData = "xAxis: { title: {text:null}, categories:[";
                webData += TextUtils.join(",",xVals);
                webData += "]}, series:[{name:\'"+name+"\',data: ["+TextUtils.join(",",vals1)+"]}]";

                String html = getHighhartsString();
                html = html.replace("<<<REPLACE_DATA_HERE>>>",webData);
                html = html.replace("height:1000px","height:"+webviewHeight+"px");

                Log.e("JISC", "HTML: "+html);
                webView.loadDataWithBaseURL("", html, "text/html", "UTF-8", "");
            }
        } else {
            if (period.getText().toString().equals(getString(R.string.last_7_days))) {

                final ArrayList<String> xVals = new ArrayList<>();

                ArrayList<Integer> vals3 = new ArrayList<>();
                ArrayList<Integer> vals4 = new ArrayList<>();

                ArrayList<String> vals1 = new ArrayList<>();
                ArrayList<String> vals2 = new ArrayList<>();

                String name = getString(R.string.me);
                String id = DataManager.getInstance().user.jisc_student_id;
                if(DataManager.getInstance().user.isDemo) {
                    id = "demouser";
                }

                Integer value_1;
                Integer value_2;

                String day;
                Calendar c = Calendar.getInstance();
                Long curr = c.getTimeInMillis() - 518400000;
                c.setTimeInMillis(curr);

                if(DataManager.getInstance().user.isDemo) {
                    for (int i = 0; i < tempList.size(); i++) {
                        if (tempList.get(i).student_id.equals(id)) {
                            value_1 = tempList.get(i).activity_points;
                            vals3.add(value_1);
                        } else {
                            value_2 = tempList.get(i).activity_points;
                            vals4.add(value_2);
                        }
                    }
                } else {
                    for (int i = 0; i < tempList.size(); i++) {
                        if (tempList.get(i).student_id.contains(id)) {
                            value_1 = tempList.get(i).activity_points;
                            vals3.add(value_1);
                        } else {
                            value_2 = tempList.get(i).activity_points;
                            vals4.add(value_2);
                        }
                    }
                }

                Collections.reverse(vals3);
                Collections.reverse(vals4);

                Date date = new Date();
                date.setTime(date.getTime() - 6*86400000);

                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM");

                for (int i = 0; i < vals3.size(); i++) {
                    day = dateFormat.format(date);
                    date.setTime(date.getTime() + 86400000);
                    vals1.add(""+vals3.get(i));
                    vals2.add(""+vals4.get(i));
                    xVals.add("\'"+day+"\'");
                }

                String webData = "xAxis: { title: {text:null}, categories:[";
                webData += TextUtils.join(",",xVals);
                webData += "]}, series:[{name:\'"+name+"\',data: ["+TextUtils.join(",",vals1)+"]},{name:\'"+compareTo.getText().toString()+"\',data: ["+TextUtils.join(",",vals2)+"]}]";

                String html = getHighhartsString();
                html = html.replace("<<<REPLACE_DATA_HERE>>>",webData);
                html = html.replace("height:1000px","height:"+webviewHeight+"px");

                webView.loadDataWithBaseURL("", html, "text/html", "UTF-8", "");

            } else if (period.getText().toString().equals(getString(R.string.last_30_days))) {

                final ArrayList<String> xVals = new ArrayList<>();

                ArrayList<Integer> vals3 = new ArrayList<>();
                ArrayList<Integer> vals4 = new ArrayList<>();

                ArrayList<String> vals1 = new ArrayList<>();
                ArrayList<String> vals2 = new ArrayList<>();

                String name = getString(R.string.me);

                String id = DataManager.getInstance().user.jisc_student_id;
                if(DataManager.getInstance().user.isDemo) {
                    id = "demouser";
                }

                Integer value_1;
                Integer value_2;
                String label;

                Calendar c = Calendar.getInstance();
                Long curr = c.getTimeInMillis() - (3 * 518400000);
                c.setTimeInMillis(curr);

                if(DataManager.getInstance().user.isDemo) {
                    for (int i = 0; i < tempList.size(); i++) {
                        if (tempList.get(i).student_id.equals(id)) {
                            value_1 = tempList.get(i).activity_points;
                            vals3.add(value_1);
                        } else {
                            value_2 = tempList.get(i).activity_points;
                            vals4.add(value_2);
                        }
                    }
                } else {
                    for (int i = 0; i < tempList.size(); i++) {
                        if (tempList.get(i).student_id.contains(id)) {
                            value_1 = tempList.get(i).activity_points;
                            vals3.add(value_1);
                        } else {
                            value_2 = tempList.get(i).activity_points;
                            vals4.add(value_2);
                        }
                    }
                }

                Collections.reverse(vals3);
                Collections.reverse(vals4);

                Integer val1 = 0;
                Integer val2 = 0;

                Date date = new Date();
                long time = date.getTime() - 21*86400000;
                date.setTime(time);

                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

                for (int i = 0; i < vals3.size(); i++) {
                    val1 = val1 + vals3.get(i);
                    val2 = val2 + vals4.get(i);
                    if (i == 6 || i == 13 || i == 20 || i == 27) {

                        label = dateFormat.format(date);
                        date.setTime(date.getTime() + 7*86400000);

                        vals1.add(""+val1);
                        vals2.add(""+val2);

                        xVals.add("\'"+label+"\'");

                        val1 = 0;
                        val2 = 0;
                    }
                }

                String webData = "xAxis: { title: {text:null}, categories:[";
                webData += TextUtils.join(",",xVals);
                webData += "]}, series:[{name:\'"+name+"\',data: ["+TextUtils.join(",",vals1)+"]},{name:\'"+compareTo.getText().toString()+"\',data: ["+TextUtils.join(",",vals2)+"]}]";

                String html = getHighhartsString();
                html = html.replace("<<<REPLACE_DATA_HERE>>>",webData);
                html = html.replace("height:1000px","height:"+webviewHeight+"px");

                webView.loadDataWithBaseURL("", html, "text/html", "UTF-8", "");
            }
        }
    }

    public String getHighhartsString() {

        try {

            String path;
            if(isBar) {
                path = "highcharts/bargraph.html";
            } else {
                path = "highcharts/linegraph.html";
            }

            StringBuilder buf = new StringBuilder();
            InputStream json = DataManager.getInstance().mainActivity.getAssets().open(path);
            BufferedReader in = new BufferedReader(new InputStreamReader(json, "UTF-8"));
            String str;
            while ((str=in.readLine()) != null) {
                buf.append(str);
            }
            in.close();
            return buf.toString();
        } catch (Exception e) {
            return "";
        }

    }
}