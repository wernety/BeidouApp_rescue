package com.beidouapp.model.adapters;


import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.beidouapp.R;
import com.beidouapp.model.Relation;
import com.beidouapp.model.base.BaseTreeAdapter;

import java.util.ArrayList;
import java.util.List;

public class locOthers extends BaseTreeAdapter <locOthers.RelationViewHolder, Relation>{

    private final float fixLeft = 36;
    private OnItemClickListener onItemClickListener;
    private List<String> OtherDeviceID;
    private List<Integer> OtherStatus;

    public locOthers(List<Relation> list, Context context) {
        super(list, context);
    }

    public locOthers(List<Relation> list, Context context, List<String> OtherDeviceID, List<Integer> OtherStatus){
        super(list,context);
        this.OtherDeviceID = OtherDeviceID;
        this.OtherStatus = OtherStatus;
//        Log.d("zw", "locOthers: 在locOthers里面的ID有哪些: " + OtherDeviceID);
    }

    @Override
    public void onBindHolder(@NonNull RelationViewHolder holder, int position, Relation bean) {
        holder.tvName.setText(bean.getLabel());
        if (bean.getChildren() != null && bean.getChildren().size() > 0){
            holder.ivNext.setVisibility(View.VISIBLE);
            holder.ivCheck.setVisibility(View.INVISIBLE);
            holder.tvOnline.setVisibility(View.INVISIBLE);
        }else {
            holder.ivNext.setVisibility(View.INVISIBLE);
            holder.ivCheck.setVisibility(View.VISIBLE);
            holder.tvOnline.setVisibility(View.VISIBLE);
        }
        if (bean.isOpen()){
            holder.ivNext.setRotation(90);
        }else {
            holder.ivNext.setRotation(0);
        }
        Log.d("zw", "onBindHolder: 在组织列表初始当中的ID号：" + bean.getId());
        int num = 0;        //这个num可以作为这个里面的全局变量，因为构造之后不会改变
        int i;
//        Log.d("zw", "locOthers: 在locOthers里面的ID有哪些: " + OtherDeviceID);
        num = OtherDeviceID.size();
        for(i=0;i<num;i++)
        {
//            Log.d("zw", "onBindHolder: 当前循环中的bean的ID是：" + bean.getId());
//            Log.d("zw", "xzw: 在locOthers循环里面的ID有哪些: " + OtherDeviceID.get(i));
            Log.d("zw", "onBindHolder: 当前循环中两者是否相等" + (OtherDeviceID.get(i).equals(bean.getId())));
            if(OtherDeviceID.get(i).equals(bean.getId()))
            {
                Log.d("zw", "onBindHolder: 当前判断中的bean的ID是：" + bean.getId());
                //相等的话，就判断当前ID对应的状态是不是为True，是就写成在线，其他的任何情况都是离线
                if(OtherStatus.get(i).equals(1)){
                    holder.tvOnline.setText("在线");
                    holder.tvOnline.setTextColor(0xFF000000);
                }
                else{
                    holder.tvOnline.setText("离线");
                    holder.tvOnline.setTextColor(Color.RED);
                }
            }
        }

        int left = (int) ((bean.getLeave() + 1) * fixLeft);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) holder.ivAvatar.getLayoutParams();
        layoutParams.leftMargin = left;
        holder.ivAvatar.setLayoutParams(layoutParams);
        holder.ivCheck.setTag(position);
        holder.ivCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null){
                    onItemClickListener.onCheckClick(v,(int) v.getTag());
                }
            }
        });

        holder.ivCheck.setImageResource(bean.isCheck() ? R.mipmap.road_checked : R.mipmap.road_check);
//        Log.d("zw", "onBindHolder: 是否被选中: " + bean.isCheck());

        holder.ivAvatar.setTag(position);
        holder.ivAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null){
                    onItemClickListener.onCheckClick(v,(int) v.getTag());
                }
            }
        });
        holder.tvName.setTag(position);
        holder.tvName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null){
                    onItemClickListener.onOpenChildClick(v,(int) v.getTag());
                }
            }
        });

    }


    @NonNull
    @Override
    public RelationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RelationViewHolder(getLayoutView(R.layout.item_locsend, parent));
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener{
        void onCheckClick(View v, int pos);
        void onOpenChildClick(View v, int pos);
    }
    public class RelationViewHolder extends RecyclerView.ViewHolder {

        TextView tvName;
        TextView tvOnline;
        ImageView ivAvatar;
        ImageView ivNext;
        ImageView ivCheck;
        public RelationViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            ivAvatar = itemView.findViewById(R.id.ivAvatar);
            ivNext = itemView.findViewById(R.id.ivNext);
            ivCheck = itemView.findViewById(R.id.ivcheck);
            tvOnline = itemView.findViewById(R.id.ivOnline);
        }
    }
}

