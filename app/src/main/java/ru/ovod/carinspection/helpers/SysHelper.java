package ru.ovod.carinspection.helpers;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;
import android.view.Gravity;
import android.widget.Toast;

import java.io.File;

import ru.ovod.carinspection.BuildConfig;

public class SysHelper {
    private static SysHelper instance;
    private static Context applicationContext;
    private DBHelper dbhelper;
    private static PhotoHelper photoHelper;

    private SysHelper (Context context){
        applicationContext = context;
        dbhelper = new DBHelper(applicationContext);
        photoHelper = new PhotoHelper();
    }

    public static SysHelper getInstance(Context context){
        if (instance == null){
            instance = new SysHelper(context);
        } else {
            if (context != null) {
                setApplicationContext(context);
            }
        }

        return instance;
    }

    public DBHelper getDbhelper() {
        return dbhelper;
    }

    public PhotoHelper getPhotoHelper() {
        return photoHelper;
    }

    public static Context getApplicationContext() {
        return applicationContext;
    }

    public static void setApplicationContext(Context applicationContext) {
        SysHelper.applicationContext = applicationContext;
    }

    // проверка доступности сети
    // ВНИМАНИЕ !!!
    // проверка пока просто проверяет, есть сеть или нет.
    // возможно, в будущем лучше будет доработать проверку доступности конкретного ресурса
    // примеров полно: http://qaru.site/questions/13922/how-to-check-internet-access-on-android-inetaddress-never-times-out
    public boolean isOnline() {
        boolean br = false;
        ConnectivityManager cm = null;
        NetworkInfo ni = null;

        cm = (ConnectivityManager) applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        ni = cm.getActiveNetworkInfo();
        br = ((null != ni) && (ni.isConnected()) && (ni.getType() == ConnectivityManager.TYPE_WIFI));

        return br;
    }

    public void showToAst(String message) {
        //создаём и отображаем текстовое уведомление
        Toast toast = Toast.makeText(applicationContext,
                message,
                Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    public static Uri getUri(File file) {
        Uri photoURI;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            /* для версси sdk < 24 */
            photoURI = Uri.fromFile(file);
        } else {
            /* для версии sdk >= 24 */
            photoURI = FileProvider.getUriForFile(applicationContext,
                    BuildConfig.APPLICATION_ID + ".provider",
                    file);
        }
        return photoURI;
    }

}
