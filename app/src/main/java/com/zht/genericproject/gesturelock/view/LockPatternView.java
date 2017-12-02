/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.zht.genericproject.gesturelock.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.os.Debug;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Base64;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.View;
import android.view.accessibility.AccessibilityManager;

import com.zht.genericproject.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Displays and detects the user's unlock attempt, which is a drag of a finger
 * across 9 regions of the screen.
 * 显示并检测用户的解锁尝试，这是手指在屏幕的9个区域的拖动。
 * <p/>
 * Is also capable of displaying a static pattern in "in progress", "wrong" or
 * "correct" states.
 * 还能够显示一个静态模式的“进步”,“错误”或“正确”状态。
 */
public class LockPatternView extends View {
    // Aspect to use when rendering this view
    private static final int ASPECT_SQUARE = 0;    // View will be the minimum of width/height
    private static final int ASPECT_LOCK_WIDTH = 1;    // Fixed width; height will be minimum of (w,h)
    private static final int ASPECT_LOCK_HEIGHT = 2;    // Fixed height; width will be minimum of (w,h)
    public static final int MIN_LOCK_PATTERN_SIZE = 4;
    private static final boolean PROFILE_DRAWING = false;
    private boolean mDrawingProfilingStarted = false;
    private Paint mPaint = new Paint();
    private Paint mPathPaint = new Paint();
    /**
     * How many milliseconds we spend animating each circle of a lock pattern
     * if the animating mode is set. The entire animation should take this
     * constant * the length of the pattern to complete.
     */
    private static final int MILLIS_PER_CIRCLE_ANIMATING = 700;
    /**
     * This can be used to avoid updating the display for very small motions or noisy panels.
     * It didn't seem to have much impact on the devices tested, so currently set to 0.
     */
    private static final float DRAG_THRESHHOLD = 0.0f;
    private OnPatternListener mOnPatternListener;
    private ArrayList<Cell> mPattern = new ArrayList<>(9);
    /**
     * Lookup table for the circles of the pattern we are currently drawing.
     * This will be the cells of the complete pattern unless we are animating,
     * in which case we use this to hold the cells we are drawing for the in
     * progress animation.
     */
    private boolean[][] mPatternDrawLookup = new boolean[3][3];
    /**
     * the in progress point:
     * - during interaction: where the user's finger is
     * - during animation: the current tip of the animating line
     */
    private float mInProgressX = -1;
    private float mInProgressY = -1;
    private long mAnimatingPeriodStart;
    private DisplayMode mPatternDisplayMode = DisplayMode.Correct;
    private boolean mInputEnabled = true;
    private boolean mInStealthMode = false;
    /**
     * 触摸屏幕滑动解锁时，取消震动   zyp 2017.02.04
     */
    private boolean mEnableHapticFeedback = false;

    private boolean mPatternInProgress = false;

    // 连线-粗细比例，0-1
    private float mDiameterFactor = 0.10f;
    // 连线-透明度，0-255
    private final int mStrokeAlpha = 255;
    // 触摸-命中率，0-1，用于防止快速滑动-导致误滑
    private float mHitFactor = 0.4f;
    private float mSquareWidth;
    private float mSquareHeight;
    private Bitmap mBitmapBtnDefault;
    /**
     * revise by zht on 2017.5.10 将触摸时单一图片修改为一个存放Bitmap的数组
     */
    //private Bitmap mBitmapBtnTouched;
    //存放九种图片的集合
    private Bitmap[][] mBitmap;
    private Bitmap mBitmapCircleDefault;
    private Bitmap mBitmapCircleGreen;
    private Bitmap mBitmapCircleRed;
    private Bitmap mBitmapArrowGreenUp;
    private Bitmap mBitmapArrowRedUp;

    private final Path mCurrentPath = new Path();
    private final Rect mInvalidate = new Rect();
    private final Rect mTmpInvalidateRect = new Rect();
    private int mBitmapWidth;
    private int mBitmapHeight;
    private int mAspect;
    private final Matrix mArrowMatrix = new Matrix();
    private final Matrix mCircleMatrix = new Matrix();
    // 缩放比例
    private final float mWeight = 0.2f;

    private int mStrokeWrongColor = Color.GREEN;
    private int mStrokeColor = Color.RED;

    //用于保存手指按下和抬起的状态
    private boolean mPatternFinish = false;
    private Bitmap mBitmapCircleGreen1;
    private Bitmap mBitmapCircleRed1;
    private boolean mPatternAnimate;
    private int mPatternAnimateTime = 0;

    /**
     * Represents a cell in the 3 X 3 matrix of the unlock pattern view.
     * 在解锁模式视图的3 X 3矩阵中表示一个单元格。
     * 这个类就是九个单元格的集合。（已经通过私有化构造函数限制，静态代码块中添加到sCells中）
     */
    public static class Cell {
        int row;
        int column;
        // keep # objects limited to 9
        static Cell[][] sCells = new Cell[3][3];

