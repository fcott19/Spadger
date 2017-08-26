package com.fcott.spadger.ui.adapter.baseadapter;

/**
 * Created by fcott on 2017/8/26.
 */

public interface OnItemLongClickListener<T> {
    void onItemLongClick(ViewHolder viewHolder, T data, int position);
}
