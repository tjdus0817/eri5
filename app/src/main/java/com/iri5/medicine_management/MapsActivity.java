package com.iri5.medicine_management;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

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
import com.iri5.medicine_management.Utils.Util;
import com.iri5.medicine_management.databinding.ActivityMapsBinding;

import java.util.HashMap;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener {
    private static final String TAG = "MapsActivity";

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private double current_lat;
    private double current_lon;

    private double marker_lat;
    private double marker_lon;
    private LocationManager lm;
    private Marker currentLocationMarker;

    private ImageButton btn_mylocation;
    private ImageButton btn_list;

    private Dialog addDialog;
    private EditText et_dialog_businessname;
    private EditText et_dialog_desc;
    private EditText et_dialog_inventory;
    private EditText et_dialog_operating_time;
    private Button btn_dialog_submit;
    private Button btn_dialog_cancle;

    private FirebaseFirestore db;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        btn_mylocation  = findViewById(R.id.btn_maps_mylocation);
        btn_list        = findViewById(R.id.btn_maps_list);
        addDialog       = new Dialog(this);
        addDialog.setContentView(R.layout.add_shop_dialog);

        et_dialog_businessname      = (EditText) addDialog.findViewById(R.id.et_shop_add_dialg_businessname);
        et_dialog_operating_time    = (EditText) addDialog.findViewById(R.id.et_shop_add_dialg_operating_time);
        et_dialog_desc              = (EditText) addDialog.findViewById(R.id.et_shop_add_dialg_desc);
        et_dialog_inventory         = (EditText) addDialog.findViewById(R.id.et_shop_add_dialg_inventory);
        btn_dialog_submit           = (Button) addDialog.findViewById(R.id.btn_shop_add_dialog_submit);
        btn_dialog_cancle           = (Button) addDialog.findViewById(R.id.btn_shop_add_dialog_cancle);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // Initialize LocationManager inside onCreate method
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Request permissions if not granted
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            return;
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

        btn_mylocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateMapLocation();
            }
        });

        btn_dialog_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String businessname = et_dialog_businessname.getText().toString();
                String operating = et_dialog_operating_time.getText().toString();
                String description = et_dialog_desc.getText().toString();
                String inventory = et_dialog_inventory.getText().toString();

                updateShop(firebaseAuth.getUid(),businessname,operating,String.valueOf(marker_lat),String.valueOf(marker_lon),description,inventory);
            }
        });

        btn_dialog_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addDialog.dismiss();
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        updateMapLocation();

        // 지도 클릭 리스너 설정
        mMap.setOnMapClickListener(this);
    }

    @Override
    public void onMapClick(LatLng latLng) {
        marker_lat = latLng.latitude;
        marker_lon = latLng.longitude;

        et_dialog_businessname.setText("");
        et_dialog_operating_time.setText("");
        et_dialog_desc.setText("");
        et_dialog_inventory.setText("");

        addDialog.show();

    }

    private void updateMapLocation() {
        if (mMap != null) {
            LatLng currentLocation = new LatLng(current_lat, current_lon);

            if (currentLocationMarker != null) {
                currentLocationMarker.remove();
            }

            MarkerOptions markerOptions = new MarkerOptions()
                    .position(currentLocation)
                    .title("현재 위치")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

            currentLocationMarker = mMap.addMarker(markerOptions);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
        }
    }



    private void updateShop(String uid, String businessname, String operatingTime, String lat, String lon, String description, String inventory) {
        // Firestore에 업데이트할 데이터
        Map<String, Object> shop = new HashMap<>();
        shop.put("businessname", businessname);
        shop.put("operatingTime", operatingTime);
        shop.put("lat", lat);
        shop.put("lon", lon);
        shop.put("description", description);
        shop.put("inventory", inventory);


        // Firestore에 데이터 업데이트
        db.collection("Shops")
                .document(uid)
                .update(shop)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Util.showToast(getApplicationContext(),"영업점이 등록되었습니다.");
                        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                        startActivity(intent);
                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating document", e);
                    }
                });
    }
}
