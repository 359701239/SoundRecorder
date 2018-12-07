package com.zuojie.soundrecorder.widget;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Outline;
import android.support.v7.widget.AppCompatImageButton;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewOutlineProvider;

/**
 * Created by zuojie on 17-3-8.
 */
public class CircleImageButton extends AppCompatImageButton {

    private Context context;

    public CircleImageButton(Context context) {
        super(context);
        init(context);
    }

    public CircleImageButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public CircleImageButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        this.context = context;
        setClipToOutline(true);
        setOutlineProvider(new CircleOutlineProvider());
    }

    public class CircleOutlineProvider extends ViewOutlineProvider {
        @Override
        public void getOutline(View view, Outline outline) {
            int width = view.getWidth();
            int height = view.getHeight();
            int insetWidth = (int) (width - context.getResources().getDimension(Resources.getSystem().getIdentifier("button_inset_horizontal_material", "dimen", "android")) * 2);
            int insetHeight = (int) (height - context.getResources().getDimension(Resources.getSystem().getIdentifier("button_inset_vertical_material", "dimen", "android")) * 2);
            int oval = Math.min(insetWidth, insetHeight);

            outline.setOval((width - oval) / 2,
                    (height - oval) / 2,
                    width - (width - oval) / 2,
                    height - (height - oval) / 2);
        }
    }
}
