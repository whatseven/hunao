package com.example.hw9.ui.home;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.hw9.R;

import java.io.IOException;
import java.util.List;


public class HomeFragment extends Fragment implements LocationListener {

    private HomeViewModel homeViewModel;

    // Location things
    protected LocationManager locationManager;
    private double longitude = -118.2838;
    private double latitude = 34.0204;

    // Weather
    private TextView mTextCity;
    private TextView mTextState;
    private TextView mTextTemp;
    private TextView mtextType;
    private RelativeLayout mBgImg;

    // Recycler things
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    // Refresher
    private SwipeRefreshLayout mSwipeRefreshLayout;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        mTextCity = root.findViewById(R.id.weather_city);
        mTextState = root.findViewById(R.id.weather_state);
        mTextTemp = root.findViewById(R.id.weather_temperature);
        mtextType = root.findViewById(R.id.weather_type);
        mBgImg = root.findViewById (R.id.weather_info);
        mSwipeRefreshLayout = root.findViewById(R.id.refresher_home);

        // Location
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Toast.makeText(getActivity(), "Please allow the location permission and try it again", Toast.LENGTH_SHORT).show();
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        Geocoder geocoder = new Geocoder(getActivity());////// Some problem with location info lifecycle
        try {
            List<Address> addr = geocoder.getFromLocation(latitude, longitude, 1);
            homeViewModel.getWeatherInfo(addr.get(0).getLocality(), addr.get(0).getAdminArea()).observe(getViewLifecycleOwner(), new Observer<List<String>>() {
                @Override
                public void onChanged(@Nullable List<String> infoList) {
                    // Text Info
                    mTextCity.setText(infoList.get(0));
                    mTextState.setText(infoList.get(1));
                    mTextTemp.setText(infoList.get(2) + "°C");
                    mtextType.setText(infoList.get(3));
                    // Background Image
                    Resources res = getResources(); //resource handle
                    Drawable drawable = res.getDrawable(getWeatherImg(infoList.get(3))); //new Image that was added to the res folder
                    mBgImg.setBackground(drawable);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }


        // Recycler View
        recyclerView = (RecyclerView) root.findViewById(R.id.home_newslist);
        recyclerView.setHasFixedSize(true);// use this setting to improve performance if you know that changes in content do not change the layout size of the RecyclerView
        layoutManager = new LinearLayoutManager(getActivity());  // use a linear layout manager
        recyclerView.setLayoutManager(layoutManager);
        homeViewModel.getNewsList().observe(getViewLifecycleOwner(), new Observer<NewsData>() {
            @Override
            public void onChanged(@Nullable NewsData infoList) {
                mAdapter = new NewsAdapter(infoList);
                recyclerView.setAdapter(mAdapter);
            }
        });

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to make your refresh action
                // CallYourRefreshingMethod();
                homeViewModel.getNewsList().observe(getViewLifecycleOwner(), new Observer<NewsData>() {
                    @Override
                    public void onChanged(@Nullable NewsData infoList) {
                        mAdapter = new NewsAdapter(infoList);
                        recyclerView.setAdapter(mAdapter);
                    }
                });
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(mSwipeRefreshLayout.isRefreshing()) {
                            mSwipeRefreshLayout.setRefreshing(false);
                        }
                    }
                }, 1000);
            }
        });

        return root;
    }

    private int getWeatherImg(String weather){
        switch (weather){
            case "Clouds":
                return R.drawable.cloudy_weather;
            case "Clear":
                return R.drawable.clear_weather;
            case "Snow":
                return R.drawable.snowy_weather;
            case "Rain":
                return R.drawable.rainy_weather;
            case "Drizzle":
                return R.drawable.rainy_weather;
            case "Thunderstorm":
                return R.drawable.thunder_weather;
            default:
                return R.drawable.sunny_weather;
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
//        Geocoder geocoder = new Geocoder(getActivity());////// Some problem with location info lifecycle
//        try {
//            List<Address> addr = geocoder.getFromLocation(latitude, longitude, 1);
//            homeViewModel.getWeatherInfo(addr.get(0).getLocality(), addr.get(0).getAdminArea()).observe(getViewLifecycleOwner(), new Observer<List<String>>() {
//                @Override
//                public void onChanged(@Nullable List<String> infoList) {
//                    // Text Info
//                    mTextCity.setText(infoList.get(0));
//                    mTextState.setText(infoList.get(1));
//                    mTextTemp.setText(infoList.get(2) + "°C");
//                    mtextType.setText(infoList.get(3));
//                    // Background Image
//                    Resources res = getResources(); //resource handle
//                    Drawable drawable = res.getDrawable(getWeatherImg(infoList.get(3))); //new Image that was added to the res folder
//                    mBgImg.setBackground(drawable);
//                }
//            });
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
