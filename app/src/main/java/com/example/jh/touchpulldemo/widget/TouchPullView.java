package com.example.jh.touchpulldemo.widget;


import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.view.animation.PathInterpolatorCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

import com.example.jh.touchpulldemo.R;

/**
 * Created by jinhui on 2017/7/12.
 * 邮箱: 1004260403@qq.com
 * <p>
 * 涉及知识点
 * 在自定义控件中绘制
 * 自定义控件拉动响应，控件高度拉动变化
 * 利用贝塞尔曲线实现粘性效果
 * <p>
 * 1、构建可拉动View
 * 添加自定义控件并绘制圆
 * 新建一个自定义控件
 * 新建基本初始化方法
 * 在自定义控件中绘制圆；
 * 2、为自定义view添加拉动响应
 * 了解基本事件传递
 * 获取拉动手势，并计算进度
 * view响应拉动并更改高度
 * 3、贝塞尔曲线
 * 又称为贝兹曲线或贝济埃曲线，是应用于二维图形应用程序的数学曲线
 * 用处：平滑，拟合圆，制造，绘图
 */

public class TouchPullView extends View {

    // 圆的画笔
    private Paint mCirclePaint;
    // 圆的半径
    private float mCircleRadius = 50;
    private float mCirclePointX, mCirclePointY;
    // 进度值
    private float mProgress;
    // 可拖动的高度
    private int mDragHeight = 300;
    // 目标宽度
    private int mTargetWidth = 400;
    // 贝塞尔曲线的路径以及画笔
    private Path mPath = new Path();
    private Paint mPathPaint;
    // 重心点最终高度，决定控制点的Y坐标
    private int mTargetGravityHeight = 10;
    // 角度变换 0-135度
    private int mTargentAngle = 105;
    // 增加速度
    private Interpolator mProgressInterpolator = new DecelerateInterpolator();
    private Interpolator mTanentAngleInterpolator;

    private Drawable mContent = null;
    private int mContentMargin = 0;

    public TouchPullView(Context context) {
        super(context);
        init(null);
    }

