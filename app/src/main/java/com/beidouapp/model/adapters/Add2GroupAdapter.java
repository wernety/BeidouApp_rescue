package com.beidouapp.model.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.beidouapp.R;
import com.beidouapp.model.messages.Friend;

import java.util.List;

public class Add2GroupAdapter extends BaseAdapter {
    private Context context;
    private View view;
    public Add2GroupViewHolder viewHolder;
    private List<Friend> data;

    public Add2GroupAdapter(Context context, List<Friend> data) {
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
        Friend person = data.get(position);
        if (convertView == null) {
            viewHolder = new Add2GroupViewHolder();
            view = View.inflate(context, R.layout.item_add_group, null);
            viewHolder.name = (TextView) view.findViewById(R.id.tv_add2group);
            viewHolder.check = (ImageView) view.findViewById(R.id.iv_check_group);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (Add2GroupViewHolder) convertView.getTag();
        }

        String name = person.getFriendName();
        viewHolder.name.setText(name);
        viewHolder.check.setImageResource(R.mipmap.road_check);
        return view;
    }

    public static class Add2GroupViewHolder {
        public TextView name;
        public ImageView check;
    }
}


