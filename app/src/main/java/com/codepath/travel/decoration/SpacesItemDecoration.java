package com.codepath.travel.decoration;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/*

Decorator which adds spacing around the tiles in a Grid layout RecyclerView. Apply to a RecyclerView with:

    SpacesItemDecoration decoration = new SpacesItemDecoration(16);
    mRecyclerView.addItemDecoration(decoration);

Feel free to add any value you wish for SpacesItemDecoration. That value determines the amount of spacing.

Source: http://blog.grafixartist.com/pinterest-masonry-layout-staggered-grid/

*/
public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
    private final int mSpace;
    public SpacesItemDecoration(int space) {
        this.mSpace = space;
    }
    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.left = mSpace;
        outRect.right = mSpace;
        outRect.bottom = mSpace;
        outRect.top = mSpace;
    }
}
