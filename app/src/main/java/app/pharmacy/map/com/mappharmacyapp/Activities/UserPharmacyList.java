package app.pharmacy.map.com.mappharmacyapp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

import app.pharmacy.map.com.mappharmacyapp.Adapters.PharmacyAdapter;
import app.pharmacy.map.com.mappharmacyapp.App.AppConfig;
import app.pharmacy.map.com.mappharmacyapp.Models.Pharmacy;
import app.pharmacy.map.com.mappharmacyapp.R;
import app.pharmacy.map.com.mappharmacyapp.Utils.ViewsUtils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UserPharmacyList extends AppCompatActivity {

    private static final String TAG = UserPharmacyList.class.getSimpleName();
    // Views
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.user_pharmacy_activity_recycler_view)
    RecyclerView pharmacyRecyclerView;
    @BindView(R.id.user_pharmacy_activity_empty_recycler)
    TextView emptyRecyclerPlaceHolder;
    @BindView(R.id.progress_bar)
    ContentLoadingProgressBar progressBar;
    @BindView(R.id.progress_layout)
    RelativeLayout progressLayout;
    // Vars
    private ArrayList<Pharmacy> pharmacyArrayList;
    private PharmacyAdapter pharmacyAdapter;
    // Firebase
    private DatabaseReference mRef;
    private FirebaseAuth mAuth;

    // OnClicks
    @OnClick(R.id.user_pharmacy_activity_other_button)
    void goToMap() {
        Intent intent = new Intent(this, UserMapActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_pharmacy_list);
        ButterKnife.bind(this);
        init();
        // toolbar
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(getString(R.string.app_name));
    }


    private void init() {
        toggleLayout(false);
        // Firebase
        mRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        pharmacyArrayList = new ArrayList<>();
        toggleLayout(false);
        new ViewsUtils().setupLinearVerticalRecView(this, pharmacyRecyclerView);
        pharmacyAdapter = new PharmacyAdapter(this, pharmacyArrayList);
        pharmacyRecyclerView.setAdapter(pharmacyAdapter);
        getPharmacyOnMap();
    }

    private void getPharmacyOnMap() {
        mRef.child(AppConfig.PHARMACY).limitToFirst(5)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Pharmacy pharmacy = snapshot.getValue(Pharmacy.class);
                            pharmacyArrayList.add(pharmacy);
                            pharmacyAdapter.notifyDataSetChanged();
                        }
                        Collections.reverse(pharmacyArrayList);
                        toggleLayout(true);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void toggleLayout(boolean flag) {
        if (flag) {
            progressLayout.setVisibility(View.GONE);
            progressBar.hide();
            if (pharmacyArrayList.isEmpty()) {
                emptyRecyclerPlaceHolder.setVisibility(View.VISIBLE);
                pharmacyRecyclerView.setVisibility(View.GONE);
            } else {

                emptyRecyclerPlaceHolder.setVisibility(View.GONE);
                pharmacyRecyclerView.setVisibility(View.VISIBLE);
            }
        } else {
            progressLayout.setVisibility(View.VISIBLE);
            progressBar.show();
            pharmacyRecyclerView.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
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
