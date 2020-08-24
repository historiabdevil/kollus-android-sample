package com.se.kollus.security;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.util.Log;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;


public class RecordingAppObserver {
    private static RecordingAppObserver instance;
    private HashSet<String> recordingAppSet;
    private HashSet<String> onNotirecordingAppSet;
    private Thread observeThread;

    private RecordingAppObserver() {
        recordingAppSet = new HashSet<String>();
        onNotirecordingAppSet = new HashSet<String>();
    }

    public static synchronized RecordingAppObserver getInstance() {
        if (instance == null) {
            instance = new RecordingAppObserver();
        }
        return instance;
    }

    public synchronized HashSet<String> getRecordingApps() {
        return recordingAppSet;
    }

    public synchronized HashSet<String> getOnNotirecordingApps() {
        return onNotirecordingAppSet;
    }

    public void addNotiApp(String app) {
        onNotirecordingAppSet.add(app);
    }

    public void removeNotiApp(String app) {
        onNotirecordingAppSet.remove(app);
    }

    public boolean isClear() {
        return onNotirecordingAppSet.size() <= 0;
    }

    public void observe(PackageManager pm) {
        if (observeThread == null) {
            ObserveRunnable runnable = new ObserveRunnable(pm);
            runnable.checkOtherAppPerms();
            observeThread = new Thread(runnable);
            observeThread.setName("Recoding App Observer");
            observeThread.start();
        } else {
            Thread.State state = observeThread.getState();
            if (state == Thread.State.TERMINATED) {
                ObserveRunnable runnable = new ObserveRunnable(pm);
                runnable.checkOtherAppPerms();
                observeThread = new Thread(runnable);
                observeThread.setName("Recoding App Observer");
                observeThread.start();
            }
        }
    }

    private class ObserveRunnable implements Runnable {
        private PackageManager pm;

        public ObserveRunnable(PackageManager pm) {
            this.pm = pm;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                checkOtherAppPerms();
                Log.i(getClass().getSimpleName(), String.format("App Size : %d", recordingAppSet.size()));
            }
        }

        public void checkOtherAppPerms() {
            Intent intent = new Intent(Intent.ACTION_MAIN, null);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);

            List<ResolveInfo> pkgAppList = this.pm.queryIntentActivities(intent, 0);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            pkgAppList.addAll(this.pm.queryIntentActivities(intent, 0));
            for (ResolveInfo resolveInfo : pkgAppList) {
                PackageInfo packageInfo = null;
                try {
                    packageInfo = this.pm.getPackageInfo(resolveInfo.activityInfo.packageName, PackageManager.GET_PERMISSIONS);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                String[] reqPerms = packageInfo.requestedPermissions;
                if (reqPerms != null) {
                    List<String> listPerms = Arrays.asList(reqPerms);
                    if (listPerms.contains(Manifest.permission.SYSTEM_ALERT_WINDOW) ||
                            (listPerms.contains(Manifest.permission.RECORD_AUDIO) &&
                                    listPerms.contains(Manifest.permission.WRITE_EXTERNAL_STORAGE) &&
                                    listPerms.contains(Manifest.permission.FOREGROUND_SERVICE))
                    ) {
                        if(packageInfo.packageName.toLowerCase().contains("rec")
                                || packageInfo.packageName.toLowerCase().contains("cap")) {
                            recordingAppSet.add(packageInfo.packageName);
                        }
                        if(!packageInfo.packageName.startsWith("com.google.android")){
                            recordingAppSet.add(packageInfo.packageName);
                        }
                    }
                }
            }

        }
    }
}
