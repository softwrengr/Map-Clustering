package com.syedmetal.task;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
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

public class MainActivity extends AppCompatActivity implements GoogleMap.OnMarkerClickListener {

    private ActivityMainBinding binding;
    private List<ApiDataModel> dataList = new ArrayList<>();
    private LatLng latLng;
    private String title;
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

        viewModel.errroMessage().observe(this,message -> {
            showToast(message);
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
        map.addMarker(new MarkerOptions().position(latLng).title(title));
        map.setOnMarkerClickListener(this::onMarkerClick);

    }

    private void setUpClusterer() {
        // Position the map.
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latLng.latitude,latLng.longitude), 6));
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

    private void showToast(String message){
        Toast.makeText(this, ""+message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        showToast(marker.getTitle());
        return false;
    }
}