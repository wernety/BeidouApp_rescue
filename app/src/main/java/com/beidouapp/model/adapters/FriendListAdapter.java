package com.beidouapp.model.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.beidouapp.R;
import com.beidouapp.model.User;

import java.util.List;

public class FriendListAdapter extends BaseAdapter {
    private List<User> data;
    private Context context;
    private FriendViewHolder viewHolder;
    private View view;

    public FriendListAdapter(Context context, List<User> data) {
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
        User friend = data.get(position);
        if (convertView == null) {
            viewHolder = new FriendViewHolder();
            view = View.inflate(context, R.layout.item_friend_or_group, null);

            viewHolder.name = (TextView) view
                    .findViewById(R.id.item_friend_or_group_tv);
            view.setTag(viewHolder);

        } else {
            view = convertView;
            viewHolder = (FriendViewHolder) convertView.getTag();
        }
        String name = friend.getNickName();
        viewHolder.name.setText(name);
        return view;
    }

}

class FriendViewHolder {
    public TextView name;
    //public ImageView code;
}
