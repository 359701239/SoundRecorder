package com.zuojie.soundrecorder.ui;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Chronometer;
import com.zuojie.soundrecorder.R;
import com.zuojie.soundrecorder.RecordingService;
import com.zuojie.soundrecorder.bean.Audio;
import com.zuojie.soundrecorder.ui.dialog.RenameDialog;
import com.zuojie.soundrecorder.widget.CircleImageButton;
import com.zuojie.soundrecorder.widget.PlayButton;
import com.zuojie.soundrecorder.widget.WaveView;

/**
 * Created by zuojie on 2018/12/06.
 */
public class RecordingActivity extends BaseActivity implements View.OnClickListener, RecordingService.StatusCallback {

    private PlayButton record;
    private CircleImageButton close, done;
    private RecordingService.ServiceBinder service;
    private Chronometer timer;
    private WaveView waveView;
    private long recordTime = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recording);
        setToolbar(true, false);

        record = findViewById(R.id.record);
        close = findViewById(R.id.close);
        done = findViewById(R.id.done);
        timer = findViewById(R.id.timer);
        waveView = findViewById(R.id.wave);

        record.setOnClickListener(this);
        close.setOnClickListener(this);
        done.setOnClickListener(this);
        record.setState(false, false);

        Looper.myQueue().addIdleHandler(() -> {
            Intent intent = new Intent(RecordingActivity.this, RecordingService.class);
            bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
            return false;
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.close:
                cancelRecord();
                break;
            case R.id.record:
                if (service != null) {
                    service.playPause();
                }
                break;
            case R.id.done:
                saveRecord();
                break;
        }
    }

    private void cancelRecord() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppTheme_Light_Dialog_NoTitle)
                .setMessage(R.string.msg_abondan)
                .setPositiveButton(R.string.dialog_btn_positive, (dialog, which) -> {
                    if (service != null) {
                        service.cancel();
                    }
                    finish();
                })
                .setNegativeButton(R.string.dialog_btn_negative, null);
        builder.show().getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
    }

    private void saveRecord() {
        if (service == null) {
            finish();
            return;
        }
        service.pause();
        String path = service.getOutputPath();
        Audio audio = new Audio(path);
        RenameDialog dialog = new RenameDialog(this, audio, success -> {
            if (success) {
                service.stop();
                setResult(MainActivity.RESULT_CODE_DIRTY);
            }
            finish();
        }, (dialog1, which) -> service.resume(), true);
        dialog.show();
    }

    @Override
    public void onBackPressed() {
        onClick(close);
    }

    @Override
    public void finish() {
        unbindService(serviceConnection);
        super.finish();
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(final ComponentName className, final IBinder binder) {
            service = (RecordingService.ServiceBinder) binder;
            service.setStatusCallback(RecordingActivity.this);
            service.start();
        }

        @Override
        public void onServiceDisconnected(final ComponentName className) {
            service = null;
        }
    };

    @Override
    public void onStartRecord() {
        setTitle(R.string.title_recording);
        timer.setBase(SystemClock.elapsedRealtime() - recordTime);
        timer.start();
        record.setState(true, true);
        waveView.resumeAnimation();
    }

    @Override
    public void onPauseRecord() {
        setTitle(R.string.title_record_pause);
        timer.stop();
        recordTime = SystemClock.elapsedRealtime() - timer.getBase();
        record.setState(false, true);
        waveView.pauseAnimation();
    }
}
