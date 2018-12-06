package com.zuojie.soundrecorder;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

/**
 * Created by zuojie on 2018/12/06.
 */
public abstract class BaseActivity extends AppCompatActivity {

    public Toolbar setToolbar(boolean showTitle, boolean homeAsUp) {
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar == null) {
            return null;
        }
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(homeAsUp);
            actionBar.setDisplayShowTitleEnabled(showTitle);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        return toolbar;
    }
}
