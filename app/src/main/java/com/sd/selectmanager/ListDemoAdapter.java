package com.sd.selectmanager;

import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.sd.lib.adapter.FSimpleAdapter;
import com.sd.lib.adapter.data.DataHolder;
import com.sd.lib.selectmanager.FSelectManager;
import com.sd.lib.selectmanager.SelectManager;

import java.util.List;

public class ListDemoAdapter extends FSimpleAdapter<DataModel> {
    private SelectManager<DataModel> mSelectManager;

    public ListDemoAdapter() {
        // adapter数据变化监听
        getDataHolder().addDataChangeCallback(new DataHolder.DataChangeCallback<DataModel>() {
            @Override
            public void onDataChanged(List<DataModel> list) {
                // 同步数据到SelectManager
                getSelectManager().setItems(list);
            }

            @Override
            public void onDataChanged(int index, DataModel data) {
                // 同步数据到SelectManager
                getSelectManager().updateItem(index, data);
            }

            @Override
            public void onDataAdded(int index, List<DataModel> list) {
                // 同步数据到SelectManager
                getSelectManager().addItems(index, list);
            }

            @Override
            public void onDataRemoved(int index, DataModel data) {
                // 同步数据到SelectManager
                getSelectManager().removeItem(data);
            }
        });
    }

    public SelectManager<DataModel> getSelectManager() {
        if (mSelectManager == null) {
            mSelectManager = new FSelectManager<>();
            // 设置多选模式
            mSelectManager.setMode(SelectManager.Mode.MULTI);
            // 设置item初始化回调
            mSelectManager.setOnItemInitCallback(new SelectManager.OnItemInitCallback<DataModel>() {
                @Override
                public void onInitItem(DataModel item) {
                    /**
                     * 假如添加的item有初始选中状态，则要在此同步选中状态到SelectManager中：mSelectManager.setSelected(item, selected);
                     */
                }
            });
            // 选中变化回调
            mSelectManager.addCallback(new SelectManager.Callback<DataModel>() {
                @Override
                public void onSelectedChanged(boolean selected, DataModel item) {
                    // 选中状态变化通知刷新adapter
                    notifyDataSetChanged();
                }
            });
        }
        return mSelectManager;
    }

    @Override
    public void onBindData(int position, View convertView, ViewGroup parent, final DataModel model) {
        Button button = get(R.id.btn, convertView);
        button.setText(model.name);

        if (getSelectManager().isSelected(model)) {
            button.setTextColor(Color.RED);
        } else {
            button.setTextColor(Color.BLACK);
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 模拟点击该项，触发选中状态变更
                getSelectManager().performClick(model);
            }
        });
    }

    @Override
    public int getLayoutId(int position, View convertView, ViewGroup parent) {
        // 返回item的布局
        return R.layout.item_listview;
    }
}
