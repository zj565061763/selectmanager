package com.fanwe.selectmanager;

import java.util.ArrayList;
import java.util.List;

public class DataModel
{
    public String name;
    public boolean selected;

    public static List<DataModel> get(int count)
    {
        final List<DataModel> list = new ArrayList<>();
        for (int i = 0; i < count; i++)
        {
            DataModel model = new DataModel();
            model.name = String.valueOf(i);
            list.add(model);
        }
        return list;
    }
}
