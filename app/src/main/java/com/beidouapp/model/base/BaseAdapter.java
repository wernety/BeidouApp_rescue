package com.beidouapp.model.base;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public abstract class BaseAdapter<T extends RecyclerView.ViewHolder,E> extends RecyclerView.Adapter<T> {

    protected List<E> list;
    protected Context context;
    private OnBaseItemClickListener onBaseItemClickListener;

    public BaseAdapter(List<E> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public final void onBindViewHolder(@NonNull T holder, int position) {
        holder.itemView.setTag(position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onBaseItemClickListener != null){
                    onBaseItemClickListener.onItemClick(view, (int) view.getTag());
                }
            }
        });
        E bean = list.get(position);
        onBindHolder(holder,position,bean);
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }
    protected View getLayoutView(int layout){
        return LayoutInflater.from(context).inflate(layout,null,false);
    }
    protected View getLayoutView(int layout, ViewGroup parent){
        return LayoutInflater.from(context).inflate(layout,parent,false);
    }
    public abstract void onBindHolder(@NonNull T holder,int position,E bean);

    public interface OnBaseItemClickListener{
        void onItemClick(View view, int position);
    }

    public void setOnBaseItemClickListener(OnBaseItemClickListener onBaseItemClickListener) {
        this.onBaseItemClickListener = onBaseItemClickListener;
    }

}
