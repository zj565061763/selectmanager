package com.fanwe.selectmanager;

import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;

import com.fanwe.lib.selectmanager.FSelectManager;
import com.fanwe.lib.selectmanager.SelectManager;

import java.util.List;

public class ListDemoAdapter extends BaseAdapter
{
    private List<DataModel> mListModel;
    private SelectManager<DataModel> mSelectManager;

    public ListDemoAdapter(List<DataModel> listModel)
    {
        mListModel = listModel;

        getSelectManager().setMode(SelectManager.Mode.MULTI); // 设置多选模式
        getSelectManager().setItems(listModel); // 设置数据
    }

    public void addModel(DataModel model)
    {
        mListModel.add(model);
        /**
         * 注意：如果你的数据集发生了变化的话要调用SelectManager的方法同步数据，更多同步数据的方法见源码
         */
        getSelectManager().appendItem(model);
        notifyDataSetChanged();
    }

    /**
     * 返回SelectManager
     *
     * @return
     */
    public SelectManager<DataModel> getSelectManager()
    {
        if (mSelectManager == null)
        {
            mSelectManager = new FSelectManager<>();
            mSelectManager.addCallback(new SelectManager.Callback<DataModel>()
            {
                @Override
                public void onSelectedChanged(boolean selected, DataModel item)
                {
                    item.selected = selected;
                    notifyDataSetChanged();
                }
            });

            /**
             * 设置item初始化回调对象
             * 如果SelectManager的数据发生变化，会回调此对象，这边可以同步选中状态
             * 比如：要把新增item的状态，同步到选择管理器中
             *
             * 如果item实现了SelectManager.Selectable接口，那么：
             * 1. SelectManager数据发生变化后，会自动同步新数据的选中状态
             * 2. 同步效率比较高，同步选中状态的时候不会判断是否包含该item
             */
            mSelectManager.setOnItemInitCallback(new SelectManager.OnItemInitCallback<DataModel>()
            {
                @Override
                public void onInitItem(DataModel item)
                {
                    /**
                     * demo这边为了演示，手动同步选中状态，建议item实现SelectManager.Selectable接口
                     */
                    mSelectManager.setSelected(item, item.selected);
                }
            });
        }
        return mSelectManager;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        final Button button = new Button(parent.getContext());
        final DataModel model = mListModel.get(position);

        button.setText(model.name);
        if (model.selected)
            button.setTextColor(Color.RED);
        else
            button.setTextColor(Color.BLACK);

        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                getSelectManager().performClick(model); // 模拟点击该项
            }
        });

        return button;
    }

    @Override
    public int getCount()
    {
        return mListModel.size();
    }

    @Override
    public Object getItem(int position)
    {
        return mListModel.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }
}
