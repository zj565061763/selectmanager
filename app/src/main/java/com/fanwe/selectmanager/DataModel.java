package com.fanwe.selectmanager;

import com.fanwe.lib.selectmanager.SelectManager;

public class DataModel implements SelectManager.Selectable
{
    public String name;
    public boolean selected;

    @Override
    public boolean isSelected()
    {
        return selected;
    }

    @Override
    public void setSelected(boolean selected)
    {
        this.selected = selected;
    }

    @Override
    public String toString()
    {
        return name;
    }
}
