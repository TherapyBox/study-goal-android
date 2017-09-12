package com.studygoal.jisc.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.studygoal.jisc.Models.Event;
import com.studygoal.jisc.Models.Targets;
import com.studygoal.jisc.R;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Marjana-Tbox on 12/09/17.
 */

public class EventsAttendedAdapter extends BaseAdapter {

    private Context context;
    public List<Event> list;

    public EventsAttendedAdapter(Context context) {
        this.context = context;
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

        convertView = LayoutInflater.from(context).inflate(R.layout.item_events_attended, parent, false);

        TextView date = (TextView) convertView.findViewById(R.id.event_item_time_ago);
        date.setText(list.get(position).date);
        TextView activity = (TextView) convertView.findViewById(R.id.event_item_activity);
        activity.setText(list.get(position).activity);
        TextView module = (TextView) convertView.findViewById(R.id.event_item_module);
        module.setText(list.get(position).module);

        return convertView;
    }

    public void updateList(ArrayList<Event> events) {
        list.addAll(events);
        notifyDataSetChanged();
    }
}
