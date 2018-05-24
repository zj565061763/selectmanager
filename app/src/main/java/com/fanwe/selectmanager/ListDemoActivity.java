package com.fanwe.selectmanager;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.TextView;

import com.fanwe.lib.selectmanager.SelectManager;

import java.util.List;

public class ListDemoActivity extends AppCompatActivity
{
    private ListView mListView;
    private TextView mTvSelectedInfo;
    private ListDemoAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_demo);
        mListView = findViewById(R.id.lv_content);
        mTvSelectedInfo = findViewById(R.id.tv_selected_info);

        mAdapter = new ListDemoAdapter(DataModel.get(50));
        mAdapter.getSelectManager().addCallback(new SelectManager.Callback<DataModel>()
        {
            @Override
            public void onSelectedChanged(boolean selected, DataModel item)
            {
                updateSelectedInfo();
            }
        });
        mListView.setAdapter(mAdapter);
    }

    /**
     * 更新选中的信息
     */
    private void updateSelectedInfo()
    {
        String info = "";
        if (mAdapter.getSelectManager().getMode().isSingleType())
        {
            final DataModel model = mAdapter.getSelectManager().getSelectedItem(); // 获得选中的项
            if (model != null)
                info = model.name;
        } else
        {
            final List<DataModel> models = mAdapter.getSelectManager().getSelectedItems(); // 获得选中的项
            for (DataModel item : models)
            {
                info += item.name;
                info += ",";
            }
        }
        mTvSelectedInfo.setText(info);
    }
}
