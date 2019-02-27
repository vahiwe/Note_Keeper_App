package com.example.editnote;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.support.v4.widget.ExploreByTouchHelper;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

import java.util.List;

/**
 * TODO: document your custom view class.
 */
public class ModuleStatusView extends View {
    private static final int EDIT_MODE_MODULE_COUNT = 7;
    private static final int INVALID_INDEX = -1;
    private static final int SHAPE_CIRCLE = 0;
    private static final float DEFAULT_OUTLINE_WIDTH_DP = 2f;
    private String mExampleString; // TODO: use a default from R.string...
    private int mExampleColor = Color.RED; // TODO: use a default from R.color...
    private float mExampleDimension = 0; // TODO: use a default from R.dimen...
    private Drawable mExampleDrawable;
    private float mSpacing;
    private float mShapeSize;
    private float mOutlineWidth;
    private Rect[] mModuleRectangles;
    private int mOutlineColor;
    private Paint mPaintOutline;
    private int mFillColor;
    private Paint mPaintFill;
    private float mRadius;
    private int mMaxHorizontalModules;
    private int mShape;
    private ModuleStatusAccessibilityHelper mAccessibilityHelper;

    public boolean[] getModuleStatus() {
        return mModuleStatus;
    }

    public void setModuleStatus(boolean[] moduleStatus) {
        mModuleStatus = moduleStatus;
    }

    private boolean[] mModuleStatus;

    public ModuleStatusView(Context context) {
        super(context);
        init(null, 0);
    }

    public ModuleStatusView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public ModuleStatusView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        if (isInEditMode())
            setupEditModeValues();

        setFocusable(true);
        mAccessibilityHelper = new ModuleStatusAccessibilityHelper(this);
        ViewCompat.setAccessibilityDelegate(this, mAccessibilityHelper);

        DisplayMetrics dm = getContext().getResources().getDisplayMetrics();
        float displayDensity = dm.density;
        float defaultOutlineWidthPixels = displayDensity * DEFAULT_OUTLINE_WIDTH_DP;
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.ModuleStatusView, defStyle, 0);

        mOutlineColor = a.getColor(R.styleable.ModuleStatusView_outlineColor, Color.BLACK);
        mShape = a.getInt(R.styleable.ModuleStatusView_shape, SHAPE_CIRCLE);
        mOutlineWidth = a.getDimension(R.styleable.ModuleStatusView_outlineWidth, defaultOutlineWidthPixels);

        a.recycle();

//        mOutlineWidth = 6f;
        mShapeSize = 144f;
        mSpacing = 30f;
        mRadius = (mShapeSize - mOutlineWidth) / 2;


        //setupModuleRectangles();


        //mOutlineColor = Color.BLACK;
        mPaintOutline = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintOutline.setStyle(Paint.Style.STROKE);
        mPaintOutline.setStrokeWidth(mOutlineWidth);
        mPaintOutline.setColor(mOutlineColor);

        mFillColor = getContext().getResources().getColor(R.color.pluralsight_orange);

        mPaintFill = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintFill.setStyle(Paint.Style.FILL);
        //mPaintFill.setStrokeWidth(mOutlineWidth);
        mPaintFill.setColor(mFillColor);


        // Set up a default TextPaint object
