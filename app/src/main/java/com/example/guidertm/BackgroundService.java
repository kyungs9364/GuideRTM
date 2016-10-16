package com.example.guidertm;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderApi;

/**
 * Created by 경석 on 2016-10-05.
 */

public class BackgroundService  extends Service {
    public BackgroundService () {

    }
    public static double Temporary_la;
    public static double Temporary_lo;


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        {
            Location location = intent.getParcelableExtra(FusedLocationProviderApi.KEY_LOCATION_CHANGED);

            if(location !=null )
            {
                if(((MainActivity) MainActivity.mContext).stopANDstart == true) {

                    double change_la = Temporary_la;
                    double change_lo = Temporary_lo;
                    Log.e("TEST", "3.3~-> " + change_la );

                    Temporary_la = location.getLatitude();
                    Temporary_lo = location.getLongitude();
                    Log.e("TEST", "3.6~-> " + Temporary_la);

                    Temporary_la = (change_la + Temporary_la) / 2;
                    Temporary_lo = (change_lo + Temporary_lo) / 2;

                    Log.e("TEST", "4~-> " + Temporary_la);
                    Log.e("TEST", "4~-> " + Temporary_lo);

                    Intent sendIntent = new Intent("com.example.guidertm.SEND_BROAD_CAST");
                    sendIntent.putExtra("sendlat", Temporary_la);
                    sendIntent.putExtra("sendlon", Temporary_lo);
                    sendBroadcast(sendIntent);
                }
                else {
                    Temporary_la = ((MainActivity) MainActivity.mContext).latitude_plic;
                    Temporary_lo = ((MainActivity) MainActivity.mContext).longitude_plic;
                   // Log.e("TEST", "5");
                }
            }
            //Log.e("TEST", "6");
            return START_NOT_STICKY;
        }
    }
    public void onDestroy()
    {
        super.onDestroy();
        Log.e("TEST", "7");

    }
}

