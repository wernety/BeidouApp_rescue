package com.beidouapp.background;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import java.security.Provider;
import java.util.List;
import java.util.Map;

/**
 * 此service只是用来做HomeFragment当中的地图更新作用,先暂时保留接口
 * 思想是根据gps位置改变后，在此service里面发送广播，然后在HomeFragment里面将接受广播
 */

public class locService extends Service {



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }
}
