package ru.ovod.carinspection;

import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
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
        try {
            cameraId = getCameraId();
            mCamera = Camera.open(cameraId);
            mCamera.getParameters();
            mCamera.setPreviewDisplay(surfaceHolder);
            setCameraDisplayOrientation((Activity)this, cameraId, mCamera);
            mCamera.startPreview();

        } catch (IOException e) {
            e.printStackTrace();
        }
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
        mCamera.takePicture(null, null, null, new Camera.PictureCallback() {

            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                Context context = sysHelper.getApplicationContext();
                try {
                    sysHelper.getPhotoHelper().createImageFile(context, "Order", inspection.getNumber());
                } catch (Exception e) {
                    Log.e("createImageFile ERROR:", "unhandled error");
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
        Log.e("Surface Changed", "format   ==   " + format + ",   width  ===  "
                + width + ", height   ===    " + height);
        try {
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.e("Surface Created", "");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.e("Surface Destroyed", "");
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

    public static void setCameraDisplayOrientation(Activity activity,
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
    }
}