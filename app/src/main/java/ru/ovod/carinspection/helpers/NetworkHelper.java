package ru.ovod.carinspection.helpers;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import ru.ovod.carinspection.pojo.Inspection;
import ru.ovod.carinspection.pojo.Photo;


/*
public interface ApiInterface {

    @Multipart
    @POST("https://smit.ovod.ru/upload/upl.php")
    Call<Inspection> fileUpload(
            @Part("sender_information") RequestBody description,
            @Part MultipartBody.Part file);

}
*/



public class NetworkHelper {


    /*
    private class SyncData extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... arg0) {
            if (inspection != null && inspection.getOrderid() > 0 && inspection.getNumber() > 0) {
                for (Photo photo : photoList) {
                    Log.e("DB ", "Начали отправку файла: " + photo.getName());


                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(final Void unused) {
            //Обновить отображение
        }
    }
    */


    public void fileUpload(ArrayList<Photo> photoList, Inspection inspection) {
        /*
        for (Photo photo : photoList) {

        }
        */
        //TODO Переделать заливку на сервер
    }
}
