# About
对选中和非选中状态进行了封装，可以方便的实现状态管理<br>
当前支持的模式:<br>
* 单选
* 单选必选
* 多选
* 多选必选

# Gradle
`implementation 'com.fanwe.android:selectmanager:1.0.9'`

# 效果
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
