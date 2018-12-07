package com.zuojie.soundrecorder.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.*;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import com.zuojie.soundrecorder.R;
import com.zuojie.soundrecorder.util.Utils;

public class Slider extends View {
    private Paint mPaintA = new Paint();
    private Paint mPaintB = new Paint();
    private RectF mDrawRect;

    private int mMaxProgress = 100;

    private int mPrimaryColor;
    private int mSecondaryColor;
    private int mThumbSpaceSize;
    private int mThumbRadius;
    private int mThumbFocusRadius;
    private int mThumbTouchRadius;
    private float mThumbPosition = 0;
    private int mGravity;
    private Interpolator mInterpolator;

    private int mTouchSlop;
    private PointF mDownPoint;
    private boolean mIsDragging;
    private boolean mIsMoved;
    private float mThumbCurrentRadius;

    public static final int duration = 200;
    private ThumbRadiusAnimator mThumbRadiusAnimator;
    private ThumbMoveAnimator mThumbMoveAnimator;
    private OnProgressChangeListener mOnPositionChangeListener;

    public interface OnProgressChangeListener {
        void onPositionChanged(Slider view, boolean fromUser, float position, int progress);

        void onStartTrackingTouch(Slider view);

        void onStopTrackingTouch(Slider view);
    }

    public Slider(Context context) {
        super(context);
        init(context, null, 0);
    }

    public Slider(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public Slider(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    protected void init(Context context, AttributeSet attrs, int defStyleAttr) {
        mDrawRect = new RectF();
        mDownPoint = new PointF();
        mThumbRadiusAnimator = new ThumbRadiusAnimator();
        mThumbMoveAnimator = new ThumbMoveAnimator();
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.Slider, defStyleAttr, 0);
        mPrimaryColor = typedArray.getColor(R.styleable.Slider_primaryColor, Color.WHITE);
        mSecondaryColor = typedArray.getColor(R.styleable.Slider_secondaryColor, Color.parseColor("#80FFFFFF"));
        mGravity = typedArray.getInteger(R.styleable.Slider_android_gravity, Gravity.CENTER);
        setEnabled(typedArray.getBoolean(R.styleable.Slider_android_enabled, true));

        mThumbSpaceSize = Utils.dpToPx(context, 3);
        mThumbRadius = Utils.dpToPx(context, 6);
        mThumbFocusRadius = Utils.dpToPx(context, 10);
        mThumbTouchRadius = Utils.dpToPx(context, 10);
        mInterpolator = new AccelerateDecelerateInterpolator();
        mThumbCurrentRadius = mThumbRadius;

        mPaintA.setAntiAlias(true);
        mPaintB.setAntiAlias(true);
        mPaintA.setStyle(Paint.Style.FILL);
        mPaintB.setStyle(Paint.Style.FILL);
        mPaintA.setStrokeWidth(Utils.dpToPx(context, 2));
        mPaintB.setStrokeWidth(Utils.dpToPx(context, 2));
        mPaintA.setColor(mSecondaryColor);
        switch (typedArray.getInteger(R.styleable.Slider_trackCap, Paint.Cap.BUTT.ordinal())) {
            case 0:
                mPaintA.setStrokeCap(Paint.Cap.BUTT);
                mPaintB.setStrokeCap(Paint.Cap.BUTT);
                break;
            case 1:
                mPaintA.setStrokeCap(Paint.Cap.ROUND);
                mPaintB.setStrokeCap(Paint.Cap.ROUND);
                break;
            case 2:
                mPaintA.setStrokeCap(Paint.Cap.SQUARE);
                mPaintB.setStrokeCap(Paint.Cap.SQUARE);
                break;
        }
        typedArray.recycle();
        setProgress(0, false);
        invalidate();
    }

    public void setMax(int maxProgress) {
        mMaxProgress = maxProgress;
    }

    public int getProgress() {
        return Math.round(getPosition() * mMaxProgress);
    }

    public synchronized void setProgress(int progress) {
        setProgress(progress, progress == 0);
    }

    public void setProgress(int progress, boolean animation) {
        if ((!mIsDragging && !mThumbMoveAnimator.isRunning())) {
            setPosition((float) Math.min(mMaxProgress, progress) / mMaxProgress, animation, false);
        }
    }

    /**
     * @return The current position of thumb in [0..1] range.
     */
    private float getPosition() {
        return mThumbMoveAnimator.isRunning() ? mThumbMoveAnimator.getPosition() : mThumbPosition;
    }

    /**
     * Set current position of thumb.
     *
     * @param pos           The position in [0..1] range.
     * @param moveAnimation Indicate that should show animation when change thumb's position.
     */
    private void setPosition(float pos, boolean moveAnimation, boolean fromUser) {
        if (pos < 0) {
            pos = 0;
        } else if (pos > 1) {
            pos = 1;
        }
        boolean change = getPosition() != pos;

        if (!moveAnimation || !mThumbMoveAnimator.startAnimation(pos)) {
            mThumbPosition = pos;
            invalidate();
        }

        int newValue = getProgress();
        float newPos = getPosition();

        if (change && mOnPositionChangeListener != null) {
            mOnPositionChangeListener.onPositionChanged(this, fromUser, newPos, newValue);
        }
    }

    public void setOnProgressChangeListener(OnProgressChangeListener listener) {
        mOnPositionChangeListener = listener;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);

        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        switch (widthMode) {
            case MeasureSpec.UNSPECIFIED:
                widthSize = getSuggestedMinimumWidth();
                break;
            case MeasureSpec.AT_MOST:
                widthSize = Math.min(widthSize, getSuggestedMinimumWidth());
                break;
            case MeasureSpec.EXACTLY:
                break;
        }

        switch (heightMode) {
            case MeasureSpec.UNSPECIFIED:
                heightSize = getSuggestedMinimumHeight();
                break;
            case MeasureSpec.AT_MOST:
                heightSize = Math.min(heightSize, getSuggestedMinimumHeight());
                break;
            case MeasureSpec.EXACTLY:
                break;
        }
        setMeasuredDimension(widthSize, heightSize);
    }

