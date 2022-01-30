package com.beidouapp.model.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;


import com.beidouapp.R;

import java.util.List;
import java.util.Map;

public class MessageAdapter extends BaseAdapter {
    private List<Map<String, Object>> data;
    private Context context;
    private ViewHolder viewHolder;
    private View view;

    public MessageAdapter(Context context, List<Map<String, Object>> data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Map<String, Object> map = data.get(position);

        if (convertView == null) {
            viewHolder = new ViewHolder();
            view = View.inflate(context, R.layout.contact_item, null);

            viewHolder.title = (TextView) view
                    .findViewById(R.id.contact_item_title);
            viewHolder.content = (TextView) view
                    .findViewById(R.id.contact_item_content);
            viewHolder.time = (TextView) view
                    .findViewById(R.id.contact_item_time);
            view.setTag(viewHolder);

        } else {
            view = convertView;
            viewHolder = (ViewHolder) convertView.getTag();
        }

        String title = map.get("title").toString();
        viewHolder.title.setText(title);
        String content = map.get("content").toString();
        viewHolder.content.setText(content);
        String time = map.get("time").toString();
        viewHolder.time.setText(time);

        return view;
    }
}


class ViewHolder {
    public TextView title;
    public TextView content;
    public TextView time;
    //public  ImageView code;
}
