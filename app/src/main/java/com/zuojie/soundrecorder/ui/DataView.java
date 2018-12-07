package com.zuojie.soundrecorder.ui;

/**
 * Created by zuojie on 2018/12/06.
 */
public interface DataView<T> {

    void loading();

    void empty();

    void show(T data);

    boolean isActive();

    void refreshData();
}
