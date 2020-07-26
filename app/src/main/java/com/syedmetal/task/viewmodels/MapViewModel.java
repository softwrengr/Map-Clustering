package com.syedmetal.task.viewmodels;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.syedmetal.task.models.ApiResponseModel;
import com.syedmetal.task.remote.ApiInterface;
import com.syedmetal.task.remote.RetrofitClass;
import com.syedmetal.task.utilities.Constants;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MapViewModel extends ViewModel {
    public MutableLiveData<Integer> progressBar = new MutableLiveData<>();
    private MutableLiveData<ApiResponseModel> liveData = new MutableLiveData<>();
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();


    public void apiCall() {
        ApiInterface services = RetrofitClass.getApiClient().create(ApiInterface.class);
        Call<ApiResponseModel> allUsers = services.discoverPage("", Constants.API_KEY);
        allUsers.enqueue(new Callback<ApiResponseModel>() {
            @Override
            public void onResponse(@NotNull Call<ApiResponseModel> call, @NotNull Response<ApiResponseModel> response) {
                if (response.body() == null) {
                    try {
                        assert response.errorBody() != null;
                        JSONObject jsonObject = new JSONObject(response.errorBody().string());
                        errorMessage.setValue("" + jsonObject.getString("message"));
                    } catch (Exception e) {
                        errorMessage.setValue("" + e.getMessage());
                    }

                } else {
                    if (!response.body().getError()) {
                        liveData.setValue(response.body());
                    }
                    else {
                        errorMessage.setValue(response.body().getStatus());
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call<ApiResponseModel> call, @NotNull Throwable t) {
                errorMessage.setValue("" + t.getMessage());
            }
        });
    }

    public MutableLiveData<ApiResponseModel> getLiveData() {
        return liveData;
    }

    public MutableLiveData<String> errroMessage() {
        return errorMessage;
    }

}
