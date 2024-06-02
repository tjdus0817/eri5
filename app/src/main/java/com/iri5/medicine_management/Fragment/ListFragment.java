package com.iri5.medicine_management.Fragment;

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
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.iri5.medicine_management.Adapter.PhamarcyAdapter;
import com.iri5.medicine_management.Model.CurrentPoint;
import com.iri5.medicine_management.Model.Pharmacy;
import com.iri5.medicine_management.R;

import java.util.ArrayList;

public class ListFragment extends Fragment {
    private static final String TAG = "ListFragment";

    FirebaseAuth firebaseAuth;
    FirebaseFirestore db;
    ListView listView;
    PhamarcyAdapter phamarcyAdapter;
    ArrayList<Pharmacy> pharmacyArrayList;
    private LocationManager lm;

    CurrentPoint currentPoint;

    public ListFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_list, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        listView = (ListView) rootView.findViewById(R.id.lv_list_fragment_list);
        currentPoint = new CurrentPoint(126.734086,127.269311);


        pharmacyArrayList = new ArrayList<Pharmacy>();
        phamarcyAdapter = new PhamarcyAdapter(currentPoint,pharmacyArrayList);
        listView.setAdapter(phamarcyAdapter);

        lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            return rootView;
        }

        final LocationListener gpsLocationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                currentPoint.setLon(location.getLongitude());
                currentPoint.setLat(location.getLatitude());
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
            currentPoint.setLon(location.getLongitude());
            currentPoint.setLat(location.getLatitude());
            updateAdapterCurrentPoint();
        }

        getShop();

        return rootView;
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

                            Pharmacy pharmacy = new Pharmacy(uid,businessname,operatingTime,description,inventory,lat,lon);
                            phamarcyAdapter.addItem(pharmacy);
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

    private void updateAdapterCurrentPoint() {
        if (phamarcyAdapter != null) {
            phamarcyAdapter.notifyDataSetChanged();
        }
    }

}