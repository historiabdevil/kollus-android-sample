package d.factory.haeming.activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.kollus.sdk.media.KollusStorage;
import com.kollus.sdk.media.util.ErrorCodes;
import com.kollus.sdk.media.util.Utils;


import java.util.List;

import d.factory.haeming.R;
import d.factory.haeming.data.ContentItem;
import d.factory.haeming.data.ContentTypes;
import d.factory.haeming.data.EncryptTypes;
import d.factory.haeming.exception.KollusException;
import d.factory.haeming.ui.content.ContentListViewAdapter;

public class MainActivity extends AppCompatActivity {

    private final String TAG = MainActivity.class.getSimpleName();

    public static KollusStorage kollusStorage;
    private final String PACKAGE_NAME = "d.factory.haeming";
    private final String EXPIRE_DATE = "2020/12/31";
    private final String APP_KEY = "1bb12c44e4b41427861afb253413fcd9409e3751";
    private static final int PERMISSION_REQUEST_STORAGE = 1;

    private ContentListViewAdapter adapter;
    private ListView listView;

    private void makeData() {
        try {
            adapter.add(new ContentItem("리이브 테스트", ContentTypes.LIVE, EncryptTypes.NONE, "aaylieayxpmcvdym"));
            adapter.add(new ContentItem("리이브 테스트", ContentTypes.VOD, EncryptTypes.KOLLUS, "e8eDr88f"));
            adapter.add(new ContentItem("밤바다 AOD", ContentTypes.AOD, EncryptTypes.KOLLUS, "c2cM2N2P"));
            adapter.add(new ContentItem("TEST AOD", ContentTypes.AOD, EncryptTypes.KOLLUS, "b28n5hAN", "https://v.kr.kollus.com/si?jwt=eyJhbGciOiAiSFMyNTYiLCJ0eXAiOiAiSldUIn0.eyJjdWlkIjoiY2F0ZW5vaWQtdGVzdCIsImV4cHQiOjIwMDAwMDAwMDAsIm1jIjpbeyJtY2tleSI6ImIyOG41aEFOIn1dfQ.YaMjNk1QCu4UDdNXHaRcuSofiSPjGMMssntSazzEJFE&custom_key=c8fe2fcc46ec0b76c2375ccba51941a6cfd81543aeeea106dff542b53be9acd2"));
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





    @RequiresApi(api = Build.VERSION_CODES.M)
    private void checkPermission() {
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            }
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.PACKAGE_USAGE_STATS},
                    PERMISSION_REQUEST_STORAGE);
        } else {
            return;
        }

        if (checkSelfPermission(Manifest.permission.WRITE_SETTINGS) != PackageManager.PERMISSION_GRANTED) {
            if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_SETTINGS)) {
            }
            requestPermissions(new String[]{Manifest.permission.WRITE_SETTINGS}, 1);
        } else {
            return;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                break;
        }
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
