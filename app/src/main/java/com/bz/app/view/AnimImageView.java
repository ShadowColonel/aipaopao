package com.bz.app.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;

/**
 * Created by ThinkPad User on 2016/11/25.
 */

public class AnimImageView extends ImageView {
    private final  static String LOG_TAG = "AnimImageView";
    private int mLargeCircleRadius;
    private int mSmallCircleRadius;
    private int mMaxRadius;
    private ValueAnimator mAnimator;

    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public AnimImageView(Context context) {
        super(context);
    }

    public AnimImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AnimImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width = 0, height = 0;
        if (widthMode == MeasureSpec.AT_MOST && heightMode == MeasureSpec.AT_MOST) {
            width = 96;
            height = 96;
        } else if (widthMeasureSpec == MeasureSpec.AT_MOST) {
            width = 96;
            height = heightSize;
        } else if (heightMeasureSpec == MeasureSpec.AT_MOST) {
            width = widthSize;
            height = 96;
        } else {
            width = widthSize;
            height = heightSize;
        }
        mMaxRadius = mLargeCircleRadius = Math.min(width, height) / 2;
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        ani();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mAnimator.cancel();
    }

    @Override
    protected void onDraw(Canvas canvas) {

        mPaint.setColor(Color.parseColor("#7FFFD4"));
        canvas.drawCircle(getWidth()/2, getHeight()/2, mLargeCircleRadius, mPaint);

        float density = getResources().getDisplayMetrics().density;
        mSmallCircleRadius = mLargeCircleRadius-(int)(density * 7);
        mPaint.setColor(Color.parseColor("#54FF9F"));

        canvas.drawCircle(getWidth()/2, getHeight()/2, mSmallCircleRadius, mPaint);
        super.onDraw(canvas);
    }


    private void ani() {
        mAnimator = new ValueAnimator();
        mAnimator.setDuration(1000);
        mAnimator.setFloatValues(0,100);
        mAnimator.setRepeatCount(-1);  //-1表示无线循环
        mAnimator.setRepeatMode(ValueAnimator.REVERSE);  //reverse：逆向重复，restart：连续重复
        mAnimator.setInterpolator(new AccelerateDecelerateInterpolator());

        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float f = animation.getAnimatedFraction();
                float density = getResources().getDisplayMetrics().density;
                float t = f * density * 4;
                mLargeCircleRadius = (int) (mMaxRadius - t);
                invalidate();

            }
        });

        mAnimator.start();

    }

}
