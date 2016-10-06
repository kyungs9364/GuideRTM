package com.example.guidertm;

import android.app.IntentService;
import android.content.Intent;
import android.location.Location;

import com.google.android.gms.location.FusedLocationProviderApi;

/**
 * Created by 경석 on 2016-10-05.
 */
public class BackgroundService extends IntentService {

    public BackgroundService() {
        super("BackgroundService");
    }
    protected void onHandleIntent(Intent intent)
    {
        Location location = intent.getParcelableExtra(FusedLocationProviderApi.KEY_LOCATION_CHANGED);
        if(location !=null)
        {
            Intent sendIntent = new Intent("com.example.guidertm.SEND_BROAD_CAST");
            sendIntent.putExtra("sendlat", location.getLatitude());
            sendIntent.putExtra("sendlon", location.getLongitude());
            sendBroadcast(sendIntent);
        }
    }
}
