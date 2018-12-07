package com.zuojie.soundrecorder.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import com.zuojie.soundrecorder.R;
import com.zuojie.soundrecorder.bean.Audio;
import com.zuojie.soundrecorder.presenter.DataPresenter;
import com.zuojie.soundrecorder.ui.adapter.ListAdapter;
import com.zuojie.soundrecorder.util.Utils;
import com.zuojie.soundrecorder.widget.CircleImageButton;
import com.zuojie.soundrecorder.widget.helper.MyDividerItemDecoration;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends BaseActivity implements View.OnClickListener, DataView<ArrayList<Audio>> {

    public static final int REQUEST_CODE = 100;
    public static final int RESULT_CODE_DIRTY = 101;
    private RecyclerView list;
    private CircleImageButton record;
    private ProgressBar loading;
    private ListAdapter adapter;
    private ImageView empty;

    private DataPresenter presenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setToolbar(true, false);

        list = findViewById(R.id.list);
        record = findViewById(R.id.record);
        loading = findViewById(R.id.loading);
        empty = findViewById(R.id.empty);

        record.setOnClickListener(this);
        list.addItemDecoration(new MyDividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        presenter = new DataPresenter(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.record:
                if (hasPermissions()) {
                    openRecordingActivity();
                } else {
                    grantPermissions(this::openRecordingActivity);
                }
                break;
        }
    }

    @Override
    protected void onPermissionsGranted() {
        super.onPermissionsGranted();
        Looper.myQueue().addIdleHandler(() -> {
            refreshData();
            return false;
        });
    }

    @Override
    protected void onPermissionsDenied() {
        super.onPermissionsDenied();
        grantPermissions(this::onPermissionsGranted);
    }

    private void openRecordingActivity() {
        String folderPath = Utils.getFolderPath();
        File file = new File(folderPath);
        if (file.exists() || file.mkdir()) {
            Intent intent = new Intent(this, RecordingActivity.class);
            startActivityForResult(intent, REQUEST_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == RESULT_CODE_DIRTY) {
            presenter.loadData();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                // TODO: 2018/12/06 Settings
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        presenter.stop();
        super.onDestroy();
    }

    @Override
    public void loading() {
        runOnUiThread(() -> {
            loading.setVisibility(View.VISIBLE);
            empty.setVisibility(View.GONE);
        });
    }

    @Override
    public void empty() {
        runOnUiThread(() -> {
            loading.setVisibility(View.GONE);
            empty.setVisibility(View.VISIBLE);
            setData(null);
        });
    }

    @Override
    public void show(ArrayList<Audio> data) {
        runOnUiThread(() -> {
            loading.setVisibility(View.GONE);
            empty.setVisibility((data == null || data.size() == 0) ? View.VISIBLE : View.GONE);
            setData(data);
        });
    }

    private void setData(ArrayList<Audio> data) {
        if (adapter == null) {
            adapter = new ListAdapter(MainActivity.this, data);
            list.setAdapter(adapter);
        } else {
            adapter.setData(data);
        }
    }

    @Override
    public boolean isActive() {
        return !isDestroyed() && !isFinishing();
    }

    @Override
    public void refreshData() {
        if (presenter != null) {
            presenter.loadData();
        }
    }
}
