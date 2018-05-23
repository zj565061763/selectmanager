package com.fanwe.lib.selectmanager;

import java.util.List;

interface SelectManager<T>
{
    void addCallback(Callback<T> callback);

    void removeCallback(Callback<T> callback);

    void setMode(FSelectManager.Mode mode);

    FSelectManager.Mode getMode();

    boolean isSingleMode();

    boolean isSelected(T item);

    int getSelectedIndex();

    List<Integer> getSelectedIndexs();

    T getSelectedItem();

    List<T> getSelectedItems();

    void selectAll();

    void performClick(int index);

    void setSelected(int index, boolean selected);

    void performClick(T item);

    void setSelected(T item, boolean selected);

    void clearSelected();

    int indexOf(T item);

    void setItems(T... items);

    void setItems(List<T> items);

    void appendItems(List<T> items);

    void appendItem(T item);

    void removeItem(T item);

    void insertItem(int index, T item);

    void insertItem(int index, List<T> items);

    void updateItem(int index, T item);

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
    }

    interface Selectable
    {
        boolean isSelected();

        void setSelected(boolean selected);
    }

    class SelectableModel implements FSelectManager.Selectable
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
}
