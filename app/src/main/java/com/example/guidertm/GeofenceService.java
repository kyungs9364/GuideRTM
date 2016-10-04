package com.example.guidertm;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;

/**
 * Created by 경석 on 2016-09-30.
 */
public class GeofenceService extends Service {

    public GeofenceService() {
    }
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        GeofencingEvent event = GeofencingEvent.fromIntent(intent);
        if (event != null) {
            int action = event.getGeofenceTransition();
            switch (action) {
                case Geofence.GEOFENCE_TRANSITION_ENTER:
                    //((CameraActivity) CameraActivity.mContext).setpush();
                    Log.e("Geofence Enter->","ENTER");
                    break;
                case Geofence.GEOFENCE_TRANSITION_EXIT:
                    Log.e("Geofence Exit->","EXIT");
                    break;
                case Geofence.GEOFENCE_TRANSITION_DWELL:
                    //((CameraActivity) CameraActivity.mContext).setpush();
                    Log.e("Geofence Dwell->","DWELL");
                    break;
                default:
                    Log.e("Geofence default ->","default");
                    break;
            }

            List<Geofence> list = event.getTriggeringGeofences();

            Log.e("Geofence list->",list.toString());

            Location location = event.getTriggeringLocation();
            Log.e("Geofence location->",String.valueOf(location.getLatitude()+location.getLongitude()));

        }
        return Service.START_NOT_STICKY;
    }

}