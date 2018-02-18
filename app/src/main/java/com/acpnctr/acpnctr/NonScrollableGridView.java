package com.acpnctr.acpnctr;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.GridView;

/**
 * Custom GridView for displaying Pulse Types in {@link PulsesActivity}
 */

public class NonScrollableGridView extends GridView {

    public NonScrollableGridView(Context context) {
        super(context);
    }
    public NonScrollableGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public NonScrollableGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int heightMeasureSpec_custom = MeasureSpec.makeMeasureSpec(
                Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec_custom);
        ViewGroup.LayoutParams params = getLayoutParams();
        params.height = getMeasuredHeight();
    }
}
