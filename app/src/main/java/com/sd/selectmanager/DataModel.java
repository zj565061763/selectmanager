package com.sd.selectmanager;

import java.util.ArrayList;
import java.util.List;

public class DataModel {
    public String name;

    public DataModel(String name) {
        this.name = name;
    }

    public static List<DataModel> get(int count) {
        final List<DataModel> list = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            list.add(new DataModel(String.valueOf(i)));
        }
        return list;
    }

    @Override
    public String toString() {
        return name;
    }
}
