package ff.windowcontrol.bean;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Feng on 2018/5/9.
 */

public abstract class CustomAdapter<T> extends RecyclerView.Adapter{
    private List<T> list;

    public CustomAdapter(List<T> list){
        this.list = list == null ?new ArrayList<T>() : list;
    }

    public List<T> getData(){
        return this.list;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof MyHolder){
            ((MyHolder<T>)holder).updateView(list.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public abstract static class MyHolder<T> extends RecyclerView.ViewHolder{

        public MyHolder(View itemView) {
            super(itemView);
        }

        public abstract void updateView(T data);
    }
}
