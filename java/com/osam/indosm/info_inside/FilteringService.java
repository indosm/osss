package com.osam.indosm.info_inside;

import android.util.Log;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.content.BroadcastReceiver;
import android.os.IBinder;

public class FilteringService extends Service {

    @Override
    public void onCreate(){
        super.onCreate();
        Log.d("Service","Ready");
        startBroadcastReceiver();
    }
    @Override
    public IBinder onBind(Intent intent){
        return null;
    }
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("Intent received - ",intent.toString());
        }
    };

    private void startBroadcastReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.android.camera.NEW_PICTURE");

        try {
            filter.addDataType("image/*");

            registerReceiver(broadcastReceiver, filter);
        } catch (MalformedMimeTypeException ex) {
            Log.d("Error", "BroadcastReceiver registration failure.");
        }
    }

    private void stopBroadcastReceiver() {
        unregisterReceiver(broadcastReceiver);
    }
}
