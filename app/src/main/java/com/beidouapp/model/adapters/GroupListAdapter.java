package com.beidouapp.model.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.beidouapp.R;
import com.beidouapp.model.messages.Group;

import java.util.List;

public class GroupListAdapter extends BaseAdapter {
    private List<Group> data;
    private Context context;
    private GroupViewHolder viewHolder;
    private View view;

    public GroupListAdapter(Context context, List<Group> data) {
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
        Group group = data.get(position);
        if (convertView == null) {
            viewHolder = new GroupViewHolder();
            view = View.inflate(context, R.layout.item_friend_or_group, null);

            viewHolder.name = (TextView) view
                    .findViewById(R.id.item_friend_or_group_tv);
            view.setTag(viewHolder);

        } else {
            view = convertView;
            viewHolder = (GroupViewHolder) convertView.getTag();
        }
        String name = group.getName();
        viewHolder.name.setText(name);
        return view;
    }

}

class GroupViewHolder {
    public TextView name;
    //public ImageView code;
}
