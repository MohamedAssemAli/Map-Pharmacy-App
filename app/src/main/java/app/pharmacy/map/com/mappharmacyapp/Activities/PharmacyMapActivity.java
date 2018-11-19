package app.pharmacy.map.com.mappharmacyapp.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

import app.pharmacy.map.com.mappharmacyapp.App.AppConfig;
import app.pharmacy.map.com.mappharmacyapp.Models.Pharmacy;
import app.pharmacy.map.com.mappharmacyapp.Models.User;
import app.pharmacy.map.com.mappharmacyapp.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PharmacyMapActivity extends FragmentActivity implements OnMapReadyCallback {

    private final String TAG = PharmacyMapActivity.class.getSimpleName();
    private static final float DEFAULT_ZOOM = 15f;
    private boolean mLocationPermissionGranted = false;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private GoogleMap mMap;
    private User user;
    private String lat, lon;
    // Firebase
    private FirebaseAuth mAuth;
    private DatabaseReference mRef;
    // Views
    @BindView(R.id.progress_bar)
    ContentLoadingProgressBar progressBar;
    @BindView(R.id.progress_layout)
    RelativeLayout progressLayout;

    // OnClicks
    @OnClick(R.id.pharmacy_map_done_btn)
    void sendData() {
        signUp(user);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pharmacy_map);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        if (intent != null) {
            user = (User) intent.getSerializableExtra(AppConfig.INTENT_KEY);
            if (user != null) {
                init();
            } else {
                closeOnError();
            }
        } else {
            closeOnError();
        }
    }

    private void signUp(final User user) {
        toggleLayout(false);
        if (UserTypeActivity.typeId == 0) {
            mAuth.createUserWithEmailAndPassword(user.getEmail(), user.getPassword())
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                String uid = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
                                Pharmacy pharmacy = new Pharmacy(uid, user.getUsername(), user.getEmail(), user.getPassword(), lat, lon);
                                mRef.child(AppConfig.PHARMACY).child(uid).setValue(pharmacy);
                                Intent intent = new Intent(PharmacyMapActivity.this, PharmacyOrdersActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                                        Intent.FLAG_ACTIVITY_CLEAR_TASK |
                                        Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                Objects.requireNonNull(PharmacyMapActivity.this).finish();
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                toggleLayout(true);
                            } else {
                                Toast.makeText(PharmacyMapActivity.this, getString(R.string.error), Toast.LENGTH_LONG).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(PharmacyMapActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                    toggleLayout(true);
                }
            });
        } else {
            toggleLayout(false);
            mAuth.createUserWithEmailAndPassword(user.getEmail(), user.getPassword()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        String uid = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
                        user.setUid(uid);
                        mRef.child(AppConfig.USERS).child(uid).setValue(user);
                        Intent intent = new Intent(PharmacyMapActivity.this, UserMapActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                                Intent.FLAG_ACTIVITY_CLEAR_TASK |
                                Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                        toggleLayout(true);
                    } else {
                        Toast.makeText(PharmacyMapActivity.this, getString(R.string.error), Toast.LENGTH_LONG).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(PharmacyMapActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                    toggleLayout(true);
                }
            });
        }
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
        }
    }

    private void initMAP() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        Objects.requireNonNull(mapFragment).getMapAsync(PharmacyMapActivity.this);
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
                                mMap.addMarker(new MarkerOptions()
                                        .position(new LatLng(current_location.getLatitude(), current_location.getLongitude()))
                                        .title("My Location")
                                        .draggable(false));

                                lat = String.valueOf(current_location.getLatitude());
                                lon = String.valueOf(current_location.getLongitude());
                            } catch (Exception e) {
                                onBackPressed();
                                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                                Toast.makeText(PharmacyMapActivity.this, getString(R.string.enable_premissions), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(PharmacyMapActivity.this, getString(R.string.enable_premissions), Toast.LENGTH_SHORT).show();
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

    private void toggleLayout(boolean flag) {
        if (flag) {
            progressLayout.setVisibility(View.GONE);
            progressBar.hide();
        } else {
            progressLayout.setVisibility(View.VISIBLE);
            progressBar.show();
        }
    }

    private void closeOnError() {
        finish();
        Toast.makeText(this, R.string.error, Toast.LENGTH_SHORT).show();
    }
}
