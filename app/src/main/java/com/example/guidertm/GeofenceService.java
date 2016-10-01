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
                    Toast.makeText(getApplicationContext(), "10m 안에 진입 하였습니다.", Toast.LENGTH_LONG).show();
                    Log.e("Geo", "10m 안에 진입 하였습니다." );
                    break;
                //case Geofence.GEOFENCE_TRANSITION_EXIT:
                case Geofence.GEOFENCE_TRANSITION_DWELL:
                    Toast.makeText(getApplicationContext(), "이미 10m 안에 들어와 있습니다." , Toast.LENGTH_LONG).show();
                    Log.e("Geo", "이미 10m 안에 들어와 있습니다." );
                    break;
            }

            List<Geofence> list = event.getTriggeringGeofences();

            Location location = event.getTriggeringLocation();
        }
        return Service.START_NOT_STICKY;
    }
}