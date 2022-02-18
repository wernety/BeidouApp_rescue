package com.beidouapp.model.adapters;

import android.content.Context;
import android.os.Handler;
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

import java.util.List;

public class traceAdapter extends BaseTreeAdapter<traceAdapter.TraceViewHolder, Relation> {

    private final float fixLeft = 36;
    private OnItemClickListener onItemClickListener;

    public traceAdapter(List<Relation> list, Context context) {
        super(list, context);
        Log.d("zw", "traceAdapter: traceAdapter初始化成功");
    }

    @Override
    public void onBindHolder(@NonNull traceAdapter.TraceViewHolder holder, int position, Relation bean) {
        holder.tvName_trace.setText(bean.getLabel());
        if (bean.getChildren() != null && bean.getChildren().size() > 0){
            holder.ivNext_trace.setVisibility(View.VISIBLE);
            holder.ivCheck_trace.setVisibility(View.INVISIBLE);
        }else {
            holder.ivNext_trace.setVisibility(View.INVISIBLE);
            holder.ivCheck_trace.setVisibility(View.VISIBLE);
        }
        if (bean.isOpen()){
            holder.ivNext_trace.setRotation(90);
        }else {
            holder.ivNext_trace.setRotation(0);
        }

        int left = (int) ((bean.getLeave() + 1) * fixLeft);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) holder.ivAvatar_trace.getLayoutParams();
        layoutParams.leftMargin = left;
        holder.ivAvatar_trace.setLayoutParams(layoutParams);
        holder.ivCheck_trace.setTag(position);
        holder.ivCheck_trace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null){
                    onItemClickListener.onCheckClick(v,(int) v.getTag());
                }
            }
        });

        holder.ivCheck_trace.setImageResource(bean.isCheck() ? R.mipmap.road_checked : R.mipmap.road_check);
//        Log.d("zw", "onBindHolder: 是否被选中: " + bean.isCheck());

        holder.ivAvatar_trace.setTag(position);
        holder.ivAvatar_trace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null){
                    onItemClickListener.onCheckClick(v,(int) v.getTag());
                }
            }
        });
        holder.tvName_trace.setTag(position);
        holder.tvName_trace.setOnClickListener(new View.OnClickListener() {
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
    public traceAdapter.TraceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TraceViewHolder(getLayoutView(R.layout.item_trace, parent));
    }

    /**
     * 回调
     */
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener{
        void onCheckClick(View v, int pos);
        void onOpenChildClick(View v, int pos);
    }


    public class TraceViewHolder extends RecyclerView.ViewHolder {
        ImageView ivAvatar_trace;
        TextView tvName_trace;
        ImageView ivNext_trace;
        ImageView ivCheck_trace;

        public TraceViewHolder(@NonNull View itemView) {
            super(itemView);
            ivAvatar_trace = itemView.findViewById(R.id.ivAvatar_trace);
            tvName_trace = itemView.findViewById(R.id.tvName_trace);
            ivNext_trace = itemView.findViewById(R.id.ivNext_trace);
            ivCheck_trace = itemView.findViewById(R.id.ivCheck_trace);
        }
    }
}
