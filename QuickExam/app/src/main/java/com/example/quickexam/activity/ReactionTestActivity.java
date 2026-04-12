package com.example.quickexam.activity;

import android.content.Intent;
import android.content.SharedPreferences;//3.29新加
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.quickexam.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ReactionTestActivity extends AppCompatActivity {

    // ==================== UI组件 ====================
    private View lightRed, lightYellow, lightGreen;
    private Button btnRed, btnGreen, btnYellow;
    private TextView tvRoundInfo, tvResultPreview;

    // ==================== 测试配置 ====================
    private static final int TOTAL_TRIALS = 8;          // 测试总次数
    private static final long MAX_REACTION_TIME = 3000; // 最大反应时间(ms)
    private static final long PENALTY_TIME = 500;       // 错误惩罚(ms)

    // ==================== 测试数据 ====================
    private int currentTrial = 0;
    private long lightOnTime = 0L;
    private final List<Long> reactionTimes = new ArrayList<>();
    private int errorCount = 0;
    private int timeoutCount = 0;

    // ==================== 颜色定义 ====================
    private static final int COLOR_RED = Color.RED;
    private static final int COLOR_YELLOW = Color.YELLOW;
    private static final int COLOR_GREEN = Color.GREEN;

    private final int[] COLOR_ARRAY = {COLOR_RED, COLOR_YELLOW, COLOR_GREEN};

    private int currentLightColor;
    private boolean isWaitingResponse = false;

    private final Handler timeoutHandler = new Handler();
    private Runnable timeoutRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reaction_test);

        initViews();
        setupButtonListeners();
        startNewTrial();
    }

    /**
     * 初始化控件
     */
    private void initViews() {
        lightRed = findViewById(R.id.lightRed);
        lightYellow = findViewById(R.id.lightYellow);
        lightGreen = findViewById(R.id.lightGreen);

        btnRed = findViewById(R.id.btnRed);
        btnGreen = findViewById(R.id.btnGreen);
        btnYellow = findViewById(R.id.btnYellow);

        tvRoundInfo = findViewById(R.id.tvRoundInfo);
        tvResultPreview = findViewById(R.id.tvResultPreview);

        updateRoundDisplay();
    }

    /**
     * 设置按钮点击事件
     */
    private void setupButtonListeners() {
        btnRed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onColorClick(COLOR_RED);
            }
        });

        btnGreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onColorClick(COLOR_GREEN);
            }
        });

        btnYellow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onColorClick(COLOR_YELLOW);
            }
        });
    }

    /**
     * 熄灭所有灯
     */
    private void turnOffAllLights() {
        lightRed.setBackgroundTintList(
                ColorStateList.valueOf(getResources().getColor(R.color.red_off))
        );
        lightYellow.setBackgroundTintList(
                ColorStateList.valueOf(getResources().getColor(R.color.yellow_off))
        );
        lightGreen.setBackgroundTintList(
                ColorStateList.valueOf(getResources().getColor(R.color.green_off))
        );
    }

    /**
     * 点亮指定颜色的灯
     */
    private void turnOnLight(int color) {
        turnOffAllLights();
        currentLightColor = color;

        switch (color) {
            case COLOR_RED:
                lightRed.setBackgroundTintList(
                        ColorStateList.valueOf(getResources().getColor(R.color.red_on))
                );
                break;

            case COLOR_YELLOW:
                lightYellow.setBackgroundTintList(
                        ColorStateList.valueOf(getResources().getColor(R.color.yellow_on))
                );
                break;

            case COLOR_GREEN:
                lightGreen.setBackgroundTintList(
                        ColorStateList.valueOf(getResources().getColor(R.color.green_on))
                );
                break;
        }
    }

    /**
     * 开始新一轮测试
     */
    private void startNewTrial() {
        // 所有测试结束
        if (currentTrial >= TOTAL_TRIALS) {
            showResults();
            return;
        }

        isWaitingResponse = true;

        // 随机颜色，避免连续两次相同
        int newColorIndex;
        do {
            newColorIndex = new Random().nextInt(COLOR_ARRAY.length);
        } while (currentTrial > 0 && COLOR_ARRAY[newColorIndex] == currentLightColor);

        int colorToShow = COLOR_ARRAY[newColorIndex];
        turnOnLight(colorToShow);
        lightOnTime = System.currentTimeMillis();

        updateRoundDisplay();

        // 设置超时任务
        timeoutRunnable = new Runnable() {
            @Override
            public void run() {
                if (isWaitingResponse) {
                    timeoutCount++;
                    isWaitingResponse = false;

                    Toast.makeText(ReactionTestActivity.this,
                            "超时！请集中注意力",
                            Toast.LENGTH_SHORT).show();

                    // 用 -1 表示超时
                    reactionTimes.add(-1L);

                    currentTrial++;
                    turnOffAllLights();

                    // 稍微停顿一下再进入下一轮，避免太突兀
                    timeoutHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            startNewTrial();
                        }
                    }, 500);
                }
            }
        };

        timeoutHandler.postDelayed(timeoutRunnable, MAX_REACTION_TIME);
    }

    /**
     * 用户点击颜色按钮
     */
    private void onColorClick(int clickedColor) {
        if (!isWaitingResponse) {
            Toast.makeText(this, "请等待灯亮起", Toast.LENGTH_SHORT).show();
            return;
        }

        // 取消超时任务
        if (timeoutRunnable != null) {
            timeoutHandler.removeCallbacks(timeoutRunnable);
        }

        isWaitingResponse = false;

        long rawReactionTime = System.currentTimeMillis() - lightOnTime;

        if (clickedColor == currentLightColor) {
            // 正确
            long finalReactionTime = rawReactionTime;
            reactionTimes.add(finalReactionTime);

            Toast.makeText(this,
                    "正确！反应时间: " + finalReactionTime + " ms",
                    Toast.LENGTH_SHORT).show();

            if (tvResultPreview != null) {
                tvResultPreview.setText("第" + (currentTrial + 1) + "次: " + finalReactionTime + " ms ✓");
            }

        } else {
            // 错误
            errorCount++;

            long penalizedTime = rawReactionTime + PENALTY_TIME;
            reactionTimes.add(penalizedTime);

            Toast.makeText(this,
                    "错误！正确颜色是 " + getColorName(currentLightColor) + "，本次加罚 " + PENALTY_TIME + " ms",
                    Toast.LENGTH_LONG).show();

            if (tvResultPreview != null) {
                tvResultPreview.setText("第" + (currentTrial + 1) + "次: 错误 ✗ (+" + PENALTY_TIME + "ms)");
            }
        }

        turnOffAllLights();
        currentTrial++;

        timeoutHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startNewTrial();
            }
        }, 500);
    }

    /**
     * 获取颜色名称
     */
    private String getColorName(int color) {
        if (color == COLOR_RED) {
            return "红";
        } else if (color == COLOR_YELLOW) {
            return "黄";
        } else if (color == COLOR_GREEN) {
            return "绿";
        }
        return "";
    }

    /**
     * 更新轮次显示
     */
    private void updateRoundDisplay() {
        if (tvRoundInfo != null) {
            tvRoundInfo.setText("第 " + (currentTrial + 1) + " / " + TOTAL_TRIALS + " 次");
        }
    }

    /**
     * 计算平均反应时间（排除超时）
     */
    private long calculateAverageReactionTime() {
        long sum = 0;
        int validCount = 0;

        for (long time : reactionTimes) {
            if (time > 0) {
                sum += time;
                validCount++;
            }
        }

        if (validCount == 0) {
            return 0;
        }

        return sum / validCount;
    }

    /**
     * 计算有效次数（排除超时）
     */
    private int calculateValidCount() {
        int validCount = 0;
        for (long time : reactionTimes) {
            if (time > 0) {
                validCount++;
            }
        }
        return validCount;
    }

    /**
     * 疲劳评估
     */
    private String evaluateFatigueLevel(long avgTime, int errorRatePercent) {
        if (avgTime <= 350 && errorRatePercent <= 10) {
            return "状态良好，反应敏捷";
        } else if (avgTime <= 500 && errorRatePercent <= 20) {
            return "轻微疲劳，建议休息";
        } else if (avgTime <= 700 || errorRatePercent <= 35) {
            return "中度疲劳，不宜执行关键任务";
        } else {
            return "严重疲劳，必须休息！";
        }
    }

    /**
     * 显示测试结果
     */
    private void showResults() {
        long avgTime = calculateAverageReactionTime();
        int validCount = calculateValidCount();
        int errorRatePercent = (errorCount * 100) / TOTAL_TRIALS;

        StringBuilder sb = new StringBuilder();
        sb.append("========== 反应测试报告 ==========\n\n");
        sb.append("测试次数: ").append(TOTAL_TRIALS).append(" 次\n");
        sb.append("有效次数: ").append(validCount).append(" 次\n");
        sb.append("错误次数: ").append(errorCount).append(" 次\n");
        sb.append("超时次数: ").append(timeoutCount).append(" 次\n");
        sb.append("错误率: ").append(errorRatePercent).append("%\n\n");

        sb.append("详细记录 (ms):\n");
        for (int i = 0; i < reactionTimes.size(); i++) {
            long time = reactionTimes.get(i);
            if (time == -1) {
                sb.append(" 第").append(i + 1).append("次: 超时\n");
            } else {
                sb.append(" 第").append(i + 1).append("次: ").append(time).append(" ms\n");
            }
        }

        sb.append("\n平均反应时间: ").append(avgTime).append(" ms\n");
        sb.append("疲劳评估: ").append(evaluateFatigueLevel(avgTime, errorRatePercent)).append("\n");
        // ========== 新增：标记反应测试已完成 ==========
        SharedPreferences prefs = getSharedPreferences("app_data", MODE_PRIVATE);//3.29新加
        prefs.edit().putBoolean("reaction_test_done", true).apply();

        // 弹出结果对话框
        new AlertDialog.Builder(this)
                .setTitle("测试完成")
                .setMessage(sb.toString())
                .setPositiveButton("完成", (dialog, which) -> finish())
                .show();

        // 标记反应测试已完成
        SharedPreferences prefs = getSharedPreferences("app_data", MODE_PRIVATE);
        prefs.edit().putBoolean("reaction_test_done", true).apply();

        new AlertDialog.Builder(this)
                .setTitle("测试完成")
                .setMessage(sb.toString())
                .setPositiveButton("重新测试", (dialog, which) -> restartTest())
                .setNegativeButton("完成", (dialog, which) -> finish())
                .setNeutralButton("分享结果", (dialog, which) -> shareResults(sb.toString()))
                .show();
    }

    /**
     * 分享测试结果
     */
    private void shareResults(String result) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, result);
        startActivity(Intent.createChooser(shareIntent, "分享测试结果"));
    }

    /**
     * 重新开始测试
     */
    private void restartTest() {
        currentTrial = 0;
        errorCount = 0;
        timeoutCount = 0;
        reactionTimes.clear();
        isWaitingResponse = false;

        if (timeoutRunnable != null) {
            timeoutHandler.removeCallbacks(timeoutRunnable);
        }

        turnOffAllLights();
        updateRoundDisplay();

        if (tvResultPreview != null) {
            tvResultPreview.setText("");
        }

        startNewTrial();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timeoutHandler.removeCallbacksAndMessages(null);
    }
}
