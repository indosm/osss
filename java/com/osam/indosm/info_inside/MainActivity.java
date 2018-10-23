package com.osam.indosm.info_inside;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.Manifest;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.osam.indosm.info_inside.Module.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import android.util.Log;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import java.net.NetworkInterface;
import java.util.List;
import java.util.Collections;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends Activity implements OnClickListener {

    private static String rank;
    private static String name;
    private static boolean isWorking=false;
    private static boolean isLocation=true;
    private String qrcodeStr;
    private final int PERMISSIONS_ACCESS_FINE_LOCATION = 1000;
    private final int PERMISSIONS_ACCESS_COARSE_LOCATION = 1001;
    private boolean isAccessFineLocation = false;
    private boolean isAccessCoarseLocation = false;
    private boolean isPermission = false;
    private TextView txtWorking;
    private TextView txtGPS;

    GpsInfo gps = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);

        SharedPreferences pref = getSharedPreferences("isSetup", Activity.MODE_PRIVATE);
        rank = pref.getString("rank", "");
        name = pref.getString("name", "");

        txtWorking = findViewById(R.id.txtWorking);
        txtGPS = findViewById(R.id.txtGPS);

        findViewById(R.id.btnHome).setOnClickListener(this);
        findViewById(R.id.btnGallery).setOnClickListener(this);
        findViewById(R.id.btnCalender).setOnClickListener(this);

        Intent intent = new Intent(this, FilteringService.class);
        startService(intent);

        gps = new GpsInfo(MainActivity.this);

    }

    @Override
    public void onStart() {
        super.onStart();
        String time = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date(System.currentTimeMillis()));
        txtWorking.setText("Working : " + isWorking);

        qrcodeStr = "{계급 : " + rank + ", 성명 : " + name + ", MAC : " + getDevicesUUID(this) + ", Time : " + time + "}";
        ImageView ImgQr = this.findViewById(R.id.ImgQr);
        ImgQr.setImageBitmap(QRFeature.getQRCodeImage(qrcodeStr, 300, 300));
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnGallery) {
            Intent intent = new Intent(this, GalleryActivity.class);
            startActivity(intent);
        }
        if (v.getId() == R.id.btnCalender) {
            Intent intent = new Intent(this, CalenderActivity.class);
            startActivity(intent);
        }
        if (v.getId() == R.id.btnHome) {
            isWorking = !isWorking;
            if (gps == null) {
                gps = new GpsInfo(MainActivity.this);
            } else {
                gps.Update();
                txtWorking.setText("Working : " + isWorking);
            }
            if (gps.isGetLocation()) {
                // check if GPS enabled
                double latitude = gps.getLatitude();
                double longitude = gps.getLongitude();
                // \n is for new line
                Toast.makeText(this, "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
            }else{
                // can't get location
                    // GPS or Network is not enabled
                    // Ask user to enable GPS/network in settings
                    gps.showSettingsAlert();
            }
        }
    }
    @Override
    public void onBackPressed() {
        finishAffinity();
    }
    public void makeNewGpsService() {
        if(gps == null) {
            gps = new GpsInfo(MainActivity.this);
        }else{
            gps.Update();
        }

    }

    private String getDevicesUUID(Context mContext) {
        final TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        final String tmDevice, tmSerial, androidId;
        tmDevice = "" + tm.getDeviceId();
        tmSerial = "" + tm.getSimSerialNumber();
        androidId = "" + android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
        UUID deviceUuid = new UUID(androidId.hashCode(), ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
        String deviceId = deviceUuid.toString();
        return deviceId;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == IntentIntegrator.REQUEST_CODE) {
            IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if (result == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}

