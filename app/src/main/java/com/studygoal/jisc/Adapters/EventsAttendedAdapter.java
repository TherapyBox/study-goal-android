package com.studygoal.jisc.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.studygoal.jisc.Models.Event;
import com.studygoal.jisc.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Marjana-Tbox on 12/09/17.
 */
public class EventsAttendedAdapter extends BaseAdapter {
    private Context mContext;
    private List<Event> mList;

    public EventsAttendedAdapter(Context context) {
        mContext = context;
        mList = new ArrayList<>();
    }

    public List<Event> getList() {
        return mList;
    }

    @Override
    public int getCount() {
        return mList.size();
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
        final Event item = mList.get(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_events_attended, parent, false);
        }

        TextView date = (TextView) convertView.findViewById(R.id.event_item_time_ago);
        date.setText(item.getDate());
        TextView activity = (TextView) convertView.findViewById(R.id.event_item_activity);
        activity.setText(item.getActivity());
        TextView module = (TextView) convertView.findViewById(R.id.event_item_module);
        module.setText(item.getModule());

        return convertView;
    }

    public void updateList(ArrayList<Event> events) {
        if (events != null && events.size() > 0) {
            mList.clear();
            mList.addAll(events);
            Collections.sort(mList, (o1, o2) -> {
                if (o1 != null && o2 != null) {
                    Long t1 = o1.getTime();
                    Long t2 = o2.getTime();

                    return t2.compareTo(t1);
                }

                return 0;
            });
            notifyDataSetChanged();
        }
    }

    public void addToList(ArrayList<Event> events) {
        if (events != null && events.size() > 0) {
            mList.addAll(events);
            Collections.sort(mList, (o1, o2) -> {
                if (o1 != null && o2 != null) {
                    Long t1 = o1.getTime();
                    Long t2 = o2.getTime();

                    return t2.compareTo(t1);
                }

                return 0;
            });
            notifyDataSetChanged();
        }
    }
}
