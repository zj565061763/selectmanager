package com.sd.lib.selectmanager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
    private final Map<T, String> mMapSelected = new IdentityHashMap<>();

    private final Map<Callback<T>, String> mCallbackHolder = new ConcurrentHashMap<>();
    private OnItemInitCallback<T> mOnItemInitCallback;
    private Map<StateInterceptor<T>, String> mStateInterceptorHolder;

    private SelectedInterceptor<T> mSelectedInterceptor;

    @Override
    public final void addCallback(final Callback<T> callback)
    {
        if (callback == null)
            return;

        mCallbackHolder.put(callback, "");
    }

    @Override
    public final void removeCallback(final Callback<T> callback)
    {
        if (callback == null)
            return;

        mCallbackHolder.remove(callback);
    }

    @Override
    public final void setOnItemInitCallback(OnItemInitCallback<T> callback)
    {
        mOnItemInitCallback = callback;
    }

    @Override
    public final void setSelectedInterceptor(SelectedInterceptor<T> interceptor)
    {
        mSelectedInterceptor = interceptor;
    }

    @Override
    public final void addStateInterceptor(StateInterceptor<T> interceptor)
    {
        if (interceptor == null)
            return;

        if (mStateInterceptorHolder == null)
            mStateInterceptorHolder = new ConcurrentHashMap<>();

        mStateInterceptorHolder.put(interceptor, "");
    }

    @Override
    public final void removeStateInterceptor(StateInterceptor<T> interceptor)
    {
        if (interceptor == null)
            return;

        if (mStateInterceptorHolder == null)
            return;

        mStateInterceptorHolder.remove(interceptor);
        if (mStateInterceptorHolder.isEmpty())
            mStateInterceptorHolder = null;
    }

    @Override
    public final void setMode(final Mode mode)
    {
        if (mode == null)
            throw new NullPointerException("mode is null");

        if (mMode != mode)
        {
            clearSelected();
            mMode = mode;
        }
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
            return mMapSelected.containsKey(item);
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
            return new ArrayList<>(mMapSelected.keySet());
    }

    @Override
    public final void selectAll()
    {
        if (getMode().isSingleType())
            throw new UnsupportedOperationException("this method is not supported for single mode");

        for (T item : mListItem)
        {
            setSelectedInternal(item, true);
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
        final boolean isSelected = isSelected(item);
        setSelectedInternal(item, !isSelected);
    }

    @Override
    public final void performClick(T item)
    {
        if (indexOf(item) < 0)
            return;

        final boolean isSelected = isSelected(item);
        setSelectedInternal(item, !isSelected);
    }

    @Override
    public final void setSelected(int index, boolean selected)
    {
        if (!isIndexLegal(index))
            return;

        final T item = mListItem.get(index);
        setSelectedInternal(item, selected);
    }

    @Override
    public final void setSelected(T item, boolean selected)
    {
        if (indexOf(item) < 0)
            return;

        setSelectedInternal(item, selected);
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
            final Iterator<T> it = mMapSelected.keySet().iterator();
            while (it.hasNext())
            {
                final T item = it.next();
                it.remove(); // 移除item
                notifyNormal(item);
            }
        }
    }

    @Override
    public final int indexOf(T item)
    {
        return listIndexOf(mListItem, item);
    }

    private void setSelectedInternal(T item, boolean selected)
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
                        if (interceptItemInternal(item, false))
                            return;

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
                    if (mMapSelected.size() > 1)
                    {
                        if (mMapSelected.containsKey(item))
                        {
                            if (interceptItemInternal(item, false))
                                return;
                        }

                        if (mMapSelected.remove(item) != null)
                            notifyNormal(item);
                    }
                }
                break;
            case MULTI:
                if (selected)
                {
                    selectItemMulti(item);
                } else
                {
                    if (mMapSelected.containsKey(item))
                    {
                        if (interceptItemInternal(item, false))
                            return;
                    }

                    if (mMapSelected.remove(item) != null)
                        notifyNormal(item);
                }
                break;
            default:
                break;
        }
    }

    private boolean interceptItemInternal(T item, boolean selected)
    {
        if (mSelectedInterceptor != null)
        {
            if (mSelectedInterceptor.interceptItem(item, selected))
                return true;
        }

        if (mStateInterceptorHolder != null)
        {
            for (StateInterceptor<T> interceptor : mStateInterceptorHolder.keySet())
            {
                if (interceptor.interceptItem(item, selected))
                    return true;
            }
        }

        return false;
    }

    private void selectItemSingle(T item)
    {
        if (mCurrentItem == item)
            return;

        if (interceptItemInternal(item, true))
            return;

        final T old = mCurrentItem;
        mCurrentItem = item;

        notifyNormal(old);
        notifySelected(item);
    }

    private void selectItemMulti(T item)
    {
        if (mMapSelected.containsKey(item))
            return;

        if (interceptItemInternal(item, true))
            return;

        mMapSelected.put(item, "");
        notifySelected(item);
    }

    private void notifyNormal(T item)
    {
        if (item == null)
            return;

        onSelectedChanged(false, item);

        for (Callback<T> callback : mCallbackHolder.keySet())
        {
            callback.onSelectedChanged(false, item);
        }
    }

    private void notifySelected(T item)
    {
        if (item == null)
            return;

        onSelectedChanged(true, item);

        for (Callback<T> callback : mCallbackHolder.keySet())
        {
            callback.onSelectedChanged(true, item);
        }
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

    @Override
    public final void setItems(List<T> items)
    {
        mCurrentItem = null;
        mMapSelected.clear();
        mListItem.clear();

        if (items != null)
            mListItem.addAll(items);

        for (T item : mListItem)
        {
            initItem(item);
        }
    }

    @Override
    public final void addItem(T item)
    {
        if (item == null)
            return;

        mListItem.add(item);
        initItem(item);
    }

    @Override
    public final void addItems(List<T> items)
    {
        if (items == null)
            return;

        mListItem.addAll(items);
        for (T item : items)
        {
            initItem(item);
        }
    }

    @Override
    public final void addItem(int index, T item)
    {
        if (item == null)
            return;

        mListItem.add(index, item);
        initItem(item);
    }

    @Override
    public final void addItems(int index, List<T> items)
    {
        if (items == null || items.isEmpty())
            return;

        mListItem.addAll(index, items);
        for (T item : items)
        {
            initItem(item);
        }
    }

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
                setSelectedInternal(item, false);
                if (isSelected(item))
                {
                    // 多选必选模式，并且当前仅有一项item，直接清空选中
                    clearSelected();
                }
            }
        }
        listRemove(mListItem, item);
    }

    @Override
    public final void updateItem(int index, T item)
    {
        if (item == null)
            return;

        mListItem.set(index, item);
        initItem(item);
    }

    //---------- data end ----------

    private void initItem(T item)
    {
        onInitItem(item);

        if (mOnItemInitCallback != null)
            mOnItemInitCallback.onInitItem(item);
    }

    protected void onInitItem(T item)
    {

    }

    //---------- utils start ----------

    private static <T> int listIndexOf(List<T> list, T item)
    {
        final int size = list.size();
        for (int i = 0; i < size; i++)
        {
            if (list.get(i) == item)
                return i;
        }
        return -1;
    }

    private static <T> boolean listRemove(List<T> list, T item)
    {
        final int index = listIndexOf(list, item);
        if (index < 0)
            return false;

        return list.remove(index) == item;
    }

    //---------- utils end ----------
}
