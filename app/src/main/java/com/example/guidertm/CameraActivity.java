package com.example.guidertm;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
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
    public static Context mContext;

    public static Double nodelon;
    public static Double nodelan;

    int i=0;
    ArrayList<NodeData> node;
    RequestThread thread;

    private boolean stopflag = false;
    BroadcastReceiver mReceiver;
    private static boolean flag=false;




    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mOverlayview = new CameraOverlayview(this);
        mContext = this;

        Intent intent = getIntent();
        latitude_ds = intent.getStringExtra("latitude_id");
        longitude_ds = intent.getStringExtra("longitude_id");

        node = (ArrayList<NodeData>) intent.getSerializableExtra("node");

        mOverlayview.setDestinationPoint(Double.parseDouble(latitude_ds), Double.parseDouble(longitude_ds));  // 목적지 값 overlayview 전송

        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        mContext.registerReceiver(((MainActivity) MainActivity.mContext).receiver, filter);


        //destination_complete();
        check(1);  // 출발지의 정보는 보내지 않아도 됨으로 check(1)로 설정

    }


    public void check(int a) {
        if (node.get(a).nodeType.equals("POINT")) {

            Log.e("ccchk","2");
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

            } else {
                Toast tMsg = Toast.makeText(getApplicationContext(), "다음노드 까지 거리 계산 중... ", Toast.LENGTH_LONG);
                tMsg.setGravity(Gravity.CENTER, 0, 0);
                tMsg.show();
            }

            if (i == 0) {
                mOverlayview.setnode(nodelan, nodelon);
                i++;
            }

            count=a;


            if(distance<=18)
            {
                mOverlayview.setdata(node.get(a).index, node.get(a).nodeType, node.get(a).turntype, distance);
                Log.d(TAG, "pupush");
                if(distance<=12)
                {
                        i=0;
                        check(a+1);
                        //Toast.makeText(this,"다음 경유지를 알려드리겠습니다.",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    thread = new RequestThread();
                    thread.start(); //check 함수를 일정시간마다 불러옴
                }
            }
            else
            {
                thread = new RequestThread();
                thread.start(); //check 함수를 일정시간마다 불러옴
            }


        } else if (node.get(a).nodeType.equals("LINE")){
            Log.e("ccchk","1");
            check(a + 1);
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
                    Thread.sleep(2000);   // 3초 뒤에 실행
                    check(count);
                    mOverlayview.setAtoB(distance);  // 노드까지의 실시간 거리 변경을 위해 선언
                    break;  // 중복적 호출을 방지하기 위해 break;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }



    public void setCurrent(double lan,double lon)
    {
        this.Slatitude=lan;
        this.Slongitude=lon;

        Log.d("cameraactivity",String.valueOf(Slatitude)+","+String.valueOf(Slongitude));
    }

    public void destination_complete()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);  // 팝업창을 띄워주기 위해 생성
        builder.setTitle("목적지에 도착하셨습니다.");
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.show();
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
        // unregisterReceiver(mReceiver);
        mContext.unregisterReceiver(((MainActivity) MainActivity.mContext).receiver);
    }
}