package com.example.guidertm;


import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;

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
                    ((CameraActivity) CameraActivity.mContext).setpush();
                    break;
                //case Geofence.GEOFENCE_TRANSITION_EXIT:
                case Geofence.GEOFENCE_TRANSITION_DWELL:
                    ((CameraActivity) CameraActivity.mContext).setpush();
                    break;
            }

            List<Geofence> list = event.getTriggeringGeofences();

            Location location = event.getTriggeringLocation();
        }
        return Service.START_NOT_STICKY;
    }

}