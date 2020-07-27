package com.syedmetal.task.viewmodels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.syedmetal.task.models.ApiResponseModel;
import com.syedmetal.task.remote.ApiInterface;
import com.syedmetal.task.remote.RetrofitClass;
import com.syedmetal.task.utilities.Constants;
import com.syedmetal.task.utilities.MyItem;
import java.util.ArrayList;
import java.util.List;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;



public class MapViewModel extends ViewModel {
    private MutableLiveData<ApiResponseModel> liveData = new MutableLiveData<>();
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private List<MyItem> itemList = new ArrayList<>();

    public void getPages() {
        ApiInterface services = RetrofitClass.getApiClient().create(ApiInterface.class);
        services.getPage("", Constants.API_KEY)
                .toObservable()
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<ApiResponseModel>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ApiResponseModel response) {

                        if(response != null){
                            errorMessage.postValue(response.getStatus());
                            if (!response.getError()) {
                                liveData.postValue(response);
                            }
                        }



                    }

        @Override
        public void onError (Throwable e){
            errorMessage.postValue(""+e.getMessage());
        }

        @Override
        public void onComplete () {

        }
    });
}


    public MutableLiveData<ApiResponseModel> getLiveData() {
        return liveData;
    }

    public MutableLiveData<String> getErrorMessage() {
        return errorMessage;
    }


}
