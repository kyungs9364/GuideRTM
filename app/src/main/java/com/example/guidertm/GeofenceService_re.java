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
 * Created by 경석 on 2016-10-01.
 */
public class GeofenceService_re extends Service {
    public GeofenceService_re() {

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
                    ((CameraActivity) CameraActivity.mContext).check_ch();
                    Toast.makeText(getApplicationContext(), "3m 이내에 진입하였습니다. 다음 경유지를 가르킵니다.", Toast.LENGTH_SHORT).show();
                    Log.e("Geo", "check2");
                    break;
                //case Geofence.GEOFENCE_TRANSITION_EXIT:
                case Geofence.GEOFENCE_TRANSITION_DWELL:
                    ((CameraActivity) CameraActivity.mContext).check_ch();
                    break;
            }

            List<Geofence> list = event.getTriggeringGeofences();

            Location location = event.getTriggeringLocation();
        }
        return Service.START_NOT_STICKY;
    }

}