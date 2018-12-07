package com.zuojie.soundrecorder;

import android.app.Service;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;
import com.zuojie.soundrecorder.util.ReflectHelper;
import com.zuojie.soundrecorder.util.Utils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;

/**
 * Created by zuojie on 2018/12/06.
 */
public class RecordingService extends Service {

    public static final String COMMAND_RECORD_START = "start";
    public static final String COMMAND_RECORD_STOP = "stop";
    public static final String COMMAND_RECORD_PAUSE = "pause";
    public static final String COMMAND_RECORD_RESUME = "resume";

    public static final int STATUS_RESET = 0;
    public static final int STATUS_RECORDING = 1;
    public static final int STATUS_PAUSE = 2;

    private MediaRecorder recorder;
    private ServiceBinder binder;
    private int status = STATUS_RESET;
    private StatusCallback statusCallback;
    private String filePath;

    private Method methodRecorderPause;
    private Method methodRecorderResume;

    @Override
    public void onCreate() {
        super.onCreate();
        binder = new ServiceBinder();
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_WB);
        recorder.setOutputFile(filePath = Utils.getFilePath());
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_WB);
        recorder.setAudioChannels(1);
        recorder.setAudioSamplingRate(44100);
        recorder.setAudioEncodingBitRate(192000);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String action = intent.getAction();
            if (action != null) {
                switch (action) {
                    case COMMAND_RECORD_START:
                        binder.start();
                        break;
                    case COMMAND_RECORD_PAUSE:
                        binder.pause();
                        break;
                    case COMMAND_RECORD_RESUME:
                        binder.resume();
                        break;
                    case COMMAND_RECORD_STOP:
                        binder.stop();
                        break;
                }
            }
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        binder.stop();
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    private void notifyStatus(int status) {
        this.status = status;
        if (statusCallback == null) {
            return;
        }
        if (status == STATUS_RECORDING) {
            statusCallback.onStartRecord();
        } else {
            statusCallback.onPauseRecord();
        }
    }

    public class ServiceBinder extends Binder {
        public void start() {
            try {
                recorder.prepare();
                recorder.start();
                notifyStatus(STATUS_RECORDING);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void pause() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                recorder.pause();
                notifyStatus(STATUS_PAUSE);
            } else {
                try {
                    if (methodRecorderPause == null) {
                        methodRecorderPause = (Method) ReflectHelper.getMethod("android.media.MediaRecorder", "pause", null);
                    }
                    if (methodRecorderPause != null) {
                        methodRecorderPause.invoke(recorder);
                        notifyStatus(STATUS_PAUSE);
                    }
                } catch (Exception e) {
                    Toast.makeText(RecordingService.this, R.string.msg_error_record_pause, Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        }

        public void resume() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                recorder.resume();
                notifyStatus(STATUS_RECORDING);
            } else {
                try {
                    if (methodRecorderResume == null) {
                        methodRecorderResume = (Method) ReflectHelper.getMethod("android.media.MediaRecorder", "resume", null);
                    }
                    if (methodRecorderResume != null) {
                        methodRecorderResume.invoke(recorder);
                        notifyStatus(STATUS_RECORDING);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        public void playPause() {
            if (status == STATUS_RECORDING) {
                pause();
            } else if (status == STATUS_PAUSE) {
                resume();
            }
        }

        public void stop() {
            if (recorder != null) {
                recorder.stop();
                recorder.release();
                notifyStatus(STATUS_RESET);
            }
            recorder = null;
        }

        public void cancel() {
            stop();
            File file = new File(filePath);
            if (file.exists()) {
                file.delete();
            }
        }

        public String getOutputPath() {
            return filePath;
        }

        public void setStatusCallback(StatusCallback callback) {
            statusCallback = callback;
        }
    }

    public interface StatusCallback {
        void onStartRecord();

        void onPauseRecord();
    }
}
