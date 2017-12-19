package com.zht.genericproject.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * 作者：zhanghaitao on 2017/11/8 13:18
 * 邮箱：820159571@qq.com
 *
 * @describe:GuideActivity的指示器
 */

public class IndicatorView extends View {

    private final String TAG = getClass().getSimpleName();

    //选中小红点的画笔
    private Paint mCurrentPaint;
    //默认小红点的画笔
    private Paint mNormalPaint;
    //view的宽度
    private int width;
    //view的高度
    private int height;
    //view的中心(X)
    private int centerWidth;
    //view的中心(Y)
    private int centerHeight;

    private int mInterval = 30; //间距

    private int mCircleCenterInterval; //圆心间距

    private int mRadius = 10; //小圆球的半径
    private int mStartCoordinate; //开始坐标
    private int mCurrentCoordinate; //选中小圆球开始坐标
    private int count;     //小圆球的数量


    //颜色
    private int paintCurrentCircleColor = Color.argb(255, 200, 0, 0);
    private int paintNormalCircleColor = Color.parseColor("#CCCCCC");

    //该方法在我们java代码添加控件时回调
    public IndicatorView(Context context) {
        super(context);
        initPaint();
    }

    //该方法在我们XML文件里添加控件时回调
    public IndicatorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initPaint();
    }

    //初始化画笔对象
    private void initPaint() {
        //创建画笔的对象,用于画选中的小圆球
        mCurrentPaint = new Paint();
        mCurrentPaint.setAntiAlias(true);//设置抗锯齿
        mCurrentPaint.setStyle(Paint.Style.FILL);//设置画笔的样式,为实心
        mCurrentPaint.setColor(paintCurrentCircleColor);//设置画笔的颜色
        mCurrentPaint.setStrokeWidth(2);//设置画笔的宽度
        //创建画笔的对象,用于画默认的小圆球
        mNormalPaint = new Paint();
        mNormalPaint.setAntiAlias(true);
        mNormalPaint.setStyle(Paint.Style.FILL);
        mNormalPaint.setColor(paintNormalCircleColor);
        mNormalPaint.setStrokeWidth(2);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = MeasureSpec.getSize(widthMeasureSpec);
        height = MeasureSpec.getSize(heightMeasureSpec);
        centerWidth = width / 2;
        centerHeight = height / 2;
        mCircleCenterInterval = 2 * mRadius + mInterval;

        if (count > 0) {
            mStartCoordinate = centerWidth - ((count - 1) * mCircleCenterInterval) / 2;
        } else {
            throw new IllegalArgumentException("数量必须初始化！");
        }

        if (mCurrentCoordinate == 0) {
            mCurrentCoordinate = mStartCoordinate;
        }

    }

    //参数就是画板，可以直接使用
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //画默认的圆
        for (int i = 0; i < count; i++) {
            canvas.drawCircle(mStartCoordinate + i * mCircleCenterInterval, centerHeight, mRadius,
                    mNormalPaint);
        }
        //画当前的圆
        canvas.drawCircle(mCurrentCoordinate, centerHeight, mRadius,
                mCurrentPaint);
    }


    public void setoffset(int position, float positionOffset) {

        if (mCurrentCoordinate != 0) {
            mCurrentCoordinate = (int) (mStartCoordinate +
                    (  position+ positionOffset)* mCircleCenterInterval);
        }
        //关键：重新绘制自定义view的方法,十分常用
        invalidate();
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void getCount(int count) {
        this.count = count;
    }

    public int getmInterval() {
        return mInterval;
    }

    public void setmInterval(int mInterval) {
        this.mInterval = mInterval;
    }

    public int getmRadius() {
        return mRadius;
    }

    public void setmRadius(int mRadius) {
        this.mRadius = mRadius;
    }

    public int getPaintCurrentCircleColor() {
        return paintCurrentCircleColor;
    }

    public void setPaintCurrentCircleColor(int paintCurrentCircleColor) {
        this.paintCurrentCircleColor = paintCurrentCircleColor;
    }

    public int getPaintNormalCircleColor() {
        return paintNormalCircleColor;
    }

    public void setPaintNormalCircleColor(int paintNormalCircleColor) {
        this.paintNormalCircleColor = paintNormalCircleColor;
    }
}