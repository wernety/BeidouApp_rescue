package com.beidouapp.model.adapters;

import android.content.Context;
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

import java.util.List;

public class RelationAdapter extends BaseTreeAdapter <RelationAdapter.RelationViewHolder, Relation>{

    private final float fixLeft = 30;
    private OnItemClickListener onItemClickListener;

    public RelationAdapter(List<Relation> list, Context context) {
        super(list, context);
    }

    @Override
    public void onBindHolder(@NonNull RelationViewHolder holder, int position, Relation bean) {
        holder.tvName.setText(bean.getLabel());
        if (bean.getChildren() != null){
            holder.ivNext.setVisibility(View.VISIBLE);
//            holder.ivCheck.setVisibility(View.INVISIBLE);
        }else {
            holder.ivNext.setVisibility(View.INVISIBLE);
//            holder.ivCheck.setVisibility(View.VISIBLE);
        }
        if (bean.isOpen()){
            holder.ivNext.setRotation(90);
        }else {
            holder.ivNext.setRotation(0);
        }
        if(bean.isCheck())
        {

        }else{

        }
        //是否离线
        int left = (int) ((bean.getLeave() + 1) * fixLeft);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) holder.ivAvatar.getLayoutParams();
        layoutParams.leftMargin = left;
        holder.ivAvatar.setLayoutParams(layoutParams);
        //holder.ivCheck.setImageResource(bean.isCheck() ? R.mipmap.road_checked : R.mipmap.road_check);
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
        return new RelationViewHolder(getLayoutView(R.layout.item_tree, parent));
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
        ImageView ivAvatar;
        ImageView ivNext;
//        ImageView ivCheck;
        public RelationViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            ivAvatar = itemView.findViewById(R.id.ivAvatar);
            ivNext = itemView.findViewById(R.id.ivNext);
//            ivCheck = itemView.findViewById(R.id.ivcheck);
        }
    }
}
