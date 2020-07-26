package com.syedmetal.task;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.syedmetal.task.databinding.ActivityMainBinding;
import com.syedmetal.task.models.ApiDataModel;
import com.syedmetal.task.utilities.MyItem;
import com.syedmetal.task.viewmodels.MapViewModel;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private List<ApiDataModel> dataList = new ArrayList<>();
    private List<MyItem> itemList = new ArrayList<>();
    private LatLng latLng;
    private String title;
    private MarkerOptions marker;
    private ClusterManager<MyItem> clusterManager;
    private GoogleMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);


        MapViewModel viewModel = new ViewModelProvider(this).get(MapViewModel.class);
        binding.setViewmodel(viewModel);
        binding.setLifecycleOwner(this);

        viewModel.apiCall();

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
            if (apiResponseModel != null) {
                dataList.addAll(apiResponseModel.getLocationData());

                binding.mapView.getMapAsync(this::showMarkers);

            }

        });


    }


    public void showMarkers(GoogleMap googleMap) {

        map = googleMap;

        if (dataList != null) {
            for (int i = 0; i < dataList.size(); i++) {
                title = dataList.get(i).getName();
                latLng = new LatLng(Double.parseDouble(dataList.get(i).getLatitude()), Double.parseDouble(dataList.get(i).getLongitude()));

            }
        }

        setUpClusterer();
        marker = new MarkerOptions().position(latLng).title(title);
        googleMap.addMarker(marker);


    }

    private void setUpClusterer() {
        // Position the map.
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latLng.latitude,latLng.longitude), 4));
        clusterManager = new ClusterManager<>(this, map);
        map.setOnCameraIdleListener(clusterManager);
        map.setOnMarkerClickListener(clusterManager);

        // Add cluster items (markers) to the cluster manager.
        addItems();
    }

    private void addItems() {

        // Add ten cluster items in close proximity, for purposes of this example.
        for(int i=0;i<dataList.size();i++){
            double offset = i / 60d;
            double lat = Double.parseDouble(dataList.get(i).getLatitude());
            double lng = Double.parseDouble(dataList.get(i).getLongitude());
            MyItem myItem = new MyItem(lat,lng);
            clusterManager.addItem(myItem);

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
}