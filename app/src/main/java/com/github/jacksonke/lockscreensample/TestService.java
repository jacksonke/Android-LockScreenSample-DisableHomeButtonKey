package com.github.jacksonke.lockscreensample;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.github.dubu.lockscreenusingservice.DebugLog;

/**
 * Created by jacksonke on 2016/2/29.
 */
public class TestService extends Service{

    @Override
    public void onCreate() {
        DebugLog.d("onCreate");
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        DebugLog.d("onStartCommand");
        if (intent != null){
            DebugLog.d("intent is not null");
        }
        else {
            DebugLog.d("intent is null");
        }

        return Service.START_NOT_STICKY;
//        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        DebugLog.d("onDestroy");
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
