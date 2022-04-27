package com.example.zoneout.bottomnavigationbar;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.example.zoneout.MainActivity;
import com.example.zoneout.R;
import com.example.zoneout.model.User;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;
import com.google.firebase.database.core.Constants;

import java.util.Map;

public class HomeFragment extends Fragment implements OnMapReadyCallback {

    private View view;
    private FragmentManager fm;
    private int postId;
    private Bundle bundle;

    private LatLng currentLocation;
    private FusedLocationProviderClient client;

    private User user;

    private GoogleMap map;
    private static final int REQUEST_CODE = 123;

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;

        putMarkers();

        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {
                postId = (int) marker.getTag();
                System.out.println("PostID" + postId);

                bundle=new Bundle();
                bundle.putInt("postId",postId);

                setArguments(bundle);

                NavController navController = Navigation.findNavController(view);

                navController.navigate(R.id.action_navigation_home_to_navigation_postfrommap, bundle);

                return true;
            }
        });
    }

    public void getCurrentLocation(){

        if(ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this.getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;
        }
        client.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location != null){
                    currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
                }
            }
        });
    }

    private void putMarkers(){
        DatabaseReference postsDatabase = MainActivity.reference.child("Posts");
        postsDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                GenericTypeIndicator<Map<String, Object>> genericTypeIndicator = new GenericTypeIndicator<Map<String, Object>>() {};
                Map<String,Object> post = snapshot.getValue(genericTypeIndicator);

                double lat = (Double) post.get("lat");
                System.out.println(lat);
                double lgn = (Double) post.get("lgn");
                int id = Math.toIntExact((long) post.get("id"));
                System.out.println(lgn);
                LatLng latLng = new LatLng(lat, lgn);


                Marker marker = map.addMarker(new MarkerOptions()
                        .position(latLng));
                marker.setTag(id);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_home, container, false);

        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.topAppBar);
        toolbar.setTitle("Home");

        fm = getChildFragmentManager();

        client = LocationServices.getFusedLocationProviderClient(this.getContext());
        //getCurrentLocation();

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(this::onMapReady);

        return view;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions, @NonNull @NotNull int[] grantResults) {
        switch (requestCode){
            case REQUEST_CODE:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    getCurrentLocation();
                }
                break;
        }
    }

    public void onClick(View v){
        Navigation.findNavController(v).navigate(R.id.action_navigation_home_to_postsList);
    }



}