package com.example.quickexam.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class MyData extends View {

    private List<Float> datas = new ArrayList<>();
    private Paint mPaint;
    private Path mPath;
    // 心电图曲线的颜色
    private int color = Color.parseColor("#4BFFD5");
    // 心电图的宽度
    private float line_width = 3f; // 稍微加粗，大屏展示更清晰
    private int view_width;
    private int view_height;
    private int baseLine; // 屏幕基准线
    private int smallGridWith = 10;
    private int bigGridWidth = smallGridWith * 5;
    private int bigGridNum;
    private int dataNumber = 5;

    private float maxSize = 0;

    // 【性能优化器】：防止每秒上百次刷新卡死安卓主线程
    private int drawCount = 0;
    private boolean isDrawFinish = false;

    // 【波形放大器】：控制波峰的高低。如果嫌矮就改大（如 0.8f），嫌高就改小（如 0.3f）
    private float yScale = 0.5f;

    public MyData(Context context) {
        super(context);
        init();
        setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent));
    }

    public MyData(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
        setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent));
    }

    public void setDataNumber(int dataNumber) {
        this.dataNumber = dataNumber;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        view_width = w;
        view_height = h;
        bigGridNum = view_height / bigGridWidth;
        baseLine = bigGridNum / 2 * bigGridWidth;
        maxSize = view_width * (1.0f) / (smallGridWith / (dataNumber * 1.0f));
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mPath.reset();
        float y;
        if (datas != null && datas.size() > 0) {
            mPath.moveTo(0, change(datas.get(0)));
            for (int i = 0; i < datas.size(); i++) {
                float calculatedY = change(datas.get(i));
                // 上下限幅，防止波峰太大飞出屏幕
                if (calculatedY > view_height) y = view_height - 5;
                else if (calculatedY < 0) y = 5;
                else y = calculatedY;

                mPath.lineTo(i * smallGridWith / dataNumber, y);
            }
            canvas.drawPath(mPath, mPaint);
        }
        isDrawFinish = true;
    }

    public void addData(Float data) {
        if (datas.size() > maxSize) {
            datas.remove(0);
        }
        datas.add(data);
        if (!isDrawFinish) return;

        drawCount++;
        // 【防卡死】：攒够 3 个点才强制刷新一次屏幕
        if (drawCount % 3 == 0) {
            invalidate();
        }
    }

    public void removeData(){
        datas.clear();
        mPath.reset();
        invalidate();
    }

    public void addData(Float data1, Float data2) {
        if (datas.size() > maxSize) {
            datas.remove(0);
            datas.remove(0); // 【修复】：连删两次头部，解决漏点 Bug
        }
        datas.add(data1);
        datas.add(data2);
        if (!isDrawFinish) return;

        drawCount++;
        if (drawCount % 3 == 0) invalidate();
    }

    /**
     * 【核心数学转换】：适配安卓倒置坐标系
     * 传入的 data 是去掉基线后的值（如波峰是 +381）。
     * baseLine - (381 * 0.5) 代表在屏幕基准线上方画点，完美呈现正向波峰！
     */
    private float change(Float data) {
        return baseLine - (data * yScale);
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setAntiAlias(true);
        mPaint.setColor(color);
        mPaint.setStrokeWidth(line_width);
        mPath = new Path();
    }
}