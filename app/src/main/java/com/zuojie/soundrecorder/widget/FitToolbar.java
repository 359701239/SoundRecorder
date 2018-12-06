package com.zuojie.soundrecorder.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.view.WindowInsets;

/**
 * Created by zuojie on 17-11-30.
 */

public class FitToolbar extends Toolbar {

    public FitToolbar(Context context) {
        super(context);
    }

    public FitToolbar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public FitToolbar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public WindowInsets onApplyWindowInsets(WindowInsets insets) {
        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) layoutParams;
            marginLayoutParams.topMargin = insets.getSystemWindowInsetTop();
        }
        return super.onApplyWindowInsets(insets);
    }
}
