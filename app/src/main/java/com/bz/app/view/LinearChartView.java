package com.bz.app.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by ThinkPad User on 2016/11/22.
 */

public class LinearChartView extends View{

    private int minWidth = 640;
    private int minHeight = 480;

    public LinearChartView(Context context) {
        super(context);
    }

    public LinearChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LinearChartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);

        int width = 0;
        if (widthMode == MeasureSpec.AT_MOST){
            width = MeasureSpec.getSize(widthMeasureSpec);
        }else if (widthMode == MeasureSpec.EXACTLY){
            width = MeasureSpec.getSize(widthMeasureSpec);
        }else if (widthMode == MeasureSpec.UNSPECIFIED){
            width = minWidth;
        }

        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int height = 0;

        if (heightMode == MeasureSpec.AT_MOST){
            height = MeasureSpec.getSize(widthMeasureSpec);
        }else if (heightMode == MeasureSpec.EXACTLY){
            height = MeasureSpec.getSize(widthMeasureSpec);
        }else if (heightMode == MeasureSpec.UNSPECIFIED){
            height = minHeight;
        }

        setMeasuredDimension(width,height);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint = new Paint();
        paint.setTextSize(20f);
        paint.setAntiAlias(true);//设置抗锯齿
        int w = getWidth();
        int h = getHeight();

        int xEnd = getWidth() - 50;
        int yEnd = getHeight() - 100;

//        paint.setColor(Color.parseColor("#86837E"));
        paint.setColor(Color.BLACK);
        canvas.drawLine(100, 150, 100, yEnd, paint);  //纵坐标
        canvas.drawLine(100, 150, 110, 167, paint);
        canvas.drawLine(100, 150, 90, 167, paint);

        canvas.drawLine(100, yEnd, xEnd, yEnd, paint);  //横坐标
        canvas.drawLine(xEnd, yEnd, xEnd - 17, yEnd - 10, paint);
        canvas.drawLine(xEnd, yEnd, xEnd - 17, yEnd + 10, paint);

        int textWidth = (int) paint.measureText("时间(min)");
        canvas.drawText("时间(min)", xEnd - textWidth, yEnd - paint.getTextSize(), paint);
        canvas.save();
        canvas.rotate(90);
        canvas.drawText("路程(km)", 140, -115, paint);
        canvas.restore();

        int xPart = (int) ((xEnd - 100) / 6.0);
        int yPart = (int) ((yEnd - 150) / 6.0);
        for (int i = 0; i < 6; i++) {
            int num = i * 5;
            int numWidth = (int) paint.measureText(String.valueOf(num));
            canvas.drawText(num + "", (100 + xPart * i) - numWidth / 2 , yEnd + 5 + paint.getTextSize(), paint);
        }
        for (int j = 1; j < 6; j++) {
            int num = j * 1;
            int numWidth = (int) paint.measureText(String.valueOf(num));
            canvas.drawText(num + "", 100 - 5 - numWidth, yEnd - j * yPart + paint.getTextSize() / 2, paint);
        }



        float startX = 100;
        float startY = yEnd;
        float endX;
        float endY = yEnd;

        for (int k = 1; k < 6; k++) {
            float random = (float) (Math.random() * 2);

            paint.setStrokeWidth(2f);
            endX = 100 + k * xPart;
            endY = endY - yPart * random;
            paint.setColor(Color.RED);
            canvas.drawLine(startX, startY, endX, endY, paint);
            paint.setColor(Color.BLUE);
            paint.setStrokeWidth(3f);
            canvas.drawPoint(endX, endY, paint);
            startX = endX;
            startY = endY;
        }
    }
}
