package com.example.guidertm;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderApi;

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