    public TouchPullView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(null);
    }

    public TouchPullView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(null);
    }

    public TouchPullView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(null);
    }

    /**
     * 初始化
     */
    private void init(AttributeSet attrs) {
        // 得到用户设置的参数
        final Context context = getContext();
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.TouchPullView, 0, 0);
        int color = array.getColor(R.styleable.TouchPullView_pColor, 0x20000000);
        mCircleRadius = array.getDimension(R.styleable.TouchPullView_pRadius, mCircleRadius);
        mDragHeight = array.getDimensionPixelOffset(R.styleable.TouchPullView_pDragHeight, mDragHeight);
        mTargentAngle = array.getInteger(R.styleable.TouchPullView_pTangentAngle, mTargentAngle);
        mTargetWidth = array.getDimensionPixelOffset(R.styleable.TouchPullView_pTargetWidth, mTargetWidth);
        mTargetGravityHeight = array.getDimensionPixelOffset(R.styleable.TouchPullView_pTargetGravityHeight, mTargetGravityHeight);
        mContent = array.getDrawable(R.styleable.TouchPullView_pContentDrawable);
        mContentMargin = array.getDimensionPixelOffset(
                R.styleable.TouchPullView_pContentDrawableMargin, 0);

        // 销毁
        array.recycle();

        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        // 设置抗锯齿
        p.setAntiAlias(true);
        // 设置防抖动
        p.setDither(true);
        // 设置为填充方式
        p.setStyle(Paint.Style.FILL);
        p.setColor(0xFFFF4081); // FF4081
        mCirclePaint = p;
        // 初始化路径部分
        p = new Paint(Paint.ANTI_ALIAS_FLAG);
        // 设置抗锯齿
        p.setAntiAlias(true);
        // 设置防抖动
        p.setDither(true);
        // 设置为填充方式
        p.setStyle(Paint.Style.FILL);
        p.setColor(0xFFFF4081);
        mPathPaint = p;
        // 切角路径插值器
        mTanentAngleInterpolator = PathInterpolatorCompat.create(
                (mCircleRadius * 2.0f) / mDragHeight,
                90.0f / mTargentAngle
        );
    }

    // 进行计算的时候可以放在onSizeChanged减少cpu的消耗
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 进行基础坐标参数系改变————————————画布转换,黑色球不会迁移左边
        int count = canvas.save();
        float tranx = (getWidth() -
                getValueByLine(getWidth(), mTargetWidth, mProgress)) / 2;
        canvas.translate(tranx, 0);
        // 画bezier曲线
        canvas.drawPath(mPath, mPathPaint);
        // 画圆
        canvas.drawCircle(mCirclePointX,
                mCirclePointY,
                mCircleRadius,
                mCirclePaint);

        Drawable drawable = mContent;
        if (drawable != null) {
            canvas.save();
            // 剪切矩形区域
            canvas.clipRect(drawable.getBounds());
            // 绘制Drawable
            drawable.draw(canvas);
            canvas.restore();
        }
        canvas.restoreToCount(count);
    }

    /**
     * 当进行测量时触发
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // 宽度的意图，宽度的类型
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        // 得到最小的拉动的高度
        int iHeight = (int) ((mDragHeight * mProgress + 0.5f)
                + getPaddingTop() + getPaddingBottom());
        int iWidth = (int) (2 * mCircleRadius + getPaddingLeft() + getPaddingRight());
        int measureWidth, measureHeight;

        if (widthMode == MeasureSpec.EXACTLY) {
            // 确切的
            measureWidth = width;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            // 最多
            measureWidth = Math.min(iWidth, width);
        } else {
            measureWidth = iWidth;
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            // 确切的
            measureHeight = height;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            // 最多
            measureHeight = Math.min(iHeight, height);
        } else {
            measureHeight = iHeight;
        }
        // 设置测量的高度宽度
        setMeasuredDimension(measureWidth, measureHeight);
    }

    /**
     * 当大小改变时触发
     *
     * @param w
     * @param h
     * @param oldw
     * @param oldh
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
//        mCirclePointX = getWidth() >> 1;
//        mCirclePointY = getHeight() >> 1;
        // 当高度变化时进行路径的更新
        updataPathLayout();
    }

    /**
     * 设置进度
     *
     * @param progress 进度
     */

    public void setProgress(float progress) {
        Log.e("TAG", "p:" + progress);
        mProgress = progress;
        // 刷新界面,请求重新布局测量
        requestLayout();
    }

    /**
     * 更新路径的相关操作
     */
    private void updataPathLayout() {
        // 获取进度
        final float progress = mProgressInterpolator.getInterpolation(mProgress);

        // 获取可绘制区域高度宽度
        final float w = getValueByLine(getWidth(), mTargetWidth, mProgress);
        final float h = getValueByLine(0, mDragHeight, mProgress);
        // x对称轴的参数, 圆的圆心X
        final float cPointx = w / 2;
        // 圆的半径
        final float cRadius = mCircleRadius;
        // 圆的圆心Y坐标
        final float cPointy = h - cRadius;
        // 控制点结束Y的值
        final float endControlY = mTargetGravityHeight;
        // 更新圆的坐标
        mCirclePointX = cPointx;
        mCirclePointY = cPointy;
        // 路径
        final Path path = mPath;
        path.reset();
        path.moveTo(0, 0);
        // 开始控制点的逻辑部分
        // 左边部分的结束点和控制点
        float lEndPointX, lEndPointY;
        float lControlPointX, lControlPointY;
        // 获得当前切线的弧度
        float angle = mTargentAngle * mTanentAngleInterpolator.getInterpolation(progress);
        double radian = Math.toRadians(angle);
        float x = (float) (Math.sin(radian) * cRadius);
        float y = (float) (Math.cos(radian) * cRadius);
        lEndPointX = cPointx - x;
        lEndPointY = cPointy + y;
        // 控制点的Y变化
        lControlPointY = getValueByLine(0, endControlY, progress);
        // 控制点与结束点之间的高度
        float tHeight = lEndPointY - lControlPointY;
        // 控制点与X的坐标距离
        float tWidth = (float) (tHeight / Math.tan(radian));
        lControlPointX = lEndPointX - tWidth;
        // 贝塞尔曲线
        path.quadTo(lControlPointX, lControlPointY, lEndPointX, lEndPointY);
        // 链接到右边
        path.lineTo(cPointx + (cPointx - lEndPointX), lEndPointY);
        // 画右边的贝塞尔曲线
        path.quadTo(cPointx + cPointx - lControlPointX, lControlPointY, w, 0);

        // 更新内容部分Drawable
        updateContentLayout(cPointx, cPointy, cRadius);

    }

    /**
     * 对内容部分进行测量并设置
     * @param cx 圆心x
     * @param cy 圆心y
     * @param radius 半径
     */
    private void updateContentLayout(float cx, float cy, float radius){
        Drawable drawable = mContent;
        if(drawable != null){
            int margin = mContentMargin;
            int l = (int) (cx - radius + margin);
            int r = (int) (cx + radius - margin);
            int t = (int) (cy - radius + margin);
            int b = (int) (cy + radius - margin);
            drawable.setBounds(l, t, r, b);
        }
    }
    /**
     * 获取当前值
     *
     * @param start    起始值
     * @param end      结束值
     * @param progress 进度
     * @return
     */
    private float getValueByLine(float start, float end, float progress) {
        return start + (end - start) * progress;
    }

    // 释放动画
    private ValueAnimator valueAnimator;

    public void release() {
        if (valueAnimator == null) {
            ValueAnimator animator = ValueAnimator.ofFloat(mProgress, 0f);
            animator.setInterpolator(new DecelerateInterpolator());
            animator.setDuration(400);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    Object val = animation.getAnimatedValue();
                    if (val instanceof Float) {
                        setProgress((Float) val);
                    }
                }
            });
            valueAnimator = animator;
        } else {
            valueAnimator.cancel();
            valueAnimator.setFloatValues(mProgress, 0f);
        }
        valueAnimator.start();
    }
}
