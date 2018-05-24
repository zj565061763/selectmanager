package com.fanwe.selectmanager;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.fanwe.lib.selectmanager.FSelectManager;

import java.util.List;

public class SimpleDemoActivity extends AppCompatActivity implements View.OnClickListener
{
    private RadioGroup rg_mode;
    private Button btn_0, btn_1, btn_2;
    private TextView tv_selected_info;

    private final FSelectManager<Button> mSelectManager = new FSelectManager<>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_demo);
        rg_mode = findViewById(R.id.rg_mode);
        btn_0 = findViewById(R.id.btn_0);
        btn_1 = findViewById(R.id.btn_1);
        btn_2 = findViewById(R.id.btn_2);
        tv_selected_info = findViewById(R.id.tv_selected_info);
        btn_0.setOnClickListener(this);
        btn_1.setOnClickListener(this);
        btn_2.setOnClickListener(this);
        initRadioGroup();

        initSelectManager();
    }

    private void initSelectManager()
    {
        // 添加状态变更回调
        mSelectManager.addCallback(new FSelectManager.Callback<Button>()
        {
            @Override
            public void onSelectedChanged(boolean selected, Button item)
            {
                if (selected)
                {
                    item.setTextColor(Color.RED);
                } else
                {
                    item.setTextColor(Color.BLACK);
                }
                updateSelectedInfo();
            }
        });
        mSelectManager.setItems(btn_0, btn_1, btn_2); // 设置要管理的数据
    }

    @Override
    public void onClick(View v)
    {
        /**
         * 模拟点击某一项
         */
        mSelectManager.performClick((Button) v);

        /**
         * 选中某一项
         */
//        mSelectManager.setSelected(btn_0, true);
    }

    private void initRadioGroup()
    {
        rg_mode.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId)
            {
                switch (checkedId)
                {
                    case R.id.rb_single:
                        // 设置单选模式
                        mSelectManager.setMode(FSelectManager.Mode.SINGLE);
                        break;
                    case R.id.rb_single_must:
                        // 设置单选必选模式，这种模式是默认的模式
                        mSelectManager.setMode(FSelectManager.Mode.SINGLE_MUST_ONE_SELECTED);
                        break;
                    case R.id.rb_multi:
                        // 设置多选模式
                        mSelectManager.setMode(FSelectManager.Mode.MULTI);
                        break;
                    case R.id.rb_multi_must:
                        // 设置多选必选模式
                        mSelectManager.setMode(FSelectManager.Mode.MULTI_MUST_ONE_SELECTED);
                        break;
                }
            }
        });
    }

    /**
     * 更新选中的信息
     */
    private void updateSelectedInfo()
    {
        String info = "";
        if (mSelectManager.getMode().isSingleType())
        {
            final Button button = mSelectManager.getSelectedItem(); // 获得选中的项
            if (button != null)
                info = button.getText().toString();
        } else
        {
            final List<Button> buttons = mSelectManager.getSelectedItems(); // 获得选中的项
            for (Button item : buttons)
            {
                info += item.getText().toString();
                info += "\r\n";
            }
        }
        tv_selected_info.setText(info);
    }
}
