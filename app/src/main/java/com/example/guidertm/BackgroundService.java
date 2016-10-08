package com.example.guidertm;

import android.app.IntentService;
import android.content.Intent;
import android.location.Location;
import android.util.Log;

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
            double Temporary_la =  ((MainActivity) MainActivity.mContext).latitude_plic;
            double Temporary_lo = ((MainActivity) MainActivity.mContext).longitude_plic;
            Log.e("TEST","3.3~-> "+ Temporary_la);

            double change_la = Temporary_la;
            double change_lo = Temporary_lo ;
            Temporary_la = location.getLatitude();
            Temporary_lo = location.getLongitude();
            Log.e("TEST","3.6~-> "+ Temporary_la);

            Temporary_la = (change_la + Temporary_la)/2;
            Temporary_lo = (change_lo + Temporary_lo)/2;


            Log.e("TEST","4~-> "+ Temporary_la);
            Log.e("TEST","4~-> "+ Temporary_lo);

            Intent sendIntent = new Intent("com.example.guidertm.SEND_BROAD_CAST");
            sendIntent.putExtra("sendlat",Temporary_la);
            sendIntent.putExtra("sendlon",Temporary_lo);
            sendBroadcast(sendIntent);
        }
    }
}
