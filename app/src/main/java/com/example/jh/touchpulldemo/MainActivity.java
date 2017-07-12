package com.example.jh.touchpulldemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import com.example.jh.touchpulldemo.widget.TouchPullView;

/**
 * 但是最后有一个问题就是教程上设置圆球白色半透明，
 * 设置不成功，问题待解决。
 *
 * 但是已经完成的很好了！
 */
public class MainActivity extends AppCompatActivity {

    // y方向最多移动值
    private static final float Touch_Move_Max_Y = 600;
    // 记录按下的点
    private float mTouchMoveStartY = 0;
    private TouchPullView mTouchPullView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTouchPullView = (TouchPullView) findViewById(R.id.touchPull);

        findViewById(R.id.activity_main).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // 得到意图
                int action = event.getActionMasked();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        mTouchMoveStartY = event.getY();
                        return true;  // return true;表示已经消费啦

                    case MotionEvent.ACTION_MOVE:
                        float y = event.getY();
                        if (y >= mTouchMoveStartY) {
                            float moveSize = y - mTouchMoveStartY;
                            float progress = moveSize >= Touch_Move_Max_Y
                                    ? 1 : moveSize / Touch_Move_Max_Y;
                            mTouchPullView.setProgress(progress);
                        }
                        return true;
                    case MotionEvent.ACTION_UP:
                        mTouchPullView.release();
                        return true;
                    default:
                        break;
                }
                return false;
            }
        });
    }
}
