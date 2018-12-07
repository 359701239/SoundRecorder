package com.zuojie.soundrecorder.ui.dialog;

import android.app.AlertDialog;
import android.content.Context;

/**
 * Created by zuojie on 2018/12/07.
 */
public abstract class BaseDialog {

    public Context context;
    public AlertDialog dialog;
    public AlertDialog.Builder builder;
    public ActionCallBack callBack;

    public BaseDialog(Context context) {
        this.context = context;
    }

    abstract void show();

    protected void onDismiss() {
        context = null;
        dialog = null;
        builder = null;
    }

    public interface ActionCallBack {
        void done(boolean success);
    }
}
