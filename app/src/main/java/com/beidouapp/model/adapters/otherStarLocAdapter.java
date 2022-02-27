package com.beidouapp.model.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.beidouapp.R;
import com.beidouapp.model.DataBase.starLocFormOtherDB;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

/**
 * 此Adapter用作otherstarActivity里面的recycleView
 */
public class otherStarLocAdapter extends RecyclerView.Adapter<otherStarLocAdapter.otherStarViewHoder>{

    List<starLocFormOtherDB> mlist = LitePal.findAll(starLocFormOtherDB.class);
    private setOnItemClickListener setOnItemClickListener;

    public otherStarLocAdapter(List<starLocFormOtherDB> starLocFormOtherDBS){
        Log.d("zw", "otherStarLocAdapter: 此时的otherStarLocAdapter中的数据为 " + mlist.get(0).getLatitude());
    }

    @NonNull
    @Override
    public otherStarViewHoder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_other_star_activity, parent, false);
        otherStarViewHoder holder = new otherStarViewHoder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull otherStarViewHoder holder, int position) {
        Log.d("zw", "onBindViewHolder: 此时开始加载数据");
        holder.tv_otherStarLocInfo.setText(mlist.get(position).getLocInfo());
        Log.d("zw", "onBindViewHolder: 加载的第一个数据是" + mlist.get(position).getLocInfo());
        holder.tv_otherStarLocTag.setText(mlist.get(position).getTag());
        Log.d("w", "onBindViewHolder: 加载第二个数据是" + mlist.get(position).getTag());
        holder.tv_otherStarLocUid.setText(mlist.get(position).getUid());

        holder.tv_otherStarLocInfo.setTag(position);
        holder.tv_otherStarLocInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setOnItemClickListener.onItemClick(v, (int) v.getTag());
            }
        });

        holder.tv_otherStarLocUid.setTag(position);
        holder.tv_otherStarLocUid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setOnItemClickListener.onItemClick(v, (int) v.getTag());
            }
        });

        holder.tv_otherStarLocTag.setTag(position);
        holder.tv_otherStarLocTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setOnItemClickListener.onItemClick(v, (int)v.getTag());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mlist.size();
    }

    static class otherStarViewHoder extends RecyclerView.ViewHolder{

        TextView tv_otherStarLocUid;
        TextView tv_otherStarLocInfo;
        TextView tv_otherStarLocTag;

        public otherStarViewHoder(@NonNull View itemView) {
            super(itemView);
            tv_otherStarLocUid = itemView.findViewById(R.id.tv_otherStarLocUid);
            tv_otherStarLocInfo = itemView.findViewById(R.id.tv_otherStarLocInfo);
            tv_otherStarLocTag = itemView.findViewById(R.id.tv_otherStarLocTag);
        }
    }

    //回调
    public interface setOnItemClickListener{
        void onItemClick(View v, int pos);
    }

    public void setOnItemClickListener(setOnItemClickListener setOnItemClickListener){
        this.setOnItemClickListener = setOnItemClickListener;
    }
}
