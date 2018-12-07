package com.zuojie.soundrecorder.presenter;

import com.zuojie.soundrecorder.bean.Audio;
import com.zuojie.soundrecorder.datasource.AudioFileDataSource;
import com.zuojie.soundrecorder.datasource.DataSource;
import com.zuojie.soundrecorder.ui.DataView;

import java.util.ArrayList;

/**
 * Created by zuojie on 2018/12/06.
 */
public class DataPresenter {

    private DataView<ArrayList<Audio>> view;
    private DataSource<ArrayList<Audio>> dataSource;

    public DataPresenter(DataView<ArrayList<Audio>> view) {
        this.view = view;
        dataSource = new AudioFileDataSource();
    }

    public void stop() {
        dataSource.release();
        view = null;
    }

    public void loadData() {
        dataSource.getDataAsync(new DataSource.LoadDataCallback<ArrayList<Audio>>() {
            @Override
            public void onDataLoaded(ArrayList<Audio> item) {
                if (view != null && view.isActive()) {
                    view.show(item);
                }
            }

            @Override
            public void onDataLoadedError() {
                if (view != null && view.isActive()) {
                    view.empty();
                }
            }
        });
    }
}
