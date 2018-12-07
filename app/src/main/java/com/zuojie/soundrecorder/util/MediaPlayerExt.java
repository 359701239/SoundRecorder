package com.zuojie.soundrecorder.util;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.widget.Toast;
import com.zuojie.soundrecorder.R;

/**
 * Created by zuojie on 17-3-3.
 */

public class MediaPlayerExt extends MediaPlayer {
    private Context context;

    private boolean isInitialized = false;
    public boolean autoPlay = false;

    public MediaPlayerExt(Context context) {
        super();
        this.context = context;
    }

    public void showError() {
        Toast.makeText(context, R.string.msg_error_play, Toast.LENGTH_SHORT).show();
    }

    public void setDataSource(String path) {
        if (isInitialized) {
            reset();
        }
        isInitialized = setDataSourceImpl(path);
    }

    private boolean setDataSourceImpl(String path) {
        try {
            if (path.startsWith("content://")) {
                setDataSource(context, Uri.parse(path));
            } else {
                super.setDataSource(path);
            }
            prepareAsync();
        } catch (Exception e) {
            e.printStackTrace();
            showError();
            return false;
        }
        return true;
    }

    @Override
    public boolean isPlaying() {
        return isInitialized && super.isPlaying();
    }

    @Override
    public void start() {
        if (isInitialized) {
            super.start();
        }
    }

    @Override
    public void pause() {
        if (isInitialized) {
            super.pause();
        }
    }

    @Override
    public void reset() {
        if (isInitialized) {
            super.reset();
        }
        isInitialized = false;
    }

    @Override
    public void release() {
        context = null;
        super.release();
    }

    public int duration() {
        if (isInitialized) {
            return getDuration();
        } else {
            return 0;
        }
    }

    public int position() {
        if (isInitialized) {
            return getCurrentPosition();
        } else {
            return 0;
        }
    }

    public void seekTo(int whereto) {
        if (isInitialized) {
            super.seekTo(whereto);
        }
    }

    public void setVolume(float vol) {
        try {
            setVolume(vol, vol);
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }
}