//        mTextPaint = new TextPaint();
//        mTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
//        mTextPaint.setTextAlign(Paint.Align.LEFT);

        // Update TextPaint and text measurements from attributes
      //  invalidateTextPaintAndMeasurements();
    }

    @Override
    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
        mAccessibilityHelper.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        return mAccessibilityHelper.dispatchKeyEvent(event) || super.dispatchKeyEvent(event);
    }

    @Override
    protected boolean dispatchHoverEvent(MotionEvent event) {
        return mAccessibilityHelper.dispatchHoverEvent(event) || super.dispatchHoverEvent(event);
    }

    private void setupEditModeValues() {
        boolean[] exampleModuleValues = new boolean[EDIT_MODE_MODULE_COUNT];
        int middle = EDIT_MODE_MODULE_COUNT / 2;
        for (int i = 0; i < middle; i++) {
            exampleModuleValues[i] = true;
        }

        setModuleStatus(exampleModuleValues);
    }

    private void setupModuleRectangles(int width) {
        int availableWidth = width - getPaddingLeft() - getPaddingRight();
        int horizontalModulesThatCanFit = (int) (availableWidth / (mShapeSize + mSpacing));
        int maxHorizontalModules = Math.min(horizontalModulesThatCanFit, mModuleStatus.length);
        mModuleRectangles = new Rect[mModuleStatus.length];

        for (int moduleIndex = 0; moduleIndex < mModuleRectangles.length; moduleIndex++) {
            int column = moduleIndex % maxHorizontalModules;
            int row = moduleIndex / maxHorizontalModules;

            int x = getPaddingLeft() + (int) (column * (mShapeSize + mSpacing));
            int y = getPaddingTop() + (int) (row * (mShapeSize + mSpacing));
            mModuleRectangles[moduleIndex] = new Rect(x, y, x + (int) mShapeSize, y + (int) (mShapeSize));
        }
    }

    private void invalidateTextPaintAndMeasurements() {
//        mTextPaint.setTextSize(mExampleDimension);
//        mTextPaint.setColor(mExampleColor);
//        mTextWidth = mTextPaint.measureText(mExampleString);
//
//        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
//        mTextHeight = fontMetrics.bottom;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int desiredWidth = 0;
        int desiredHeight = 0;

        int specWidth = MeasureSpec.getSize(widthMeasureSpec);
        int availableWidth = specWidth - getPaddingLeft() - getPaddingRight();
        int horizontalModulesThatCanFit = (int) (availableWidth / (mShapeSize + mSpacing));
        mMaxHorizontalModules = Math.min(horizontalModulesThatCanFit, mModuleStatus.length);

        desiredWidth = (int) ((mMaxHorizontalModules * (mShapeSize + mSpacing)) - mSpacing);
        desiredWidth += getPaddingRight() + getPaddingLeft();

        int rows = ((mModuleStatus.length - 1) / mMaxHorizontalModules) + 1;
        desiredHeight = (int) (((mShapeSize + mSpacing) * rows) - mSpacing);
        desiredHeight += getPaddingBottom() + getPaddingTop();

        int width = resolveSizeAndState(desiredWidth, widthMeasureSpec, 0);
        int height = resolveSizeAndState(desiredHeight, heightMeasureSpec, 0);

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        setupModuleRectangles(w);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        for (int moduleIndex = 0; moduleIndex < mModuleRectangles.length; moduleIndex++) {
            if (mShape == SHAPE_CIRCLE) {
                float x = mModuleRectangles[moduleIndex].centerX();
                float y = mModuleRectangles[moduleIndex].centerY();
//            mRadius = (mShapeSize - mOutlineWidth) / 2;

                if (mModuleStatus[moduleIndex])
                    canvas.drawCircle(x, y, mRadius, mPaintFill);

                canvas.drawCircle(x, y, mRadius, mPaintOutline);
            } else {
                drawSquare(canvas, moduleIndex);
            }
        }
        // TODO: consider storing these as member variables to reduce
        // allocations per draw cycle.
//        int paddingLeft = getPaddingLeft();
//        int paddingTop = getPaddingTop();
//        int paddingRight = getPaddingRight();
//        int paddingBottom = getPaddingBottom();
//
//        int contentWidth = getWidth() - paddingLeft - paddingRight;
//        int contentHeight = getHeight() - paddingTop - paddingBottom;

        // Draw the text.
//        canvas.drawText(mExampleString,
//                paddingLeft + (contentWidth - mTextWidth) / 2,
//                paddingTop + (contentHeight + mTextHeight) / 2,
//                mTextPaint);

        // Draw the example drawable on top of the text.
//        if (mExampleDrawable != null) {
//            mExampleDrawable.setBounds(paddingLeft, paddingTop,
//                    paddingLeft + contentWidth, paddingTop + contentHeight);
//            mExampleDrawable.draw(canvas);
//        }
    }

    private void drawSquare(Canvas canvas, int moduleIndex) {
        Rect moduleRectangle = mModuleRectangles[moduleIndex];

        if(mModuleStatus[moduleIndex])
            canvas.drawRect(moduleRectangle, mPaintFill);

        canvas.drawRect(moduleRectangle.left + (mOutlineWidth/2),
                moduleRectangle.top + (mOutlineWidth/2),
                moduleRectangle.right - (mOutlineWidth/2),
                moduleRectangle.bottom - (mOutlineWidth/2),
                mPaintOutline);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                return true;
            case MotionEvent.ACTION_UP:
                int moduleIndex = findItemAtPoint(event.getX(), event.getY());
                onModuleSelected(moduleIndex);
                return true;
        }
        return super.onTouchEvent(event);
    }

    private void onModuleSelected(int moduleIndex) {
        if (moduleIndex == INVALID_INDEX)
            return;

        mModuleStatus[moduleIndex] = ! mModuleStatus[moduleIndex];

        invalidate();

        mAccessibilityHelper.invalidateVirtualView(moduleIndex);
        mAccessibilityHelper.sendEventForVirtualView(moduleIndex, AccessibilityNodeInfoCompat.ACTION_CLICK);
    }

    private int findItemAtPoint(float x, float y) {
        int moduleIndex = INVALID_INDEX;
        for (int i = 0; i < mModuleRectangles.length; i++) {
            if (mModuleRectangles[i].contains((int) x, (int) y)) {
                moduleIndex = i;
                break;
            }
        }

        return moduleIndex;
    }

    /**
     * Gets the example string attribute value.
     *
     * @return The example string attribute value.
     */
    public String getExampleString() {
        return mExampleString;
    }

    /**
     * Sets the view's example string attribute value. In the example view, this string
     * is the text to draw.
     *
     * @param exampleString The example string attribute value to use.
     */
    public void setExampleString(String exampleString) {
        mExampleString = exampleString;
        invalidateTextPaintAndMeasurements();
    }

    /**
     * Gets the example color attribute value.
     *
     * @return The example color attribute value.
     */
    public int getExampleColor() {
        return mExampleColor;
    }

    /**
     * Sets the view's example color attribute value. In the example view, this color
     * is the font color.
     *
     * @param exampleColor The example color attribute value to use.
     */
    public void setExampleColor(int exampleColor) {
        mExampleColor = exampleColor;
        invalidateTextPaintAndMeasurements();
    }

    /**
     * Gets the example dimension attribute value.
     *
     * @return The example dimension attribute value.
     */
    public float getExampleDimension() {
        return mExampleDimension;
    }

    /**
     * Sets the view's example dimension attribute value. In the example view, this dimension
     * is the font size.
     *
     * @param exampleDimension The example dimension attribute value to use.
     */
    public void setExampleDimension(float exampleDimension) {
        mExampleDimension = exampleDimension;
        invalidateTextPaintAndMeasurements();
    }

    /**
     * Gets the example drawable attribute value.
     *
     * @return The example drawable attribute value.
     */
    public Drawable getExampleDrawable() {
        return mExampleDrawable;
    }

    /**
     * Sets the view's example drawable attribute value. In the example view, this drawable is
     * drawn above the text.
     *
     * @param exampleDrawable The example drawable attribute value to use.
     */
    public void setExampleDrawable(Drawable exampleDrawable) {
        mExampleDrawable = exampleDrawable;
    }

    private class ModuleStatusAccessibilityHelper extends ExploreByTouchHelper {
        public ModuleStatusAccessibilityHelper(@NonNull View host) {
            super(host);
        }

        @Override
        protected int getVirtualViewAt(float v, float v1) {
            int moduleIndex = findItemAtPoint(v, v1);
            return moduleIndex == INVALID_INDEX ? ExploreByTouchHelper.INVALID_ID : moduleIndex;
        }

        @Override
        protected void getVisibleVirtualViews(List<Integer> list) {
            if (mModuleRectangles == null)
                return;

            for (int i = 0; i < mModuleRectangles.length; i++) {
                list.add(i);
            }

        }

        @Override
        protected void onPopulateNodeForVirtualView(int i, @NonNull AccessibilityNodeInfoCompat accessibilityNodeInfoCompat) {
            accessibilityNodeInfoCompat.setFocusable(true);
            accessibilityNodeInfoCompat.setBoundsInParent(mModuleRectangles[i]);
            accessibilityNodeInfoCompat.setContentDescription("Module " + i);

            accessibilityNodeInfoCompat.setCheckable(true);
            accessibilityNodeInfoCompat.setChecked(mModuleStatus[i]);

            accessibilityNodeInfoCompat.addAction(AccessibilityNodeInfoCompat.ACTION_CLICK);
        }

        @Override
        protected boolean onPerformActionForVirtualView(int i, int i1, @Nullable Bundle bundle) {
            switch (i1) {
                case AccessibilityNodeInfoCompat.ACTION_CLICK:
                    onModuleSelected(i);
                    return true;
            }
            return false;
        }
    }
}
