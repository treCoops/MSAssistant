package com.example.msassistant.FragmentController;


import android.Manifest;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.developer.kalert.KAlertDialog;
import com.example.msassistant.R;
import com.github.capur16.digitspeedviewlib.DigitSpeedView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class Fragment_Home extends Fragment implements OnMapReadyCallback, LocationListener {

    private GoogleMap mMap;
    private boolean is_gps_allowed = false;

    DigitSpeedView digitSpeedView;
    Button btn_real_left;
    Button btn_real_center;
    Button btn_real_right;
    Button btn_left;
    Button btn_right;
    Button btn_sos;

    DatabaseReference databaseReference;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        digitSpeedView = view.findViewById(R.id.digit_speed_view);
        btn_real_left = view.findViewById(R.id.btn_real_left);
        btn_real_center = view.findViewById(R.id.btn_real_center);
        btn_real_right = view.findViewById(R.id.btn_real_right);
        btn_left = view.findViewById(R.id.btn_left);
        btn_right = view.findViewById(R.id.btn_right);
        btn_sos = view.findViewById(R.id.btn_sos);

        databaseReference = FirebaseDatabase.getInstance().getReference("motorCycle");


        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET}
                        , 10);
            }
        } else {
            is_gps_allowed = true;
        }

        MapView mapView = view.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        mapView.getMapAsync(this);


        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                try{

                    digitSpeedView.updateSpeed(Integer.parseInt(dataSnapshot.child("SPEED").getValue().toString()));

                    boolean flag = true;

                    if(Integer.parseInt(dataSnapshot.child("SPEED").getValue().toString()) > 80){
                        if (flag){
                            new KAlertDialog(getContext(), KAlertDialog.CUSTOM_IMAGE_TYPE)
                                    .setTitleText("Warning")
                                    .setContentText("High Speed Level Exceeded!")
                                    .setCustomImage(R.drawable.ic_warning_alert)
                                    .show();
                        }

                        flag = false;
                    }else{
                        flag = true;
                    }


                    switch (Integer.parseInt(dataSnapshot.child("360").getValue().toString())) {
                        case 113:
                            btn_real_left.setTextColor(getResources().getColor(R.color.red));
                            break;

                        case 112:
                            btn_real_left.setTextColor(getResources().getColor(R.color.yellow));
                            break;

                        case 111:
                            btn_real_left.setTextColor(getResources().getColor(R.color.green));
                            break;

                        case 213:
                            btn_real_center.setTextColor(getResources().getColor(R.color.red));
                            break;

                        case 212:
                            btn_real_center.setTextColor(getResources().getColor(R.color.yellow));
                            break;

                        case 211:
                            btn_real_center.setTextColor(getResources().getColor(R.color.green));
                            break;

                        case 313:
                            btn_real_right.setTextColor(getResources().getColor(R.color.red));
                            break;

                        case 312:
                            btn_real_right.setTextColor(getResources().getColor(R.color.yellow));
                            break;

                        case 311:
                            btn_real_right.setTextColor(getResources().getColor(R.color.green));
                            break;
                    }

                    if(Integer.parseInt(dataSnapshot.child("AcceleroMeter").getValue().toString()) == 1){
                        btn_left.setTextColor(getResources().getColor(R.color.red));
                        btn_right.setTextColor(getResources().getColor(R.color.green));
                    }

                    if(Integer.parseInt(dataSnapshot.child("AcceleroMeter").getValue().toString()) == 2){
                        btn_right.setTextColor(getResources().getColor(R.color.red));
                        btn_left.setTextColor(getResources().getColor(R.color.green));
                    }

                    if(Integer.parseInt(dataSnapshot.child("AcceleroMeter").getValue().toString()) == 0){
                        btn_left.setTextColor(getResources().getColor(R.color.green));
                        btn_right.setTextColor(getResources().getColor(R.color.green));
                    }




                }catch (NullPointerException e){
                    Log.e("null message : ",e.getMessage());
                    Toast.makeText(getContext(), "Database parameters invalid", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        Objects.requireNonNull(getActivity()).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Home");
    }

    @Override
    public void onLocationChanged(Location location) {

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

    @Override
    public void onMapReady(GoogleMap googleMap) {

        if (is_gps_allowed) {
            mMap = googleMap;
            mMap.animateCamera(CameraUpdateFactory.zoomTo(17.0f));

            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);


        }
    }
}
