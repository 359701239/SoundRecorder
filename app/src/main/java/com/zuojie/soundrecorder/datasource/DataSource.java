package com.zuojie.soundrecorder.datasource;

/**
 * Created by zuojie on 2018/1/18.
 */

public interface DataSource<T> {

    interface LoadDataCallback<T> {

        void onDataLoaded(T data);

        void onDataLoadedError();
    }

    void getDataAsync(LoadDataCallback<T> callback);

    T getData();

    void release();
}
