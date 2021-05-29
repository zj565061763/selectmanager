package com.sd.lib.selectmanager;

import android.view.View;
import android.view.View.OnClickListener;

public class FSelectViewManager<T extends View> extends FSelectManager<T> implements OnClickListener
{
    @Override
    protected void onInitItem(T item)
    {
        super.onInitItem(item);
        item.setOnClickListener(this);
        item.setSelected(false);
    }

    @Override
    protected void onSelectedChanged(boolean selected, T item)
    {
        super.onSelectedChanged(selected, item);
        item.setSelected(selected);
    }

    @Override
    public void onClick(View v)
    {
        if (getMode() == Mode.SINGLE_MUST_ONE_SELECTED)
        {
            final T selectedItem = getSelectedItem();
            if (selectedItem != null && selectedItem == v)
                onSingleSelectedItemClick(selectedItem);
        }

        performClick((T) v);
    }

    /**
     * 已经选中的Item被点击（单选模式）
     */
    protected void onSingleSelectedItemClick(T item)
    {
    }
}
