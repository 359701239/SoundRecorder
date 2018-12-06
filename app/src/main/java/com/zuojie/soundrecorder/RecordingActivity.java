package com.zuojie.soundrecorder;

import android.os.Bundle;
import android.support.annotation.Nullable;

/**
 * Created by zuojie on 2018/12/06.
 */
public class RecordingActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setToolbar(true, true);
    }
}
