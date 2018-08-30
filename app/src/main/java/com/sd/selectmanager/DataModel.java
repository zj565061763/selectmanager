package com.sd.selectmanager;

import com.sd.lib.selectmanager.SelectManager;

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
