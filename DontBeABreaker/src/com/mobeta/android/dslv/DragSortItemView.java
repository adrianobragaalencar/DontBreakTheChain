package com.mobeta.android.dslv;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

/**
 * Lightweight ViewGroup that wraps list items obtained from user's
 * ListAdapter. ItemView expects a single child that has a definite
 * height (i.e. the child's layout height is not MATCH_PARENT).
 * The width of
 * ItemView will always match the width of its child (that is,
 * the width MeasureSpec given to ItemView is passed directly
 * to the child, and the ItemView measured width is set to the
 * child's measured width). The height of ItemView can be anything;
 * the 
 * 
 *
 * The purpose of this class is to optimize slide
 * shuffle animations.
 */
public class DragSortItemView extends ViewGroup {

    private int mGravity = Gravity.TOP;

    public DragSortItemView(Context context) {
        super(context);
        // always init with standard ListView layout params
        setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
                									 ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    public void setGravity(int gravity) {
        mGravity = gravity;
    }

    /*
     * (non-Javadoc)
     * @see android.view.ViewGroup#onLayout(boolean, int, int, int, int)
     */
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        final View child = getChildAt(0);
        if (child == null) {
            return;
        }
        if (mGravity == Gravity.TOP) {
            child.layout(0, 0, getMeasuredWidth(), child.getMeasuredHeight());
        } else {
            child.layout(0, getMeasuredHeight() - child.getMeasuredHeight(), getMeasuredWidth(), getMeasuredHeight());
        }
    }

    /*
     * (non-Javadoc)
     * @see android.view.View#onMeasure(int, int)
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        
        final int height;
        final int width      = MeasureSpec.getSize(widthMeasureSpec);
        final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        final View child     = getChildAt(0);
        if (child == null) {
            setMeasuredDimension(0, width);
            return;
        }
        if (child.isLayoutRequested()) {
            // Always let child be as tall as it wants.
            measureChild(child, widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
        }
        if (heightMode == MeasureSpec.UNSPECIFIED) {
            ViewGroup.LayoutParams lp = getLayoutParams();
            height                    = (lp.height > 0) ? lp.height : child.getMeasuredHeight();
        } else {
        	height                    = MeasureSpec.getSize(heightMeasureSpec);
        }
        setMeasuredDimension(width, height);
    }
}
