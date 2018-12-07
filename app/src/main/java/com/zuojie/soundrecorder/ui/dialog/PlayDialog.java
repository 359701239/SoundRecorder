package com.zuojie.soundrecorder.ui.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.TextView;
import com.zuojie.soundrecorder.R;
import com.zuojie.soundrecorder.bean.Audio;
import com.zuojie.soundrecorder.util.MediaPlayerExt;
import com.zuojie.soundrecorder.widget.PlayButton;
import com.zuojie.soundrecorder.widget.Slider;

/**
 * Created by zuojie on 2018/12/07.
 */
public class PlayDialog extends BaseDialog implements View.OnClickListener,
        MediaPlayer.OnPreparedListener,
        MediaPlayer.OnCompletionListener,
        Slider.OnProgressChangeListener {

    private Audio audio;
    private MediaPlayerExt player;
    private Handler handler = new Handler();

    private StringBuilder timeBuilder = new StringBuilder();

    private PlayButton playPause;
    private Slider slider;
    private TextView nowTime, wholeTime;
    private boolean isTracking = false;
    private boolean updateAble = false;
    private boolean autoPlay = true;

    public PlayDialog(Context context, Audio audio) {
        super(context);
        this.audio = audio;
        init();
    }

    private void init() {
        View content = View.inflate(context, R.layout.dialog_playing, null);
        playPause = content.findViewById(R.id.playPause);
        slider = content.findViewById(R.id.seekBar);
        nowTime = content.findViewById(R.id.nowTime);
        wholeTime = content.findViewById(R.id.wholeTime);

        playPause.setState(false, false);
        playPause.setOnClickListener(this);
        slider.setOnProgressChangeListener(this);
        builder = new AlertDialog.Builder(context)
                .setView(content)
                .setTitle(audio.name)
                .setNegativeButton(R.string.dialog_btn_close, null)
                .setOnDismissListener(dialog -> {
                    onDismiss();
                });
    }

    @Override
    public void show() {
        dialog = builder.create();
        dialog.show();
        player = new MediaPlayerExt(context);
        player.setOnPreparedListener(this);
        player.setOnCompletionListener(this);
        player.setDataSource(audio.path);
    }

    @Override
    protected void onDismiss() {
        super.onDismiss();
        handler.removeCallbacks(updateProgress);
        handler.removeCallbacks(updateNowTime);
        if (player != null) {
            player.release();
            player = null;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.playPause:
                playPause();
                break;
        }
    }

    private void playPause() {
        if (player == null) {
            return;
        }
        if (player.isPlaying()) {
            pause();
        } else {
            start();
        }
    }

    private void pause() {
        player.pause();
        updateAble = false;
        playPause.setState(false, true);
    }

    private void start() {
        player.start();
        updateAble = true;
        playPause.setState(true, true);
    }

    @Override
    public void onPrepared(MediaPlayer player) {
        if (autoPlay) {
            start();
            wholeTime.setText(DateUtils.formatElapsedTime(timeBuilder, player.getDuration() / 1000));
            slider.setMax(player.getDuration());
            handler.post(updateProgress);
            handler.post(updateNowTime);
            autoPlay = false;
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        updateAble = false;
        player.setDataSource(audio.path);
        playPause.setState(false, true);
        slider.setProgress(0);
        nowTime.setText(DateUtils.formatElapsedTime(timeBuilder, 0));
    }

    @Override
    public void onPositionChanged(Slider view, boolean fromUser, float position, int progress) {

    }

    @Override
    public void onStartTrackingTouch(Slider view) {
        isTracking = true;
    }

    @Override
    public void onStopTrackingTouch(Slider view) {
        player.seekTo(view.getProgress());
        handler.postDelayed(() -> isTracking = false, Slider.duration);
    }

    private Runnable updateProgress = new Runnable() {
        @Override
        public void run() {
            handler.postDelayed(this, 33);
            if (!isTracking && updateAble) {
                slider.setProgress(player.getCurrentPosition());
            }
        }
    };

    private Runnable updateNowTime = new Runnable() {
        @Override
        public void run() {
            handler.postDelayed(this, 250);
            if (!isTracking && updateAble) {
                nowTime.setText(DateUtils.formatElapsedTime(timeBuilder,
                        player.getCurrentPosition() / 1000));
            }
        }
    };
}
