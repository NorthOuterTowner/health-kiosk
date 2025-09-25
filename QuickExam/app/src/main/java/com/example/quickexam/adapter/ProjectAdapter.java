package com.example.quickexam.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quickexam.R;
import com.example.quickexam.bean.ConfigBean;

import java.util.ArrayList;
import java.util.List;

public class ProjectAdapter extends RecyclerView.Adapter<ProjectAdapter.RecyclerHolder> {
    private Context mContext;
    private List<ConfigBean.SettingBean.ProjectBean> dataList = new ArrayList<>();
    private OnItemClickListener listener;
    public ProjectAdapter(RecyclerView recyclerView) {
        this.mContext = recyclerView.getContext();
    }

    public void setData(List<ConfigBean.SettingBean.ProjectBean> dataList) {
        if (null != dataList) {
            this.dataList.clear();
            this.dataList.addAll(dataList);
            notifyDataSetChanged();
        }
    }
    public List<ConfigBean.SettingBean.ProjectBean> getDataList() {
        return this.dataList;
    }
    public void changeData()
    {
        notifyDataSetChanged();
    }
    @Override
    public RecyclerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.project_item, parent, false);
        return new RecyclerHolder(view);
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }
    public void setOnItemClickListener(OnItemClickListener listenser) {
        this.listener = listenser;
    }
    @Override
    public void onBindViewHolder(RecyclerHolder holder, int position) {
        if (dataList.get(position).getIs_open() == 1) {
            holder.title.setText(dataList.get(position).getName());
            holder.num.setText(dataList.get(position).getValue());
            switch (dataList.get(position).getColor()){
                case 0:
                    holder.relative.setBackgroundResource(R.drawable.project_tw);
                    break;
                case 1:
                    holder.relative.setBackgroundResource(R.drawable.project_xy);
                    break;
                case 2:
                    holder.relative.setBackgroundResource(R.drawable.project_xy2);
                    break;
            }

//            switch (dataList.get(position).getName()) {
//                case "体温℃":
//                    holder.relative.setBackgroundResource(R.drawable.project_tw);
//                    break;
//                case "酒精度mg/100ml":
//                    holder.relative.setBackgroundResource(R.drawable.project_tw);
//                    break;
//                case "开始":
//                    holder.relative.setBackgroundResource(R.drawable.project_xy2);
//                    break;
//                case "心率":
//                    holder.relative.setBackgroundResource(R.drawable.project_tw);
//                    break;
//                case "血氧%":
//                    holder.relative.setBackgroundResource(R.drawable.project_tw);
//                    break;
//            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        Log.e("click", "!");
                        listener.onItemClick(position);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    class RecyclerHolder extends RecyclerView.ViewHolder {
        TextView title, num, unit;
        RelativeLayout relative;

        private RecyclerHolder(View itemView) {
            super(itemView);
            relative = (RelativeLayout) itemView.findViewById(R.id.relative);
            title = (TextView) itemView.findViewById(R.id.title);
            num = (TextView) itemView.findViewById(R.id.num);
            unit = (TextView) itemView.findViewById(R.id.unit);
        }
    }
}
