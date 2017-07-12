package com.example.jh.touchpulldemo.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by jinhui on 2017/7/12.
 * 邮箱: 1004260403@qq.com
 *
 * 实现贝塞尔曲线找点，并实现4阶贝塞尔曲线效果
 * 实现5-7阶贝塞尔曲线
 */

public class BezierView extends View {
    
    public BezierView(Context context) {
        super(context);
        init();
    }

    public BezierView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BezierView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public BezierView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private Path mSrcBezier = new Path();
    private Path mBezier = new Path();
    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private void init() {
        Paint paint = mPaint;
        // 设置抗锯齿
        paint.setAntiAlias(true);
        // 设置防抖动
        paint.setDither(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(8);
        // 初始化源贝塞尔线
        mSrcBezier.cubicTo(200, 700, 500, 1200, 700, 200);
        // 初始化贝塞尔曲线4阶~
        new Thread(){
            @Override
            public void run() {
                super.run();
                initBezier();
            }
        }.start();
    }

    /**
     * 初始化贝塞尔曲线
     */
    private void initBezier(){
        // (0,0)(300,300)(200,700)(500,500)(700,1200)
//        float[] xPoints = new float[]{0, 300, 200, 500, 700};
//        float[] yPoints = new float[]{0, 300, 700, 1200, 200};

        // 对比系统三阶贝塞尔曲线
//        float[] xPoints = new float[]{0, 200, 500, 700};
//        float[] yPoints = new float[]{0, 700, 1200, 200};

        // 4阶效果
//        float[] xPoints = new float[]{0, 200, 500, 700, 800};
//        float[] yPoints = new float[]{0, 700, 1200, 200, 800};
        // 5阶
//        float[] xPoints = new float[]{0, 200, 500, 700, 800, 500};
//        float[] yPoints = new float[]{0, 700, 1200, 200, 800, 1300};
        // 6阶
//        float[] xPoints = new float[]{0, 200, 500, 700, 800, 500, 600};
//        float[] yPoints = new float[]{0, 700, 1200, 200, 800, 1300, 600};
        // 7阶
//        float[] xPoints = new float[]{0, 200, 500, 700, 800, 500, 600, 200};
//        float[] yPoints = new float[]{0, 700, 1200, 200, 800, 1300, 600, 1000};
        // 8阶
        float[] xPoints = new float[]{0, 200, 500, 700, 800, 500, 600, 200, 800};
        float[] yPoints = new float[]{0, 700, 1200, 200, 800, 1300, 600, 1000, 1600};

        Path path = mBezier;
        int fps = 20000;  // 精度
        for (int i = 0; i <= fps ; i++) {
            // 进度
            float progress = i/(float)fps; //
            float x = calculateBezier(progress, xPoints);
            float y = calculateBezier(progress, yPoints);
            // 使用链接的方式,当xy变动足够小的情况下，就是平滑曲线了
            path.lineTo(x, y);
            //刷新界面
            postInvalidate();
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 计算某时刻的贝塞尔所处的值(x或y)
     * @param t 时间（0-1）
     * @param values 贝塞尔点集合
     * @return 当时t时刻的贝塞尔所处点
     */
    private float calculateBezier(float t, float... values){
        // 采用双层循环
        final int len = values.length;
        for(int i = len -1; i > 0; i--){
            for (int j = 0; j < i; j++) {
                // 计算
                values[j] = values[j] + (values[j + 1] - values[j]) * t;
            }
        }

        // 运算时结构保存在第一位，
        // 所以返回第一位
        return values[0];
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.setColor(0x40000000);
        canvas.drawPath(mSrcBezier, mPaint);

        mPaint.setColor(Color.RED);
        canvas.drawPath(mBezier, mPaint);
    }
}
