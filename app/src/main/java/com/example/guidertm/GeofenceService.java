package com.example.guidertm;


import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

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
                    //if(((CameraActivity)CameraActivity.mContext).nodelan != null)
                        ((CameraActivity) CameraActivity.mContext).setpush();

                    Toast.makeText(getApplicationContext(), "10m 이내에 진입하였습니다.", Toast.LENGTH_SHORT).show();
                    Log.e("Geo", "check");
                    break;
               //case Geofence.GEOFENCE_TRANSITION_EXIT:
                case Geofence.GEOFENCE_TRANSITION_DWELL:
                    ((CameraActivity) CameraActivity.mContext).setpush();
                    Log.e("Geo", "check2");
                    break;
            }

            List<Geofence> list = event.getTriggeringGeofences();

            Location location = event.getTriggeringLocation();
        }
        return Service.START_NOT_STICKY;
    }

}