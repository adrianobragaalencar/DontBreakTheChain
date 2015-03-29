package com.mobeta.android.dslv;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;

/**
 * Simple implementation of the FloatViewManager class. Uses list
 * items as they appear in the ListView to create the floating View.
 */
public class SimpleFloatViewManager implements DragSortListView.FloatViewManager {

    private Bitmap mFloatBitmap;
    private ImageView mImageView;
    private int mFloatBGColor = Color.BLACK;
    private final ListView mListView;

    public SimpleFloatViewManager(ListView lv) {
        mListView = lv;
    }

    public void setBackgroundColor(int color) {
        mFloatBGColor = color;
    }

    /**
     * This simple implementation creates a Bitmap copy of the
     * list item currently shown at ListView <code>position</code>.
     */
    @Override
    public View onCreateFloatView(int position) {
        // Guaranteed that this will not be null? I think so. Nope, got
        // a NullPointerException once...
        final View view = mListView.getChildAt(position + mListView.getHeaderViewsCount() - mListView.getFirstVisiblePosition());
        if (view == null) {
            return null;
        }
        view.setPressed(false);

        // Create a copy of the drawing cache so that it does not get
        // recycled by the framework when the list tries to clean up memory
        //v.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        view.setDrawingCacheEnabled(true);
        mFloatBitmap = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);

        if (mImageView == null) {
            mImageView = new ImageView(mListView.getContext());
        }
        mImageView.setBackgroundColor(mFloatBGColor);
        mImageView.setPadding(0, 0, 0, 0);
        mImageView.setImageBitmap(mFloatBitmap);
        mImageView.setLayoutParams(new ViewGroup.LayoutParams(view.getWidth(), view.getHeight()));

        return mImageView;
    }

    /*
     * (non-Javadoc)
     * @see com.mobeta.android.dslv.DragSortListView.FloatViewManager#onDragFloatView(android.view.View, android.graphics.Point, android.graphics.Point)
     */
    @Override
    public void onDragFloatView(View floatView, Point position, Point touch) {
    }

    /*
     * (non-Javadoc)
     * @see com.mobeta.android.dslv.DragSortListView.FloatViewManager#onDestroyFloatView(android.view.View)
     */
    @Override
    public void onDestroyFloatView(View floatView) {
        ((ImageView) floatView).setImageDrawable(null);
        mFloatBitmap.recycle();
        mFloatBitmap = null;
    }
}

