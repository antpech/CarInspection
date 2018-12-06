package ru.ovod.carinspection.helpers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import ru.ovod.carinspection.BuildConfig;

public class PhotoHelper {
    String mCurrentPhotoPath;
    String mCurrentPhotoName;
    SysHelper sysHelper;

    static final int REQUEST_TAKE_PHOTO = 3333;
    public PhotoHelper(SysHelper sysHelper) {
        this.sysHelper = sysHelper;
    }
    public PhotoHelper() {
        this.sysHelper = null;
    }
    public String getmCurrentPhotoPath() {
        return mCurrentPhotoPath;
    }

    public String getmCurrentPhotoName() {
        return mCurrentPhotoName;
    }

    private File createImageFile(String prefix, int number) throws IOException {
        // Create an image file name
        String timeStamp = new java.text.SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = prefix + "_" + String.valueOf(number) + "_" + timeStamp + "_";
        File storageDir = sysHelper.getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        mCurrentPhotoName = image.getName();
        return image;
    }

    public void takePhoto(String prefix, int number) {
        if (sysHelper != null) {
            Context context = sysHelper.getApplicationContext();
            Uri photoURI;

            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            // Ensure that there's a camera activity to handle the intent
            if (takePictureIntent.resolveActivity(context.getPackageManager()) != null) {
                // Create the File where the photo should go
                File photoFile = null;
                try {
                    photoFile = createImageFile(prefix, number);
                } catch (IOException ex) {
                    // Error occurred while creating the File
                }
                // Continue only if the File was successfully created
                if (photoFile != null) {
                    if (context instanceof Activity) {
                        photoURI = sysHelper.getUri(photoFile);
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                        ((Activity) context).startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                    } else {
                        Log.e("PhotoHelper", "Context should be an instanceof Activity.");
                    }

                } else {
                    Log.e("PhotoHelper", "Photo file is null");
                }
            }
        } else {
            Log.e("PhotoHelper", "You should set sysHelper");
        }
    }

    public float getRotateAngle(String path) {
        float angle;
        ExifInterface ei;
        try {
            ei = new ExifInterface(path);
        } catch (Exception e) {
            Log.d("PhotoHelper", e.getMessage());
            return 0;
        }

        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED);
        switch(orientation) {

            case ExifInterface.ORIENTATION_ROTATE_90:
                angle = 90;
                break;

            case ExifInterface.ORIENTATION_ROTATE_180:
                angle = 180;
                break;

            case ExifInterface.ORIENTATION_ROTATE_270:
                angle = 270;
                break;

            case ExifInterface.ORIENTATION_NORMAL:
            default:
                angle = 0;
        }

        return angle;
    }

}
