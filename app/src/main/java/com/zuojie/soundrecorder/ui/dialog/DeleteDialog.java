package com.zuojie.soundrecorder.ui.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.widget.Toast;
import com.zuojie.soundrecorder.R;
import com.zuojie.soundrecorder.bean.Audio;
import com.zuojie.soundrecorder.rxjava.SimpleRxJava;
import io.reactivex.ObservableOnSubscribe;

import java.io.File;

import static android.content.DialogInterface.BUTTON_POSITIVE;

/**
 * Created by zuojie on 2018/12/07.
 */
public class DeleteDialog extends BaseDialog {
    private Audio audio;

    public DeleteDialog(Context context, Audio audio, ActionCallBack callBack) {
        super(context);
        this.audio = audio;
        this.callBack = callBack;
        init();
    }

    private void init() {
        builder = new AlertDialog.Builder(context, R.style.AppTheme_Light_Dialog_NoTitle)
                .setMessage(R.string.dialog_msg_delete)
                .setPositiveButton(R.string.dialog_btn_positive, (dialog, which) -> {
                    SimpleRxJava.create((ObservableOnSubscribe<Boolean>) emitter -> {
                        File file = new File(audio.path);
                        boolean result = file.exists() && file.delete();
                        emitter.onNext(result);
                    }).subscribe(success -> {
                        Toast.makeText(context, success ? R.string.msg_delete_success : R.string.msg_delete_error, Toast.LENGTH_SHORT).show();
                        if (callBack != null) {
                            callBack.done(success);
                        }
                    });
                })
                .setNegativeButton(R.string.dialog_btn_negative, null);
    }

    @Override
    public void show() {
        dialog = builder.create();
        dialog.show();
        dialog.getButton(BUTTON_POSITIVE).setTextColor(Color.BLACK);
    }

    @Override
    protected void onDismiss() {
        super.onDismiss();
    }
}
