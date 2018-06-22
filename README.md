# About
对选中和非选中状态进行了封装，可以方便的实现状态管理<br>
当前支持的模式：<br>
* 单选
* 单选必选
* 多选
* 多选必选

# Gradle
[![](https://jitpack.io/v/zj565061763/selectmanager.svg)](https://jitpack.io/#zj565061763/selectmanager)

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

针对第一个问题，SelectManager中提供了一系列操作数据的方法，可以看下面的demo<br>
针对第二个问题，有以下两种实现方案：
1. 让item实体实现SelectManager.Selectable接口既可，这里会有一个冲突，就是单选模式情况下，旧数据已经有选中的item，这时候
新数据中也有选中的item，这种情况下，同步后，选中的就是新的item<br>
2. 如果item实体实现不想SelectManager.Selectable接口，那么可以给SelectManager设置一个SelectManager.OnItemInitCallback对象，
SelectManager会在数据变更的时候通知这个回调对象，可以在这个回调里面同步选中状态：
```java
mSelectManager.setOnItemInitCallback(new SelectManager.OnItemInitCallback<DataModel>()
{
    @Override
    public void onInitItem(DataModel item)
    {
        /**
         * 手动同步item的选中状态到SelectManager中
         */
        mSelectManager.setSelected(item, item.isSelected());
    }
});
```

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
为了方便演示adapter数据变更的问题，此处用了作者的另一个[adapter](https://github.com/zj565061763/adapter)库来演示，可以获得adapter的数据持有者DataHolder来监听数据的变化

```java
public class ListDemoAdapter extends FSimpleAdapter<DataModel>
{
    private SelectManager<DataModel> mSelectManager;

    public ListDemoAdapter()
    {
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
```

# SelectManager接口
```java
public interface SelectManager<T>
{
    /**
     * 添加回调对象
     *
     * @param callback
     */
    void addCallback(Callback<T> callback);

    /**
     * 移除回调对象
     *
     * @param callback
     */
    void removeCallback(Callback<T> callback);

    /**
     * 设置item初始化回调对象
     *
     * @param callback
     */
    void setOnItemInitCallback(OnItemInitCallback<T> callback);

    /**
     * 设置选择模式
     *
     * @param mode
     */
    void setMode(Mode mode);

    /**
     * 返回当前的选择模式
     *
     * @return
     */
    Mode getMode();

    /**
     * item是否被选中
     *
     * @param item
     * @return
     */
    boolean isSelected(T item);

    /**
     * 返回当前选中的位置，{@link Mode#isSingleType()} == true 的时候才可以调用此方法
     *
     * @return
     */
    int getSelectedIndex();

    /**
     * 返回当前选中的位置，{@link Mode#isSingleType()} == false 的时候才可以调用此方法
     *
     * @return
     */
    List<Integer> getSelectedIndexs();

    /**
     * 返回当前选中的item，{@link Mode#isSingleType()} == true 的时候才可以调用此方法
     *
     * @return
     */
    T getSelectedItem();

    /**
     * 返回当前选中的item，{@link Mode#isSingleType()} == false 的时候才可以调用此方法
     *
     * @return
     */
    List<T> getSelectedItems();

    /**
     * 全部选中，{@link Mode#isSingleType()} == false 的时候才可以调用此方法
     */
    void selectAll();

    /**
     * 模拟点击该位置
     *
     * @param index
     */
    void performClick(int index);

    /**
     * 设置该位置的选中状态
     *
     * @param index
     * @param selected
     */
    void setSelected(int index, boolean selected);

    /**
     * 模拟点击该项
     *
     * @param item
     */
    void performClick(T item);

    /**
     * 设置该项的选中状态
     *
     * @param item
     * @param selected
     */
    void setSelected(T item, boolean selected);

    /**
     * 清空选中的项
     */
    void clearSelected();

    /**
     * 返回item的位置
     *
     * @param item
     * @return
     */
    int indexOf(T item);

    //---------- data start ----------

    /**
     * 设置数据
     *
     * @param items
     */
    void setItems(T... items);

    /**
     * 设置数据
     *
     * @param items
     */
    void setItems(List<T> items);

    /**
     * 在末尾添加数据
     *
     * @param item
     */
    void addItem(T item);

    /**
     * 在末尾添加数据
     *
     * @param items
     */
    void addItems(List<T> items);

    /**
     * 在index位置添加数据
     *
     * @param index
     * @param item
     */
    void addItem(int index, T item);

    /**
     * 在index位置添加数据
     *
     * @param index
     * @param items
     */
    void addItems(int index, List<T> items);

    /**
     * 移除数据
     *
     * @param item
     */
    void removeItem(T item);

    /**
     * 更新index位置的数据
     *
     * @param index
     * @param item
     */
    void updateItem(int index, T item);

    //---------- data end ----------

    enum Mode
    {
        /**
         * 单选，必须选中一项
         */
        SINGLE_MUST_ONE_SELECTED,
        /**
         * 单选，可以一项都没选中
         */
        SINGLE,
        /**
         * 多选，必须选中一项
         */
        MULTI_MUST_ONE_SELECTED,
        /**
         * 多选，可以一项都没选中
         */
        MULTI;

        /**
         * 是否是单选类型，{@link Mode#SINGLE}或者{@link Mode#SINGLE_MUST_ONE_SELECTED}
         *
         * @return
         */
        public boolean isSingleType()
        {
            return this == SINGLE || this == SINGLE_MUST_ONE_SELECTED;
        }
    }

    interface Callback<T>
    {
        /**
         * 状态变化回调
         *
         * @param selected true-选中，false-未选中
         * @param item
         */
        void onSelectedChanged(boolean selected, T item);
    }

    /**
     * 数据变更的时候会触发此回调来初始化item
     *
     * @param <T>
     */
    interface OnItemInitCallback<T>
    {
        void onInitItem(T item);
    }

    interface Selectable
    {
        boolean isSelected();

        void setSelected(boolean selected);
    }

    class SelectableModel implements Selectable
    {
        private boolean selected;

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
}
```
