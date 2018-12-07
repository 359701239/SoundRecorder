package com.zuojie.soundrecorder.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;
import com.zuojie.soundrecorder.R;

/**
 * Created by zuojie on 2018/12/06.
 */
public abstract class BaseActivity extends AppCompatActivity {

    public String[] permissions = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO};
    public static final int PERMISSION_REQUEST = 520;
    private PermissionGrantedCallback callback;

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
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        return toolbar;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (hasPermissions()) {
            onPermissionsGranted();
        } else {
            onPermissionsDenied();
        }
    }

    protected void onPermissionsGranted() {

    }

    protected void onPermissionsDenied() {

    }

    protected boolean hasPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && permissions != null) {
            for (String permission : permissions) {
                if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    public void grantPermissions(PermissionGrantedCallback callback) {
        this.callback = callback;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && permissions != null) {
            requestPermissions(permissions, PERMISSION_REQUEST);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST) {
            for (int i = 0; i < grantResults.length; i++) {
                int grantResult = grantResults[i];
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    showPermissionInfo(isPermissionGrantAble(permissions[i]));
                    return;
                }
            }
            if (callback != null) {
                callback.onPermissionGranted();
            }
        }
    }

    private void showPermissionInfo(boolean grantAble) {
        if (grantAble) {
            showPermissionDeniedInfo();
        } else {
            showPermissionErrorInfo();
        }
    }

    private boolean isPermissionGrantAble(String permission) {
        return ActivityCompat.shouldShowRequestPermissionRationale(BaseActivity.this, permission);
    }

    private void showPermissionDeniedInfo() {
        Toast.makeText(this, R.string.msg_error_permission, Toast.LENGTH_SHORT).show();
    }

    private void showPermissionErrorInfo() {
        Toast.makeText(this, R.string.msg_error_permission, Toast.LENGTH_SHORT).show();
    }

    public interface PermissionGrantedCallback {
        void onPermissionGranted();
    }
}
