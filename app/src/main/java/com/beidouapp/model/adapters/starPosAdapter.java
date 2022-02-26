package com.beidouapp.model.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.beidouapp.R;
import com.beidouapp.model.DataBase.starposDB;
import com.beidouapp.ui.fragment.starPos;

import java.util.ArrayList;
import java.util.List;

/**
 * 这个Adapter是用来加载收藏点那个recycleView的
 */
public class starPosAdapter extends RecyclerView.Adapter<starPosAdapter.ViewHoder> {

    List<starposDB> mlist = new ArrayList<>();
    private OnItemClickListener onItemClickListener;

    public starPosAdapter(List<starposDB> list){
        mlist = list;
    }


    @NonNull
    @Override
    public ViewHoder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_starfragment, parent, false);
        ViewHoder holder = new ViewHoder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHoder holder, int position) {
        starposDB starposDB = mlist.get(position);
        holder.tv_uid.setText(starposDB.getUid());
        holder.tv_postag.setText(starposDB.getTag());
        holder.tv_starpos.setText(starposDB.getText());

        holder.tv_uid.setTag(position);
        holder.tv_uid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener!=null){
                    onItemClickListener.onItemClick(v, (int)v.getTag());
                }
            }
        });
        holder.tv_uid.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (onItemClickListener!=null){
                    onItemClickListener.onItemLongClick(v, (int)v.getTag());
                }
                return false;
            }
        });

        holder.tv_postag.setTag(position);
        holder.tv_postag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener!=null){
                    onItemClickListener.onItemClick(v, (int)v.getTag());
                }
            }
        });
        holder.tv_postag.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (onItemClickListener!=null){
                    onItemClickListener.onItemLongClick(v, (int)v.getTag());
                }
                return false;
            }
        });

        holder.tv_starpos.setTag(position);
        holder.tv_starpos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener!=null){
                    onItemClickListener.onItemClick(v, (int)v.getTag());
                }
            }
        });
        holder.tv_starpos.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (onItemClickListener!=null){
                    onItemClickListener.onItemLongClick(v, (int)v.getTag());
                }
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mlist.size();
    }

    //回调
    public interface OnItemClickListener{
        void onItemClick(View v, int pos);
        void onItemLongClick(View v, int pos);
    }

    public void setOnItemClickListener(starPosAdapter.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void deleteData(int pos){
        Log.d("zw", "deleteData: 此时开始删除recycleView里面的数据");
        mlist.remove(pos);
        notifyItemRemoved(pos);
        if(pos != getItemCount()) {
            notifyItemRangeChanged(pos, getItemCount());
        }
    }

    //ViewHolder
    static class ViewHoder extends RecyclerView.ViewHolder{

        TextView tv_starpos;
        TextView tv_postag;
        TextView tv_uid;

        public ViewHoder(@NonNull View itemView) {
            super(itemView);
            tv_starpos = (TextView) itemView.findViewById(R.id.tv_starpos);
            tv_postag = (TextView) itemView.findViewById(R.id.tv_postag);
            tv_uid = (TextView) itemView.findViewById(R.id.tv_uid);
        }
    }
}
