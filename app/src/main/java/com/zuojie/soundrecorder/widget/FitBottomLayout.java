package com.zuojie.soundrecorder.widget;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.WindowInsets;
import android.widget.LinearLayout;

/**
 * Created by zuojie on 17-3-8.
 */

public class FitBottomLayout extends LinearLayout {

    public FitBottomLayout(@NonNull Context context) {
        super(context);
    }

    public FitBottomLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public FitBottomLayout(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public WindowInsets onApplyWindowInsets(WindowInsets insets) {
        int bottom = insets.getSystemWindowInsetBottom();
        if (bottom > 0) {
            setPadding(getPaddingLeft(), getPaddingTop(), getPaddingRight(), getPaddingBottom() + insets.getSystemWindowInsetBottom());
        }
        return super.onApplyWindowInsets(insets);
    }
}
