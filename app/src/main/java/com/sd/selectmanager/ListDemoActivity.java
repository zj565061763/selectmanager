package com.sd.selectmanager;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.sd.lib.selectmanager.SelectManager;

import java.util.List;

public class ListDemoActivity extends AppCompatActivity {
    private ListView mListView;
    private final ListDemoAdapter mAdapter = new ListDemoAdapter();
    private TextView tv_selected_info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_demo);
        mListView = findViewById(R.id.lv_content);
        tv_selected_info = findViewById(R.id.tv_selected_info);
        mListView.setAdapter(mAdapter);

        mAdapter.getSelectManager().addCallback(new SelectManager.Callback<DataModel>() {
            @Override
            public void onSelectedChanged(boolean selected, DataModel item) {
                if (mAdapter.getSelectManager().getMode().isSingleType()) {
                    // 得到当前选中的item数据
                    final DataModel selectedItem = mAdapter.getSelectManager().getSelectedItem();
                    if (selectedItem != null) {
                        tv_selected_info.setText(selectedItem.toString());
                    }
                } else {
                    // 得到当前选中的item数据
                    final List<DataModel> listSelected = mAdapter.getSelectManager().getSelectedItems();
                    tv_selected_info.setText(TextUtils.join(",", listSelected));
                }
            }
        });

        // 设置数据
        mAdapter.getDataHolder().setData(DataModel.get(50));
    }
}
