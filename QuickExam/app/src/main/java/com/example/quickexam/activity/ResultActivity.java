package com.example.quickexam.activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;

import com.example.quickexam.R;
import com.example.quickexam.adapter.Gridadapter;
import com.example.quickexam.bean.ResultBean;
import com.example.quickexam.databinding.ActivityMainBinding;
import com.example.quickexam.databinding.ActivityResultBinding;
import com.example.quickexam.utils.NavigationBarUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.example.quickexam.MainApplication.m_iMenuTask;
import static com.example.quickexam.MainApplication.miflytts;

public class ResultActivity extends AppCompatActivity {

    private ActivityResultBinding binding;
    private int back = 10;
    private int RESULT_BACK = 3000;
    private final int TIME_CODE = 1;
    private HashMap<Integer, ResultBean> lists;
    private final Handler jumpHandler = new Handler();
    //在主线程里面处理消息并更新UI界面
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            bundle = msg.getData();
            String data;
            switch (msg.what) {
                case TIME_CODE:
                    binding.time.setText(back + "");
                    break;
            }
        }
    };
    private Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NavigationBarUtil.focusNotAle(getWindow());
        binding = ActivityResultBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        NavigationBarUtil.hideNavigationBar(getWindow());
        NavigationBarUtil.clearFocusNotAle(getWindow());
        initIntent();
        initThread();
        initView();
        initData();
    }

    private void initIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            lists = (HashMap<Integer, ResultBean>) intent.getSerializableExtra("result");
        }
    }

    private void initThread() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (back >= 0) {
                    try {
                        Thread.sleep(1000);
                        if (back < 0) {
                            break;
                        }
                        back--;
                        if (back == 0) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    setResult(RESULT_BACK);
                                    finish();
                                }
                            });
                            break;
                        }
                        mHandler.sendEmptyMessage(TIME_CODE);
                    }  catch (InterruptedException e) {
                        e.printStackTrace();
                        break;
                    }
                }
            }
        }).start();
    }

    private void initView() {
        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back = -1;
                setResult(RESULT_BACK);
                finish();
            }
        });
    }

    private void initData() {
        if (lists != null) {
            binding.grid.setAdapter(new Gridadapter(lists, this));
        }

        // 保存体检完成状态
        final SharedPreferences prefs = getSharedPreferences("app_data", MODE_PRIVATE);
        prefs.edit().putBoolean("exam_completed", true).apply();

        // 延迟2秒后跳转到反应测试（让用户先看到体检结果）
        jumpHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                boolean reactionTestDone = prefs.getBoolean("reaction_test_done", false);
                if (!reactionTestDone && !isFinishing()) {
                    // 停止结果页倒计时，避免后台自动finish
                    back = -1;

                    Intent intent = new Intent(ResultActivity.this, ReactionTestActivity.class);
                    startActivity(intent);
                }
            }
        }, 2000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        jumpHandler.removeCallbacksAndMessages(null);
        mHandler.removeCallbacksAndMessages(null);
    }
}
