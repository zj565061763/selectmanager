package com.fanwe.selectmanager;

import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;

import com.fanwe.lib.selectmanager.FSelectManager;

import java.util.List;

public class ListDemoAdapter extends BaseAdapter
{
    private List<DataModel> mListModel;
    private FSelectManager<DataModel> mSelectManager;

    public ListDemoAdapter(List<DataModel> listModel)
    {
        mListModel = listModel;

        getSelectManager().setMode(FSelectManager.Mode.MULTI); // 设置多选模式
        getSelectManager().setItems(listModel); // 设置数据
    }

    public void addModel(DataModel model)
    {
        mListModel.add(model);
        /**
         * 注意：如果你的数据集发生了变化的话要调用FSelectManager的方法同步数据，更多同步数据的方法见源码
         */
        getSelectManager().appendItem(model);
        notifyDataSetChanged();
    }

    /**
     * 返回FSelectManager
     *
     * @return
     */
    public FSelectManager<DataModel> getSelectManager()
    {
        if (mSelectManager == null)
        {
            mSelectManager = new FSelectManager<DataModel>()
            {
                @Override
                protected void onInitItem(DataModel item)
                {
                    super.onInitItem(item);
                    /**
                     * 如果FSelectManager的数据发生变化，会回调此方法，这边可以同步选中状态
                     * 比如：要把新增item的状态，同步到选择管理器中
                     *
                     * 为了提高同步效率，建议实体实现FSelectManager.Selectable接口
                     * 实现此接口后，当FSelectManager的数据发生变化后，会自动同步新数据的选中状态
                     */
                    mSelectManager.setSelected(item, item.selected);
                }
            };
            mSelectManager.addCallback(new FSelectManager.Callback<DataModel>()
            {
                @Override
                public void onNormal(DataModel item)
                {
                    item.selected = false; // item状态设置为false
                    notifyDataSetChanged();
                }

                @Override
                public void onSelected(DataModel item)
                {
                    item.selected = true; // item状态设置为true
                    notifyDataSetChanged();
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
