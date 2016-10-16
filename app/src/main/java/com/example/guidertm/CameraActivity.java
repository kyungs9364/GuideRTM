package com.example.guidertm;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by 경석 on 2016-09-08.
 */
//ㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇ
public class CameraActivity extends Activity {

    String TAG = "PAAR";
    CameraPreview mCameraPreview;
    CameraOverlayview mOverlayview = null;
    public static String latitude_ds;
    public static String longitude_ds;
    public static double Slatitude;
    public static double Slongitude;
    public static int count;
    public static int distance;
    public static int fix_nodedistance;
    public static double Ddistance;
    public static Context mContext;

    public static Double nodelon;
    public static Double nodelan;

    ArrayList<NodeData> node;
    RequestThread thread;
    private boolean stopflag = false;
    int i = 0;
    int j = 0;
    Location location;
    LocationManager locationManager;
    LocationListener locationListener;
    PendingIntent proximityIntent_10;
    PendingIntent proximityIntent_3;


    BroadcastReceiver mReceiver;



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mOverlayview = new CameraOverlayview(this);
        mContext = this;

        Intent intent = getIntent();
        latitude_ds = intent.getStringExtra("latitude_id");
        longitude_ds = intent.getStringExtra("longitude_id");
        Ddistance = intent.getDoubleExtra("distance", 0);
        //Log.e("distan","1 = "+Ddistance );

        node = (ArrayList<NodeData>) intent.getSerializableExtra("node");

        locationManager=(LocationManager) getSystemService(Context.LOCATION_SERVICE);


        IntentFilter intentfilter = new IntentFilter();
        intentfilter.addAction("com.example.guidertm.SEND_BROAD_CAST");

        //동적 리시버 구현
        mReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                Slatitude = intent.getDoubleExtra("sendlat", 0);
                Slongitude = intent.getDoubleExtra("sendlon", 0);

                Log.d(TAG, "sta_현위치 위도=" + Slatitude);
                Log.d(TAG, "sta_현위치 경도=" + Slongitude);

                mOverlayview.setCurrentPoint(Slatitude, Slongitude, (Ddistance + distance));

            }
        };
        registerReceiver(mReceiver, intentfilter);


        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        mContext.registerReceiver(((MainActivity) MainActivity.mContext).receiver, filter);



        Intent Pintent_10 = new Intent("com.example.guidertmcom.proximityalert_10");
        proximityIntent_10 = PendingIntent.getBroadcast(this, 0, Pintent_10, 0);
        IntentFilter Pfilter_10 = new IntentFilter("com.example.guidertmcom.proximityalert_10");
        registerReceiver(receiver_proxi_10, Pfilter_10);

        Intent Pintent_3 = new Intent("com.example.guidertmcom.proximityalert_3");
        proximityIntent_3 = PendingIntent.getBroadcast(this, 0, Pintent_3, 0);
        IntentFilter Pfilter_3 = new IntentFilter("com.example.guidertmcom.proximityalert_3");
        registerReceiver(receiver_proxi_3, Pfilter_3);


        check(1);  // 출발지의 정보는 보내지 않아도 됨으로 check(1)로 설정

        mOverlayview.setDestinationPoint(Double.parseDouble(latitude_ds), Double.parseDouble(longitude_ds));  // 목적지 값 overlayview 전송

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

                if (Slatitude != 0) {
                    Location locationA = new Location("Point A");
                    Location locationB = new Location("Point B");
                    locationA.setLongitude(Slongitude);
                    locationA.setLatitude(Slatitude);
                    locationB.setLongitude(nodelon);
                    locationB.setLatitude(nodelan);
                    distance = (int) locationA.distanceTo(locationB);
                    Log.d(TAG, "AtoB =  " + distance);
                    if (j == 0) {
                        fix_nodedistance = distance;
                        Ddistance = (Ddistance - fix_nodedistance);
                        //Log.e("distan","3 = "+fix_nodedistance );
                        //Log.e("distan","2 = "+Ddistance );
                        j++;
                    }
                } else {
                    Toast tMsg = Toast.makeText(getApplicationContext(), "다음노드 까지 거리 계산 중... ", Toast.LENGTH_LONG);
                    tMsg.setGravity(Gravity.CENTER, 0, 0);
                    tMsg.show();
                }


                if (i == 0) {
                    mOverlayview.setnode(nodelan, nodelon);
                    //((MainActivity) MainActivity.mContext).Geofence(nodelan, nodelon);

                    i++;
                }

                count = a;

                if(nodelan!=null)
                {
                    locationManager.addProximityAlert(nodelan, nodelon, 10f, -1, proximityIntent_10);
                    locationManager.addProximityAlert(nodelan,nodelon,3f,-1,proximityIntent_3);
                }
                //((MainActivity) MainActivity.mContext).Geofence_re(nodelan, nodelon);

                thread = new RequestThread();
                thread.start(); //check 함수를 일정시간마다 불러옴

                /*if (nodelan - 0.0001 < Slatitude && Slatitude < nodelan + 0.0001 && nodelon - 0.0001 < Slongitude && Slongitude < nodelon + 0.0001) {
                    mOverlayview.setdata(node.get(a).index, node.get(a).nodeType, Double.parseDouble(data[1]), Double.parseDouble(data[0]), node.get(a).turntype);
                    //Toast.makeText(getApplicationContext(), node.get(a).turntype , Toast.LENGTH_LONG).show();
                    check(a + 1);
                }*/
            } else if (node.get(a).nodeType.equals("LINE")) {
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
        j = 0;
        check(count + 1);
    }

    public BroadcastReceiver receiver_proxi_10 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            boolean isEntering = intent.getBooleanExtra(LocationManager.KEY_PROXIMITY_ENTERING, false);
            if (isEntering) {
                Toast.makeText(context, "10m 후에"+node.get(count).turntype+"입니다.", Toast.LENGTH_LONG).show();
                setpush();
            } /*else {
                Toast.makeText(context, "목표 지점에서 벗어납니다.", Toast.LENGTH_LONG).show();
            }*/
        }
    };

    public BroadcastReceiver receiver_proxi_3 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            boolean isEntering = intent.getBooleanExtra(LocationManager.KEY_PROXIMITY_ENTERING, false);
            if (isEntering) {
                Toast.makeText(context, "다음 경유지를 알려드리겠습니다", Toast.LENGTH_LONG).show();
                check_ch();
            } /*else {
                Toast.makeText(context, "목표 지점에서 벗어납니다.", Toast.LENGTH_LONG).show();
            }*/
        }
    };



    public void onPause() {
        if (mCameraPreview.inPreview) {
            mCameraPreview.camera.startPreview();
        }
        super.onPause();
        ((MainActivity) MainActivity.mContext).stopANDstart = false;
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
        mContext.unregisterReceiver(((MainActivity) MainActivity.mContext).receiver);
        unregisterReceiver(receiver_proxi_10);
        unregisterReceiver(receiver_proxi_3);
        locationManager.removeProximityAlert(proximityIntent_10);
        locationManager.removeProximityAlert(proximityIntent_3);
    }
}