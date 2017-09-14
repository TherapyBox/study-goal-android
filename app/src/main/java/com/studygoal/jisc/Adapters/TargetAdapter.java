package com.studygoal.jisc.Adapters;

import android.app.Dialog;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.activeandroid.query.Select;
import com.bumptech.glide.Glide;
import com.studygoal.jisc.Managers.DataManager;
import com.studygoal.jisc.Managers.LinguisticManager;
import com.studygoal.jisc.Models.ActivityHistory;
import com.studygoal.jisc.Models.Module;
import com.studygoal.jisc.Models.Targets;
import com.studygoal.jisc.R;
import com.studygoal.jisc.Utils.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class TargetAdapter extends BaseAdapter {

    public interface TargetAdapterListener {
        void onDelete(Targets target, int finalPosition);

        void onEdit(Targets targets);
    }

    private Context mContext;

    public List<Targets> list;

    private TargetAdapterListener mListener;

    public TargetAdapter(Context context, TargetAdapterListener listener) {
        mContext = context;
        mListener = listener;
        list = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        position = list.size() - 1 - position;
        final Targets item = list.get(position);

        Module module = new Select().from(Module.class).where("module_id = ?", item.module_id).executeSingle();

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_target, parent, false);
        }

        List<ActivityHistory> activityHistoryList;

        if (module != null) {
            activityHistoryList = new Select().from(ActivityHistory.class).where("module_id = ?", item.module_id).and("activity = ?", item.activity).execute();
        } else {
            activityHistoryList = new Select().from(ActivityHistory.class).where("activity = ?", item.activity).execute();
        }

        Calendar date = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String current_date = dateFormat.format(date.getTime());

        boolean dueToday = false;

        SimpleDateFormat shortDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date convertedDate = null;
        try {
            convertedDate = shortDateFormat.parse(current_date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar dueDate = Calendar.getInstance();
        dueDate.setTime(convertedDate);
        String shortCurrentDate = shortDateFormat.format(Calendar.getInstance().getTime());

        switch (item.time_span.toLowerCase()) {
            case "daily": {
                String time = current_date.split(" ")[0];
                List<ActivityHistory> tmp = new ArrayList<>();
                for (int i = 0; i < activityHistoryList.size(); i++) {
                    if (time.equals(activityHistoryList.get(i).created_date.split(" ")[0]))
                        tmp.add(activityHistoryList.get(i));
                }
                activityHistoryList.clear();
                activityHistoryList.addAll(tmp);
                dueToday = true;
                break;
            }
            case "weekly": {
                List<ActivityHistory> tmp = new ArrayList<>();
                for (int i = 0; i < activityHistoryList.size(); i++) {
                    if (Utils.isInSameWeek(activityHistoryList.get(i).created_date.split(" ")[0]))
                        tmp.add(activityHistoryList.get(i));
                }
                activityHistoryList.clear();
                activityHistoryList.addAll(tmp);
                if(dueDate.DAY_OF_WEEK == 7){
                    dueToday = true;
                }
                break;
            }
            case "monthly": {
                String time = current_date.split(" ")[0].split("-")[0] + "-" + current_date.split(" ")[0].split("-")[1];
                List<ActivityHistory> tmp = new ArrayList<>();
                for (int i = 0; i < activityHistoryList.size(); i++) {
                    if (time.equals(activityHistoryList.get(i).created_date.split(" ")[0].split("-")[0] + "-" + activityHistoryList.get(i).created_date.split(" ")[0].split("-")[1]))
                        tmp.add(activityHistoryList.get(i));
                }
                activityHistoryList.clear();
                activityHistoryList.addAll(tmp);
                dueDate.set(Calendar.DAY_OF_MONTH, dueDate.getActualMaximum(Calendar.DAY_OF_MONTH));
                String nextDueDate = shortDateFormat.format(dueDate.getTime());
                if(shortCurrentDate.equals(nextDueDate)){
                    dueToday = true;
                }
                break;
            }
        }

        int necessary_time = Integer.parseInt(item.total_time);
        int spent_time = 0;

        for (int i = 0; i < activityHistoryList.size(); i++) {
            spent_time += Integer.parseInt(activityHistoryList.get(i).time_spent);
        }

        Log.d("Spend", "getView: " + spent_time + " " + necessary_time);
        if (dueToday && (spent_time < necessary_time))
            convertView.findViewById(R.id.colorbar).setBackgroundColor(0xFFFF0000);
        else if (spent_time < necessary_time)
            convertView.findViewById(R.id.colorbar).setBackgroundColor(0xFFff7400);
        else
            convertView.findViewById(R.id.colorbar).setBackgroundColor(0xFF00FF00);

        try {
            Glide.with(DataManager.getInstance().mainActivity).load(LinguisticManager.getInstance().images.get(item.activity)).into((ImageView) convertView.findViewById(R.id.activity_icon));
        } catch (Exception e) {
        }

        TextView textView = (TextView) convertView.findViewById(R.id.target_item_text);
        textView.setTypeface(DataManager.getInstance().myriadpro_regular);

        String text = "";
        text += LinguisticManager.getInstance().present.get(item.activity) + " ";
        int hour = Integer.parseInt(item.total_time) / 60;
        int minute = Integer.parseInt(item.total_time) % 60;
        text += (hour == 1) ? "1 " + mContext.getString(R.string.hour) : hour + " " + mContext.getString(R.string.hours) + " ";
        if (minute > 0)
            text += ((minute == 1) ? " " + mContext.getString(R.string.and) + " 1 " + mContext.getString(R.string.minute) + " " : " " + mContext.getString(R.string.and) + " " + minute + " " + mContext.getString(R.string.minutes) + " ");

        if (item.time_span.length() > 0)
            text += item.time_span.toLowerCase();

        if (module != null && module.name.length() > 0) {
            text += " " + mContext.getString(R.string._for) + " " + module.name;
        }

        textView.setText(text);

        final com.daimajia.swipe.SwipeLayout swipeLayout = (com.daimajia.swipe.SwipeLayout) convertView.findViewById(R.id.swipelayout);
        convertView.findViewById(R.id.edit).setOnClickListener(v -> {
            swipeLayout.close(true);

            if (mListener != null) {
                mListener.onEdit(item);
            }
        });

        final int finalPosition = position;
        convertView.findViewById(R.id.delete).setOnClickListener(v -> {
            final Dialog dialog = new Dialog(DataManager.getInstance().mainActivity);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_confirmation);
            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

            if (DataManager.getInstance().mainActivity.isLandscape) {
                DisplayMetrics displaymetrics = new DisplayMetrics();
                DataManager.getInstance().mainActivity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
                int width = (int) (displaymetrics.widthPixels * 0.45);

                WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
                params.width = width;
                dialog.getWindow().setAttributes(params);
            }

            ((TextView) dialog.findViewById(R.id.dialog_title)).setTypeface(DataManager.getInstance().oratorstd_typeface);
            ((TextView) dialog.findViewById(R.id.dialog_title)).setText(R.string.confirmation);

            ((TextView) dialog.findViewById(R.id.dialog_message)).setTypeface(DataManager.getInstance().myriadpro_regular);
            ((TextView) dialog.findViewById(R.id.dialog_message)).setText(R.string.confirm_delete_message);

            ((TextView) dialog.findViewById(R.id.dialog_no_text)).setTypeface(DataManager.getInstance().myriadpro_regular);
            ((TextView) dialog.findViewById(R.id.dialog_no_text)).setText(R.string.no);

            ((TextView) dialog.findViewById(R.id.dialog_ok_text)).setTypeface(DataManager.getInstance().myriadpro_regular);
            ((TextView) dialog.findViewById(R.id.dialog_ok_text)).setText(R.string.yes);

            dialog.findViewById(R.id.dialog_ok).setOnClickListener(v1 -> {
                dialog.dismiss();
                swipeLayout.close(true);

                if (mListener != null) {
                    mListener.onDelete(item, finalPosition);
                }
            });

            dialog.findViewById(R.id.dialog_no).setOnClickListener(v12 -> dialog.dismiss());
            dialog.show();
        });

        convertView.setTag(item.target_id);
        return convertView;
    }
}
