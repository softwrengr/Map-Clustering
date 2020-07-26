package com.syedmetal.task.remote;

import com.syedmetal.task.models.ApiResponseModel;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ApiInterface {

    @FormUrlEncoded
    @POST("api/msDiscoverPage")
    Call<ApiResponseModel> discoverPage(@Field("searchText") String searchText,
                                        @Field("apiKey") String apiKey);
}