    @Override
    public int getSuggestedMinimumWidth() {
        return (mThumbFocusRadius) * 4 + getPaddingLeft() + getPaddingRight();
    }

    @Override
    public int getSuggestedMinimumHeight() {
        return (mThumbFocusRadius * 2) + getPaddingTop() + getPaddingBottom();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mDrawRect.left = getPaddingLeft() + mThumbRadius;
        mDrawRect.right = w - getPaddingRight() - mThumbRadius;

        int align = mGravity & Gravity.VERTICAL_GRAVITY_MASK;

        int height = mThumbFocusRadius * 2;
        switch (align) {
            case Gravity.TOP:
                mDrawRect.top = getPaddingTop();
                mDrawRect.bottom = mDrawRect.top + height;
                break;
            case Gravity.BOTTOM:
                mDrawRect.bottom = h - getPaddingBottom();
                mDrawRect.top = mDrawRect.bottom - height;
                break;
            default:
                mDrawRect.top = (h - height) / 2f;
                mDrawRect.bottom = mDrawRect.top + height;
                break;
        }
    }

    private boolean isThumbHit(float x, float y, float radius) {
        float cx = mDrawRect.width() * mThumbPosition + mDrawRect.left;
        float cy = mDrawRect.centerY();

        return x >= cx - radius && x <= cx + radius && y >= cy - radius && y < cy + radius;
    }

