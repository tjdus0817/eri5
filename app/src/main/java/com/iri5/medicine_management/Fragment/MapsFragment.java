package com.iri5.medicine_management.Fragment;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.iri5.medicine_management.R;

public class MapsFragment extends Fragment implements OnMapReadyCallback {

    private static final String TAG = "MapsFragment";

    private GoogleMap mMap;
    private double current_lat;
    private double current_lon;
    private LocationManager lm;
    private Marker currentLocationMarker;

    FirebaseAuth firebaseAuth;
    FirebaseFirestore db;
    boolean asyn_flag;

    public MapsFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.activity_maps, container, false);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();


        lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            return rootView;
        }

        final LocationListener gpsLocationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                current_lon = location.getLongitude(); // 경도
                current_lat = location.getLatitude(); // 위도
                updateMapLocation();
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };

        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, gpsLocationListener);
        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1, gpsLocationListener);

        // 초기 위치 설정
        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location != null) {
            current_lat = location.getLatitude();
            current_lon = location.getLongitude();
            updateMapLocation();
        }

        return rootView;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // 지도 준비가 되었을 때 초기 위치로 이동
        updateMapLocation();
    }

    private void updateMapLocation() {
        if (mMap != null) {
            LatLng currentLocation = new LatLng(current_lat, current_lon);

            // 기존 마커가 있으면 제거
            if (currentLocationMarker != null) {
                currentLocationMarker.remove();
            }

            // 새로운 마커 추가
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(currentLocation)
                    .title("현재 위치")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)); // 마커 색상 설정

            currentLocationMarker = mMap.addMarker(markerOptions);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));

            getShop();
        }
    }

    private void getShop() {
        db.collection("Shops")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            String uid = documentSnapshot.getString("uid");
                            String businessname = documentSnapshot.getString("businessname");
                            String operatingTime = documentSnapshot.getString("operatingTime");
                            String lat = documentSnapshot.getString("lat");
                            String lon = documentSnapshot.getString("lon");
                            String description = documentSnapshot.getString("description");
                            String inventory = documentSnapshot.getString("inventory");


                            Log.d(TAG, "Business Name: " + businessname);
                            Log.d(TAG, "Operating Time: " + operatingTime);
                            Log.d(TAG, "Latitude: " + lat);
                            Log.d(TAG, "Longitude: " + lon);
                            Log.d(TAG, "Description: " + description);
                            Log.d(TAG, "Inventory: " + inventory);

                            LatLng markerLocation = new LatLng(Double.valueOf(lat), Double.valueOf(lon));

                            MarkerOptions markerOptions = new MarkerOptions()
                                    .position(markerLocation)
                                    .title(businessname)
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)); // 마커 색상 설정
                            if (uid.equals(firebaseAuth.getUid())){
                                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                            }
                            currentLocationMarker = mMap.addMarker(markerOptions);
                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error getting document", e);
                    }
                });
    }
}
