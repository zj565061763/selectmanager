package com.sd.selectmanager;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.ListView;
import android.widget.TextView;

import com.sd.lib.selectmanager.SelectManager;

import java.util.List;

public class ListDemoActivity extends AppCompatActivity
{
    private ListView mListView;
    private ListDemoAdapter mAdapter = new ListDemoAdapter();

    private TextView tv_selected_info;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_demo);
        mListView = findViewById(R.id.lv_content);
        tv_selected_info = findViewById(R.id.tv_selected_info);
        mListView.setAdapter(mAdapter);

        mAdapter.getSelectManager().addCallback(new SelectManager.Callback<DataModel>()
        {
            @Override
            public void onSelectedChanged(boolean selected, DataModel item)
            {
                if (mAdapter.getSelectManager().getMode().isSingleType())
                {
                    /**
                     * 得到当前选中的item数据
                     */
                    DataModel selectedItem = mAdapter.getSelectManager().getSelectedItem();

                    if (selectedItem != null)
                        tv_selected_info.setText(selectedItem.toString());
                } else
                {
                    /**
                     * 得到当前选中的item数据
                     */
                    List<DataModel> listSelected = mAdapter.getSelectManager().getSelectedItems();

                    tv_selected_info.setText(TextUtils.join(",", listSelected));
                }
            }
        });

        for (int i = 0; i < 50; i++)
        {
            DataModel model = new DataModel();
            model.name = String.valueOf(i);
            mAdapter.getDataHolder().addData(model); // 向adapter添加数据
        }
    }
}
