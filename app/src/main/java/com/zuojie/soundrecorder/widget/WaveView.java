package com.zuojie.soundrecorder.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;
import com.zuojie.soundrecorder.R;
import com.zuojie.soundrecorder.util.Utils;

/**
 * Created by zuojie on 2018/12/07.
 */
public class WaveView extends View {

    private Context mContext;
    private int A;
    private int K;
    private int waveColor = 0xAAFF7E37;
    private float φ;
    private float waveSpeed = 5f;
    private double ω;
    private double startPeriod;
    private boolean waveStart;

    private Path path;
    private Paint paint;

    private static final int SIN = 0;
    private static final int COS = 1;

    private int waveType;

    private static final int TOP = 0;
    private static final int BOTTOM = 1;

    private int waveFillType;

    private ValueAnimator valueAnimator;

    public WaveView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        getAttr(context, attrs);
        K = A;
        initPaint();

        initAnimation();
    }

    private void getAttr(Context context, AttributeSet attrs) {
        TypedArray typedArray = mContext.obtainStyledAttributes(attrs, R.styleable.WaveView);

        waveType = typedArray.getInt(R.styleable.WaveView_waveType, SIN);
        waveFillType = typedArray.getInt(R.styleable.WaveView_waveFillType, BOTTOM);
        A = typedArray.getDimensionPixelOffset(R.styleable.WaveView_waveAmplitude, Utils.dpToPx(context, 10));
        waveColor = typedArray.getColor(R.styleable.WaveView_waveColor, waveColor);
        waveSpeed = typedArray.getFloat(R.styleable.WaveView_waveSpeed, waveSpeed);
        startPeriod = typedArray.getFloat(R.styleable.WaveView_waveStartPeriod, 0);
        waveStart = typedArray.getBoolean(R.styleable.WaveView_waveStart, false);

        typedArray.recycle();
    }

    private void initPaint() {
        path = new Path();
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setColor(waveColor);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        ω = 2 * Math.PI / getWidth();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        switch (waveType) {
            case SIN:
                drawSin(canvas);
                break;
            case COS:
                drawCos(canvas);
                break;
        }
    }

    private void drawCos(Canvas canvas) {
        switch (waveFillType) {
            case TOP:
                fillTop(canvas);
                break;
            case BOTTOM:
                fillBottom(canvas);
                break;
        }
    }

    private void drawSin(Canvas canvas) {
        switch (waveFillType) {
            case TOP:
                fillTop(canvas);
                break;
            case BOTTOM:
                fillBottom(canvas);
                break;
        }
    }

    private void fillTop(Canvas canvas) {
        φ -= waveSpeed / 100;
        float y;

        path.reset();
        path.moveTo(0, getHeight());

        for (float x = 0; x <= getWidth(); x += 20) {
            y = (float) (A * Math.sin(ω * x + φ + Math.PI * startPeriod) + K);
            path.lineTo(x, getHeight() - y);
        }

        path.lineTo(getWidth(), 0);
        path.lineTo(0, 0);
        path.close();
        canvas.drawPath(path, paint);
    }

    private void fillBottom(Canvas canvas) {
        φ -= waveSpeed / 100;
        float y;

        path.reset();
        path.moveTo(0, 0);

        for (float x = 0; x <= getWidth(); x += 20) {
            y = (float) (A * Math.sin(ω * x + φ + Math.PI * startPeriod) + K);
            path.lineTo(x, y);
        }

        path.lineTo(getWidth(), getHeight());
        path.lineTo(0, getHeight());
        path.close();

        canvas.drawPath(path, paint);
    }

    private void initAnimation() {
        valueAnimator = ValueAnimator.ofInt(0, getWidth());
        valueAnimator.setDuration(1000);
        valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.addUpdateListener(animation -> invalidate());
        if (waveStart) {
            valueAnimator.start();
        }
    }

    public void startAnimation() {
        if (valueAnimator != null) {
            valueAnimator.start();
        }
    }

    public void stopAnimation() {
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
    }

    public void pauseAnimation() {
        if (valueAnimator != null) {
            if (valueAnimator.isStarted()) {
                valueAnimator.pause();
            }
        }
    }

    public void resumeAnimation() {
        if (valueAnimator != null) {
            if (valueAnimator.isStarted()) {
                valueAnimator.resume();
            } else {
                valueAnimator.start();
            }
        }
    }
}