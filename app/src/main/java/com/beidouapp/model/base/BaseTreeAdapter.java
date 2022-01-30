package com.beidouapp.model.base;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public abstract class BaseTreeAdapter<T extends RecyclerView.ViewHolder,E extends BaseModel> extends BaseAdapter<T ,E> {


    public BaseTreeAdapter(List<E> list, Context context) {
        super(list, context);
    }
    private int oldCheck = -1;
    public void setCheck(List<? extends BaseModel> mList, int position) {
        if (oldCheck >= 0 && mList.size() > oldCheck){
            mList.get(oldCheck).setCheck(false);
        }
        mList.get(position).setCheck(true);
    }

    public void setCheck(){

    }
    public<W extends BaseModel> void setOpenOrClose(List<W> mList, int pos) {
        W model = mList.get(pos);
        if (model.isOpen()){
            //如果是展开  把他关闭
            model.setOpen(false);
            //移除子集
            removeChild(model.getId(),mList,0);
        }else {
            //关闭状态  就是展开
            model.setOpen(true);
            List<W> children = (List<W>) model.getChildren();
            int size = children.size();
            //pos是你点击的item的position
            int leave = model.getLeave() + 1;
            for (int i = 0;i < size;i++){
                children.get(i).setLeave(leave);
            }
            mList.addAll(pos+1,children);
        }
    }
    private<W extends BaseModel> void removeChild(String parentId, List<W>mList, int start){

        for (int removeIndex = start;removeIndex < mList.size();removeIndex++){

            W model = mList.get(removeIndex);
            if (parentId.equals(model.getParentId())){
                mList.remove(removeIndex);
                removeIndex--;
                //这里使用递归去删除子集的子集
                if (model.getChildren() != null && model.getChildren().size() > 0 && model.isOpen()){
                    model.setOpen(false);
                    removeChild(model.getId(),mList,removeIndex);
                }
            }
        }
    }
}
