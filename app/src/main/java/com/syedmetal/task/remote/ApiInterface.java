package com.syedmetal.task.remote;

import com.syedmetal.task.models.ApiResponseModel;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ApiInterface {

    @FormUrlEncoded
    @POST("api/msDiscoverPage")
    Call<ApiResponseModel> discoverPage(@Field("searchText") String searchText,
                                        @Field("apiKey") String apiKey);

    @FormUrlEncoded
    @POST("api/msDiscoverPage")
    Flowable<ApiResponseModel> getPage(@Field("searchText") String searchText,
                                       @Field("apiKey") String apiKey);
}
