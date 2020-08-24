package com.se.kollus.activity;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.kollus.sdk.media.KollusStorage;
import com.kollus.sdk.media.util.ErrorCodes;
import com.kollus.sdk.media.util.Utils;


import java.util.List;
import java.util.Set;

import com.se.kollus.R;
import com.se.kollus.data.ContentItem;
import com.se.kollus.data.ContentTypes;
import com.se.kollus.data.EncryptTypes;
import com.se.kollus.exception.KollusException;
import com.se.kollus.security.RecordingAppObserver;
import com.se.kollus.ui.content.ContentListViewAdapter;

public class MainActivity extends AppCompatActivity {

    private final String TAG = MainActivity.class.getSimpleName();

    public static KollusStorage kollusStorage;
    private final String PACKAGE_NAME = "com.se.kollus";
    private final String EXPIRE_DATE = "2025/12/31";
    private final String APP_KEY = "29f1a4958aa77264c6218e5b6e8fd39ffb93122c";
    private static final int PERMISSION_REQUEST_STORAGE = 1;

    private ContentListViewAdapter adapter;
    private ListView listView;

    private void makeData() {
        try {
            adapter.add(new ContentItem("TEST VOD", ContentTypes.VOD, EncryptTypes.KOLLUS, "e8eDr88f"));
        } catch (KollusException kex) {
            Log.e(TAG, kex.getMessage());
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        PackageManager pm = getPackageManager();
        List<ApplicationInfo> applicationInfoList = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        for (ApplicationInfo info : applicationInfoList) {
            Intent intent = pm.getLaunchIntentForPackage(info.packageName);
            if (intent != null) {
                if (info.name != null) {

                    Log.i("APPS FOUND", info.name);
                }
            }
        }
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        checkPermission();
//        if (!checkOpsPermission()) {
//            startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
//        }
//        if (!permissionGrantred()) {
//            startActivity(new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS));
//        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        kollusStorage = KollusStorage.getInstance(getApplicationContext());
        int ret = kollusStorage.initialize(APP_KEY, EXPIRE_DATE, PACKAGE_NAME);
        if (!kollusStorage.isReady()) {
            if (ret != ErrorCodes.ERROR_OK) {
                Log.e(TAG, "Kollus Storage Init Fail");
                if (ret != ErrorCodes.ERROR_EXPIRED_KEY) {
                    Log.e(TAG, "Kollus SDK Key Expired");
                }
                return;
            }
            kollusStorage.setDevice(
                    Utils.getStoragePath(MainActivity.this),
                    Utils.getPlayerIdSha1(MainActivity.this),
                    Utils.getPlayerIdMd5(MainActivity.this),
                    Utils.isTablet(MainActivity.this));
        }
        kollusStorage.setNetworkTimeout(5, 3);

        adapter = new ContentListViewAdapter();
        listView = (ListView) findViewById(R.id.listView);
        makeData();
        listView.setAdapter(adapter);
        setListViewHeightBasedOnChildren(listView);
        listView.setClickable(true);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            Log.i(TAG, "ITEM TOUCH: " + position);
            try {
                ContentItem item = (ContentItem) parent.getItemAtPosition(position);
                Intent intent = null;
                switch (item.getContentType()) {
                    case AOD:
                        intent = new Intent(MainActivity.this, AudioPlayerActivity.class);
                        break;
                    case VOD:
                        intent = new Intent(MainActivity.this, VodPlayerActivity.class);
                        break;
                    case LIVE:
                        intent = new Intent(MainActivity.this, LivePlayerActivity.class);
                        break;
                }
                intent.putExtra("playUrl", item.getPlayUrl());
                startActivity(intent);
            } catch (KollusException kex) {
                Log.e(TAG, kex.getMessage());
            }
        });

    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.AT_MOST);

        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }


    private PermissionListener permissionListener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
        }

        @Override
        public void onPermissionDenied(List<String> deniedPermissions) {
//            Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_WRITE_SETTINGS);
//            intent.setData(Uri.parse("package:" + getPackageName()));
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(intent);
        }
    };


    @RequiresApi(api = Build.VERSION_CODES.M)
    private void checkPermission() {
        TedPermission.with(this)
                .setPermissionListener(permissionListener)
                .setRationaleMessage("저장된 컨텐츠를 사용하기 위해 권한을 허용 해주세요")
                .setDeniedMessage("권한을 거절 하셨군요\n하지만 [설정] > [권한] 에서 권한을 허용할 수 있어요.")
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .check();
//        TedPermission.with(this)
//                .setPermissionListener(permissionListener)
//                .setRationaleMessage("화면 밝기등을 조절 하기 위해서는 권한을 허용 해주세요")
//                .setPermissions(Manifest.permission.WRITE_SETTINGS)
//                .check();
        Set<String> sets = NotificationManagerCompat.getEnabledListenerPackages(this);
        if (sets != null && sets.contains(getPackageName())) {

        } else {
            Intent intent = new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
//            intent.setData(Uri.parse("package:" + getPackageName()));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            startActivity(intent);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("INTENT TEST", data.toString());
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


}
