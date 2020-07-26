package com.syedmetal.task.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ApiResponseModel {
    @SerializedName("error")
    @Expose
    private Boolean error;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("locationData")
    @Expose
    private List<ApiDataModel> locationData = null;

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<ApiDataModel> getLocationData() {
        return locationData;
    }

    public void setLocationData(List<ApiDataModel> locationData) {
        this.locationData = locationData;
    }
}
