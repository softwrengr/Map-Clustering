package com.syedmetal.task;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.syedmetal.task.databinding.ActivityMainBinding;
import com.syedmetal.task.models.ApiResponseModel;
import com.syedmetal.task.viewmodels.MapViewModel;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_main);


        MapViewModel viewModel = new ViewModelProvider(this).get(MapViewModel.class);
        binding.setViewmodel(viewModel);
        binding.setLifecycleOwner(this);

        viewModel.apiCall();

        viewModel.getLiveData().observe(this, apiResponseModel -> {
            if(apiResponseModel != null){

            }
        });


        //showing map
        binding.mapView.onCreate(savedInstanceState);
        binding.mapView.onResume();
        try {
            MapsInitializer.initialize(this);
            binding.mapView.getMapAsync(MainActivity.this);

        } catch (Exception e) {
            e.printStackTrace();
        }
        //end map block
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng sydney = new LatLng(25.253197, 55.338931);
        googleMap.addMarker(new MarkerOptions().position(sydney)
                .title("Dubai"));
        googleMap.setMaxZoomPreference(15);
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sydney,14.0f));
    }

    @Override
    public void onResume() {
        binding.mapView.onResume();
        super.onResume();
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