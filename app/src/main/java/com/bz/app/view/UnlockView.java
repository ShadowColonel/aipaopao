package com.bz.app.view;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

/**
 * Created by ThinkPad User on 2016/11/29.
 */

public class UnlockView extends View {

    private final static String LOG_TAG = "UnlockView";
    private LinearGradient mLinearGradient;
    private Matrix mGradientMatrix;
    private Paint mPaint;
    private Paint.FontMetrics fm;
    private int mTextHeight;
    private int mTextWidth;
    private int mViewWidth = 0;
    private int mTranslate = 0;


    public UnlockView(Context context) {
        this(context,null);
    }

    public UnlockView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public UnlockView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setTextSize(48);
        fm = mPaint.getFontMetrics();
        mTextHeight = (int) Math.ceil(fm.bottom - fm.top);
        mTextWidth = (int) Math.ceil(mPaint.measureText(">>>>>> 滑动解锁 >>>>>>"));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
        int width = 0;
        int height = 0;

        if (widthSpecMode == MeasureSpec.EXACTLY) {
            width = widthSpecSize;
        } else {
            width = mTextWidth;
        }

        if (heightSpecMode == MeasureSpec.EXACTLY) {
            height = heightSpecSize;
        } else {
            height = mTextHeight;
        }
        startX = (width - mTextWidth) / 2;
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (mViewWidth == 0) {
            mViewWidth = getMeasuredWidth();
            if (mViewWidth > 0) {
                mLinearGradient = new LinearGradient(
                        0,
                        0,
                        mViewWidth,
                        0,
                        new int[]{
                                0x33ffffff, 0xffffffff,
                                0x33ffffff},
                        null,
                        Shader.TileMode.CLAMP);
                mPaint.setShader(mLinearGradient);
                mGradientMatrix = new Matrix();
            }
        }
    }

    private int startX;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int startY = (getHeight() + mTextHeight) / 2;
        canvas.drawText(">>>>>> 滑动解锁 >>>>>>", startX, startY, mPaint);

        if (mGradientMatrix != null) {
            mTranslate += mViewWidth / 10;
            if (mTranslate > 2 * mViewWidth) {
                mTranslate = -mViewWidth;
            }
            mGradientMatrix.setTranslate(mTranslate, 0);
            mLinearGradient.setLocalMatrix(mGradientMatrix);
            postInvalidateDelayed(100);
        }
    }

    private float mLastEventX;
    private float mTouchSlop;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
        final float x = event.getX();
        final float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastEventX = x;
                break;

            case MotionEvent.ACTION_MOVE:
                float diffX = x-mLastEventX;
                Log.v(LOG_TAG,"diffX:"+diffX+"/"+mTouchSlop);
                if (diffX > mTouchSlop * 2 ) {
                    anim();
                }
                mLastEventX = x;
                break;
        }

        return true;
    }

    private void anim() {
        final int startX = 0;
        final int deltaX = getResources().getDisplayMetrics().widthPixels;
        final ValueAnimator animator = ValueAnimator.ofInt(0, 1).setDuration(1000);
//        animator.setRepeatMode(ValueAnimator.REVERSE);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float fraction = animator.getAnimatedFraction();
                scrollTo(startX - (int) (deltaX * fraction), 0);
            }
        });
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (mListener != null) mListener.unLock();
                scrollTo(0,0);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animator.start();
    }

    private OnLockListener mListener;
    public void setOnLockListener(OnLockListener listener){
        mListener = listener;
    }

    public interface OnLockListener {
        void unLock();
        void lock();
    }

}
