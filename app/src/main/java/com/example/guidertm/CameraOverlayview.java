package com.example.guidertm;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;

/**
 * Created by 경석 on 2016-09-08.
 */
public class CameraOverlayview extends View implements SensorEventListener {

    SensorManager sensorManager;

    private String TAG = "PAAR";

    int orientationSensor;
    float headingAngle;
    float pitchAngle;
    float rollAnlge;

    int accelerometerSensor;
    float xAxis;//x축
    float yAxis;//y축
    float zAxis;//z축

    float a = 0.8f;
    float mLowPassHeading;
    float mLowPassPitch;
    float mLowPassRoll;
    float mLowPassX;
    float mLowPassY;
    float mLowPassZ;
    public static String nodetype;
    public static String index;
    public static Double nodelan;
    public static Double nodelon;
    public static String turntype;
    public static double sta_latitude;
    public static double sta_longitude;
    public static double des_latitude;
    public static double des_longitude;
    public static double distance;
    Bitmap mPalaceIconBitmap;
    Bitmap mBitmap;
    public static int nodeDistace; // 실시간 x
    public static int nodeAtoB; // 실시간 거리 체크
    Bitmap LeftIcon;
    Bitmap RigftIcon;
    public static int mWidth;
    public static int mHeight;
    Paint mPaint;
    int mShadowXMargin;
    int mShadowYMargin;
    Paint mShadowPaint;
    Paint mTextPaint;
    int mVisibleDistance = 10;
    float mXCompassDegree;
    float mYCompassDegree;
    float mRCompassDegree;

    RequestThread thread;
    public static String arrowchange; // 화살표 스위칭을 위해 선언


    // CameraActivity mContext;
    // MainActivity mainActivity;


    public CameraOverlayview(Context context) {
        super(context);
        //mContext = (CameraActivity) context;
        //mainActivity = (MainActivity) context;
        initBitamaps();
        initSensor(context);
        initPaints();
    }

    private void initBitamaps() {
        // TODO Auto-generated method stub
        mPalaceIconBitmap = BitmapFactory.decodeResource(getResources(),
                R.drawable.place);
        mPalaceIconBitmap = Bitmap.createScaledBitmap(mPalaceIconBitmap, 100,
                100, true);


    }


    private void drawGrid(double tAx, double tAy, double tBx, double tBy,
                          Canvas pCanvas, Paint pPaint, double Ddistance) {

        // TODO Auto-generated method stub

        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setTextSize(25);
        // 현재 위치와 랜드마크의 위치를 계산하는 공식
        double mXDegree = (double) (Math.atan2((double) (tBy - tAy)
                , (double) (tBx - tAx)) * 180.0 / Math.PI);
        float mYDegree = mYCompassDegree; // 기기의 기울임각도
        float mRDegree = mRCompassDegree;

        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(Color.WHITE);
        mTextPaint.setTextSize(35);


        if(turntype == null || arrowchange==turntype) {
            mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.kok);
            mBitmap = Bitmap.createScaledBitmap(mBitmap, mWidth / 8, mHeight / 4, true);
            pCanvas.drawBitmap(mBitmap, (mWidth * 3 / 7), (mHeight * 3 / 5), null);
            pCanvas.drawText("Point 까지 " + nodeAtoB + " m " , (mWidth * 5 / 12), (mHeight * 2 / 5), mTextPaint);
        }
        else if(turntype != null && arrowchange!=turntype)
        {
            if(turntype.equals("좌회전")) {
                mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.left);
                mBitmap = Bitmap.createScaledBitmap(mBitmap, mWidth / 8, mHeight / 4, true);
                pCanvas.drawBitmap(mBitmap, (mWidth * 3 / 7), (mHeight * 3 / 5), null);
                //pCanvas.drawBitmap(LeftIcon, (mWidth * 3 / 7), (mHeight * 3 / 5), null);
                pCanvas.drawText(nodeDistace + "m 후에 " + turntype, (mWidth * 5 / 12), (mHeight * 2 / 5), mTextPaint);

                thread = new RequestThread();
                thread.start();
            }
            else if(turntype.equals("우회전")) {
                mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.right);
                mBitmap = Bitmap.createScaledBitmap(mBitmap, mWidth / 8, mHeight / 4, true);
                pCanvas.drawBitmap(mBitmap, (mWidth * 3 / 7), (mHeight * 3 / 5), null);
                //pCanvas.drawBitmap(RigftIcon, (mWidth * 3 / 7), (mHeight * 3 / 5), null);
                pCanvas.drawText(nodeDistace+"m 후에 " +turntype, (mWidth * 5 / 12), (mHeight * 2 / 5), mTextPaint);

