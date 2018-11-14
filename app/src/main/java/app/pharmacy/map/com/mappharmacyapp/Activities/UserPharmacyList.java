package app.pharmacy.map.com.mappharmacyapp.Activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

import app.pharmacy.map.com.mappharmacyapp.Adapters.PharmacyAdapter;
import app.pharmacy.map.com.mappharmacyapp.App.AppConfig;
import app.pharmacy.map.com.mappharmacyapp.Models.Pharmacy;
import app.pharmacy.map.com.mappharmacyapp.R;
import app.pharmacy.map.com.mappharmacyapp.Utils.ViewsUtils;
import butterknife.BindView;
import butterknife.ButterKnife;

public class UserPharmacyList extends AppCompatActivity {

    private static final String TAG = UserPharmacyList.class.getSimpleName();
    // Views
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.toolbar_title)
    TextView toolbarTitle;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_pharmacy_list);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        toggleLayout(false);
        // Firebase
        mRef = FirebaseDatabase.getInstance().getReference();
        pharmacyArrayList = new ArrayList<>();
        toggleLayout(false);
        new ViewsUtils().setupLinearVerticalRecView(this, pharmacyRecyclerView);
        pharmacyAdapter = new PharmacyAdapter(this, pharmacyArrayList);
        pharmacyRecyclerView.setAdapter(pharmacyAdapter);
        getOrders();
    }

    private void getOrders() {
        mRef.child(AppConfig.PHARMACY)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            pharmacyArrayList.clear();
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                Pharmacy pharmacy = snapshot.getValue(Pharmacy.class);
                                pharmacyArrayList.add(pharmacy);
                                pharmacyAdapter.notifyDataSetChanged();
                            }
                            Collections.reverse(pharmacyArrayList);
                            toggleLayout(true);
                        } else {
                            toggleLayout(true);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.d(TAG, databaseError.getMessage());
                        Log.d(TAG, databaseError.getDetails());
                        Toast.makeText(UserPharmacyList.this, R.string.error, Toast.LENGTH_LONG).show();
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
                emptyRecyclerPlaceHolder.setVisibility(View.VISIBLE);
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
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
