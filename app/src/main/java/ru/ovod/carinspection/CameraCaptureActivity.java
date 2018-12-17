package ru.ovod.carinspection;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;

import ru.ovod.carinspection.helpers.SysHelper;
import ru.ovod.carinspection.pojo.Inspection;
import ru.ovod.carinspection.pojo.Photo;

public class CameraCaptureActivity extends AppCompatActivity implements SurfaceHolder.Callback {

    private Camera mCamera;
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private Button capture_image;

    private SysHelper sysHelper;
    private Inspection inspection;
    private int cameraId;
    private boolean cameraConfigured=false;
    private int angle;

    private static String TAG = "CameraCaptureActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_layout);

        sysHelper = SysHelper.getInstance(this);
        Intent intent = getIntent();
        inspection = intent.getParcelableExtra("Inspection");


        capture_image = (Button) findViewById(R.id.capture_image);
        capture_image.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                capture();
            }
        });
        surfaceView = (SurfaceView) findViewById(R.id.surfaceview);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(CameraCaptureActivity.this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        openCamera();
    }

    private void openCamera(){
        cameraId = getCameraId();
        mCamera = Camera.open(cameraId);
        angle = setCameraDisplayOrientation((Activity)this, cameraId, mCamera);
        initPreview(surfaceHolder, surfaceView.getWidth(), surfaceView.getHeight(), angle);
        mCamera.startPreview();
    }

    private int getCameraId(){
        int numberOfCameras = android.hardware.Camera.getNumberOfCameras();
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        for (int i = 0; i < numberOfCameras; i++) {
            android.hardware.Camera.getCameraInfo(i, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                return i;
            }
        }
        return 0;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mCamera.stopPreview();
                if (mCamera != null) {
                    mCamera.release();
                    mCamera = null;
                }

                Intent answerIntent = new Intent();
                setResult(RESULT_OK, answerIntent);
                finish();
                return true;

        }

        return super.onOptionsItemSelected(item);
    }

    private void capture() {
        angle = setCameraDisplayOrientation(this, cameraId, mCamera);
        Camera.Parameters parameters=mCamera.getParameters();
        parameters.setRotation(angle);
        mCamera.setParameters(parameters);

        mCamera.takePicture(null, null, null, new Camera.PictureCallback() {

            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                Context context = sysHelper.getApplicationContext();
                try {
                    sysHelper.getPhotoHelper().createImageFile(context, "Order", inspection.getNumber());
                } catch (Exception e) {
                    Log.e(TAG, "createImageFile unhandled error");
                }

                String PATH = sysHelper.getPhotoHelper().getmCurrentPhotoPath();
                try {
                    FileOutputStream fos=new FileOutputStream(PATH);

                    fos.write(data);
                    fos.close();
                }
                catch (java.io.IOException e) {

                }

                Photo photo = new Photo(0
                        ,sysHelper.getPhotoHelper().getmCurrentPhotoPath()
                        ,sysHelper.getPhotoHelper().getmCurrentPhotoName(), 0, inspection.get_inspectionid());
                photo = sysHelper.getDbhelper().insPhoto(photo);
                if (photo != null) {
                    sysHelper.showToAst("Фотография сохранена");
                }

                mCamera.startPreview();
            }
        });
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
        Log.e(TAG, "Surface Changed, format   ==   " + format + ",   width  ===  "
                + width + ", height   ===    " + height);
        initPreview(holder, width, height, angle);
        mCamera.startPreview();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.e(TAG, "Surface Created");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.e(TAG, "Surface Destroyed");
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mCamera == null) {
            openCamera();
        }
    }

    private void initPreview(SurfaceHolder holder, int width, int height, int angle) {
        if (mCamera != null && holder.getSurface() != null) {
            try {
                mCamera.setPreviewDisplay(holder);
            }
            catch (Throwable t) {
                Log.e(TAG,
                        "Exception in setPreviewDisplay()", t);
                sysHelper.showToAst(t.getMessage());
            }

            if (!cameraConfigured) {
                Camera.Parameters parameters=mCamera.getParameters();
                Camera.Size size=getBestPreviewSize(width, height, parameters);
                Camera.Size pictureSize=getLargestPictureSize(parameters);

                if (size != null && pictureSize != null) {
                    parameters.setSceneMode(mCamera.getParameters().SCENE_MODE_HDR);
                    parameters.setJpegThumbnailSize(400,400);
                    parameters.set("anti-shake", 1);


                    List<String> SceneModes = mCamera.getParameters().getSupportedSceneModes();
                    for (String mode: SceneModes){
                        if (mode == mCamera.getParameters().SCENE_MODE_STEADYPHOTO){
                            parameters.setSceneMode(mCamera.getParameters().SCENE_MODE_STEADYPHOTO);
                            break;
                        }
                    }

                    parameters.setPreviewSize(size.width, size.height);
                    parameters.setPictureSize(pictureSize.width,
                            pictureSize.height);

                    parameters.setPictureFormat(ImageFormat.JPEG);
                    parameters.setRotation(angle);
                    mCamera.setParameters(parameters);
                    cameraConfigured=true;
                }
            }
        }
    }

    private Camera.Size getBestPreviewSize(int width, int height,
                                           Camera.Parameters parameters) {
        Camera.Size result=null;

        for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
            if (size.width <= width && size.height <= height) {
                if (result == null) {
                    result=size;
                }
                else {
                    int resultArea=result.width * result.height;
                    int newArea=size.width * size.height;

                    if (newArea > resultArea) {
                        result=size;
                    }
                }
            }
        }

        return(result);
    }

    private Camera.Size getLargestPictureSize(Camera.Parameters parameters) {
        Camera.Size result=null;

        for (Camera.Size size : parameters.getSupportedPictureSizes()) {
            if (result == null) {
                result=size;
            }
            else {
                int resultArea=result.width * result.height;
                int newArea=size.width * size.height;

                if (newArea > resultArea) {
                    result=size;
                }
            }
        }

        return(result);
    }

    private static int setCameraDisplayOrientation(Activity activity,
                                                   int cameraId, android.hardware.Camera camera) {

        android.hardware.Camera.CameraInfo info =
                new android.hardware.Camera.CameraInfo();

        android.hardware.Camera.getCameraInfo(cameraId, info);

        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;

        switch (rotation) {
            case Surface.ROTATION_0: degrees = 0; break;
            case Surface.ROTATION_90: degrees = 90; break;
            case Surface.ROTATION_180: degrees = 180; break;
            case Surface.ROTATION_270: degrees = 270; break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);

        return result;
    }
}