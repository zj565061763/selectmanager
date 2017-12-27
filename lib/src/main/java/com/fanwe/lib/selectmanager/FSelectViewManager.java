package com.fanwe.lib.selectmanager;

import android.view.View;
import android.view.View.OnClickListener;

public class FSelectViewManager<T extends View> extends FSelectManager<T>
{
    @Override
    protected void initItem(int index, T item)
    {
        item.setOnClickListener(mOnClickListener);
        notifyNormal(index, item);
        super.initItem(index, item);
    }

    private OnClickListener mOnClickListener = new OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            performClick((T) v);
        }
    };

    @Override
    protected void notifyNormal(int index, T item)
    {
        item.setSelected(false);
        super.notifyNormal(index, item);
    }

    @Override
    protected void notifySelected(int index, T item)
    {
        item.setSelected(true);
        super.notifySelected(index, item);
    }
}
