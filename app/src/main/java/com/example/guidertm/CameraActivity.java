package com.example.guidertm;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.ViewGroup;
import android.view.WindowManager;

import java.util.ArrayList;

/**
 * Created by 경석 on 2016-09-08.
 */

public class CameraActivity extends Activity {

    String TAG="PAAR";
    CameraPreview mCameraPreview;
    CameraOverlayview mOverlayview=null;
    public static String latitude_ds;
    public static String longitude_ds;
    public static double Slatitude;
    public static double Slongitude;
    public static int count;
    ArrayList<NodeData> node;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mOverlayview=new CameraOverlayview(this);
        Intent intent = getIntent();
        latitude_ds = intent.getStringExtra("latitude_id");
        longitude_ds = intent.getStringExtra("longitude_id");
        node = (ArrayList<NodeData>) intent.getSerializableExtra("node");

        //Slatitude = ((MainActivity) MainActivity.mContext).latitude_plic;  // 현재위치는 가져왔는데, 실시간 변경이 안됨..;
        //Slongitude = ((MainActivity) MainActivity.mContext).longitude_plic;

        check(1);  // 출발지의 정보는 보내지 않아도 됨으로 check(1)로 설정


        //Toast.makeText(getApplicationContext(), node.get(0).nodeType +"\n"+ node.get(0).coordinate +"\n"+ node.get(0).turntype, Toast.LENGTH_SHORT).show();

        //mOverlayview.setCurrentPoint(Double.parseDouble(latitude_st),Double.parseDouble(longitude_st));  // 현재위치 값 overlayview 전송
        mOverlayview.setDestinationPoint(Double.parseDouble(latitude_ds), Double.parseDouble(longitude_ds));  // 목적지 값 overlayview 전송
    }

    public void check(int a)
    {
        if(a>node.size())
            return ;
        else {
            if(node.get(a).nodeType.equals("POINT")) {
                String[] data = node.get(a).coordinate.split(",");
                Double nodelon = Double.parseDouble(data[0]);
                Double nodelan = Double.parseDouble(data[1]);
                Log.e("NODE", "nodelon=" + nodelon);
                Log.e("NODE","lon="+Slongitude);
                Log.e("NODE", "nodelan="+ nodelan);
                Log.e("NODE","lan="+Slatitude);

                Location locationA = new Location("Point A");
                Location locationB = new Location("Point B");
                locationA.setLongitude(Slongitude);
                locationA.setLatitude(Slatitude);
                locationB.setLongitude(nodelon);
                locationB.setLatitude(nodelan);
                int distance = (int) locationA.distanceTo(locationB);
                Log.d(TAG, "AtoB =  " + distance);

                count = a;

                if(distance < 10) // 10m(오차범위) 이내가 되면 노드정보를 overlayview에 전송
                {
                    mOverlayview.setdata(node.get(a).index, node.get(a).nodeType, Double.parseDouble(data[1]), Double.parseDouble(data[0]), node.get(a).turntype);
                    check(a + 1);
                    //Log.e("NODE","check a" + a);
                }
                else {
                    RequestThread thread = new RequestThread();
                    thread.start(); //check 함수를 일정시간마다 불러옴
                }

                /*if (nodelan - 0.0001 < Slatitude && Slatitude < nodelan + 0.0001 && nodelon - 0.0001 < Slongitude && Slongitude < nodelon + 0.0001) {
                    mOverlayview.setdata(node.get(a).index, node.get(a).nodeType, Double.parseDouble(data[1]), Double.parseDouble(data[0]), node.get(a).turntype);
                    //Toast.makeText(getApplicationContext(), node.get(a).turntype , Toast.LENGTH_LONG).show();
                    check(a + 1);
                }*/
            }
            else {
                check(a + 1);
            }
        }
    }


    public void onResume() {
        super.onResume();

        mCameraPreview = new CameraPreview(this);
        mOverlayview=new CameraOverlayview(this);


        Display display = ((WindowManager) this
                .getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int height = display.getHeight();
        int width = display.getWidth();

        mOverlayview.setOverlaySize((int) width, height);

        setContentView(mCameraPreview, new ViewGroup.LayoutParams((int) width,
                height));
        addContentView(mOverlayview, new ViewGroup.LayoutParams((int) width,
                height));

        /*GpslocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);//위치 관리자 객체 구하기
        GpslocationListener = new LocationListener() {//리스너 정의
            @Override
            public void onLocationChanged(Location location) { //위치 업데이트시 리스너 호출
                double latitude;//위도
                double longitude;//경도
                //변경되는 location값 받는 메소드
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                Log.d(TAG, "latitude=" + String.valueOf(latitude));
                Log.d(TAG, "longitude=" + String.valueOf(longitude));
                mOverlayview.setCurrentPoint(latitude, longitude);
            }
            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }
            @Override
            public void onProviderEnabled(String provider) {
            }
            @Override
            public void onProviderDisabled(String provider) {
            }
        };*/

        //GpslocationManager.requestLocationUpdates(GpslocationManager.GPS_PROVIDER, 0, 0,GpslocationListener);
        //(서비스제공자의 상수값, 업데이트간격,기기 움직이는 미터 단위의 최소거리, 알림을 받을 locationListener)
    }

    class RequestThread extends  Thread
    {
        public  void run()
        {
            try
            {
                Thread.sleep(3000);   // 3초 뒤에 실행
                check(count);
            }catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
    public void setCurrent(double latitude_st, double longitude_st)//현위치 좌표 정보 얻기
    {
        this.Slatitude =latitude_st;
        this.Slongitude =longitude_st;
        Log.d(TAG, "cameraactivity sta_la=" + String.valueOf(Slatitude));  // 값이 들어가있나 확인용
        Log.d(TAG, "cameraactivity sta_lo=" + String.valueOf(Slongitude));  // 동일
    }

    public void onPause() {
        if (mCameraPreview.inPreview) {
            mCameraPreview.camera.startPreview();
        }
        super.onPause();
    }

    protected void onDestroy() {
        super.onDestroy();
        latitude_ds = null;
        longitude_ds = null;
    }
}