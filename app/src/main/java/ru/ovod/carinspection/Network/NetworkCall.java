package ru.ovod.carinspection.Network;

import android.content.Context;
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
import ru.ovod.carinspection.AddCarInspectionActivity;
import ru.ovod.carinspection.adapters.AddCarInspectionAdapter;
import ru.ovod.carinspection.helpers.SysHelper;
import ru.ovod.carinspection.pojo.Inspection;
import ru.ovod.carinspection.pojo.Photo;


public class NetworkCall {
    private class MyInt {
        private int co;

        public MyInt() {
            this.co = 0;
        }

        public void setCo(int co) {
            this.co = co;
        }

        public int getCo() {
            return co;
        }
    }


    public void fileUpload(final Context context, Inspection inspection, final SysHelper sysHelper) {
        final AddCarInspectionActivity activity = (AddCarInspectionActivity) context;
        if (activity.getAdapter().getGalleryList().isEmpty()) { return; }

        final MyInt photoCo = new MyInt();
        File file;
        RequestBody requestFile;
        MultipartBody.Part body = null;
        Call<ResponseModel> call = null;

        for (final Photo photo: activity.getAdapter().getGalleryList()) {
            if (photo.getIssync() == 1) { continue; }
            photoCo.setCo(photoCo.getCo()+1);
        }

        if (photoCo.getCo() > 0) {
            sysHelper.getProgressBar().setVisibility(ProgressBar.VISIBLE);

            ApiInterface apiInterface = RetrofitApiClient.getClient().create(ApiInterface.class);

            Gson gson = new Gson();
            String patientData = gson.toJson(inspection);
            //new ImageSenderInfo(String.valueOf(inspection.getOrderid()), String.valueOf(inspection.getNumber())));
            RequestBody description = RequestBody.create(MultipartBody.FORM, patientData);
            Log.e("JSON toSend:", patientData);

            for (final Photo photo : activity.getAdapter().getGalleryList()) {
                if (photo.getIssync() == 1) {
                    continue;
                }

                file = new File(photo.getPath());
                requestFile = RequestBody.create(MediaType.parse("image"), file);
                body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);
                call = apiInterface.fileUpload(description, body);

                call.enqueue(new Callback<ResponseModel>() {
                    @Override
                    public void onResponse(@NonNull Call<ResponseModel> call, @NonNull Response<ResponseModel> response) {
                        ResponseModel responseModel = response.body();
                        if (responseModel != null) {
                            sysHelper.getDbhelper().updPhotoSync(new String[]{responseModel.getMessage()});
                            photo.setIssync(1);
                            photoCo.setCo(photoCo.getCo() - 1);
                            if (photoCo.getCo() == 0) {
                                sysHelper.getProgressBar().setVisibility(ProgressBar.INVISIBLE);
                                sysHelper.showToAst("Все фотографии загружены");
                                if ((context != null) && (activity != null)) {
                                    activity.getBtnSync().setEnabled(true);
                                    activity.getAdapter().notifyDataSetChanged();
                                }
                            }
                        } else
                            Log.e("JSON toSend:", responseModel.getMessage());
                    }

                    @Override
                    public void onFailure(@NonNull Call<ResponseModel> call, @NonNull Throwable t) {
                        Log.e("JSON toSend:", t.getMessage());
                    }
                });
            }
        }



    }

}
