package com.beidouapp.model.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.beidouapp.R;
import com.beidouapp.ui.fragment.starPos;

import java.util.ArrayList;
import java.util.List;

public class starPosAdapter extends RecyclerView.Adapter<starPosAdapter.ViewHoder> {

    List<starPos> mlist = new ArrayList<>();

    public starPosAdapter(List<starPos> list){
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
        starPos starPos = mlist.get(position);
        holder.tv_uid.setText(starPos.getUid());
        holder.tv_postag.setText(starPos.getTag());
        holder.tv_starpos.setText(starPos.getText());
    }

    @Override
    public int getItemCount() {
        return mlist.size();
    }

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
