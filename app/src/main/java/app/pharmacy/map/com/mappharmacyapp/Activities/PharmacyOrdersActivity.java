package app.pharmacy.map.com.mappharmacyapp.Activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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

import app.pharmacy.map.com.mappharmacyapp.Adapters.PharmacyOrdersAdapter;
import app.pharmacy.map.com.mappharmacyapp.App.AppConfig;
import app.pharmacy.map.com.mappharmacyapp.Models.Order;
import app.pharmacy.map.com.mappharmacyapp.R;
import app.pharmacy.map.com.mappharmacyapp.Utils.ViewsUtils;
import butterknife.BindView;
import butterknife.ButterKnife;

public class PharmacyOrdersActivity extends AppCompatActivity {

    private static final String TAG = PharmacyOrdersActivity.class.getSimpleName();
    // Views
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.pharmacy_orders_activity_recycler_view)
    RecyclerView ordersRecyclerView;
    @BindView(R.id.pharmacy_orders_activity_empty_recycler)
    TextView emptyRecyclerPlaceHolder;
    @BindView(R.id.progress_bar)
    ContentLoadingProgressBar progressBar;
    @BindView(R.id.progress_layout)
    RelativeLayout progressLayout;
    // Vars
    private ArrayList<Order> ordersArrayList;
    private PharmacyOrdersAdapter pharmacyOrdersAdapter;
    // Firebase
    private DatabaseReference mRef;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pharmacy_orders);
        ButterKnife.bind(this);
        init();

        // toolbar
        setSupportActionBar(toolbar);

    }

    @Override
    protected void onResume() {
        super.onResume();
        getUserCredentials();
    }

    private void getUserCredentials() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();
            getOrders(uid);
        }
    }

    private void init() {
        toggleLayout(false);
        // Firebase
        mAuth = FirebaseAuth.getInstance();
        mRef = FirebaseDatabase.getInstance().getReference();
        ordersArrayList = new ArrayList<>();
        toggleLayout(false);
        new ViewsUtils().setupLinearVerticalRecView(this, ordersRecyclerView);
        pharmacyOrdersAdapter = new PharmacyOrdersAdapter(this, ordersArrayList);
        ordersRecyclerView.setAdapter(pharmacyOrdersAdapter);
    }

    private void getOrders(String uid) {
        mRef.child(AppConfig.PHARMACY_ORDERS)
                .child(uid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            ordersArrayList.clear();
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                Order order = snapshot.getValue(Order.class);
                                Objects.requireNonNull(order).setIndex(String.valueOf(ordersArrayList.size() + 1));
                                ordersArrayList.add(order);
                                pharmacyOrdersAdapter.notifyDataSetChanged();
                            }
                            Collections.reverse(ordersArrayList);
                            toggleLayout(true);
                        } else {
                            toggleLayout(true);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.d(TAG, databaseError.getMessage());
                        Log.d(TAG, databaseError.getDetails());
                        Toast.makeText(PharmacyOrdersActivity.this, R.string.error, Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void toggleLayout(boolean flag) {
        if (flag) {
            progressLayout.setVisibility(View.GONE);
            progressBar.hide();
            if (ordersArrayList.isEmpty()) {
                emptyRecyclerPlaceHolder.setVisibility(View.VISIBLE);
                ordersRecyclerView.setVisibility(View.GONE);
            } else {
                emptyRecyclerPlaceHolder.setVisibility(View.GONE);
                ordersRecyclerView.setVisibility(View.VISIBLE);
            }
        } else {
            progressLayout.setVisibility(View.VISIBLE);
            progressBar.show();
            ordersRecyclerView.setVisibility(View.GONE);
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
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // check if user is signed in and update UI accordingly
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            sendToStart();
        }
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

