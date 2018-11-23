package app.pharmacy.map.com.mappharmacyapp.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

import app.pharmacy.map.com.mappharmacyapp.App.AppConfig;
import app.pharmacy.map.com.mappharmacyapp.Models.Pharmacy;
import app.pharmacy.map.com.mappharmacyapp.R;
import app.pharmacy.map.com.mappharmacyapp.Utils.MapUtil;
import butterknife.BindView;
import butterknife.ButterKnife;

public class UserMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private final String TAG = UserMapActivity.class.getSimpleName();
    private static final float DEFAULT_ZOOM = 15f;
    private boolean mLocationPermissionGranted = false;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private GoogleMap mMap;
    private MapUtil mapUtil;
    // Firebase
    private DatabaseReference mRef;
    private FirebaseAuth mAuth;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_map);
        ButterKnife.bind(this);
        init();
        // toolbar
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(getString(R.string.app_name));
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    private void init() {
        getLocationPermission();
        // Firebase
        mAuth = FirebaseAuth.getInstance();
        mRef = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (mLocationPermissionGranted) {
            getDeviceLocation();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true); //to get blue marker with GPS icon
            mMap.getUiSettings().setMyLocationButtonEnabled(true); //to hide GPS icon
            mapUtil = new MapUtil();
            getPharmacyOnMap();
            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    goToMakeOrder(getPharmacy(Objects.requireNonNull(marker.getTag()).toString()));
                    return false;
                }
            });
        }
    }

    ArrayList<Pharmacy> pharmacyArrayList = new ArrayList<>();

    private void getPharmacyOnMap() {
        mRef.child(AppConfig.PHARMACY).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                pharmacyArrayList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    final Pharmacy pharmacy = snapshot.getValue(Pharmacy.class);
                    Double lat = Double.parseDouble(Objects.requireNonNull(pharmacy).getLat());
                    Double lon = Double.parseDouble(pharmacy.getLon());
                    pharmacyArrayList.add(pharmacy);
                    mapUtil.addMarker(mMap, Objects.requireNonNull(pharmacy.getUsername()), new LatLng(lat, lon), pharmacy.getUid());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private Pharmacy getPharmacy(String tag) {
        for (Pharmacy pharmacy : pharmacyArrayList) {
            if (pharmacy.getUid().equals(tag))
                return pharmacy;
        }
        return null;
    }

    private void goToMakeOrder(Pharmacy pharmacy) {
        Intent intent = new Intent(this, UserMakeOrderActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(AppConfig.INTENT_KEY, pharmacy);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void initMAP() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        Objects.requireNonNull(mapFragment).getMapAsync(UserMapActivity.this);
    }

    private void getDeviceLocation() {
        FusedLocationProviderClient mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        try {
            if (mLocationPermissionGranted) {
                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Location current_location = (Location) task.getResult();
                            try {
                                moveCamera(new LatLng(current_location.getLatitude(), current_location.getLongitude()), DEFAULT_ZOOM);

                            } catch (Exception e) {
                                onBackPressed();
                                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                                Toast.makeText(UserMapActivity.this, getString(R.string.enable_premissions), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(UserMapActivity.this, getString(R.string.enable_premissions), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } else {
                Log.e(TAG, "getDeviceLocation: else");
            }
        } catch (SecurityException e) {
            Log.e(TAG, "getDeviceLocation: " + e.getMessage());
        }
    }


    private void moveCamera(LatLng latLng, float zoom) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    private void getLocationPermission() {
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionGranted = true;
                initMAP();
            } else {
                ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    for (int grantResult : grantResults) {
                        if (grantResult != PackageManager.PERMISSION_GRANTED) {
                            mLocationPermissionGranted = false;
                            return;
                        }
                    }
                    mLocationPermissionGranted = true;
                    //initialize our map
                    initMAP();
                }
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.logout:
                logoutUser();
                return true;
            case R.id.my_orders:
                Intent intent = new Intent(this, UserOrdersActivity.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.user_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void sendToStart() {
        Intent intent = new Intent(this, UserTypeActivity.class);
        startActivity(intent);
        finish();
    }

    private void logoutUser() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            mAuth.signOut();
            sendToStart();
        }
    }
}
