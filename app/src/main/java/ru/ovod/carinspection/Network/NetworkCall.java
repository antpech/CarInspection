package ru.ovod.carinspection.Network;

import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.ProgressBar;

import com.google.gson.Gson;

import java.io.File;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.ovod.carinspection.pojo.Inspection;
import ru.ovod.carinspection.pojo.Photo;


public class NetworkCall {
    public String fileUpload(Inspection inspection, Photo photo) {
        String result = null;

        File file;
        RequestBody requestFile;
        MultipartBody.Part body = null;
        Call<ResponseModel> call = null;
        ResponseModel responseModel = null;

        ApiInterface apiInterface = RetrofitApiClient.getClient().create(ApiInterface.class);

        Gson gson = new Gson();
        String patientData = gson.toJson(inspection);
        //new ImageSenderInfo(String.valueOf(inspection.getOrderid()), String.valueOf(inspection.getNumber())));
        RequestBody description = RequestBody.create(MultipartBody.FORM, patientData);
        Log.e("JSON toSend:", patientData);

        file = new File(photo.getPath());
        requestFile = RequestBody.create(MediaType.parse("image"), file);
        body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);

        //Synchronous call
        call = apiInterface.fileUpload(description, body);
        try {
            responseModel = call.execute().body();
        } catch (Exception e){
            responseModel = null;
        }

        if (responseModel != null) {
            result = responseModel.getMessage();
        }
        return result;

    }

}