        static {
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    sCells[i][j] = new Cell(i, j);
                }
            }
        }

        /**
         * cell 构造函数
         *
         * @param row    The row of the cell.
         * @param column The column of the cell.
         */
        private Cell(int row, int column) {
            checkRange(row, column);
            this.row = row;
            this.column = column;
        }

        public int getRow() {
            return row;
        }

        public int getColumn() {
            return column;
        }

        /**
         * @param row    The row of the cell.
         * @param column The column of the cell.
         */
        public static synchronized Cell of(int row, int column) {
            //检查范围---保证是x在0-2 和y在0-2的范围中
            checkRange(row, column);
            return sCells[row][column];
        }

        private static void checkRange(int row, int column) {
            if (row < 0 || row > 2) {
                throw new IllegalArgumentException("row must be in range 0-2");
            }
            if (column < 0 || column > 2) {
                throw new IllegalArgumentException("column must be in range 0-2");
            }
        }

        public String toString() {
            return "(row=" + row + ",clmn=" + column + ")";
        }
    }

    /**
     * How to display the current pattern.
     * 如何显示当前的图案
     */
    public enum DisplayMode {

        /**
         * The pattern drawn is correct (i.e draw it in a friendly color)
         * 绘制的图案是正确的(i。e 以一种正确的颜色画出来)
         */
        Correct,
        /**
         * Animate the pattern (for demo, and help).
         * 激活图案(用于演示和帮助)。
         */
        Animate,
        /**
         * The pattern is wrong (i.e draw a foreboding color)
         * 这种图案是错误的(i.e 画出一种不正确的颜色)
         */
        Wrong
    }

    /**
     * The call back interface for detecting patterns entered by the user.
     * 用于检测用户输入的图案的回调接口。
     */
    public interface OnPatternListener {
        /**
         * A new pattern has begun.
         * 一种新的图案已经开始。
         */
        void onPatternStart();

        /**
         * The pattern was cleared.
         * 这个图案被清除了
         */
        void onPatternCleared();

        /**
         * The user extended the pattern currently being drawn by one cell.
         * 用户扩展了当前由一个单元格绘制的图案。
         *
         * @param pattern The pattern with newly added cell.
         *                新添加单元格的图案
         */
        void onPatternCellAdded(List<Cell> pattern);

        /**
         * A pattern was detected from the user.
         * 从用户中检测到一个图案。
         *
         * @param pattern The pattern.
         */
        void onPatternDetected(List<Cell> pattern);
    }

    public LockPatternView(Context context) {
        this(context, null);
    }

    public LockPatternView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.LockPatternView);

        final String aspect = a.getString(R.styleable.LockPatternView_aspect);

        if ("square".equals(aspect)) {
            mAspect = ASPECT_SQUARE;
        } else if ("lock_width".equals(aspect)) {
            mAspect = ASPECT_LOCK_WIDTH;
        } else if ("lock_height".equals(aspect)) {
            mAspect = ASPECT_LOCK_HEIGHT;
        } else {
            mAspect = ASPECT_SQUARE;
        }

        setClickable(true);
        // 防锯齿
        mPathPaint.setAntiAlias(true);
        // 防抖动
        mPathPaint.setDither(true);
        // 画笔颜色
        mPathPaint.setColor(mStrokeWrongColor);
        // 画笔透明度
        mPathPaint.setAlpha(mStrokeAlpha);
        // 画笔类型: STROKE - 空心;FILL - 实心;FILL_AND_STROKE - 用契形填充
        mPathPaint.setStyle(Paint.Style.STROKE);
        // 画笔接洽点类型 如影响矩形角的外轮廓
        mPathPaint.setStrokeJoin(Paint.Join.ROUND);
        // 画笔笔刷类型 如影响画笔始末端
        mPathPaint.setStrokeCap(Paint.Cap.ROUND);


        // 默认时-圆心
        mBitmapBtnDefault = getBitmapFor(R.drawable.wolf_dot);
        // 触摸时-圆心
        //mBitmapBtnTouched = getBitmapFor(R.drawable.ic_test1);
        // 触摸时-圆心
        mBitmap = new Bitmap[3][3];
        mBitmap[0][0] = getBitmapFor(R.drawable.wolf_expression1);
        mBitmap[0][1] = getBitmapFor(R.drawable.wolf_expression2);
        mBitmap[0][2] = getBitmapFor(R.drawable.wolf_expression3);
        mBitmap[1][0] = getBitmapFor(R.drawable.wolf_expression4);
        mBitmap[1][1] = getBitmapFor(R.drawable.wolf_expression5);
        mBitmap[1][2] = getBitmapFor(R.drawable.wolf_expression6);
        mBitmap[2][0] = getBitmapFor(R.drawable.wolf_expression7);
        mBitmap[2][1] = getBitmapFor(R.drawable.wolf_expression8);
        mBitmap[2][2] = getBitmapFor(R.drawable.wolf_expression9);


        // 默认时-圆环  绘画的时候
        mBitmapCircleDefault = getBitmapFor(R.drawable.wolf_dot);


        // 正确时-圆环
        mBitmapCircleGreen = getBitmapFor(R.drawable.wolf_unlock_right1);
        mBitmapCircleGreen1 = getBitmapFor(R.drawable.wolf_unlock_right2);
        // 错误时-圆环
        mBitmapCircleRed = getBitmapFor(R.drawable.wolf_unlock_wrong1);
        mBitmapCircleRed1 = getBitmapFor(R.drawable.wolf_unlock_wrong2);

        // 正确时-指向
        mBitmapArrowGreenUp = getBitmapFor(R.drawable.ssmm_circle_click);
        // 错误时-指向
        mBitmapArrowRedUp = getBitmapFor(R.drawable.ssmm_mistake);


        // 默认时-圆心、触摸时-圆心、 默认时-圆环、 正确时-圆环、 错误时-圆环
        final Bitmap bitmaps[] = {mBitmapBtnDefault, /*mBitmapBtnTouched,*/ mBitmapCircleDefault, /*mBitmapCircleGreen, mBitmapCircleRed*/};

        for (Bitmap bitmap : bitmaps) {
            mBitmapWidth = Math.max(mBitmapWidth, bitmap.getWidth());
            mBitmapHeight = Math.max(mBitmapHeight, bitmap.getHeight());
        }
    }

    private Bitmap getBitmapFor(int resId) {
        return BitmapFactory.decodeResource(getContext().getResources(), resId);
    }

    /**
     * @return Whether the view is in stealth mode.
     * 视图是否处于隐身状态
     */
    public boolean isInStealthMode() {
        return mInStealthMode;
    }

    /**
     * @return Whether the view has tactile feedback enabled.
     */
    public boolean isTactileFeedbackEnabled() {
        return mEnableHapticFeedback;
    }

    /**
     * Set whether the view is in stealth mode. If true, there will be no
     * visible feedback as the user enters the pattern.
     * 设置视图是否处于隐身模式。如果是真的，就不会有
     * 当用户输图片时，可以看到明显的反馈。
     *
     * @param inStealthMode Whether in stealth mode.是否在隐形模式下
     */
    public void setInStealthMode(boolean inStealthMode) {
        mInStealthMode = inStealthMode;
    }

    /**
     * Set whether the view will use tactile feedback. If true, there will be
     * tactile feedback as the user enters the pattern.
     * 设置视图是否会使用触觉反馈。如果是真的，就会有
     * 用户输入图案时的触觉反馈。
     *
     * @param tactileFeedbackEnabled Whether tactile feedback is enabled
     */
    public void setTactileFeedbackEnabled(boolean tactileFeedbackEnabled) {
        mEnableHapticFeedback = tactileFeedbackEnabled;
    }

    /**
     * Set the call back for pattern detection.
     * 为图案检测 设置回调监听。
     *
     * @param onPatternListener The call back.
     */
    public void setOnPatternListener(OnPatternListener onPatternListener) {
        mOnPatternListener = onPatternListener;
    }

    /**
     * Set the pattern explicitely (rather than waiting for the user to input
     * a pattern).
     * 将图案设置为明确(而不是等待用户输入一种图案)。
     *
     * @param displayMode How to display the pattern.
     * @param pattern     The pattern.
     */
    public void setPattern(DisplayMode displayMode, List<Cell> pattern) {
        mPattern.clear();
        mPattern.addAll(pattern);
        clearPatternDrawLookup();
        for (Cell cell : pattern) {
            mPatternDrawLookup[cell.getRow()][cell.getColumn()] = true;
        }

        setDisplayMode(displayMode);
    }

    /**
     * Set the display mode of the current pattern. This can be useful, for
     * instance, after detecting a pattern to tell this view whether change the
     * in progress result to correct or wrong.
     * <p>
     * 设置当前图案的显示模式。例如，
     * 在检测到一个图案之后，可以对这个图案进行检测，
     * 以确定是否更改了所取得的结果，从而使其正确或错误。
     *
     * @param displayMode The display mode.
     */
    public void setDisplayMode(DisplayMode displayMode) {
        mPatternDisplayMode = displayMode;
        if (displayMode == DisplayMode.Animate) {
            if (mPattern.size() == 0) {
                throw new IllegalStateException("you must have a pattern to " + "animate if you want to set the display mode to animate");
            }
            mAnimatingPeriodStart = SystemClock.elapsedRealtime();
            final Cell first = mPattern.get(0);
            mInProgressX = getCenterXForColumn(first.getColumn());
            mInProgressY = getCenterYForRow(first.getRow());
            clearPatternDrawLookup();
        }
        invalidate();
    }

    private void notifyCellAdded() {
        if (mOnPatternListener != null) {
            mOnPatternListener.onPatternCellAdded(mPattern);
        }
    }

    private void notifyPatternStarted() {
        if (mOnPatternListener != null) {
            mOnPatternListener.onPatternStart();
        }
    }

    private void notifyPatternDetected() {
        if (mOnPatternListener != null) {
            mOnPatternListener.onPatternDetected(mPattern);
        }
    }

    private void notifyPatternCleared() {
        if (mOnPatternListener != null) {
            mOnPatternListener.onPatternCleared();
        }
    }

    /**
     * Clear the pattern.
     * 清除图案。
     */
    public void clearPattern() {
        resetPattern();
    }

    /**
     * Reset all pattern state.
     * 重置所有图案状态。
     */
    private void resetPattern() {
        mPattern.clear();
        clearPatternDrawLookup();
        mPatternDisplayMode = DisplayMode.Correct;
        invalidate();
    }

    /**
     * Clear the pattern lookup table.
     * 清除图案查通过找表
     */
    private void clearPatternDrawLookup() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                mPatternDrawLookup[i][j] = false;
            }
        }
    }

    /**
     * Disable input (for instance when displaying a message that will
     * timeout so user doesn't get view into messy state).
     * 禁用输入(例如，在显示消息时
     * 超时，因此用户不会将视图变成混乱状态)。
     */
    public void disableInput() {
        mInputEnabled = false;
    }

    /**
     * Enable input.
     * 使输入。
     */
    public void enableInput() {
        mInputEnabled = true;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        final int width = w - getPaddingLeft() - getPaddingRight();
        mSquareWidth = width / 3.0f;

        final int height = h - getPaddingTop() - getPaddingBottom();
        mSquareHeight = height / 3.0f;
    }

    private int resolveMeasured(int measureSpec, int desired) {
        int result;
        int specSize = MeasureSpec.getSize(measureSpec);
        switch (MeasureSpec.getMode(measureSpec)) {
            case MeasureSpec.UNSPECIFIED:
                result = desired;
                break;
            case MeasureSpec.AT_MOST:
                result = Math.max(specSize, desired);
                break;
            case MeasureSpec.EXACTLY:
            default:
                result = specSize;
        }
        return result;
    }

    @Override
    protected int getSuggestedMinimumWidth() {
        // View should be large enough to contain 3 side-by-side target bitmaps
        return 3 * mBitmapWidth;
    }

    @Override
    protected int getSuggestedMinimumHeight() {
        // View should be large enough to contain 3 side-by-side target bitmaps
        return 3 * mBitmapWidth;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int minimumWidth = getSuggestedMinimumWidth();
        final int minimumHeight = getSuggestedMinimumHeight();
        int viewWidth = resolveMeasured(widthMeasureSpec, minimumWidth);
        int viewHeight = resolveMeasured(heightMeasureSpec, minimumHeight);

        switch (mAspect) {
            case ASPECT_SQUARE:
                viewWidth = viewHeight = Math.min(viewWidth, viewHeight);
                break;
            case ASPECT_LOCK_WIDTH:
                viewHeight = Math.min(viewWidth, viewHeight);
                break;
            case ASPECT_LOCK_HEIGHT:
                viewWidth = Math.min(viewWidth, viewHeight);
                break;
        }
        // Logger.v(TAG, "LockPatternView dimensions: " + viewWidth + "x" + viewHeight);
        setMeasuredDimension(viewWidth, viewHeight);
    }

    /**
     * Determines whether the point x, y will add a new point to the current
     * pattern (in addition to finding the cell, also makes heuristic choices
     * such as filling in gaps based on current pattern).
     * <p>
     * 确定x，y是否会为当前的图案添加一个新的点
     * (除了寻找单元格之外，还会做出一些启发式的选择，
     * 比如根据当前的模式填充间隙)。
     *
     * @param x The x coordinate.
     * @param y The y coordinate.
     */
    private Cell detectAndAddHit(float x, float y) {
        final Cell cell = checkForNewHit(x, y);
        if (cell != null) {

            // check for gaps in existing pattern
            Cell fillInGapCell = null;
            final ArrayList<Cell> pattern = mPattern;
            if (!pattern.isEmpty()) {
                final Cell lastCell = pattern.get(pattern.size() - 1);
                int dRow = cell.row - lastCell.row;
                int dColumn = cell.column - lastCell.column;

                int fillInRow = lastCell.row;
                int fillInColumn = lastCell.column;

                if (Math.abs(dRow) == 2 && Math.abs(dColumn) != 1) {
                    fillInRow = lastCell.row + ((dRow > 0) ? 1 : -1);
                }

                if (Math.abs(dColumn) == 2 && Math.abs(dRow) != 1) {
                    fillInColumn = lastCell.column + ((dColumn > 0) ? 1 : -1);
                }

                fillInGapCell = Cell.of(fillInRow, fillInColumn);
            }

            if (fillInGapCell != null && !mPatternDrawLookup[fillInGapCell.row][fillInGapCell.column]) {
                addCellToPattern(fillInGapCell);
            }
            addCellToPattern(cell);
            if (mEnableHapticFeedback) {
                performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY, HapticFeedbackConstants.FLAG_IGNORE_VIEW_SETTING
                        | HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
            }
            return cell;
        }
        return null;
    }

    private void addCellToPattern(Cell newCell) {
        mPatternDrawLookup[newCell.getRow()][newCell.getColumn()] = true;
        mPattern.add(newCell);
        notifyCellAdded();
    }

    // helper method to find which cell a point maps to
    private Cell checkForNewHit(float x, float y) {

        final int rowHit = getRowHit(y);
        if (rowHit < 0) {
            return null;
        }
        final int columnHit = getColumnHit(x);
        if (columnHit < 0) {
            return null;
        }

        if (mPatternDrawLookup[rowHit][columnHit]) {
            return null;
        }
        return Cell.of(rowHit, columnHit);
    }

    /**
     * Helper method to find the row that y falls into.
     * 辅助方法来查找y的行。
     *
     * @param y The y coordinate
     * @return The row that y falls in, or -1 if it falls in no row.
     */
    private int getRowHit(float y) {

        final float squareHeight = mSquareHeight;
        float hitSize = squareHeight * mHitFactor;

        float offset = getPaddingTop() + (squareHeight - hitSize) / 2f;
        for (int i = 0; i < 3; i++) {
            final float hitTop = offset + squareHeight * i;
            if (y >= hitTop && y <= hitTop + hitSize) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Helper method to find the column x fallis into.
     * 帮助找到列向量x的方法。
     *
     * @param x The x coordinate.
     * @return The column that x falls in, or -1 if it falls in no column.
     */
    private int getColumnHit(float x) {
        final float squareWidth = mSquareWidth;
        float hitSize = squareWidth * mHitFactor;

        float offset = getPaddingLeft() + (squareWidth - hitSize) / 2f;
        for (int i = 0; i < 3; i++) {

            final float hitLeft = offset + squareWidth * i;
            if (x >= hitLeft && x <= hitLeft + hitSize) {
                return i;
            }
        }
        return -1;
    }

    @SuppressLint("NewApi")
    @Override
    public boolean onHoverEvent(MotionEvent event) {
        AccessibilityManager accessibilityManager = (AccessibilityManager) getContext().getSystemService(Context.ACCESSIBILITY_SERVICE);
        if (accessibilityManager.isTouchExplorationEnabled()) {
            final int action = event.getAction();
            switch (action) {
                case MotionEvent.ACTION_HOVER_ENTER:
                    event.setAction(MotionEvent.ACTION_DOWN);
                    break;
                case MotionEvent.ACTION_HOVER_MOVE:
                    event.setAction(MotionEvent.ACTION_MOVE);
                    break;
                case MotionEvent.ACTION_HOVER_EXIT:
                    event.setAction(MotionEvent.ACTION_UP);
                    break;
            }
            onTouchEvent(event);
            event.setAction(action);
        }
        return super.onHoverEvent(event);
    }

    /**
     * 触摸事件：在这里处理触摸时，图片的改变和轨迹的绘画，
     * 需要改变绘画效果，在这里看起
     *
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!mInputEnabled || !isEnabled()) {
            return false;
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                handleActionDown(event);
                return true;
            case MotionEvent.ACTION_UP:
                /**
                 * revise by yanqingyan on 2017.1.22 在移动监听时添加mPatternDisplayMode值为Correct，保证划线过程中为红色
                 */
                mPatternDisplayMode = DisplayMode.Correct;
                handleActionUp(event);
                return true;
            case MotionEvent.ACTION_MOVE:
                /**
                 * revise by yanqingyan on 2017.1.22 在移动监听时添加mPatternDisplayMode值为wrong，保证划线过程中为绿色
                 */
                mPatternDisplayMode = DisplayMode.Wrong;
                handleActionMove(event);
                return true;
            case MotionEvent.ACTION_CANCEL:
                if (mPatternInProgress) {
                    mPatternInProgress = false;
                    resetPattern();
                    notifyPatternCleared();
                }
                if (PROFILE_DRAWING) {
                    if (mDrawingProfilingStarted) {
                        Debug.stopMethodTracing();
                        mDrawingProfilingStarted = false;
                    }
                }
                return true;
        }
        return false;
    }

    /**
     * 处理手指滑动的事件
     *
     * @param event
     */
    private void handleActionMove(MotionEvent event) {
        // Handle all recent motion events so we don't skip any cells even when the device is busy...
        final float radius = (mSquareWidth * mDiameterFactor * 0.5f);
        final int historySize = event.getHistorySize();
        mTmpInvalidateRect.setEmpty();
        boolean invalidateNow = false;
        for (int i = 0; i < historySize + 1; i++) {
            final float x = i < historySize ? event.getHistoricalX(i) : event.getX();
            final float y = i < historySize ? event.getHistoricalY(i) : event.getY();
            Cell hitCell = detectAndAddHit(x, y);
            final int patternSize = mPattern.size();
            if (hitCell != null && patternSize == 1) {
                mPatternInProgress = true;
                notifyPatternStarted();
            }
            // note current x and y for rubber banding of in progress patterns
            final float dx = Math.abs(x - mInProgressX);
            final float dy = Math.abs(y - mInProgressY);
            if (dx > DRAG_THRESHHOLD || dy > DRAG_THRESHHOLD) {
                invalidateNow = true;
            }

            if (mPatternInProgress && patternSize > 0) {
                final ArrayList<Cell> pattern = mPattern;
                final Cell lastCell = pattern.get(patternSize - 1);
                float lastCellCenterX = getCenterXForColumn(lastCell.column);
                float lastCellCenterY = getCenterYForRow(lastCell.row);

                // Adjust for drawn segment from last cell to (x,y). Radius accounts for line width.
                float left = Math.min(lastCellCenterX, x) - radius;
                float right = Math.max(lastCellCenterX, x) + radius;
                float top = Math.min(lastCellCenterY, y) - radius;
                float bottom = Math.max(lastCellCenterY, y) + radius;

                // Invalidate between the pattern's new cell and the pattern's previous cell
                if (hitCell != null) {
                    final float width = mSquareWidth * 0.5f;
                    final float height = mSquareHeight * 0.5f;
                    final float hitCellCenterX = getCenterXForColumn(hitCell.column);
                    final float hitCellCenterY = getCenterYForRow(hitCell.row);

                    left = Math.min(hitCellCenterX - width, left);
                    right = Math.max(hitCellCenterX + width, right);
                    top = Math.min(hitCellCenterY - height, top);
                    bottom = Math.max(hitCellCenterY + height, bottom);
                }

                // Invalidate between the pattern's last cell and the previous location
                mTmpInvalidateRect.union(Math.round(left), Math.round(top), Math.round(right), Math.round(bottom));
            }
        }
        mInProgressX = event.getX();
        mInProgressY = event.getY();

        // To save updates, we only invalidate if the user moved beyond a certain amount.
        if (invalidateNow) {
            mInvalidate.union(mTmpInvalidateRect);
            invalidate(mInvalidate);
            mInvalidate.set(mTmpInvalidateRect);
        }
    }

    /**
     * 处理事件弹起---当手指抬起时执行
     *
     * @param event
     */
    private void handleActionUp(MotionEvent event) {
        // report pattern detected
        if (!mPattern.isEmpty()) {
            mPatternInProgress = false;
            /**
             * revise by zht on 2017.5.10
             * 在手指抬起时，表示绘制图案结束
             * mPatternDrawLookup--->决定哪些图片需要绘制选择的效果。
             * 在这里全都这只为true，表示所有图片都要绘制。
             */
            mPatternFinish = true;
            mPatternAnimate = true;
            // mPatternDrawLookup
            //将所有的cell设置为TRUE，在手指抬起后，会将所有的图片绘制正确或错误的图片
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    mPatternDrawLookup[i][j] = true;
                }
            }

            notifyPatternDetected();
            invalidate();
        }
        if (PROFILE_DRAWING) {
            if (mDrawingProfilingStarted) {
                Debug.stopMethodTracing();
                mDrawingProfilingStarted = false;
            }
        }
    }

    /**
     * 动作处理：当手指按下的时候执行
     */
    private void handleActionDown(MotionEvent event) {
        //重新设置图案
        resetPattern();

        final float x = event.getX();
        final float y = event.getY();

        //获取到当前的单元格
        final Cell hitCell = detectAndAddHit(x, y);

        if (hitCell != null) {
            mPatternInProgress = true;
            mPatternDisplayMode = DisplayMode.Correct;
            notifyPatternStarted();
        } else if (mPatternInProgress) {
            mPatternInProgress = false;
            notifyPatternCleared();
        }
        if (hitCell != null) {

            final float startX = getCenterXForColumn(hitCell.column);
            final float startY = getCenterYForRow(hitCell.row);

            final float widthOffset = mSquareWidth / 2f;
            final float heightOffset = mSquareHeight / 2f;

            invalidate((int) (startX - widthOffset), (int) (startY - heightOffset), (int) (startX + widthOffset), (int) (startY + heightOffset));
        }
        mInProgressX = x;
        mInProgressY = y;
        if (PROFILE_DRAWING) {
            if (!mDrawingProfilingStarted) {
                Debug.startMethodTracing("LockPatternDrawing");
                mDrawingProfilingStarted = true;
            }
        }
    }

    private float getCenterXForColumn(int column) {
        return getPaddingLeft() + column * mSquareWidth + mSquareWidth / 2f;
    }

    private float getCenterYForRow(int row) {
        return getPaddingTop() + row * mSquareHeight + mSquareHeight / 2f;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        final ArrayList<Cell> pattern = mPattern;
        final int count = pattern.size();
        final boolean[][] drawLookup = mPatternDrawLookup;

        if (mPatternDisplayMode == DisplayMode.Animate) {
            // figure out which circles to draw + 1 so we pause on complete pattern
            final int oneCycle = (count + 1) * MILLIS_PER_CIRCLE_ANIMATING;
            final int spotInCycle = (int) (SystemClock.elapsedRealtime() - mAnimatingPeriodStart) % oneCycle;
            final int numCircles = spotInCycle / MILLIS_PER_CIRCLE_ANIMATING;

            clearPatternDrawLookup();
            for (int i = 0; i < numCircles; i++) {
                final Cell cell = pattern.get(i);
                drawLookup[cell.getRow()][cell.getColumn()] = true;
            }

            // figure out in progress portion of ghosting line
            final boolean needToUpdateInProgressPoint = numCircles > 0 && numCircles < count;

            if (needToUpdateInProgressPoint) {
                final float percentageOfNextCircle = ((float) (spotInCycle % MILLIS_PER_CIRCLE_ANIMATING)) / MILLIS_PER_CIRCLE_ANIMATING;

                final Cell currentCell = pattern.get(numCircles - 1);
                final float centerX = getCenterXForColumn(currentCell.column);
                final float centerY = getCenterYForRow(currentCell.row);

                final Cell nextCell = pattern.get(numCircles);
                final float dx = percentageOfNextCircle * (getCenterXForColumn(nextCell.column) - centerX);
                final float dy = percentageOfNextCircle * (getCenterYForRow(nextCell.row) - centerY);
                mInProgressX = centerX + dx;
                mInProgressY = centerY + dy;
            }
            // TODO: Infinite loop here...
            invalidate();
        }

        final float squareWidth = mSquareWidth;
        final float squareHeight = mSquareHeight;

        //2017.1.22 修改画笔的粗细将0.5f改为0.1f
        float radius = (squareWidth * mDiameterFactor * 0.1f);
        mPathPaint.setStrokeWidth(radius);

        /** 错误时，画笔颜色调整
         * revise by yanqingyan on 2017.1.22 start
         */
        if (mPatternDisplayMode == DisplayMode.Correct) {
            mPathPaint.setColor(mStrokeColor);
            mPathPaint.setAlpha(mStrokeAlpha);
        } else {
            mPathPaint.setColor(mStrokeWrongColor);
            mPathPaint.setAlpha(mStrokeAlpha);
        }
        /**
         * end
         */
        final Path currentPath = mCurrentPath;
        currentPath.rewind();

        // draw the circles
        final int paddingTop = getPaddingTop();
        final int paddingLeft = getPaddingLeft();

        // TODO: the path should be created and cached every time we hit-detect a cell
        // only the last segment of the path should be computed here
        // draw the path of the pattern (unless the user is in progress, and
        // we are in stealth mode)
        final boolean drawPath = (!mInStealthMode || mPatternDisplayMode == DisplayMode.Wrong);

        // draw the arrows associated with the path (unless the user is in progress, and
        // we are in stealth mode)
        boolean oldFlag = (mPaint.getFlags() & Paint.FILTER_BITMAP_FLAG) != 0;
        mPaint.setFilterBitmap(true); // draw with higher quality since we render with transforms
        /*
        // 画指向
        if (drawPath) {
            for (int i = 0; i < count - 1; i++) {
                Cell cell = pattern.get(i);
                Cell next = pattern.get(i + 1);

                // only draw the part of the pattern stored in
                // the lookup table (this is only different in the case
                // of animation).
                if (!drawLookup[next.row][next.column]) {
                    break;
                }

                float leftX = paddingLeft + cell.column * squareWidth;
                float topY = paddingTop + cell.row * squareHeight;

               drawArrow(canvas, leftX, topY, cell, next);
            }
        }
        */
        // 画连线
        if (drawPath) {
            boolean anyCircles = false;
            /**
             * revise by zht on 2017.5.10
             * 如果此时手指抬起，则不用画连线
             */
            if (mPatternFinish) {
                //将绘制状态重置
                mPatternFinish = false;


            } else {
                for (int i = 0; i < count; i++) {
                    //获取当前的cell
                    Cell cell = pattern.get(i);

                    // only draw the part of the pattern stored in
                    // the lookup table (this is only different in the case
                    // of animation).
                    //如果当前cell没有选中就返回
                    if (!drawLookup[cell.row][cell.column]) {
                        break;
                    }
                    anyCircles = true;
                    //获取中心带的坐标
                    float centerX = getCenterXForColumn(cell.column);
                    float centerY = getCenterYForRow(cell.row);

                    if (i == 0) {
                        currentPath.moveTo(centerX, centerY);
                    } else {
                        currentPath.lineTo(centerX, centerY);
                    }
                }

                // add last in progress section
                if ((mPatternInProgress || mPatternDisplayMode == DisplayMode.Animate) && anyCircles) {


                    currentPath.lineTo(mInProgressX, mInProgressY);
                }
                canvas.drawPath(currentPath, mPathPaint);
            }


        }

        // 画底图
        for (int i = 0; i < 3; i++) {
            float topY = paddingTop + i * squareHeight;
            // float centerY = getPaddingTop() + i * mSquareHeight + (mSquareHeight / 2);
            for (int j = 0; j < 3; j++) {
                float leftX = paddingLeft + j * squareWidth;

                drawCircle(canvas, (int) leftX, (int) topY, drawLookup[i][j], i, j);
            }
        }

        if (mPatternAnimate) {
            mPatternFinish = true;
            if (mPatternAnimateTime > 5) {
                mPatternAnimateTime = 0;
                mPatternAnimate = false;
                mPatternFinish = false;
            }
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    mPatternAnimateTime++;
                    postInvalidate();

                }
            }, 200);
        }

        mPaint.setFilterBitmap(oldFlag); // restore default flag
    }


    /**
     * 绘制方向的点
     *
     * @param canvas
     * @param leftX
     * @param topY
     * @param start
     * @param end
     */
    private void drawArrow(Canvas canvas, float leftX, float topY, Cell start, Cell end) {
        boolean green = mPatternDisplayMode != DisplayMode.Wrong;

        final int endRow = end.row;
        final int startRow = start.row;
        final int endColumn = end.column;
        final int startColumn = start.column;

        // offsets for centering the bitmap in the cell
        final int offsetX = ((int) mSquareWidth - mBitmapWidth) / 2;
        final int offsetY = ((int) mSquareHeight - mBitmapHeight) / 2;

        // compute transform to place arrow bitmaps at correct angle inside circle.
        // This assumes that the arrow image is drawn at 12:00 with it's top edge
        // coincident with the circle bitmap's top edge.

        //正确时-指向图片 ：// 错误时-指向
        Bitmap arrow = green ? mBitmapArrowGreenUp : mBitmapArrowRedUp;
        final int cellWidth = mBitmapWidth;
        final int cellHeight = mBitmapHeight;

        // the up arrow bitmap is at 12:00, so find the rotation from x axis and add 90 degrees.
        final float theta = (float) Math.atan2((double) (endRow - startRow), (double) (endColumn - startColumn));
        final float angle = (float) Math.toDegrees(theta) + 90.0f;

        // compose matrix
        float sx = Math.min(mSquareWidth / mBitmapWidth, 1.0f) - mWeight;
        float sy = Math.min(mSquareHeight / mBitmapHeight, 1.0f) - mWeight;
        mArrowMatrix.setTranslate(leftX + offsetX, topY + offsetY); // transform to cell position
        mArrowMatrix.preTranslate(mBitmapWidth / 2, mBitmapHeight / 2);
        mArrowMatrix.preScale(sx, sy);
        mArrowMatrix.preTranslate(-mBitmapWidth / 2, -mBitmapHeight / 2);
        mArrowMatrix.preRotate(angle, cellWidth / 2.0f, cellHeight / 2.0f);  // rotate about cell center
        mArrowMatrix.preTranslate((cellWidth - arrow.getWidth()) / 2.0f, 0.0f); // translate to 12:00 pos
        canvas.drawBitmap(arrow, mArrowMatrix, mPaint);
    }

    /**
     * mBitmapBtnTouched
     *
     * @param canvas        canvas
     * @param leftX         leftX
     * @param topY          topY
     * @param partOfPattern Whether this circle is part of the pattern.
     */
    private void drawCircle(Canvas canvas, int leftX, int topY, boolean partOfPattern,
                            int i, int j) {
        /*//外圆
        Bitmap outerCircle;
        //内圆
        Bitmap innerCircle;*/
        /**
         * revise by zht on 2017.5.10
         * 修改之前由内外圆组成的图案，直接用一个图片代替。
         */
        Bitmap myCircle = null;


        if (!partOfPattern || (mInStealthMode && mPatternDisplayMode != DisplayMode.Wrong)) {
            // unselected circle
            /*//默认时-圆环
            outerCircle = mBitmapCircleDefault;
            //默认时-圆心
            innerCircle = mBitmapBtnDefault;*/

            // unselected circle
            /**
             * revise by zht on 2017.5.10
             * 在图案没有被选择的时候，绘制小红点
             */
            myCircle = mBitmapBtnDefault;

        } else if (mPatternInProgress) {
            // user is in middle of drawing a pattern
            //用户处于绘图案的中间

            /*// 正确时-圆环
            outerCircle = mBitmapCircleGreen;

            innerCircle = mBitmapBtnTouched;*/
            // user is in middle of drawing a pattern
            //用户处于绘图案的中间
            /**
             * revise by zht on 2017.5.10
             * 在图案没有选择的时候，判断被选择的cell的位子，依据位子绘画对应的图片
             * 绘制九个图片对应的位置
             */

            myCircle = mBitmap[i][j];
            if (myCircle == null) {
                myCircle = mBitmapBtnDefault;
            }
           // myCircle = mBitmapBtnDefault;


        } else if (mPatternDisplayMode == DisplayMode.Wrong) {
            /* //  the pattern is wrong
            //  图案是错误的
           //错误时-圆环
            outerCircle = mBitmapCircleRed;
            //默认时-圆心
            innerCircle = mBitmapBtnDefault;*/
            if (mPatternAnimateTime % 2 == 0) {
                myCircle = mBitmapCircleRed;
            } else {
                myCircle = mBitmapCircleRed1;
            }


        } else if (mPatternDisplayMode == DisplayMode.Correct || mPatternDisplayMode == DisplayMode.Animate) {
            /*// the pattern is correct
            // 图案是正确的
            /正确时-圆环
            outerCircle = mBitmapCircleGreen;
            //默认时-圆心
            innerCircle = mBitmapBtnDefault;*/


            if (mPatternAnimateTime % 2 == 0) {
                myCircle = mBitmapCircleGreen;
            } else {
                myCircle = mBitmapCircleGreen1;
            }

        } else {
            throw new IllegalStateException("unknown display mode " + mPatternDisplayMode);
        }

        final int offsetX = ((int) mSquareWidth - mBitmapWidth) / 2;
        final int offsetY = ((int) mSquareHeight - mBitmapHeight) / 2;

        // Allow circles to shrink if the view is too small to hold them.
        // 如果视图太小而不能容纳它们，则允许圆圈收缩。
        float sx = (Math.min(mSquareWidth / mBitmapWidth, 1.0f) - mWeight);
        float sy = (Math.min(mSquareHeight / mBitmapHeight, 1.0f) - mWeight);

        mCircleMatrix.setTranslate(leftX + offsetX, topY + offsetY);
        mCircleMatrix.preTranslate(mBitmapWidth / 2, mBitmapHeight / 2);
        mCircleMatrix.preScale(sx, sy);
        mCircleMatrix.preTranslate(-mBitmapWidth / 2, -mBitmapHeight / 2);

        /*canvas.drawBitmap(outerCircle, mCircleMatrix, mPaint);
        canvas.drawBitmap(innerCircle, mCircleMatrix, mPaint);*/
        canvas.drawBitmap(myCircle, mCircleMatrix, mPaint);
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        return new SavedState(superState, patternToString(mPattern), mPatternDisplayMode.ordinal(), mInputEnabled, mInStealthMode, mEnableHapticFeedback);
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        final SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        setPattern(DisplayMode.Wrong, stringToPattern(ss.getSerializedPattern()));
        mPatternDisplayMode = DisplayMode.values()[ss.getDisplayMode()];
        mInputEnabled = ss.isInputEnabled();
        mInStealthMode = ss.isInStealthMode();
        mEnableHapticFeedback = ss.isTactileFeedbackEnabled();
    }

    /**
     * Deserialize a pattern.
     *
     * @param string The pattern serialized with {@link #patternToString}
     * @return The pattern.
     */
    public static List<Cell> stringToPattern(String string) {
        List<Cell> result = new ArrayList<>();
        try {
            final byte[] bytes = Base64.decode(string, Base64.DEFAULT);
            if (bytes.length > 9 || bytes.length < 2) {
                throw new Exception();
            }
            for (byte b : bytes) {
                result.add(Cell.of(b / 3, b % 3));
            }
        } catch (Exception e) {
            final byte[] bytes = string.getBytes();
            for (byte b : bytes) {
                result.add(Cell.of(b / 3, b % 3));
            }
        }
        return result;
    }

    /**
     * Serialize a pattern.
     *
     * @param pattern The pattern.
     * @return The pattern in string form.
     */
    public static String patternToString(List<Cell> pattern) {
        if (pattern == null) {
            return "";
        }
        final int patternSize = pattern.size();

        byte[] res = new byte[patternSize];
        for (int i = 0; i < patternSize; i++) {
            Cell cell = pattern.get(i);
            res[i] = (byte) (cell.getRow() * 3 + cell.getColumn());
        }
        return new String(Base64.encode(res, Base64.DEFAULT));
    }

    /**
     * The parecelable for saving and restoring a lock pattern view.
     */
    private static class SavedState extends BaseSavedState {
        private final String mSerializedPattern;
        private final int mDisplayMode;
        private final boolean mInputEnabled;
        private final boolean mInStealthMode;
        private final boolean mTactileFeedbackEnabled;

        /**
         * Constructor called from {@link LockPatternView#onSaveInstanceState()}
         */
        private SavedState(Parcelable superState, String serializedPattern, int displayMode, boolean inputEnabled, boolean inStealthMode,
                           boolean tactileFeedbackEnabled) {
            super(superState);
            mSerializedPattern = serializedPattern;
            mDisplayMode = displayMode;
            mInputEnabled = inputEnabled;
            mInStealthMode = inStealthMode;
            mTactileFeedbackEnabled = tactileFeedbackEnabled;
        }

        /**
         * Constructor called from {@link #CREATOR}
         */
        private SavedState(Parcel in) {
            super(in);
            mSerializedPattern = in.readString();
            mDisplayMode = in.readInt();
            mInputEnabled = (Boolean) in.readValue(null);
            mInStealthMode = (Boolean) in.readValue(null);
            mTactileFeedbackEnabled = (Boolean) in.readValue(null);
        }

        public String getSerializedPattern() {
            return mSerializedPattern;
        }

        public int getDisplayMode() {
            return mDisplayMode;
        }

        public boolean isInputEnabled() {
            return mInputEnabled;
        }

        public boolean isInStealthMode() {
            return mInStealthMode;
        }

        public boolean isTactileFeedbackEnabled() {
            return mTactileFeedbackEnabled;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeString(mSerializedPattern);
            dest.writeInt(mDisplayMode);
            dest.writeValue(mInputEnabled);
            dest.writeValue(mInStealthMode);
            dest.writeValue(mTactileFeedbackEnabled);
        }

        public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };


    }
}
