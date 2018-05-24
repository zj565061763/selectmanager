package com.fanwe.lib.selectmanager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 选择管理器
 *
 * @param <T>
 */
public class FSelectManager<T> implements SelectManager<T>
{
    private Mode mMode = Mode.SINGLE_MUST_ONE_SELECTED;
    private final List<T> mListItem = new ArrayList<>();

    private T mCurrentItem;
    private final List<T> mListSelected = new ArrayList<>();

    private final List<Callback<T>> mListCallback = new CopyOnWriteArrayList<>();

    @Override
    public final void addCallback(final Callback<T> callback)
    {
        if (callback == null || mListCallback.contains(callback))
            return;

        mListCallback.add(callback);
    }

    @Override
    public final void removeCallback(final Callback<T> callback)
    {
        mListCallback.remove(callback);
    }

    @Override
    public final void setMode(final Mode mode)
    {
        if (mode == null)
            return;

        clearSelected();
        mMode = mode;
    }

    @Override
    public final Mode getMode()
    {
        return mMode;
    }

    @Override
    public final boolean isSelected(final T item)
    {
        if (item == null)
            return false;

        if (getMode().isSingleType())
            return item == mCurrentItem;
        else
            return listContains(mListSelected, item);
    }

    @Override
    public final int getSelectedIndex()
    {
        final T item = getSelectedItem();
        return indexOf(item);
    }

    @Override
    public final List<Integer> getSelectedIndexs()
    {
        final List<Integer> list = new ArrayList<>();

        final List<T> listItem = getSelectedItems();
        for (T item : listItem)
        {
            list.add(indexOf(item));
        }
        return list;
    }

    @Override
    public final T getSelectedItem()
    {
        if (getMode().isSingleType())
            return mCurrentItem;
        else
            throw new UnsupportedOperationException("this method is not supported for multi mode");

    }

    @Override
    public final List<T> getSelectedItems()
    {
        if (getMode().isSingleType())
            throw new UnsupportedOperationException("this method is not supported for single mode");
        else
            return new ArrayList<>(mListSelected);
    }

    @Override
    public final void selectAll()
    {
        if (getMode().isSingleType())
            throw new UnsupportedOperationException("this method is not supported for single mode");

        for (T item : mListItem)
        {
            setSelectedWithoutCheckContains(item, true);
        }
    }

    private boolean isIndexLegal(int index)
    {
        return index >= 0 && index < mListItem.size();
    }

    @Override
    public final void performClick(int index)
    {
        if (!isIndexLegal(index))
            return;

        final T item = mListItem.get(index);
        setSelectedWithoutCheckContains(item, !isSelected(item));
    }

    @Override
    public final void setSelected(int index, boolean selected)
    {
        if (!isIndexLegal(index))
            return;

        final T item = mListItem.get(index);
        setSelectedWithoutCheckContains(item, selected);
    }

    @Override
    public final void performClick(T item)
    {
        if (!listContains(mListItem, item))
            return;

        setSelectedWithoutCheckContains(item, !isSelected(item));
    }

    @Override
    public final void setSelected(T item, boolean selected)
    {
        if (!listContains(mListItem, item))
            return;

        setSelectedWithoutCheckContains(item, selected);
    }

    @Override
    public final void clearSelected()
    {
        if (getMode().isSingleType())
        {
            if (mCurrentItem != null)
            {
                final T old = mCurrentItem;
                mCurrentItem = null;
                notifyNormal(old);
            }
        } else
        {
            if (!mListSelected.isEmpty())
            {
                final Iterator<T> it = mListSelected.iterator();
                while (it.hasNext())
                {
                    final T item = it.next();
                    it.remove(); // 移除item
                    notifyNormal(item);
                }
            }
        }
    }

    @Override
    public final int indexOf(T item)
    {
        return listIndexOf(mListItem, item);
    }

    private void setSelectedWithoutCheckContains(T item, boolean selected)
    {
        if (item == null)
            return;

        switch (mMode)
        {
            case SINGLE_MUST_ONE_SELECTED:
                if (selected)
                {
                    selectItemSingle(item);
                }
                break;
            case SINGLE:
                if (selected)
                {
                    selectItemSingle(item);
                } else
                {
                    if (mCurrentItem == item)
                    {
                        final T old = mCurrentItem;
                        mCurrentItem = null;

                        notifyNormal(old);
                    }
                }
                break;
            case MULTI_MUST_ONE_SELECTED:
                if (selected)
                {
                    selectItemMulti(item);
                } else
                {
                    if (listContains(mListSelected, item))
                    {
                        if (mListSelected.size() > 1)
                        {
                            listRemove(mListSelected, item);
                            notifyNormal(item);
                        }
                    }
                }
                break;
            case MULTI:
                if (selected)
                {
                    selectItemMulti(item);
                } else
                {
                    if (listContains(mListSelected, item))
                    {
                        listRemove(mListSelected, item);
                        notifyNormal(item);
                    }
                }
                break;
            default:
                break;
        }
    }

