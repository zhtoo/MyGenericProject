package com.zht.genericproject.view;

import android.animation.FloatEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

import java.util.ArrayList;
import java.util.List;

/**
 * 类太阳系星球转动
 * Created by zht on 17/3/20.
 */
public class SolarSystemView extends View implements Runnable {

    public static final int FLUSH_RATE = 40;
    public static final int FLUSH_RATE_LIMITATION = 16;

    private int mFlushRate = FLUSH_RATE;
    private int paintCount;
    private float pivotX;
    private float pivotY;
    private Paint mTrackPaint;
    private Paint mPlanetPaint;
    private Paint mBackgroundPaint;
    private List<Planet> planets;
    private Bitmap mCacheBitmap;

    private ValueAnimator mAccelerateAnimator;
    private ValueAnimator mDecelerateAnimator;

    //构造方法
    public SolarSystemView(Context context) {
        this(context, null);
    }

    //构造方法
    public SolarSystemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * 构造方法
     *
     * @param context      上下文
     * @param attrs        传递资源文件
     * @param defStyleAttr
     */
    public SolarSystemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        planets = new ArrayList<>();

        mTrackPaint = new Paint();//绘画行星轨迹的画笔
        mTrackPaint.setStyle(Paint.Style.STROKE);//描边
        mTrackPaint.setAntiAlias(true);