                thread = new RequestThread();
                thread.start();
            }
        }


        // 4/4분면을 고려하여 0~360도가 나오게 설정
        /*if (tBx > tAx && tBy > tAy) {
            ;
        } else if (tBx < tAx && tBy > tAy) {
            mXDegree += 180;
        } else if (tBx < tAx && tBy < tAy) {
            mXDegree += 180;
        } else if (tBx > tAx && tBy < tAy) {
            mXDegree += 360;
        }*/

        // 두 위치간의 각도에 현재 스마트폰이 동쪽기준 바라보고 있는 방향 만큼 더해줌
        // 360도(한바퀴)가 넘었으면 한바퀴 회전한것이기에 360를 빼줌
        if (mXDegree + mXCompassDegree < 360) {
            mXDegree += mXCompassDegree;
        } else if (mXDegree + mXCompassDegree >= 360) {
            mXDegree = mXDegree + mXCompassDegree - 360;
        }


        // 계산된 각도 만큼 기기 정중앙 화면 기준 어디에 나타날지 계산함
        float mX = 0;
        float mY = 0;

        Log.d(TAG, "rrrrrmXDegree=" + String.valueOf(mXDegree));
        // 동일
        if (mXDegree >165 && mXDegree <195) {
            if (mRDegree > -90 && mRDegree < -75) {

                mX = (float) mWidth
                        - (float) ((mXDegree - 165) * ((float) mWidth / 20));

                mRDegree = -(mRDegree);

                mY = (mRDegree * ((float) mHeight / 180));


                // icon의  핸드폰 디스플레이 위치값(값이 변경될때마다 흔들림)
                // 두 위치간의 거리를 계산함
                    /*Location locationA = new Location("Point A");
                    Location locationB = new Location("Point B");
                    locationA.setLongitude(tAx);
                    locationA.setLatitude(tAy);
                    locationB.setLongitude(tBx);
                    locationB.setLatitude(tBy);
                    int distance = (int) locationA.distanceTo(locationB);*/

                Bitmap tIconBitmap = mPalaceIconBitmap;
                int iconWidth, iconHeight;
                iconWidth = tIconBitmap.getWidth();
                iconHeight = tIconBitmap.getHeight();


                int distance = (new Double(Ddistance)).intValue(); // int로 형변환.
                pCanvas.drawBitmap(tIconBitmap, mX - (iconWidth / 2), mY - (iconHeight / 2), pPaint);
                // 거리는 1000미터 이하와 초과로 나누어 m, Km로 출력
                if (distance <= mVisibleDistance * 1000) {
                    if (distance < 1000) {
                        pCanvas.drawBitmap(tIconBitmap, mX - (iconWidth / 2), mY
                                - (iconHeight / 2), pPaint);

                        pCanvas.drawText(distance + "m",
                                mX - pPaint.measureText(distance + "m") / 2
                                        + mShadowXMargin, mY + iconHeight / 2 + 60
                                        + mShadowYMargin, mShadowPaint);

                        pCanvas.drawText(distance + "m",
                                mX - pPaint.measureText(distance + "m") / 2, mY
                                        + iconHeight / 2 + 60, pPaint);

                    } else if (distance >= 1000) {
                        float fDistance = (float) distance / 1000;
                        fDistance = (float) Math.round(fDistance * 10) / 10;

                        pCanvas.drawBitmap(tIconBitmap, mX - (iconWidth / 2), mY
                                - (iconHeight / 2), pPaint);

                        pCanvas.drawText(fDistance + "Km",
                                mX - pPaint.measureText(fDistance + "Km") / 2
                                        + mShadowXMargin, mY + iconHeight / 2 + 60
                                        + mShadowYMargin, mShadowPaint);

                        pCanvas.drawText(fDistance + "Km",
                                mX - pPaint.measureText(fDistance + "Km") / 2, mY
                                        + iconHeight / 2 + 60, pPaint);

                    }
                }
            }
        }
        mHandler.sendEmptyMessageDelayed(0, 10);
    }

    //로우패스 필터
    float lowPass(float current, float last) {
        return last * (1.0f - a) + current * a;//새로운 값 = 이전값*(1-가중치) + 현재값 * 가중치
    }

    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ORIENTATION) {
            headingAngle = sensorEvent.values[0];
            pitchAngle = sensorEvent.values[1];
            rollAnlge = sensorEvent.values[2];

            Log.d(TAG, "head=" + String.valueOf(headingAngle));
            Log.d(TAG, "roll=" + String.valueOf(rollAnlge));


            mXCompassDegree = lowPass(headingAngle, mXCompassDegree);
            mYCompassDegree = lowPass(pitchAngle, mYCompassDegree);
            mRCompassDegree = lowPass(rollAnlge, mRCompassDegree);

            Log.d(TAG, "mXDegree=" + String.valueOf(mXCompassDegree));
            Log.d(TAG, "mYD=" + String.valueOf(mYCompassDegree));


        } else if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            xAxis = sensorEvent.values[0];
            yAxis = sensorEvent.values[1];
            zAxis = sensorEvent.values[2];


            mLowPassX = lowPass(xAxis, mLowPassX);
            mLowPassY = lowPass(yAxis, mLowPassY);
            mLowPassZ = lowPass(zAxis, mLowPassZ);

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void initSensor(Context context) {
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        orientationSensor = Sensor.TYPE_ORIENTATION;
        accelerometerSensor = Sensor.TYPE_ACCELEROMETER;
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(orientationSensor), SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(accelerometerSensor), SensorManager.SENSOR_DELAY_NORMAL);

    }

    public void onDraw(Canvas canvas) {

        canvas.save();
        canvas.rotate(180, mWidth / 2, mHeight / 2);//화면 돌리기

        getLocation(canvas);

        canvas.restore();
    }

    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            invalidate();
            mHandler.sendEmptyMessageDelayed(0, 10);
        }
    };

    private void initPaints() {
        // TODO Auto-generated method stub
        mShadowXMargin = 2;
        mShadowYMargin = 2;

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.rgb(238, 229, 222));
        mPaint.setTextSize(25);

        mShadowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mShadowPaint.setColor(Color.BLACK);
        mShadowPaint.setTextSize(25);
    }

    public void getLocation(Canvas canvas) {
        double tAx, tAy, tBx, tBy;
        tAx = sta_longitude;//현위치 경도좌표
        tAy = sta_latitude;//현위치 위도좌표
        tBx = des_longitude;//임의 경도좌표
        tBy = des_latitude;//임의 위도 좌표
        //drawGrid(tAx,tAy,tBx,tBy,canvas,mPaint);//이미지 그려주기
        drawGrid(tAx, tAy, tBx, tBy, canvas, mPaint, distance);

    }

    public void setCurrentPoint(double latitude_st, double longitude_st, double distance)//현위치 좌표 정보 얻기
    {
        this.sta_latitude = latitude_st;
        this.sta_longitude = longitude_st;
        this.distance = distance;
        //Log.d(TAG, "sta_la=" + String.valueOf(sta_latitude));  // 값이 들어가있나 확인용
        //Log.d(TAG, "sta_lo=" + String.valueOf(sta_longitude));  // 동일
        Log.d(TAG, "distance=" + String.valueOf(distance));
    }

    public void setDestinationPoint(double latitude_ds, double longitude_ds)//도착지 좌표 정보 얻기
    {
        this.des_latitude = latitude_ds;
        this.des_longitude = longitude_ds;
        Log.d(TAG, "des_la=" + String.valueOf(des_latitude));  // 값이 들어가있나 확인용
        Log.d(TAG, "des_lo=" + String.valueOf(des_longitude));  // 동일
    }

    // 카메라 액티비티에서 오버레이 화면 크기를 설정함
    public void setOverlaySize(int width, int height) {
        // TODO Auto-generated method stub
        mWidth = width;
        mHeight = height;
    }

    public void setdata(String index, String nodetype, Double nodelan, Double nodelon, String turntype, int distance) {
        this.index = index;
        this.nodetype = nodetype;
        this.nodelan = nodelan;
        this.nodelon = nodelon;
        this.turntype = turntype;
        this.nodeDistace=distance;
        Log.e("Node", "index2=" + this.index);
        Log.e("Node", "nodetype2=" + this.nodetype);
        Log.e("Node", "nodelon2=" + String.valueOf(this.nodelon));
        Log.e("Node", "nodelan2=" + String.valueOf(this.nodelan));
        Log.e("Node", "turntype2=" + this.turntype);
        Log.e("Node", "distatncee2=" + this.nodeDistace);
    }

    public void setAtoB(int distance)   // 실시간 거리 변경을 위해 선언
    {
        this.nodeAtoB=distance;
    }
    class RequestThread extends  Thread
    {
        public  void run() {
            try {
                Thread.sleep(5000);   // 3초 뒤에 실행
                arrowchange = turntype;

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}