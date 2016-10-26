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
 * Created by 경석 on 2016-09-08. igigigiigigigiigigi
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
    float mLowPassX;
    float mLowPassY;
    float mLowPassZ;
    public static String nodetype;
    public static String index;
    public static Double nodelan;
    public static Double nodelon;
    public static Double nodelan_arrow;
    public static Double nodelon_arrow;
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


    Bitmap LeftIcon1;
    Bitmap LeftIcon2;
    Bitmap LeftIcon3;
    Bitmap RigftIcon1;
    Bitmap RigftIcon2;
    Bitmap RigftIcon3;
    Bitmap BackIcon;

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

        double degree = (double) (Math.atan2((double) (nodelan_arrow - tAy)
                , (double) (nodelon_arrow - tAx)) * 180.0 / Math.PI);


        // 4/4분면을 고려하여 0~360도가 나오게 설정
        /*if (nodelon_arrow > tAx && nodelan_arrow > tAy) {
            ;
            Log.e("4/4", " = 1");
        } else if (nodelon_arrow < tAx && nodelan_arrow > tAy) {
            degree += 180;
            Log.e("4/4", " = 2");
        } else if (nodelon_arrow  < tAx && nodelan_arrow < tAy) {
            degree += 180;
            Log.e("4/4", " = 3");
        } else if (nodelon_arrow  > tAx && nodelan_arrow < tAy) {
            degree += 360;
            Log.e("4/4", " = 4");
        }*/

        if (degree + mXCompassDegree < 360) {
            degree += mXCompassDegree;
        } else if (degree + mXCompassDegree >= 360) {
            degree = degree + mXCompassDegree - 360;
        }

        if(degree<0)
        {
            degree+=360;
        }

        Log.d(TAG, "rrrrraaasa=" + String.valueOf(degree));

        mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.direct);
        mBitmap = Bitmap.createScaledBitmap(mBitmap, mWidth / 7, mHeight / 6, true);
        BackIcon = BitmapFactory.decodeResource(getResources(), R.drawable.back);
        BackIcon = Bitmap.createScaledBitmap(BackIcon, mWidth / 8, mHeight / 6, true);
        LeftIcon1 = BitmapFactory.decodeResource(getResources(), R.drawable.left_arrow1);
        LeftIcon1 = Bitmap.createScaledBitmap(LeftIcon1, mWidth / 8, mHeight / 6, true);
        LeftIcon2 = BitmapFactory.decodeResource(getResources(), R.drawable.left_arrow2);
        LeftIcon2 = Bitmap.createScaledBitmap(LeftIcon2, mWidth / 7, mHeight / 6, true);
        LeftIcon3 = BitmapFactory.decodeResource(getResources(), R.drawable.left_arrow3);
        LeftIcon3 = Bitmap.createScaledBitmap(LeftIcon3, mWidth / 8, mHeight / 6, true);
        RigftIcon1 = BitmapFactory.decodeResource(getResources(), R.drawable.right_arrow1);
        RigftIcon1 = Bitmap.createScaledBitmap(RigftIcon1, mWidth / 8, mHeight / 6, true);
        RigftIcon2 = BitmapFactory.decodeResource(getResources(), R.drawable.right_arrow2);
        RigftIcon2 = Bitmap.createScaledBitmap(RigftIcon2, mWidth / 7, mHeight / 6, true);
        RigftIcon3 = BitmapFactory.decodeResource(getResources(), R.drawable.right_arrow3);
        RigftIcon3 = Bitmap.createScaledBitmap(RigftIcon3, mWidth / 8, mHeight / 6, true);




        pCanvas.drawText("Point 까지 " + nodeAtoB + " m ", (mWidth * 2 / 7), (mHeight * 2 / 6), mTextPaint);
        pCanvas.drawText("Point 까지 " + nodeAtoB + " m ", (mWidth * 5 / 7), (mHeight * 2 / 6), mTextPaint);



        if(turntype == null || arrowchange==turntype) {
            if (degree >=340 || degree <=20) {
                //pCanvas.drawText("Point 까지 " + nodeAtoB + " m ", (mWidth * 5 / 12), (mHeight * 2 / 5), mTextPaint);
                pCanvas.drawBitmap(mBitmap, (mWidth * 2 / 9), (mHeight * 4 / 6), null);
                pCanvas.drawBitmap(mBitmap, (mWidth * 5 / 8), (mHeight * 4 / 6), null);
                //pCanvas.drawBitmap(mBitmap2, 1720, 820, null);
                //오른쪽 이미지
                //pCanvas.drawBitmap(mBitmap, (mWidth * 3 / 7), (mHeight * 3 / 5), null);
            }
            else if(degree>=300&&degree<340)
            {
                pCanvas.drawBitmap(RigftIcon1, (mWidth * 2 / 9), (mHeight * 4 / 6), null);
                pCanvas.drawBitmap(RigftIcon1, (mWidth * 5 / 8), (mHeight * 4 / 6), null);
            }
            else if(degree>=260&&degree<300)
            {
                pCanvas.drawBitmap(RigftIcon2, (mWidth * 2 / 9), (mHeight * 4 / 6), null);
                pCanvas.drawBitmap(RigftIcon2, (mWidth * 5 / 8), (mHeight * 4 / 6), null);
            }
            else if(degree>=220&&degree<260)
            {
                pCanvas.drawBitmap(RigftIcon3, (mWidth * 2 / 9), (mHeight * 4 / 6), null);
                pCanvas.drawBitmap(RigftIcon3, (mWidth * 5 / 8), (mHeight * 4 / 6), null);
            }
            else if(degree>140&&degree<220)
            {
                pCanvas.drawBitmap(BackIcon, (mWidth * 2 / 9), (mHeight * 4 / 6), null);
                pCanvas.drawBitmap(BackIcon, (mWidth * 5 / 8), (mHeight * 4 / 6), null);
            }
            else if(degree>20&&degree<=60)
            {
                pCanvas.drawBitmap(LeftIcon1, (mWidth * 2 / 9), (mHeight * 4 / 6), null);
                pCanvas.drawBitmap(LeftIcon1, (mWidth * 5 / 8), (mHeight * 4 / 6), null);
            }
            else if(degree>60&&degree<=100)
            {
                pCanvas.drawBitmap(LeftIcon2, (mWidth * 2 / 9), (mHeight * 4 / 6), null);
                pCanvas.drawBitmap(LeftIcon2, (mWidth * 5 / 8), (mHeight * 4 / 6), null);
            }
            else if(degree>100&&degree<=140)
            {
                pCanvas.drawBitmap(LeftIcon3, (mWidth * 2 / 9), (mHeight * 4 / 6), null);
                pCanvas.drawBitmap(LeftIcon3, (mWidth * 5 / 8), (mHeight * 4 / 6), null);
            }
        }


        else if(turntype != null && arrowchange!=turntype)
        {
            if(turntype.equals("좌회전") || turntype.equals("8시 방향 좌회전") || turntype.equals("10시 방향 좌회전")) {
                mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.left);
                mBitmap = Bitmap.createScaledBitmap(mBitmap, mWidth / 8, mHeight / 4, true);
                pCanvas.drawBitmap(mBitmap, (mWidth * 3 / 7), (mHeight * 3 / 5), null);
                //pCanvas.drawBitmap(LeftIcon, (mWidth * 3 / 7), (mHeight * 3 / 5), null);
                pCanvas.drawText(nodeDistace + "m 후에 " + turntype, (mWidth * 5 / 12), (mHeight * 2 / 5), mTextPaint);

                if (thread == null) // thread 가 null 일 경우만 실행
                {
                    thread = new RequestThread();
                    thread.start();
                }
            }
            else if(turntype.equals("우회전") || turntype.equals("2시 방향 우회전") || turntype.equals("4시 방향 우회전")) {
                mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.right);
                mBitmap = Bitmap.createScaledBitmap(mBitmap, mWidth / 8, mHeight / 4, true);
                pCanvas.drawBitmap(mBitmap, (mWidth * 3 / 7), (mHeight * 3 / 5), null);
                //pCanvas.drawBitmap(RigftIcon, (mWidth * 3 / 7), (mHeight * 3 / 5), null);
                pCanvas.drawText(nodeDistace+"m 후에 " +turntype, (mWidth * 5 / 12), (mHeight * 2 / 5), mTextPaint);

                if (thread == null) // thread 가 null 일 경우만 실행
                {
                    thread = new RequestThread();
                    thread.start();
                }
            }
            else if(turntype.equals("U-turn")) {
                mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.uturn);
                mBitmap = Bitmap.createScaledBitmap(mBitmap, mWidth / 8, mHeight / 4, true);
                pCanvas.drawBitmap(mBitmap, (mWidth * 3 / 7), (mHeight * 3 / 5), null);
                //pCanvas.drawBitmap(RigftIcon, (mWidth * 3 / 7), (mHeight * 3 / 5), null);
                pCanvas.drawText(nodeDistace + "m 후에 " + turntype, (mWidth * 5 / 12), (mHeight * 2 / 5), mTextPaint);

                if (thread == null) // thread 가 null 일 경우만 실행
                {
                    thread = new RequestThread();
                    thread.start();
                }
            }
            else if(turntype.equals("육교")) {
                mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.crossover);
                mBitmap = Bitmap.createScaledBitmap(mBitmap, mWidth / 8, mHeight / 4, true);
                pCanvas.drawBitmap(mBitmap, (mWidth * 3 / 7), (mHeight * 3 / 5), null);
                //pCanvas.drawBitmap(RigftIcon, (mWidth * 3 / 7), (mHeight * 3 / 5), null);
                pCanvas.drawText(nodeDistace + "m 후에 " + turntype, (mWidth * 5 / 12), (mHeight * 2 / 5), mTextPaint);

                if (thread == null) // thread 가 null 일 경우만 실행
                {
                    thread = new RequestThread();
                    thread.start();
                }
            }
            else if(turntype.equals("계단 진입") || turntype.equals("계단 + 경사로 진입")) {
                mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.stair);
                mBitmap = Bitmap.createScaledBitmap(mBitmap, mWidth / 8, mHeight / 4, true);
                pCanvas.drawBitmap(mBitmap, (mWidth * 3 / 7), (mHeight * 3 / 5), null);
                //pCanvas.drawBitmap(RigftIcon, (mWidth * 3 / 7), (mHeight * 3 / 5), null);
                pCanvas.drawText(nodeDistace + "m 후에 " + turntype, (mWidth * 5 / 12), (mHeight * 2 / 5), mTextPaint);

                if (thread == null) // thread 가 null 일 경우만 실행
                {
                    thread = new RequestThread();
                    thread.start();
                }
            }
            else if(turntype.equals("횡단보도") || turntype.equals("좌측 횡단보도") || turntype.equals("우측 횡단보도")
                    || turntype.equals("8시 방향 횡단보도") || turntype.equals("10시 방향 횡단보도")
                    || turntype.equals("2시 방향 횡단보도") || turntype.equals("4시 방향 횡단보도")) {
                mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.crosswalk);
                mBitmap = Bitmap.createScaledBitmap(mBitmap, mWidth / 8, mHeight / 4, true);
                pCanvas.drawBitmap(mBitmap, (mWidth * 3 / 7), (mHeight * 3 / 5), null);
                //pCanvas.drawBitmap(RigftIcon, (mWidth * 3 / 7), (mHeight * 3 / 5), null);
                pCanvas.drawText(nodeDistace + "m 후에 " + turntype, (mWidth * 5 / 12), (mHeight * 2 / 5), mTextPaint);

                if (thread == null) // thread 가 null 일 경우만 실행
                {
                    thread = new RequestThread();
                    thread.start();
                }
            }
            else if(turntype.equals("엘리베이터")) {
                mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.elevator);
                mBitmap = Bitmap.createScaledBitmap(mBitmap, mWidth / 8, mHeight / 4, true);
                pCanvas.drawBitmap(mBitmap, (mWidth * 3 / 7), (mHeight * 3 / 5), null);
                //pCanvas.drawBitmap(RigftIcon, (mWidth * 3 / 7), (mHeight * 3 / 5), null);
                pCanvas.drawText(nodeDistace + "m 후에 " + turntype, (mWidth * 5 / 12), (mHeight * 2 / 5), mTextPaint);

                if (thread == null) // thread 가 null 일 경우만 실행
                {
                    thread = new RequestThread();
                    thread.start();
                }
            }
            else
            {
                pCanvas.drawText(nodeDistace+"m 후에" +turntype,(mWidth * 5 / 12), (mHeight * 2 / 5),mTextPaint);
                if (thread == null) // thread 가 null 일 경우만 실행
                {
                    thread = new RequestThread();
                    thread.start();
                }
            }
        }


        // 4/4분면을 고려하여 0~360도가 나오게 설정
        /*if (tBx > tAx && tBy > tAy) {
            ;
            Log.e("4/4", " = r1");
        } else if (tBx < tAx && tBy > tAy) {
            mXDegree += 180;
            Log.e("4/4", " = r2");
        } else if (tBx < tAx && tBy < tAy) {
            mXDegree += 180;
            Log.e("4/4", " = r3");
        } else if (tBx > tAx && tBy < tAy) {
            mXDegree += 360;
            Log.e("4/4", " = r4");
        }*/



        // 두 위치간의 각도에 현재 스마트폰이 동쪽기준 바라보고 있는 방향 만큼 더해줌
        // 360도(한바퀴)가 넘었으면 한바퀴 회전한것이기에 360를 빼줌
        if (mXDegree + mXCompassDegree < 360) {
            mXDegree += mXCompassDegree;
        } else if (mXDegree + mXCompassDegree >= 360) {
            mXDegree = mXDegree + mXCompassDegree - 360;
        }

        if(mXDegree<0)
        {
            mXDegree+=360;
        }


        // 계산된 각도 만큼 기기 정중앙 화면 기준 어디에 나타날지 계산함
        float mX = 0;
        float mY = 0;

        Log.d(TAG, "rrrrrmXDegree=" + String.valueOf(mXDegree));
        // 동일
        if (mXDegree >=345 || mXDegree <=15) {
            if (mRDegree > 75 && mRDegree < 90) {

                Log.d(TAG,"wwwwwwwwwwmx=dddddddddd");
                if(mXDegree<=15)
                {
                    mX = ((float) mWidth
                            - (float) ((15+mXDegree) * ((float) mWidth / 30)))/2;
                }
                else if(mXDegree>=345)
                {
                    mX = ((float) mWidth
                            - (float) ((mXDegree - 345) * ((float) mWidth / 30)))/2;
                }


                mY = mHeight /(float)2.5;
                Log.d(TAG,"wwwwwwwwwwmx="+mY);
                Log.d(TAG,"wwwwwwwwwwmwidth="+mHeight);
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

                        pCanvas.drawBitmap(tIconBitmap, mX - (iconWidth / 2)+(mWidth * 3 / 7), mY
                                - (iconHeight / 2), pPaint);


                        pCanvas.drawText(distance + "m",
                                mX - pPaint.measureText(distance + "m") / 2
                                        + mShadowXMargin, mY + iconHeight / 2 + 60
                                        + mShadowYMargin, mShadowPaint);

                        pCanvas.drawText(distance + "m",
                                mX - pPaint.measureText(distance + "m") / 2, mY
                                        + iconHeight / 2 + 60, pPaint);


                        pCanvas.drawText(distance + "m",
                                mX - pPaint.measureText(distance + "m") / 2
                                        + mShadowXMargin+(mWidth * 3 / 7), mY + iconHeight / 2 + 60
                                        + mShadowYMargin, mShadowPaint);

                        pCanvas.drawText(distance + "m",
                                mX - pPaint.measureText(distance + "m") / 2 +(mWidth * 3 / 7), mY
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

                        pCanvas.drawBitmap(tIconBitmap, mX - (iconWidth / 2)+(mWidth * 3 / 7), mY
                                - (iconHeight / 2), pPaint);

                        pCanvas.drawText(fDistance + "Km",
                                mX - pPaint.measureText(fDistance + "Km") / 2
                                        + mShadowXMargin+(mWidth * 3 / 7), mY + iconHeight / 2 + 60
                                        + mShadowYMargin, mShadowPaint);

                        pCanvas.drawText(fDistance + "Km",
                                mX - pPaint.measureText(fDistance + "Km") / 2+(mWidth * 3 / 7), mY
                                        + iconHeight / 2 + 60, pPaint);

                    }
                }
            }
        }
        mHandler.sendEmptyMessage(0);
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

            mHandler.sendEmptyMessage(0);
        } /*else if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            xAxis = sensorEvent.values[0];
            yAxis = sensorEvent.values[1];
            zAxis = sensorEvent.values[2];
            mLowPassX = lowPass(xAxis, mLowPassX);
            mLowPassY = lowPass(yAxis, mLowPassY);
            mLowPassZ = lowPass(zAxis, mLowPassZ);
        }*/
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void initSensor(Context context) {
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        orientationSensor = Sensor.TYPE_ORIENTATION;
        //accelerometerSensor = Sensor.TYPE_ACCELEROMETER;
    }

    public void resumesensor()
    {
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(orientationSensor), SensorManager.SENSOR_DELAY_NORMAL);
        //sensorManager.registerListener(this, sensorManager.getDefaultSensor(accelerometerSensor), SensorManager.SENSOR_DELAY_NORMAL);
    }


    public void onDraw(Canvas canvas) {

        canvas.save();
        //canvas.rotate(180, mWidth / 2, mHeight / 2);//화면 돌리기

        getLocation(canvas);

        //canvas.restore();
    }

    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            invalidate();
            mHandler.sendEmptyMessageDelayed(0, 100);
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
        Log.d(TAG, "sta_la=" + String.valueOf(sta_latitude));  // 값이 들어가있나 확인용
        Log.d(TAG, "sta_lo=" + String.valueOf(sta_longitude));  // 동일
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

    public void setdata(String index, String nodetype, String turntype, int distance) {
        this.index = index;
        this.nodetype = nodetype;
        //this.nodelan = nodelan;
        //this.nodelon = nodelon;
        this.turntype = turntype;
        this.nodeDistace=distance;
        Log.e("Node", "index2=" + this.index);
        Log.e("Node", "nodetype2=" + this.nodetype);
        Log.e("Node", "nodelon2=" + String.valueOf(this.nodelon));
        Log.e("Node", "nodelan2=" + String.valueOf(this.nodelan));
        Log.e("Node", "turntype2=" + this.turntype);
        Log.e("Node", "distatncee2=" + this.nodeDistace);
    }
    public void setnode(Double nodelan, Double nodelon)   // 실시간 거리 변경을 위해 선언
    {
        this.nodelan_arrow = nodelan;
        this.nodelon_arrow = nodelon;
    }
    public void setAtoB(int distance)   // 실시간 거리 변경을 위해 선언
    {
        this.nodeAtoB=distance;
    }
    class RequestThread extends  Thread
    {
        public  void run() {
            try {
                Thread.sleep(5000);   // 5초 뒤에 실행
                arrowchange = turntype;
                thread = null; // 5초뒤에 thread 를 null 로 바꿔준다. (데이터 중복 현상 제거)

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // 카메라 액티비티가 소멸될때 센서 리스너를 해제
    public void viewDestory() {
        sensorManager.unregisterListener(this);
        mHandler.removeMessages(0);
    }
}