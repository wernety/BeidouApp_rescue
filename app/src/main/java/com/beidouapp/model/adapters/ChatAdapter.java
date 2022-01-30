package com.beidouapp.model.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.beidouapp.R;
import com.beidouapp.model.messages.ChatMessage;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ChatAdapter extends BaseAdapter {
    List<ChatMessage> chatMessageList;
    LayoutInflater inflater;
    Context context;

    public ChatAdapter(Context context, List<ChatMessage> list) {
        this.chatMessageList = list;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        if (chatMessageList.get(position).getIsMeSend() == 1)
            return 0;
        else
            return 1;
    }

    @Override
    public int getCount() {
        return chatMessageList.size();
    }

    @Override
    public Object getItem(int position) {
        return chatMessageList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ChatMessage chatMessage = chatMessageList.get(position);
        String content = chatMessage.getContent();
        String time = formatTime(chatMessage.getTime());
        String nickname = chatMessage.getName();
        int isMeSend = chatMessage.getIsMeSend();
        //int isRead = chatMessage.getIsRead();
        final ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();
            if (isMeSend == 0) {
                convertView = inflater.inflate(R.layout.item_chat_receive_text, parent, false);
                holder.tv_content = convertView.findViewById(R.id.tv_content);
                holder.tv_sendtime = convertView.findViewById(R.id.tv_sendtime);
                holder.tv_display_name = convertView.findViewById(R.id.tv_display_name);
            } else {
                convertView = inflater.inflate(R.layout.item_chat_send_text, parent, false);
                holder.tv_content = convertView.findViewById(R.id.tv_content);
                holder.tv_sendtime = convertView.findViewById(R.id.tv_sendtime);
                //holder.tv_isRead = convertView.findViewById(R.id.tv_isRead);
            }
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tv_sendtime.setText(time);
        holder.tv_content.setVisibility(View.VISIBLE);
        holder.tv_content.setText(content);

        if (isMeSend == 1) {

//            if (isRead == 0) {
//                holder.tv_isRead.setText("未读");
//                holder.tv_isRead.setTextColor(context.getResources().getColor(R.color.jmui_jpush_blue));
//            } else if (isRead == 1) {
//                holder.tv_isRead.setText("已读");
//                holder.tv_isRead.setTextColor(Color.GRAY);
//            } else {
//                holder.tv_isRead.setText("");
//            }
        }else{
            holder.tv_display_name.setVisibility(View.VISIBLE);
            holder.tv_display_name.setText(nickname);
        }

        return convertView;
    }

    class ViewHolder {
        private TextView tv_content;
        private TextView tv_sendtime;
        private TextView tv_display_name;
        //private TextView tv_isRead;
    }

    private String formatTime(String timeMillis) {
        long timeMillisl=Long.parseLong(timeMillis);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(timeMillisl);
        return simpleDateFormat.format(date);
    }
}