    private void selectItemSingle(T item)
    {
        if (mCurrentItem == item)
            return;

        final T old = mCurrentItem;
        mCurrentItem = item;

        notifyNormal(old);
        notifySelected(item);
    }

    private void selectItemMulti(T item)
    {
        if (listContains(mListSelected, item))
            return;

        mListSelected.add(item);
        notifySelected(item);
    }

    private void notifyNormal(T item)
    {
        if (item == null)
            return;

        for (Callback<T> callback : mListCallback)
        {
            callback.onSelectedChanged(false, item);
        }
        onSelectedChanged(false, item);
    }

    private void notifySelected(T item)
    {
        if (item == null)
            return;

        for (Callback<T> callback : mListCallback)
        {
            callback.onSelectedChanged(true, item);
        }
        onSelectedChanged(true, item);
    }

    /**
     * 选中状态变化
     *
     * @param selected
     * @param item
     */
    protected void onSelectedChanged(boolean selected, T item)
    {
    }

    //---------- data start ----------

    /**
     * 设置数据
     *
     * @param items
     */
    @Override
    public final void setItems(T... items)
    {
        List<T> listItem = null;
        if (items != null && items.length > 0)
        {
            listItem = Arrays.asList(items);
        }
        setItems(listItem);
    }

    /**
     * 设置数据
     *
     * @param items
     */
    @Override
    public final void setItems(List<T> items)
    {
        mCurrentItem = null;
        mListSelected.clear();
        mListItem.clear();

        if (items != null) mListItem.addAll(items);

        for (T item : mListItem)
        {
            onInitItem(item);
        }
    }

    /**
     * 添加数据
     *
     * @param items
     */
    @Override
    public final void appendItems(List<T> items)
    {
        if (items == null)
            return;

        mListItem.addAll(items);
        for (T item : items)
        {
            onInitItem(item);
        }
    }

    /**
     * 添加数据
     *
     * @param item
     */
    @Override
    public final void appendItem(T item)
    {
        if (item == null)
            return;

        mListItem.add(item);
        onInitItem(item);
    }

    /**
     * 移除数据
     *
     * @param item
     */
    @Override
    public final void removeItem(T item)
    {
        if (isSelected(item))
        {
            if (getMode().isSingleType())
            {
                clearSelected();
            } else
            {
                setSelectedWithoutCheckContains(item, false);
                if (isSelected(item))
                {
                    // 多选必选模式，并且当前仅有一项item，直接清空选中
                    clearSelected();
                }
            }
        }
        listRemove(mListItem, item);
    }

    /**
     * 插入数据
     *
     * @param index
     * @param item
     */
    @Override
    public final void insertItem(int index, T item)
    {
        if (item == null)
            return;

        mListItem.add(index, item);
        onInitItem(item);
    }

    /**
     * 插入数据
     *
     * @param index
     * @param items
     */
    @Override
    public final void insertItem(int index, List<T> items)
    {
        if (items == null || items.isEmpty())
            return;

        mListItem.addAll(index, items);
        for (T item : items)
        {
            onInitItem(item);
        }
    }

    /**
     * 更新数据
     *
     * @param index
     * @param item
     */
    @Override
    public final void updateItem(int index, T item)
    {
        if (item == null)
            return;

        mListItem.set(index, item);
        onInitItem(item);
    }

    //---------- data end ----------

    /**
     * 设置数据后会遍历调用此方法对每个数据进行初始化
     *
     * @param item
     */
    protected void onInitItem(T item)
    {
        if (item instanceof Selectable)
        {
            final boolean selected = ((Selectable) item).isSelected();
            setSelectedWithoutCheckContains(item, selected);
        }
    }

    //---------- utils start ----------

    private static <T> boolean listContains(List<T> list, T item)
    {
        return list.contains(item);
    }

    private static <T> int listIndexOf(List<T> list, T item)
    {
        return list.indexOf(item);
    }

    private static <T> boolean listRemove(List<T> list, T item)
    {
        return list.remove(item);
    }

    //---------- utils end ----------
}
