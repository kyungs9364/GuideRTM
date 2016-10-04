package com.example.guidertm;

import android.app.Activity;
import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.Display;
import android.view.ViewGroup;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.jar.Manifest;

/**
 * Created by 경석 on 2016-09-08.
 */

public class CameraActivity extends Activity{

    String TAG = "PAAR";
    CameraPreview mCameraPreview;
    CameraOverlayview mOverlayview = null;
    GeofenceService mGeofenceService;
    public static String latitude_ds;
    public static String longitude_ds;
    public static double Slatitude;
    public static double Slongitude;
    public static int count;
    public static int distance;
    public static Context mContext;

    public static Double nodelon;
    public static Double nodelan;


    ArrayList<NodeData> node;
    RequestThread thread;
    private boolean stopflag = false;
    int i = 0;

    BroadcastReceiver mReceiver;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mOverlayview = new CameraOverlayview(this);
        mGeofenceService = new GeofenceService();
        mContext = this;

        Intent intent = getIntent();
        latitude_ds = intent.getStringExtra("latitude_id");
        longitude_ds = intent.getStringExtra("longitude_id");
        node = (ArrayList<NodeData>) intent.getSerializableExtra("node");

        IntentFilter intentfilter = new IntentFilter();
        intentfilter.addAction("com.example.guidertm.SEND_BROAD_CAST");


        //동적 리시버 구현
        mReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                Slatitude = intent.getDoubleExtra("sendlat",0);
                Slongitude = intent.getDoubleExtra("sendlon",0);
                Log.d(TAG, "현위치 위도="+Slatitude);
                Log.d(TAG, "현위치 경도="+Slongitude);
            }
        };
        registerReceiver(mReceiver, intentfilter);

        check(1);  // 출발지의 정보는 보내지 않아도 됨으로 check(1)로 설정

        mOverlayview.setDestinationPoint(Double.parseDouble(latitude_ds), Double.parseDouble(longitude_ds));  // 목적지 값 overlayview 전송
        //((MainActivity) MainActivity.mContext).Geofence(nodelan, nodelon);
    }


    public void check(int a) {
        if (a > node.size())
            return;
        else {
            if (node.get(a).nodeType.equals("POINT")) {
                String[] data = node.get(a).coordinate.split(",");
                /*Double*/
                nodelon = Double.parseDouble(data[0]);
                /*Double*/
                nodelan = Double.parseDouble(data[1]);
                Log.e("NODE", "a=" + a);
                Log.e("NODE", "nodelon=" + nodelon);
                Log.e("NODE", "lon=" + Slongitude);
                //.e("NODE", "nodelan="+ nodelan);
                // Log.e("NODE","lan="+Slatitude);

                Location locationA = new Location("Point A");
                Location locationB = new Location("Point B");
                locationA.setLongitude(Slongitude);
                locationA.setLatitude(Slatitude);
                locationB.setLongitude(nodelon);
                locationB.setLatitude(nodelan);
                distance = (int) locationA.distanceTo(locationB);
                Log.d(TAG, "AtoB =  " + distance);

                if (i == 0) {
                    mOverlayview.setnode(nodelan, nodelon);
                    i++;
                }

                count = a;
                //((MainActivity) MainActivity.mContext).Geofence(nodelan, nodelon);
                //((MainActivity) MainActivity.mContext).Geofence_re(nodelan, nodelon);

                thread = new RequestThread();
                thread.start(); //check 함수를 일정시간마다 불러옴

            }

            else if (node.get(a).nodeType.equals("LINE")) {
                check(a + 1);
            }
        }
    }

    public void onResume() {
        super.onResume();

        mCameraPreview = new CameraPreview(this);
        mOverlayview = new CameraOverlayview(this);


        Display display = ((WindowManager) this
                .getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int height = display.getHeight();
        int width = display.getWidth();

        mOverlayview.setOverlaySize((int) width, height);

        setContentView(mCameraPreview, new ViewGroup.LayoutParams((int) width,
                height));
        addContentView(mOverlayview, new ViewGroup.LayoutParams((int) width,
                height));

        mOverlayview.resumesensor();
    }

    class RequestThread extends Thread {
        public void run() {
            while (!stopflag) {
                try {
                    Thread.sleep(3000);   // 3초 뒤에 실행
                    check(count);
                    mOverlayview.setAtoB(distance);  // 노드까지의 실시간 거리 변경을 위해 선언
                    break;  // 중복적 호출을 방지하기 위해 break;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void setpush() {
        mOverlayview.setdata(node.get(count).index, node.get(count).nodeType, nodelan, nodelon, node.get(count).turntype, distance);
    }

    public void check_ch() {
        i = 0;
        check(count + 1);
    }

    public void onPause() {
        if (mCameraPreview.inPreview) {
            mCameraPreview.camera.startPreview();
        }
        super.onPause();
    }

    public void onStop() {
        super.onStop();
    }

    protected void onDestroy() {
        super.onDestroy();
        //thread.interrupt();
        latitude_ds = null;
        longitude_ds = null;
        stopflag = true;
        mOverlayview.viewDestory();
        unregisterReceiver(mReceiver);
    }

}