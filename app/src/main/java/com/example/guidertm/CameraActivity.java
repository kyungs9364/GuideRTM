package com.example.guidertm;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
    ArrayList<NodeData> node;
    //LocationManager GpslocationManager;
    //LocationListener GpslocationListener;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mOverlayview=new CameraOverlayview(this);
        Intent intent = getIntent();
        latitude_ds = intent.getStringExtra("latitude_id");
        longitude_ds = intent.getStringExtra("longitude_id");
        // latitude_st = intent.getStringExtra("st_latitude_id");
        // longitude_st = intent.getStringExtra("st_longitude_id");
        node = (ArrayList<NodeData>) intent.getSerializableExtra("node");
        //Toast.makeText(getApplicationContext(), ""+latitude_st+","+longitude_st+"", Toast.LENGTH_SHORT).show();

        mOverlayview.setdata(node.get(0).nodeType);


        //mOverlayview.setCurrentPoint(Double.parseDouble(latitude_st),Double.parseDouble(longitude_st));  // 현재위치 값 overlayview 전송
        mOverlayview.setDestinationPoint(Double.parseDouble(latitude_ds), Double.parseDouble(longitude_ds));  // 목적지 값 overlayview 전송
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