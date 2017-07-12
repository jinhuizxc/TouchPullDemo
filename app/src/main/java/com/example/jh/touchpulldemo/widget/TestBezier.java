package com.example.jh.touchpulldemo.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by jinhui on 2017/7/12.
 * 邮箱: 1004260403@qq.com
 * 实现1-3阶贝塞尔曲线
 */

public class TestBezier extends View {

    public TestBezier(Context context) {
        super(context);
        init();
    }
    
    public TestBezier(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TestBezier(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public TestBezier(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Path mPath = new Path();
    private void init() {
        // 建议写成局部变量的方式来引用
        Paint paint = mPaint;
        // 设置抗锯齿
        paint.setAntiAlias(true);
        // 设置防抖动
        paint.setDither(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(8);

        // 一阶贝塞尔曲线————bezier
        Path path = mPath;
        path.moveTo(100, 100);
        path.lineTo(400, 400);
        // 二阶贝塞尔曲线
        path.quadTo(600, 100, 800, 400);
        // 相对的实现
//        path.rQuadTo(200, -300, 400, 0);
        path.moveTo(400, 800);
        // 三阶贝塞尔曲线
//        path.cubicTo(500, 600, 700, 1000, 800, 800);
        path.rCubicTo(100, -200, 300, 400, 400, 0);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawPath(mPath, mPaint);
        canvas.drawPoint(600, 100, mPaint);
        canvas.drawPoint(500, 600, mPaint);
        canvas.drawPoint(700, 1200, mPaint);
    }
}
