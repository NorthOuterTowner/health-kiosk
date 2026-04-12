package com.example.quickexam.activity; // 请替换为你的实际包名

import android.content.DialogInterface;
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
import com.example.quickexam.R; // 确保导入正确的R
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ReactionTestActivity extends AppCompatActivity {

    // ==================== UI组件 ====================
    private View lightRed, lightYellow, lightGreen;
    private Button btnRed, btnGreen, btnYellow;
    private TextView tvRoundInfo, tvResultPreview;  // 用于显示轮次和实时结果

    // ==================== 测试配置 ====================
    private static final int TOTAL_TRIALS = 8;      // 测试总次数（可改为5-10）
    private static final long MAX_REACTION_TIME = 3000; // 最大反应时间(ms)，超过视为无效/超时
    private static final long PENALTY_TIME = 500;   // 错误回答的惩罚时间(ms)，影响反应速度

    // ==================== 测试数据 ====================
    private int currentTrial = 0;                   // 当前进行到第几次
    private long lightOnTime = 0L;                  // 当前灯亮起的时刻
    private final List<Long> reactionTimes = new ArrayList<>(); // 存储每次正确反应时间
    private int errorCount = 0;                     // 错误次数统计
    private int timeoutCount = 0;                   // 超时次数统计

    // ==================== 颜色定义 ====================
    private static final int COLOR_RED = Color.RED;
    private static final int COLOR_YELLOW = Color.YELLOW;
    private static final int COLOR_GREEN = Color.GREEN;
    private final int[] COLOR_ARRAY = {COLOR_RED, COLOR_YELLOW, COLOR_GREEN};
    private final String[] COLOR_NAMES = {"红", "黄", "绿"};

    private int currentLightColor;                  // 当前亮起的颜色
    private boolean isWaitingResponse = false;      // 是否在等待用户响应
    private Handler timeoutHandler = new Handler(); // 用于超时处理
    private Runnable timeoutRunnable;               // 超时任务

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reaction_test);

        // 初始化UI组件
        initViews();

        // 设置按钮点击监听
        setupButtonListeners();

        // 开始第一次测试
        startNewTrial();
    }

    private void initViews() {
        // 三个灯
        lightRed = findViewById(R.id.lightRed);
        lightYellow = findViewById(R.id.lightYellow);
        lightGreen = findViewById(R.id.lightGreen);

        // 三个按钮
        btnRed = findViewById(R.id.btnRed);
        btnGreen = findViewById(R.id.btnGreen);
        btnYellow = findViewById(R.id.btnYellow);

        // 可选：添加显示轮次和实时结果的TextView（需要在布局中添加，见下方说明）
        tvRoundInfo = findViewById(R.id.tvRoundInfo);
        tvResultPreview = findViewById(R.id.tvResultPreview);

        // 如果布局中没有这些TextView，可以注释掉上面两行，或临时创建Toast提示
        if (tvRoundInfo == null) {
            // 如果没有添加TextView，可以通过Toast显示轮次
            // 建议在布局中添加，见文章末尾的补充说明
        }
        updateRoundDisplay();
    }

    private void setupButtonListeners() {
        btnRed.setOnClickListener(v -> onColorClick(COLOR_RED));
        btnGreen.setOnClickListener(v -> onColorClick(COLOR_GREEN));
        btnYellow.setOnClickListener(v -> onColorClick(COLOR_YELLOW));
    }

    /**
     * 关闭所有灯（熄灭状态）
     */
    private void turnOffAllLights() {
        lightRed.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.red_off)));
        lightYellow.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.yellow_off)));
        lightGreen.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.green_off)));
    }

    /**
     * 点亮指定颜色的灯
     */
    private void turnOnLight(int color) {
        turnOffAllLights();
        currentLightColor = color;

        switch (color) {
            case COLOR_RED:
                lightRed.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.red_on)));
                break;
            case COLOR_YELLOW:
                lightYellow.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.yellow_on)));
                break;
            case COLOR_GREEN:
                lightGreen.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.green_on)));
                break;
        }
    }

    /**
     * 开始新一轮测试
     */
    private void startNewTrial() {
        // 检查是否完成所有测试
        if (currentTrial >= TOTAL_TRIALS) {
            showResults();
            return;
        }

        // 重置等待状态
        isWaitingResponse = true;

        // 随机亮灯（排除连续相同颜色，增加测试有效性）
        int newColorIndex;
        do {
            newColorIndex = new Random().nextInt(COLOR_ARRAY.length);
        } while (currentTrial > 0 && COLOR_ARRAY[newColorIndex] == currentLightColor);

        int colorToShow = COLOR_ARRAY[newColorIndex];
        turnOnLight(colorToShow);
        lightOnTime = System.currentTimeMillis();

        // 更新轮次显示
        updateRoundDisplay();

        // 设置超时处理（超过MAX_REACTION_TIME未响应，视为超时）
        timeoutRunnable = () -> {
            if (isWaitingResponse) {
                // 超时未响应
                timeoutCount++;
                isWaitingResponse = false;

                // 显示超时提示
                Toast.makeText(this, "超时！请集中注意力", Toast.LENGTH_SHORT).show();

                // 记录无效反应（用-1标记）
                reactionTimes.add(-1L);

                // 自动进入下一轮
                currentTrial++;
                turnOffAllLights();
                startNewTrial();
            }
        };
        timeoutHandler.postDelayed(timeoutRunnable, MAX_REACTION_TIME);
    }

    /**
     * 处理用户点击颜色按钮
     */
    private void onColorClick(int clickedColor) {
        // 如果不在等待响应状态，忽略点击
        if (!isWaitingResponse) {
            Toast.makeText(this, "请等待灯亮起", Toast.LENGTH_SHORT).show();
            return;
        }

        // 取消超时任务
        timeoutHandler.removeCallbacks(timeoutRunnable);
        isWaitingResponse = false;

        // 计算原始反应时间
        long rawReactionTime = System.currentTimeMillis() - lightOnTime;

        // 判断是否正确
        if (clickedColor == currentLightColor) {
            // ========== 正确反应 ==========
            long finalReactionTime = rawReactionTime;

            // 显示本次反应时间
            Toast.makeText(this, "正确！反应时间: " + finalReactionTime + " ms", Toast.LENGTH_SHORT).show();
            reactionTimes.add(finalReactionTime);

            // 可选：更新预览显示（如果有TextView）
            if (tvResultPreview != null) {
                tvResultPreview.setText("第" + (currentTrial + 1) + "次: " + finalReactionTime + " ms ✓");
            }

        } else {
            // ========== 错误反应 ==========
            errorCount++;

            // 应用惩罚：将原始时间加上惩罚时间作为本次“有效反应时间”
            // 这样错误会显著降低平均反应速度，模拟疲劳状态下的判断失误影响
            long penalizedTime = rawReactionTime + PENALTY_TIME;
            reactionTimes.add(penalizedTime);

            Toast.makeText(this, "错误！正确颜色是 " + getColorName(currentLightColor) +
                    "，本次加罚 " + PENALTY_TIME + " ms", Toast.LENGTH_LONG).show();

            if (tvResultPreview != null) {
                tvResultPreview.setText("第" + (currentTrial + 1) + "次: 错误 ✗ (+" + PENALTY_TIME + "ms)");
            }
        }

        // 关闭所有灯
        turnOffAllLights();

        // 进入下一轮
        currentTrial++;
        startNewTrial();
    }

    /**
     * 获取颜色名称
     */
    private String getColorName(int color) {
        if (color == COLOR_RED) return "红";
        if (color == COLOR_YELLOW) return "黄";
        if (color == COLOR_GREEN) return "绿";
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
     * 计算平均反应时间（排除无效/超时数据）
     */
    private long calculateAverageReactionTime() {
        long sum = 0;
        int validCount = 0;
        for (long time : reactionTimes) {
            if (time > 0) {  // 排除超时标记的-1
                sum += time;
                validCount++;
            }
        }
        if (validCount == 0) return 0;
        return sum / validCount;
    }

    /**
     * 根据平均反应时间和错误率评估疲劳程度
     */
    private String evaluateFatigueLevel(long avgTime, int errorRatePercent) {
        // 参考值：正常反应时间200-400ms，铁路司机标准更严格
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
     * 显示最终测试结果
     */
    private void showResults() {
        // 计算统计数据
        long avgTime = calculateAverageReactionTime();
        int validCount = (int) reactionTimes.stream().filter(t -> t > 0).count();
        int errorRatePercent = (errorCount * 100) / TOTAL_TRIALS;

        // 构建结果字符串
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
                sb.append("  第").append(i + 1).append("次: 超时\n");
            } else {
                sb.append("  第").append(i + 1).append("次: ").append(time).append(" ms\n");
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

        // 弹出结果对话框
        new AlertDialog.Builder(this)
                .setTitle("测试完成")
                .setMessage(sb.toString())
                .setPositiveButton("重新测试", (dialog, which) -> restartTest())
                .setNegativeButton("返回", (dialog, which) -> finish())
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
        // 重置所有数据
        currentTrial = 0;
        errorCount = 0;
        timeoutCount = 0;
        reactionTimes.clear();
        isWaitingResponse = false;

        // 取消任何正在进行的超时任务
        if (timeoutRunnable != null) {
            timeoutHandler.removeCallbacks(timeoutRunnable);
        }

        // 关闭所有灯
        turnOffAllLights();

        // 开始新一轮测试
        startNewTrial();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 清理Handler，防止内存泄漏
        if (timeoutHandler != null) {
            timeoutHandler.removeCallbacksAndMessages(null);
        }
    }
}