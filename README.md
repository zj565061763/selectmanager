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

1. 创建SelectManager
```java
private final SelectManager<Button> mSelectManager = new FSelectManager<>();
```

2. 添加选中状态变化回调
```java
mSelectManager.addCallback(new SelectManager.Callback<Button>()
{
    @Override
    public void onSelectedChanged(boolean selected, Button item)
    {
        if (selected)
            item.setTextColor(Color.RED);
        else
            item.setTextColor(Color.BLACK);

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
                    mSelectManager.setMode(SelectManager.Mode.SINGLE);
                    break;
                case R.id.rb_single_must:
                    // 设置单选必选模式，这种模式是默认的模式
                    mSelectManager.setMode(SelectManager.Mode.SINGLE_MUST_ONE_SELECTED);
                    break;
                case R.id.rb_multi:
                    // 设置多选模式
                    mSelectManager.setMode(SelectManager.Mode.MULTI);
                    break;
                case R.id.rb_multi_must:
                    // 设置多选必选模式
                    mSelectManager.setMode(SelectManager.Mode.MULTI_MUST_ONE_SELECTED);
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

6. 获得选中的item
```java
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
```

# 列表中效果
![](http://thumbsnap.com/i/JFJpyuU1.gif?0522)
<br>

如果在列表中用SelectManager的话，要注意列表的数据改变(增加，删除，修改)的时候，要和SelectManager进行同步：
1. 数据的同步，即SelectManager中的item列表要和用户adapter中的item列表数据一致
2. 选中状态的同步，比如adapter新增了一项数据是选中的，那么也要把这一项的选中状态同步给SelectManager

针对第一个问题，SelectManager中提供了一系列操作数据的方法，可以看底部的SelectManager接口<br>
针对第二个问题，只要让你的item实体实现SelectManager.Selectable接口既可<br>
<br>
下面是demo：<br>

1. 创建实体
```java
public class DataModel implements SelectManager.Selectable
{
    public String name;
    public boolean selected;

    @Override
    public boolean isSelected()
    {
        return selected;
    }

    @Override
    public void setSelected(boolean selected)
    {
        this.selected = selected;
    }
}
```

2. 创建Adapter <br>
为了方便演示adapter数据变更的问题，此处用了作者的另一个adapter库来演示，可以获得adapter的数据持有者DataHolder来监听数据的变化

```java
public class ListDemoAdapter extends FSimpleAdapter<DataModel>
{
    private SelectManager<DataModel> mSelectManager;

    public ListDemoAdapter(Activity activity)
    {
        super(activity);
        /**
         * 设置多选模式
         */
        getSelectManager().setMode(SelectManager.Mode.MULTI);

        /**
         * adapter 数据变化监听
         */
        getDataHolder().addDataChangeCallback(new DataHolder.DataChangeCallback<DataModel>()
        {
            @Override
            public void onDataChanged(List<DataModel> list)
            {
                /**
                 * 同步数据到SelectManager
                 */
                getSelectManager().setItems(list);
            }

            @Override
            public void onDataChanged(int index, DataModel data)
            {
                /**
                 * 同步数据到SelectManager
                 */
                getSelectManager().updateItem(index, data);
            }

            @Override
            public void onDataAdded(int index, List<DataModel> list)
            {
                /**
                 * 同步数据到SelectManager
                 */
                getSelectManager().addItems(index, list);
            }

            @Override
            public void onDataRemoved(int index, DataModel data)
            {
                /**
                 * 同步数据到SelectManager
                 */
                getSelectManager().removeItem(data);
            }
        });
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
                    /**
                     * 由于item实现了SelectManager.Selectable接口，所以这一句不用执行
                     */
                    // item.setSelected(selected);

                    /**
                     * 选中状态变化通知刷新adapter
                     */
                    notifyDataSetChanged();
                }
            });
        }
        return mSelectManager;
    }

    @Override
    public void onBindData(int position, View convertView, ViewGroup parent, final DataModel model)
    {
        Button button = get(R.id.btn, convertView);
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
                /**
                 * 模拟点击该项，触发选中状态变更
                 */
                getSelectManager().performClick(model);
            }
        });
    }

    @Override
    public int getLayoutId(int position, View convertView, ViewGroup parent)
    {
        /**
         * 返回item的布局
         */
        return R.layout.item_listview;
    }
}
```

3. 得到选中的数据
```java
public class ListDemoActivity extends AppCompatActivity
{
    private ListView mListView;
    private ListDemoAdapter mAdapter;

    private TextView tv_selected_info;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_demo);
        mListView = findViewById(R.id.lv_content);
        tv_selected_info = findViewById(R.id.tv_selected_info);

        mAdapter = new ListDemoAdapter(this);
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
        mListView.setAdapter(mAdapter);

        for (int i = 0; i < 50; i++)
        {
            DataModel model = new DataModel();
            model.name = String.valueOf(i);
            mAdapter.getDataHolder().addData(model); // 向adapter添加数据
        }
    }
}
```
