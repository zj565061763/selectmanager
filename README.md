# About
对选中和非选中状态进行了封装，可以方便的实现状态管理<br>
当前支持的模式：<br>
* 单选
* 单选必选
* 多选
* 多选必选

# Gradle
`implementation 'com.fanwe.android:selectmanager:1.0.9'`

# 简单效果
![](http://thumbsnap.com/i/sodYq9ca.gif?0522)
![](http://thumbsnap.com/i/vKHV9N5l.gif?0522)
![](http://thumbsnap.com/i/SQrJHAoa.gif?0522)
![](http://thumbsnap.com/i/Z9rxk88m.gif?0522)
<br>

1. 创建选择管理器
```java
private final FSelectManager<Button> mSelectManager = new FSelectManager<>();
```

2. 添加选中状态变化回调
```java
mSelectManager.addCallback(new FSelectManager.Callback<Button>()
{
    @Override
    public void onNormal(Button item)
    {
        // 状态正常回调
        item.setTextColor(Color.BLACK);
        updateSelectedInfo();
    }

    @Override
    public void onSelected(Button item)
    {
        // 状态选中回调
        item.setTextColor(Color.RED);
        updateSelectedInfo();
    }
});
```

3. 设置要管理的数据
```java
mSelectManager.setItems(btn_0, btn_1, btn_2);
```

4. 设置模式
```java
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
```

5. 触发状态变化
```java
public void onClick(View v)
{
    /**
     * 模拟点击某一项
     */
    mSelectManager.performClick((Button) v);

    /**
     * 选中某一项
     */
 // mSelectManager.setSelected(btn_0, true);
}
```

# 列表中效果
![](http://thumbsnap.com/i/JFJpyuU1.gif?0522)

1. 创建实体
```java
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
```

2. 创建Adapter
adapter的写法仅仅用来演示，性能的问题请开发者自己进行优化
```java
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
```

3. 外部监听数据
```java
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
        mAdapter.getSelectManager().addCallback(new FSelectManager.Callback<DataModel>()
        {
            @Override
            public void onNormal(DataModel item)
            {
                updateSelectedInfo();
            }

            @Override
            public void onSelected(DataModel item)
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
        if (mAdapter.getSelectManager().isSingleMode())
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
```
