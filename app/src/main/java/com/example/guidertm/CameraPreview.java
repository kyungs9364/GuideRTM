package com.example.guidertm;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by 경석 on 2016-09-08.
 */
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback{

    String TAG = "PAAR";
    SurfaceHolder previewHolder;
    Camera camera;
    boolean inPreview;

    public CameraPreview(Context context){
        super(context);

        inPreview = false;


        previewHolder = getHolder();
        previewHolder.addCallback(this);
        previewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

    }

    public void surfaceCreated(SurfaceHolder holder) {
        if(camera == null) {
            camera = Camera.open();
        }
        try{
            previewHolder.setKeepScreenOn(true);//화면 켜짐 유지
            camera.setPreviewDisplay(previewHolder);
        }
        catch (Throwable t)
        {
            Log.e(TAG, "Exception in setPreviewDisplay()", t);//오류 시 로그 출력
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {//surfaceVew가 안드로이드에 의해 변경될때 호출

        camera.startPreview();
        inPreview = true;
    }
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        //SurfaceView가 종료될때 호출
        camera.stopPreview();
        camera.release();
        camera=null;
    }
};