        mPlanetPaint = new Paint();//绘画行星的画笔
        mPlanetPaint.setStyle(Paint.Style.FILL);//布满整个屏幕
        mPlanetPaint.setAntiAlias(true);
    }

    /**
     * 设置中心点
     *
     * @param x 在屏幕上的x坐标
     * @param y 在屏幕上的y坐标
     */
    public void setPivotPoint(float x, float y) {
        pivotX = x;
        pivotY = y;
        paintCount = 0;
        prepare();
    }

    /**
     * 添加行星的集合（有多少个就加多少个）
     * 这个方法是需要去实现的
     *
     * @param planets
     */
    public void addPlanets(List<Planet> planets) {
        this.planets.addAll(planets);
    }

    public void addPlanets(Planet planet) {
        planets.add(planet);
    }

    /**
     * 移除所有的行星
     */
    public void clear() {
        planets.clear();
    }

    /**
     * （同步） 准备
     */
    public synchronized void prepare() {
        //没有行星，就不执行
        if (planets.size() == 0) return;

        if (mCacheBitmap != null) {
            mCacheBitmap.recycle();
            mCacheBitmap = null;
        }
        mCacheBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        //宽、高、透明度
        Canvas canvas = new Canvas(mCacheBitmap);//将图片添加到画布中
        if (getBackground() != null) {
            getBackground().draw(canvas);
        }
        if (mBackgroundPaint != null && mBackgroundPaint.getShader() != null) {
            canvas.drawRect(0, 0, getWidth(), getHeight(), mBackgroundPaint);
        }
        for (Planet planet : planets) {
            //绘画轨迹
            mTrackPaint.setStrokeWidth(planet.getTrackWidth());
            mTrackPaint.setColor(planet.getTrackColor());
            canvas.drawCircle(pivotX, pivotY, planet.getRadius(), mTrackPaint);
        }
        postRepaint();
    }

    /**
     * 设置背景渐变
     * 设置中心点之后再做此事
     *
     * @param x  pivot x
     * @param y  pivot y
     * @param r  radius of gradient
     * @param sc start color
     * @param ec end color
     */
    public void setRadialGradient(float x, float y, float r, int sc, int ec) {
        mBackgroundPaint = new Paint();
        mBackgroundPaint.setStyle(Paint.Style.FILL);
        mBackgroundPaint.setAntiAlias(true);
        mBackgroundPaint.setShader(new RadialGradient(x, y, r, sc, ec, Shader.TileMode.CLAMP));
        prepare();
    }

    /**
     * 当尺寸发生改变
     *
     * @param w
     * @param h
     * @param oldw
     * @param oldh
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        prepare();
    }

    /**
     * 绘制方法
     *
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        if (planets.size() == 0) return;

        int count = canvas.save();
        if (mCacheBitmap != null) canvas.drawBitmap(mCacheBitmap, 0, 0, mPlanetPaint);
        for (Planet planet : planets) {
            double y;
            double x;
            float angle;
            if (planet.isClockwise()) {
                angle = (planet.getOriginAngle() + paintCount * planet.getAngleRate()) % 360;
            } else {
                angle = 360 - (planet.getOriginAngle() + paintCount * planet.getAngleRate()) % 360;
            }
            x = Math.cos(angle) * planet.getRadius() + pivotX;
            y = Math.sin(angle) * planet.getRadius() + pivotY;
            mPlanetPaint.setColor(planet.getColor());
            canvas.drawCircle((float) x, (float) y, planet.getSelfRadius(), mPlanetPaint);
        }
        canvas.restoreToCount(count);
        ++paintCount;
        if (paintCount < 0) paintCount = 0;
    }

    /**
     * 将布置重画
     */
    private void postRepaint() {
        removeCallbacks(this);
        postDelayed(this, mFlushRate);
    }

    /**
     *
     */
    @Override
    public void run() {
        //使无效；使无价值
        invalidate();
        postRepaint();
    }

    /**
     * Attached:
     * adj. 附加的；依恋的，充满爱心的
     * v. 附上（attach的过去分词）
     * This is called when the view is attached to a window.  At this point it
     * has a Surface and will start drawing.  Note that this function is
     * guaranteed to be called before {@link #onDraw(android.graphics.Canvas)},
     * however it may be called any time before the first onDraw -- including
     * before or after {@link #onMeasure(int, int)}.
     * 当（自定义||系统的）view被添加到一个窗口时这个方法被调用。在这一点的基础上它有一个Surface并且开始绘画。
     * 注意,这个函数一定是在onDraw方法之前被调用，然而这个方法会在第一次执行onDraw之前的任何时候被调用，
     * 包括在onMeasure方法执行之后。
     */
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        prepare();
    }

    /**
     *  on 在……时候
     *  adj. 分离的，分开的；超然的
     *  Detached
     *  v. 分离
     *  This is called when the view is detached from a window.  At this point it
     *  no longer has a surface for drawing.
     *  当这个view从一个窗口脱离（销毁||制空）时该方法被调用。在这一点的基础上它不再有为了绘制的surface
     */
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mCacheBitmap != null) {
            mCacheBitmap.recycle();
            mCacheBitmap = null;
        }
    }

    /**
     * 加速动画
     *
     * @return
     */
    private ValueAnimator getAccelerateAnimator() {
        if (mAccelerateAnimator != null) return mAccelerateAnimator;
        // public static final int FLUSH_RATE_LIMITATION = 16;//最小速率
        mAccelerateAnimator = ValueAnimator.ofFloat(mFlushRate, FLUSH_RATE_LIMITATION);
        mAccelerateAnimator.setEvaluator(new FloatEvaluator());
        mAccelerateAnimator.setInterpolator(new DecelerateInterpolator());
        mAccelerateAnimator.setDuration(1000);
        mAccelerateAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mFlushRate = ((Float) animation.getAnimatedValue()).intValue();
            }
        });
        return mAccelerateAnimator;
    }

    /**
     * 减速动画
     * @return
     */
    private ValueAnimator getDecelerateAnimator() {
        if (mDecelerateAnimator != null) return mDecelerateAnimator;
        // public static final int FLUSH_RATE = 40;
        mDecelerateAnimator = ValueAnimator.ofFloat(mFlushRate, FLUSH_RATE);
        mDecelerateAnimator.setEvaluator(new FloatEvaluator());
        mDecelerateAnimator.setInterpolator(new AccelerateInterpolator());
        mDecelerateAnimator.setDuration(1000);
        mDecelerateAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mFlushRate = ((Float) animation.getAnimatedValue()).intValue();
            }
        });
        return mDecelerateAnimator;
    }

    // 都是在主线程内操作,所以不会有生产者消费者问题

    /**
     *加速
     */
    public void accelerate() {
        if (mFlushRate == FLUSH_RATE_LIMITATION) return;
        mFlushRate = FLUSH_RATE; // reset flush rate
        ValueAnimator animator = getAccelerateAnimator();
        if (animator.isRunning()) animator.cancel();
        animator.setFloatValues(mFlushRate, FLUSH_RATE_LIMITATION);
        animator.start();
    }

    /**
     * 减速
     */
    public void decelerate() {
        if (mAccelerateAnimator != null && mAccelerateAnimator.isRunning()) {
            mAccelerateAnimator.cancel();
        }
        if (mFlushRate == FLUSH_RATE) return;
        ValueAnimator animator = getDecelerateAnimator();
        if (animator.isRunning()) animator.cancel();
        long duration = (long) (((float) FLUSH_RATE - mFlushRate) / FLUSH_RATE * 1000);
        if (duration == 0) {
            mFlushRate = FLUSH_RATE;
            return;
        }
        animator.setDuration(duration);
        animator.setFloatValues(mFlushRate, FLUSH_RATE);
        animator.start();
    }

    /**
     * 行星
     */
    public static class Planet {
        private int mRadius = 100;//半径
        private int mSelfRadius = 8;//自己的半径 绘画行星的半径
        private int mTrackWidth = 1;//轨迹宽度
        private int mColor = Color.parseColor("#CCCCCC");//行星的颜色
        private int mTrackColor = Color.parseColor("#CCCCCC");//轨迹的颜色
        private float mAngleRate = 0.01F; //角率
        private int mOriginAngle = 0;  //起始角度
        private boolean isClockwise = true; //顺时针旋转

        //下面是以上变量的set/get方法，在需要设置或者获取相关值的时候可以获取
        public int getRadius() {
            return mRadius;
        }

        public void setRadius(int mRadius) {
            this.mRadius = mRadius;
        }

        public int getSelfRadius() {
            return mSelfRadius;
        }

        public void setSelfRadius(int mSelfRadius) {
            this.mSelfRadius = mSelfRadius;
        }

        public int getTrackWidth() {
            return mTrackWidth;
        }

        public void setTrackWidth(int mTrackWidth) {
            this.mTrackWidth = mTrackWidth;
        }

        public int getColor() {
            return mColor;
        }

        public void setColor(int mColor) {
            this.mColor = mColor;
        }

        public int getTrackColor() {
            return mTrackColor;
        }

        public void setTrackColor(int mTrackColor) {
            this.mTrackColor = mTrackColor;
        }

        public float getAngleRate() {
            return mAngleRate;
        }

        public void setAngleRate(float mAngleRate) {
            this.mAngleRate = mAngleRate;
        }

        public boolean isClockwise() {
            return isClockwise;
        }

        public void setClockwise(boolean clockwise) {
            isClockwise = clockwise;
        }

        public int getOriginAngle() {
            return mOriginAngle;
        }

        public void setOriginAngle(int mOriginAngle) {
            this.mOriginAngle = mOriginAngle;
        }
    }
}
