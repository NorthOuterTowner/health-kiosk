package com.example.quickexam.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.quickexam.MainApplication;
import com.example.quickexam.R;
import com.example.quickexam.activity.FragmentActivity;
import com.example.quickexam.view.PColumn;
import com.seeta.facedb.faceinfo;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import android_serialport_api.SerialPortUtil;

import static com.example.quickexam.MainApplication.instance;
import static com.example.quickexam.MainApplication.m_iMenuTask;
import static com.example.quickexam.MainApplication.m_serialportutil;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BloodFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BloodFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private PColumn height;
    private PColumn low;
    private final int changeVIEW = 1111;
    private final int commitVIEW = 1112;
    private int back = -1;
    private String upStr = "";
    private String sys = "";
    private String dia = "";
    private String hr = "";
    private TextView hp;
    private TextView lp;
    private TextView pulse;
    private FragmentActivity activity;

    public BloodFragment() {
        // Required empty public constructor
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void handleMessage(Message msg) {    
            super.handleMessage(msg);
            switch (msg.what) {
                case changeVIEW:
                    height.setData(Integer.parseInt(upStr), 200);
                    low.setData(Integer.parseInt(upStr), 200);
                    break;
                case commitVIEW:
                    height.setData(Integer.parseInt(sys), 200);
                    low.setData(Integer.parseInt(dia), 200);
                    hp.setText(sys);
                    lp.setText(dia);
                    pulse.setText(hr);
                    closeActivity();
                    break;
            }
        }
    };

    private void closeActivity() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                back = 5;
                while (back >= 0) {
                    try {
                        Thread.sleep(1000);
                        back--;
                        if (back == 0) {
                            m_iMenuTask = 1;
                            activity = (FragmentActivity) getActivity();
                            Intent intent = new Intent();
                            if (!activity.recognizedName.isEmpty())
                                intent.putExtra("name", activity.recognizedName);
                            activity.setResult(2000, intent);
                            activity.finish();
                            if (instance != null) {
                                if (activity.changeBP) {
                                    instance.updateLamp(new faceinfo(activity.id, activity.recognizedName, activity.sex, activity.age,
                                            activity.height, activity.weight, 0, activity.feats, sys, dia, hr,
                                            String.valueOf(System.currentTimeMillis())), activity._id);
                                } else {
                                    instance.saveLamp(
                                            new faceinfo(activity.id, activity.recognizedName, activity.sex, activity.age,
                                                    activity.height, activity.weight, 0, activity.feats, sys, dia, hr,
                                                    String.valueOf(System.currentTimeMillis())));
                                }
                                activity.changeBP = false;
                            }
                            //Application app = (Application)activity.getApplication();
                        }

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BloodFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BloodFragment newInstance(String param1, String param2) {
        BloodFragment fragment = new BloodFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            //Fragment显示时调用串口上传数据
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View inflate = inflater.inflate(R.layout.fargment_blood_p, container, false);
        initView(inflate);
        initData();
        return inflate;
    }

    private void initData() {
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
        m_serialportutil.sendSPStr("BldPr");
    }

    private void initView(View inflate) {
        //高压
        height = inflate.findViewById(R.id.column_height);
        //height.setData(data,MAX);//data 串口上传数值   MAX 最大数值
        //低压
        low = inflate.findViewById(R.id.column_low);
        //low.setData(data,MAX);//data 串口上传数值   MAX 最大数值
        //收缩压
        hp = inflate.findViewById(R.id.hp);
        //舒张压
        lp = inflate.findViewById(R.id.lp);
        //脉搏
        pulse = inflate.findViewById(R.id.pulse);
    }

    private void change() {
        activity = (FragmentActivity) getActivity();
        activity.changeMain();
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(String string) {
        if (string != null) {
            String[] split = string.split(",");
            if (split[0].equals("mmHg")) {
                if (split.length > 2)
                    if (!upStr.equals(split[1])) {
                        upStr = split[1];
                        mHandler.sendEmptyMessage(changeVIEW);
                    }
            } else if (split[0].equals("BlR")) {
                if (split.length >= 4) {
                    sys = split[1];
                    dia = split[2];
                    hr = split[3];
                    mHandler.sendEmptyMessage(commitVIEW);
                }
            }
        }
    }

    private class MessageEvent {
        private String str;

        public MessageEvent(String s) {
            str = s;
        }
    }

    @Override
    public void onDestroy() {
        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}