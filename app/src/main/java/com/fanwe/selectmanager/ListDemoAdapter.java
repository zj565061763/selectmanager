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

    /**
     * 返回选择管理器
     *
     * @return
     */
    public FSelectManager<DataModel> getSelectManager()
    {
        if (mSelectManager == null)
        {
            mSelectManager = new FSelectManager<>();
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
