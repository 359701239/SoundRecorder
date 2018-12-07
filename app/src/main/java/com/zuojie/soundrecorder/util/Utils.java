package com.zuojie.soundrecorder.util;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.util.TypedValue;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by zuojie on 2018/3/31.
 */

public class Utils {

    private static InputMethodManager imm;

    public static void openIM(Context context, View view) {
        if (imm == null) {
            imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        }
        if (imm != null) {
            imm.showSoftInput(view, 0);
        }
    }

    public static boolean checkoutListIsEmpty(ArrayList list) {
        return list == null || list.size() <= 0;
    }

    public static Uri getFileUri(Context context, String path) {
        if (Build.VERSION.SDK_INT >= 24) {
            return FileProvider.getUriForFile(context, context.getPackageName() + ".provider", new File(path));
        } else {
            return Uri.fromFile(new File(path));
        }
    }

    public static int dpToPx(Context context, float dp) {
        return (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics()) + 0.5f);
    }

    public static String getFilePath() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        String fileName = String.format(Locale.ENGLISH, "新录音 %02d.%02d.%02d.amr", hour, minute, second);
        return getFolderPath() + fileName;
    }

    public static String getFolderPath() {
        return Environment.getExternalStorageDirectory().getAbsolutePath() + "/SoundRecorder/";
    }
}
