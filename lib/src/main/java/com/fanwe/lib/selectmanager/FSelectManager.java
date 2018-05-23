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
public class FSelectManager<T>
{
    private Mode mMode = Mode.SINGLE_MUST_ONE_SELECTED;
    private final List<T> mListItem = new ArrayList<>();

    private T mCurrentItem;
    private final List<T> mListSelected = new ArrayList<>();

    private boolean mIsEnable = true;
    private final List<Callback<T>> mListCallback = new CopyOnWriteArrayList<>();

    /**
     * 添加选择状态回调
     *
     * @param callback
     */
    public final void addCallback(final Callback<T> callback)
    {
        if (callback == null || mListCallback.contains(callback))
            return;

        mListCallback.add(callback);
    }

    /**
     * 移除选择状态回调
     *
     * @param callback
     */
    public final void removeCallback(final Callback<T> callback)
    {
        mListCallback.remove(callback);
    }

    /**
     * 设置是否开启管理功能
     *
     * @param enable
     */
    public final void setEnable(final boolean enable)
    {
        mIsEnable = enable;
    }

    /**
     * 是否开启了管理功能，默认true，开启
     *
     * @return
     */
    public final boolean isEnable()
    {
        return mIsEnable;
    }

    /**
     * 设置选中模式
     *
     * @param mode
     */
    public final void setMode(final Mode mode)
    {
        if (mode == null)
            return;

        clearSelected();
        mMode = mode;
    }

    /**
     * 返回当前的模式
     *
     * @return
     */
    public final Mode getMode()
    {
        return mMode;
    }

    /**
     * 是否单选模式
     *
     * @return
     */
    public final boolean isSingleMode()
    {
        switch (mMode)
        {
            case SINGLE:
            case SINGLE_MUST_ONE_SELECTED:
                return true;
            default:
                return false;
        }
    }

    /**
     * 某个Item是否被选中
     *
     * @param item
     * @return
     */
    public final boolean isSelected(final T item)
    {
        if (item == null)
            return false;

        if (isSingleMode())
            return item == mCurrentItem;
        else
            return listContains(mListSelected, item);
    }

    /**
     * 返回选中的位置(单选模式)
     *
     * @return
     */
    public final int getSelectedIndex()
    {
        final T item = getSelectedItem();
        return indexOf(item);
    }

    /**
     * 返回选中的位置(多选模式)
     *
     * @return
     */
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

    /**
     * 返回选中项(单选模式)
     *
     * @return
     */
    public final T getSelectedItem()
    {
        if (isSingleMode())
            return mCurrentItem;
        else
            throw new UnsupportedOperationException("this method is not supported for multi mode");

    }

    /**
     * 返回选中项(多选模式)
     *
     * @return
     */
    public final List<T> getSelectedItems()
    {
        if (isSingleMode())
            throw new UnsupportedOperationException("this method is not supported for single mode");
        else
            return new ArrayList<>(mListSelected);
    }

    /**
     * 全选(多选模式)
     */
    public final void selectAll()
    {
        if (isSingleMode())
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

    /**
     * 模拟点击该位置
     *
     * @param index
     */
    public final void performClick(int index)
    {
        if (!isIndexLegal(index))
            return;

        final T item = mListItem.get(index);
        setSelectedWithoutCheckContains(item, !isSelected(item));
    }

    /**
     * 设置该位置的选中状态
     *
     * @param index
     * @param selected
     */
    public final void setSelected(int index, boolean selected)
    {
        if (!isIndexLegal(index))
            return;

        final T item = mListItem.get(index);
        setSelectedWithoutCheckContains(item, selected);
    }

    /**
     * 模拟点击该项
     *
     * @param item
     */
    public final void performClick(T item)
    {
        if (!listContains(mListItem, item))
            return;

        setSelectedWithoutCheckContains(item, !isSelected(item));
    }

    /**
     * 设置该项的选中状态
     *
     * @param item
     * @param selected
     */
    public final void setSelected(T item, boolean selected)
    {
        if (!listContains(mListItem, item))
            return;

        setSelectedWithoutCheckContains(item, selected);
    }

    private void setSelectedWithoutCheckContains(T item, boolean selected)
    {
        if (item == null || !mIsEnable)
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
            callback.onNormal(item);
        }
        onNormal(item);
    }

    private void notifySelected(T item)
    {
        if (item == null)
            return;

        for (Callback<T> callback : mListCallback)
        {
            callback.onSelected(item);
        }
        onSelected(item);
    }

    protected void onNormal(T item)
    {

    }

    protected void onSelected(T item)
    {

    }

    /**
     * 清除选中
     */
    public final void clearSelected()
    {
        if (isSingleMode())
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
                    it.remove();
                    notifyNormal(item);
                }
                mListSelected.clear(); //再清一次
            }
        }
    }

    /**
     * 返回item的位置
     *
     * @param item
     * @return
     */
    public final int indexOf(T item)
    {
        return listIndexOf(mListItem, item);
    }

    //---------- data start ----------

    /**
     * 设置数据
     *
     * @param items
     */
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
    public final void setItems(List<T> items)
    {
        mCurrentItem = null;
        mListSelected.clear();
        mListItem.clear();
        if (items != null)
        {
            mListItem.addAll(items);
        }

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
    public final void appendItems(List<T> items)
    {
        if (items == null)
            return;

        mListItem.addAll(items);
        for (T item : items)
        {
            synchronizeSelected(item);
            onInitItem(item);
        }
    }

    /**
     * 添加数据
     *
     * @param item
     */
    public final void appendItem(T item)
    {
        if (item == null)
            return;

        mListItem.add(item);
        synchronizeSelected(item);
        onInitItem(item);
    }

    /**
     * 移除数据
     *
     * @param item
     */
    public final void removeItem(T item)
    {
        if (isSelected(item))
        {
            if (isSingleMode())
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
    public final void insertItem(int index, T item)
    {
        if (item == null)
            return;

        mListItem.add(index, item);
        synchronizeSelected(item);
        onInitItem(item);
    }

    /**
     * 插入数据
     *
     * @param index
     * @param items
     */
    public final void insertItem(int index, List<T> items)
    {
        if (items == null || items.isEmpty())
            return;

        mListItem.addAll(index, items);
        for (T item : items)
        {
            synchronizeSelected(item);
            onInitItem(item);
        }
    }

    /**
     * 更新数据
     *
     * @param index
     * @param item
     */
    public final void updateItem(int index, T item)
    {
        if (item == null)
            return;

        mListItem.set(index, item);
        synchronizeSelected(item);
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

    }

    private void synchronizeSelected(T item)
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
        if (item == null)
        {
            return false;
        }
        return list.contains(item);
    }

    private static <T> int listIndexOf(List<T> list, T item)
    {
        if (item == null)
        {
            return -1;
        }
        return list.indexOf(item);
    }

    private static <T> boolean listRemove(List<T> list, T item)
    {
        if (item == null)
        {
            return false;
        }
        return list.remove(item);
    }

    //---------- utils end ----------

    public enum Mode
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
    }

    public interface Selectable
    {
        boolean isSelected();

        void setSelected(boolean selected);
    }

    public static class SelectableModel implements Selectable
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

    public interface Callback<T>
    {
        /**
         * item正常回调
         *
         * @param item
         */
        void onNormal(T item);

        /**
         * item选中回调
         *
         * @param item
         */
        void onSelected(T item);
    }
}
