package com.zuojie.soundrecorder.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.os.Handler;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import com.zuojie.soundrecorder.R;
import com.zuojie.soundrecorder.util.Utils;
import com.zuojie.soundrecorder.widget.helper.PlayPauseDrawable;

/**
 * Created by zuojie on 17-3-8.
 */
public class PlayButton extends AppCompatImageView {

    private int centerY;
    private int centerX;
    private float radius;
    private float stokeWidth;
    private int stokeColor;
    private Paint paint;
    private boolean shortPress;
    private Handler longHandler;
    private boolean effective;
    private PlayPauseDrawable playPauseDrawable;
    private ValueAnimator animator;

    public PlayButton(Context context) {
        super(context);
        init(context, null);
    }

    public PlayButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public PlayButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawCircle(centerX, centerY, radius, paint);
    }

    @Override
    protected void onSizeChanged(int wigth, int heighe, int oldwigth, int oldheighe) {
        super.onSizeChanged(wigth, heighe, oldwigth, oldheighe);
        centerX = wigth / 2;
        centerY = heighe / 2;
        radius = Math.min(wigth, heighe) / 2 - stokeWidth;
    }

    public void setColor(int color) {
        paint.setColor(color);
        invalidate();
    }

    private void init(Context context, AttributeSet attrs) {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Style.STROKE);

        if (attrs != null) {
            final TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.PlayButton);
            stokeWidth = (int) typedArray.getDimension(R.styleable.PlayButton_stokeWidth, Utils.dpToPx(getContext(), 2f));
            stokeColor = typedArray.getColor(R.styleable.PlayButton_stokeColor, Color.WHITE);
            typedArray.recycle();
        }

        paint.setStrokeWidth(stokeWidth);
        paint.setColor(stokeColor);
        playPauseDrawable = new PlayPauseDrawable(getContext(), stokeColor);
        setImageDrawable(playPauseDrawable);
    }

    public void setState(boolean isPlaying, boolean animate) {
        if (isPlaying) {
            playPauseDrawable.setPause(animate);
        } else {
            playPauseDrawable.setPlay(animate);
        }
    }

    private void animateUp() {
        if (animator != null && animator.isRunning()) {
            animator.cancel();
        }
        animator = ValueAnimator.ofFloat(getScaleX(), 1f);
        animator.setDuration(128);
        animator.setInterpolator(new AccelerateInterpolator());
        animator.addUpdateListener(animation -> {
            float value = (float) animation.getAnimatedValue();
            setScaleX(value);
            setScaleY(value);
        });
        animator.start();
    }

    private void animateDown() {
        if (animator != null && animator.isRunning()) {
            animator.cancel();
        }
        animator = ValueAnimator.ofFloat(getScaleX(), 0.9f);
        animator.setDuration(128);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.addUpdateListener(animation -> {
            float value = (float) animation.getAnimatedValue();
            setScaleX(value);
            setScaleY(value);
        });
        animator.start();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                shortPress = true;
                effective = true;
                animateDown();
                longHandler = new Handler();
                longHandler.postDelayed(() -> {
                    shortPress = false;
                    animateUp();
                    if (effective) {
                        performLongClick();
                    }
                }, 500);
                return true;
            }
            case MotionEvent.ACTION_MOVE: {
                effective = false;
                return true;
            }
            case MotionEvent.ACTION_UP: {
                longHandler.removeCallbacksAndMessages(null);
                if (shortPress) {
                    animateUp();
                    performClick();
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    @Override
    public boolean performLongClick() {
        return super.performLongClick();
    }
}
