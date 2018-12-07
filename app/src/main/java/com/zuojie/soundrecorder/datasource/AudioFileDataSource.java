package com.zuojie.soundrecorder.datasource;

import com.zuojie.soundrecorder.bean.Audio;
import com.zuojie.soundrecorder.util.Utils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by zuojie on 2018/12/06.
 */
public class AudioFileDataSource implements DataSource<ArrayList<Audio>> {

    private ExecutorService service;

    public AudioFileDataSource() {
        service = Executors.newSingleThreadExecutor();
    }

    @Override
    public void getDataAsync(final DataSource.LoadDataCallback<ArrayList<Audio>> callback) {
        if (!service.isShutdown()) {
            service.execute(new DataRunnable(callback));
        }
    }

    @Override
    public ArrayList<Audio> getData() {
        return null;
    }

    @Override
    public void release() {
        service.shutdown();
    }

    private static class DataRunnable implements Runnable {
        DataSource.LoadDataCallback<ArrayList<Audio>> callback;

        private DataRunnable(LoadDataCallback<ArrayList<Audio>> callback) {
            this.callback = callback;
        }

        @Override
        public void run() {
            File folder = new File(Utils.getFolderPath());
            if (folder.exists() && folder.isDirectory()) {
                File[] files = folder.listFiles();
                ArrayList<Audio> audios = new ArrayList<>();
                if (files == null || files.length == 0) {
                    callback.onDataLoadedError();
                    return;
                }
                for (File file : files) {
                    long lastModified = file.lastModified();
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss", Locale.ENGLISH);
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(lastModified);
                    audios.add(new Audio(file.getName(), format.format(calendar.getTime()), file.getPath(), lastModified));
                }
                Collections.sort(audios);
                callback.onDataLoaded(audios);
            } else {
                callback.onDataLoadedError();
            }
        }
    }
}
