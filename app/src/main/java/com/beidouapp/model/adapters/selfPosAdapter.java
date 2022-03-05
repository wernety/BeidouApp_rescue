package com.beidouapp.model.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.beidouapp.R;
import com.beidouapp.ui.fragment.selfFragment;
import com.beidouapp.ui.fragment.starPos;

import java.util.ArrayList;
import java.util.List;

public class selfPosAdapter extends RecyclerView.Adapter<selfPosAdapter.selfViewHolder> {

    List<starPos> mlist = new ArrayList<starPos>();
    private OnItemClickListener onItemClickListener;

    public selfPosAdapter(List<starPos> list){
        this.mlist = list;
        Log.d("zw", "selfPosAdapter: 此时的列表是" + mlist);
    }

    @NonNull
    @Override
    public selfViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_selffragment, parent, false);
        Log.d("zw", "onCreateViewHolder: 创建了selfViewHolder的item");
        selfViewHolder holder = new selfViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull selfViewHolder holder, int position) {
        starPos starPos = mlist.get(position);
        holder.tv2_uid.setText(starPos.getUid());
        Log.d("zw", "onBindViewHolder: 此时的需要设置的用户是" + starPos.getUid());
        if (starPos.getTag().isEmpty()){
            holder.tv2_postag.setText("没有信息");
        }else{
            holder.tv2_postag.setText(starPos.getTag());
        }
        if(starPos.getText().isEmpty()){
            holder.tv2_selfpos.setText("没有位置名称");
        }else {
            holder.tv2_selfpos.setText(starPos.getText());
        }
        if (starPos.getStatus().equals("1")){
            holder.tv2_posStatus.setText("已发布");
        }else {
            holder.tv2_posStatus.setText("未发布");
        }
        holder.tv2_selfpos.setTag(position);
        holder.tv2_selfpos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener!=null){
                    onItemClickListener.onItemClick(v, (int)v.getTag());
                }
            }
        });
        holder.tv2_selfpos.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (onItemClickListener!=null){
                    onItemClickListener.onItemLongClick(v, (int)v.getTag());
                }
                return false;
            }
        });


        holder.tv2_postag.setTag(position);
        holder.tv2_postag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener!=null){
                    onItemClickListener.onItemClick(v, (int)v.getTag());
                }
            }
        });
        holder.tv2_postag.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (onItemClickListener!=null){
                    onItemClickListener.onItemLongClick(v, (int)v.getTag());
                }
                return false;
            }
        });

        holder.tv2_uid.setTag(position);
        holder.tv2_uid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //点击uid应该跳转到用户详情
            }
        });
    }

    //回调接口
    public void setOnItemClickListener(selfPosAdapter.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener{
        void onItemClick(View v, int pos);
        void onItemLongClick(View v, int pos);
    }

    @Override
    public int getItemCount() {
        return mlist.size();
    }

    public void deleteData(int pos){
        Log.d("zw", "deleteData: 此时开始删除recycleView里面的数据");
        mlist.remove(pos);
        notifyItemRemoved(pos);
        if(pos != getItemCount()) {
            notifyItemRangeChanged(pos, getItemCount());
        }
    }

    public void uploadSuccess(int pos){
        mlist.get(pos).setStatus("1");
        notifyDataSetChanged();
    }

    public void cancelUpload(int pos){
        mlist.get(pos).setStatus("0");
        notifyDataSetChanged();
    }

    public void changeInfo(String info, int pos){
        mlist.get(pos).setLocInfo(info);
    }

    public class selfViewHolder extends RecyclerView.ViewHolder{
        TextView tv2_selfpos;
        TextView tv2_postag;
        TextView tv2_uid;
        TextView tv2_posStatus;


        public selfViewHolder(@NonNull View itemView) {
            super(itemView);
            tv2_selfpos = (TextView) itemView.findViewById(R.id.tv2_selfpos);
            tv2_postag = (TextView) itemView.findViewById(R.id.tv2_postag);
            tv2_uid = (TextView) itemView.findViewById(R.id.tv2_uid);
            tv2_posStatus = (TextView) itemView.findViewById(R.id.tv2_posStatus);
        }
    }
}
