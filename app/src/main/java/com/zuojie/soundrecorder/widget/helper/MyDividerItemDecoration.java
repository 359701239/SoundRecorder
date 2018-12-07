package com.zuojie.soundrecorder.widget.helper;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.DividerItemDecoration;
import com.zuojie.soundrecorder.R;

/**
 * Created by zuojie on 2018/12/07.
 */
public class MyDividerItemDecoration extends DividerItemDecoration {
    public MyDividerItemDecoration(Context context, int orientation) {
        super(context, orientation);
        Drawable drawable = context.getDrawable(R.drawable.bg_item_divider);
        if (drawable != null) {
            setDrawable(drawable);
        }
    }
}
