package com.example.quickexam.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.quickexam.R;
import com.example.quickexam.bean.ResultBean;

import java.util.HashMap;
import java.util.List;

public class Gridadapter extends BaseAdapter {
    private HashMap<Integer,ResultBean> lists;
    private Context context;
    private LayoutInflater inflater;

    public Gridadapter(HashMap<Integer,ResultBean> lists, Context context) {
        super();
        this.lists = lists;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return lists.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return lists.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View v, ViewGroup parent) {
        v = inflater.inflate(R.layout.grid_item, null);
        TextView text = (TextView) v.findViewById(R.id.text);
        TextView result = (TextView) v.findViewById(R.id.result);
        TextView unit = (TextView) v.findViewById(R.id.unit);
        result.setText(lists.get(position).getResult());
        switch (lists.get(position).getType()) {
            case 0://体温
                text.setText("体温");
                unit.setText("℃");
                break;
            case 1://酒精
                text.setText("酒精");
                unit.setText("%");
                break;
            case 2://ECG
                text.setText("心率");
                unit.setText("HR/min");
                break;
            case 3://SPO2
                text.setText("血氧");
                unit.setText("%");
                break;
            case 4://疲劳
                text.setText("疲劳");
                unit.setText("");
                break;
            case 5://血压
                text.setText("血压");
                unit.setText("mmhg");
                break;
            case 6://PPG
                text.setText("PPG");
                unit.setText("");
                break;
        }
        return v;
    }


}

