package com.sd.lib.selectmanager;

import android.view.View;
import android.view.View.OnClickListener;

public class FSelectViewManager<T extends View> extends FSelectManager<T>
{
    private final OnClickListener mOnClickListener = new OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            performClick((T) v);
        }
    };

    @Override
    protected void onInitItem(T item)
    {
        super.onInitItem(item);
        item.setOnClickListener(mOnClickListener);
        item.setSelected(false);
    }

    @Override
    protected void onSelectedChanged(boolean selected, T item)
    {
        super.onSelectedChanged(selected, item);
        item.setSelected(selected);
    }
}
