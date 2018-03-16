package com.fanwe.lib.selectmanager;

import android.view.View;
import android.view.View.OnClickListener;

public class FSelectViewManager<T extends View> extends FSelectManager<T>
{
    private OnClickListener mOnClickListener = new OnClickListener()
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
    protected void onNormal(int index, T item)
    {
        super.onNormal(index, item);
        item.setSelected(false);
    }

    @Override
    protected void onSelected(int index, T item)
    {
        super.onSelected(index, item);
        item.setSelected(true);
    }
}
