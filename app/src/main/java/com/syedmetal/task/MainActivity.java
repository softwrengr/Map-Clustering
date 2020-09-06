package com.syedmetal.task;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.algo.NonHierarchicalViewBasedAlgorithm;
import com.syedmetal.task.databinding.ActivityMainBinding;
import com.syedmetal.task.models.ApiDataModel;
import com.syedmetal.task.models.ApiResponseModel;
import com.syedmetal.task.utilities.MyItem;
import com.syedmetal.task.utilities.NetworkUtils;
import com.syedmetal.task.viewmodels.MapViewModel;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private ClusterManager<MyItem> clusterManager;
    private MapViewModel viewModel;
    private List<MyItem> itemList = new ArrayList<>();
    private NonHierarchicalViewBasedAlgorithm<MyItem> mAlgorithm = new NonHierarchicalViewBasedAlgorithm<>(10, 10);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);


        viewModel = new ViewModelProvider(this).get(MapViewModel.class);
        binding.setViewmodel(viewModel);
        binding.setLifecycleOwner(this);

        if(NetworkUtils.isNetworkConnected(getApplicationContext())){
            viewModel.getPages();
        }
        else {
            showToast("you have no active connection");
        }

        //map block
        binding.mapView.onCreate(savedInstanceState);
        binding.mapView.onResume();
        try {
            MapsInitializer.initialize(this);

        } catch (Exception e) {
            e.printStackTrace();
        }
        //end map block

        viewModel.getLiveData().observe(this, apiResponseModel -> {
            binding.mapView.getMapAsync(this::showMarkers);
            addItem(apiResponseModel);
        });

        viewModel.getErrorMessage().observe(this,s -> showToast(s));
    }


    private void showMarkers(GoogleMap googleMap) {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        mAlgorithm.updateViewSize(metrics.widthPixels,metrics.heightPixels);

        clusterManager = new ClusterManager<>(this, googleMap);
        clusterManager.setAlgorithm(mAlgorithm);

//        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latLng.latitude,latLng.longitude), 6));
        googleMap.setOnCameraIdleListener(clusterManager);


    }


    private void addItem(ApiResponseModel response){
        for (int j = 0; j < response.getLocationData().size(); j++) {

            double lat = 0, lng = 0;

            for (int i = 0; i < 10; i++) {
                double offset = i / 60d;
                lat = Double.parseDouble(response.getLocationData().get(j).getLatitude()) + offset;
                lng = Double.parseDouble(response.getLocationData().get(j).getLongitude()) + offset;
            }

            String title = response.getLocationData().get(j).getName();
            itemList.add(new MyItem(lat, lng, title));

        }


        //just checking branches
        // Add ten cluster items in close proximity, for purposes of this example.
        mAlgorithm.lock();
        try {
            mAlgorithm.addItems(itemList);
        } finally {
            mAlgorithm.unlock();
        }
    }

    @Override
    public void onPause() {
        binding.mapView.onPause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        binding.mapView.onDestroy();
        binding = null;
        super.onDestroy();
    }

    private void showToast(String message) {
        Toast.makeText(this, "" + message, Toast.LENGTH_SHORT).show();
    }

}