    private double distance(float x1, float y1, float x2, float y2) {
        return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        if (!isEnabled()) {
            return false;
        }

        float x = event.getX();
        float y = event.getY();
        float position;

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mIsDragging = isThumbHit(x, y, mThumbTouchRadius > 0 ? mThumbTouchRadius : mThumbRadius);
                mDownPoint.set(x, y);
                if (mIsDragging) {
                    mThumbMoveAnimator.stopAnimation(false);
                    mThumbRadiusAnimator.startAnimation(mThumbFocusRadius);
                    mOnPositionChangeListener.onStartTrackingTouch(this);
                }
                if (getParent() != null) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (distance(mDownPoint.x, mDownPoint.y, x, y) >= mTouchSlop) {
                    mIsMoved = true;
                    mIsDragging = true;
                    position = (x - mDrawRect.left) / mDrawRect.width();
                    setPosition(position, false, true);
                }
                break;
            case MotionEvent.ACTION_UP:
                if (mIsDragging) {
                    mThumbRadiusAnimator.startAnimation(mThumbRadius);
                    mOnPositionChangeListener.onStopTrackingTouch(this);

                    if (getParent() != null) {
                        getParent().requestDisallowInterceptTouchEvent(false);
                    }
                } else {
                    position = (x - mDrawRect.left) / mDrawRect.width();
                    setPosition(position, !mIsMoved, true);
                    mOnPositionChangeListener.onStopTrackingTouch(this);
                }
                mIsMoved = false;
                mIsDragging = false;
                break;
        }
        return true;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float x = mDrawRect.width() * mThumbPosition + mDrawRect.left;
        float y = mDrawRect.centerY();
        int filledPrimaryColor = isEnabled() ? mPrimaryColor : mSecondaryColor;

        if (isEnabled()) {
            canvas.drawLine(mDrawRect.left, mDrawRect.centerY(), mDrawRect.right, mDrawRect.centerY(), mPaintA);

            mPaintB.setColor(filledPrimaryColor);
            canvas.drawLine(mDrawRect.left, mDrawRect.centerY(), x, mDrawRect.centerY(), mPaintB);
        } else {
            float startX = x + mThumbCurrentRadius + mThumbSpaceSize < mDrawRect.right ? x + mThumbCurrentRadius + mThumbSpaceSize : mDrawRect.right;
            canvas.drawLine(startX, mDrawRect.centerY(), mDrawRect.right, mDrawRect.centerY(), mPaintA);

            mPaintB.setColor(filledPrimaryColor);
            float endX = x - mThumbCurrentRadius - mThumbSpaceSize > mDrawRect.left ? x - mThumbCurrentRadius - mThumbSpaceSize : mDrawRect.left;
            canvas.drawLine(mDrawRect.left, mDrawRect.centerY(), endX, mDrawRect.centerY(), mPaintB);
        }
        canvas.drawCircle(x, y, mThumbCurrentRadius, mPaintB);
    }

    private class ThumbRadiusAnimator {
        private ValueAnimator animator;
        private boolean isRunning = false;
        private float mStartRadius;

        private boolean isRunning() {
            return isRunning;
        }

        private boolean startAnimation(int radius) {
            if (mThumbCurrentRadius == radius) {
                return false;
            }
            isRunning = true;
            mStartRadius = mThumbCurrentRadius;
            animator = ValueAnimator.ofFloat(mStartRadius, radius);
            animator.setDuration(duration);
            animator.setInterpolator(mInterpolator);
            animator.addUpdateListener(animation -> {
                mThumbCurrentRadius = (float) animation.getAnimatedValue();
                invalidate();
            });
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    isRunning = false;
                }
            });
            animator.start();
            return true;
        }
    }

    private class ThumbMoveAnimator {

        private ValueAnimator animator;
        private boolean isRunning = false;
        private float mStartPosition;
        private float mPosition;

        private boolean isRunning() {
            return isRunning;
        }

        public float getPosition() {
            return mPosition;
        }

        private boolean startAnimation(float position) {
            if (mThumbPosition == position) {
                return false;
            }
            isRunning = true;
            mStartPosition = mThumbPosition;
            mPosition = position;

            animator = ValueAnimator.ofFloat(mStartPosition, mPosition);
            animator.setDuration(duration);
            animator.setInterpolator(mInterpolator);
            animator.addUpdateListener(animation -> {
                mThumbPosition = (float) animation.getAnimatedValue();
                invalidate();
            });
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    isRunning = false;
                }
            });
            animator.start();
            return true;
        }

        private void stopAnimation(boolean saveEnd) {
            if (isRunning) {
                animator.cancel();
                isRunning = false;
                if (saveEnd) {
                    mThumbPosition = mPosition;
                }
                invalidate();
            }
        }
    }
}
