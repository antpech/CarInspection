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




        /*

        ApiInterface apiInterface = RetrofitApiClient.getClient().create(ApiInterface.class);
        Logger.addLogAdapter(new AndroidLogAdapter());

        File file = new File(filePath);
        //create RequestBody instance from file
        RequestBody requestFile = RequestBody.create(MediaType.parse("image"), file);

        // MultipartBody.Part is used to send also the actual file name
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);

        Gson gson = new Gson();
        String patientData = gson.toJson(imageSenderInfo);
        Log.e("JSON toSend:", patientData);

        RequestBody description = RequestBody.create(okhttp3.MultipartBody.FORM, patientData);

        // finally, execute the request
        Call<ResponseModel> call = apiInterface.fileUpload(description, body);
        call.enqueue(new Callback<ResponseModel>() {
            @Override
            public void onResponse(@NonNull Call<ResponseModel> call, @NonNull Response<ResponseModel> response) {
                Logger.d("Response: " + response);

                ResponseModel responseModel = response.body();

                if(responseModel != null){
                    EventBus.getDefault().post(new EventModel("response", responseModel.getMessage()));
                    Logger.d("Response code " + response.code() +
                            " Response Message: " + responseModel.getMessage());
                } else
                    EventBus.getDefault().post(new EventModel("response", "ResponseModel is NULL"));
            }

            @Override
            public void onFailure(@NonNull Call<ResponseModel> call, @NonNull Throwable t) {
                Logger.d("Exception: " + t);
                EventBus.getDefault().post(new EventModel("response", t.getMessage()));
            }
        });

        */
}
