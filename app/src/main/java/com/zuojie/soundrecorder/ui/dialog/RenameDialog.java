package com.zuojie.soundrecorder.ui.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.zuojie.soundrecorder.R;
import com.zuojie.soundrecorder.bean.Audio;
import com.zuojie.soundrecorder.util.Utils;

import java.io.File;
import java.util.Locale;

public class RenameDialog extends BaseDialog {

    private Handler handler;
    private EditText editText;
    private TextView editTextLength;
    private Button positive;
    private Audio audio;
    private static final String FORMAT = ".amr";

    public RenameDialog(Context context, Audio audio, ActionCallBack callBack, DialogInterface.OnClickListener negativeButtonListener, boolean fromRecording) {
        super(context);
        this.audio = audio;
        this.callBack = callBack;
        this.handler = new Handler();
        View content = View.inflate(context, R.layout.dialog_rename, null);
        editText = content.findViewById(R.id.edit);
        editTextLength = content.findViewById(R.id.edit_size);
        editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s)) {
                    if (dialog != null) {
                        positive.setEnabled(false);
                    }
                } else {
                    if (dialog != null) {
                        positive.setEnabled(true);
                    }
                }
                editTextLength.setText(String.format(Locale.ENGLISH, "%d/30", s.length()));
            }
        });
        builder = new AlertDialog.Builder(context)
                .setView(content)
                .setTitle(fromRecording ? R.string.dialog_title_save : R.string.dialog_title_rename)
                .setNegativeButton(fromRecording ? R.string.dialog_btn_resumerecord : R.string.dialog_btn_negative, negativeButtonListener)
                .setPositiveButton(R.string.dialog_btn_positive, (dialog, which) -> {
                    String input = editText.getText().toString();
                    if (context == null) {
                        return;
                    }
                    callBack.done(renameFile(audio.path, input + FORMAT) >= 0);
                }).setOnDismissListener(dialog -> {
                    onDismiss();
                });
    }

    @Override
    public void show() {
        setEditText(audio.name);
        dialog = builder.show();
        dialog.setCanceledOnTouchOutside(false);
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);
        positive = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        positive.setEnabled(!TextUtils.isEmpty(editText.getText()));
        handler.postDelayed(openIM, context.getResources().getInteger(R.integer.duration_open_im));
    }

    @Override
    protected void onDismiss() {
        super.onDismiss();
        handler.removeCallbacks(openIM);
        handler = null;
    }

    private int renameFile(String path, String newName) {
        File oldFile = new File(path);
        if (!oldFile.getName().equals(newName)) {
            File newFile = new File(oldFile.getParent() + "/" + newName);
            if (!oldFile.exists()) {
                return -1;
            }
            if (newFile.exists()) {
                Toast.makeText(context, R.string.msg_error_rename_exist, Toast.LENGTH_SHORT).show();
                return 0;
            } else {
                if (oldFile.renameTo(newFile)) {
                    return 1;
                }
                return 0;
            }
        }
        return 0;
    }

    private Runnable openIM = () -> Utils.openIM(context, editText);

    public void setEditText(String text) {
        if (editText != null && !TextUtils.isEmpty(text)) {
            editText.setText(text);
            editText.setSelection(text.length());
        }
    }
}
