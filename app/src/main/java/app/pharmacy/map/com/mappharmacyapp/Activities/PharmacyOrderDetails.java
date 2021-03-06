package app.pharmacy.map.com.mappharmacyapp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

import app.pharmacy.map.com.mappharmacyapp.App.AppConfig;
import app.pharmacy.map.com.mappharmacyapp.Models.Order;
import app.pharmacy.map.com.mappharmacyapp.Models.User;
import app.pharmacy.map.com.mappharmacyapp.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PharmacyOrderDetails extends AppCompatActivity {

    private static final String TAG = PharmacyOrderDetails.class.getSimpleName();
    // Vars
    Order order;
    // Firebase
    private DatabaseReference mRef;
    private FirebaseAuth mAuth;
    // Views
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.order_details_activity_username)
    TextView orderUsernameTxt;
    @BindView(R.id.order_details_activity_order_number)
    TextView orderNumberTxt;
    @BindView(R.id.order_details_activity_order)
    TextView orderTxt;
    @BindView(R.id.order_details_activity_state_radio_group)
    RadioGroup stateRadioGroup;
    @BindView(R.id.order_details_activity_pending_state)
    RadioButton pendingStateRadioButton;
    @BindView(R.id.order_details_activity_available_state)
    RadioButton deliveredStateRadioButton;
    @BindView(R.id.progress_bar)
    ContentLoadingProgressBar progressBar;
    @BindView(R.id.progress_layout)
    RelativeLayout progressLayout;

    // OnClicks
    @OnClick(R.id.order_details_activity_done_btn)
    void sendData() {
        // get selected radio button from radioGroup
        int selectedId = stateRadioGroup.getCheckedRadioButtonId();
        // find the radioButton by returned id
        RadioButton radioButton = findViewById(selectedId);
        int state = radioButton.getId();
        if (state == R.id.order_details_activity_available_state)
            updateOrder(order, 1);
        else
            updateOrder(order, 0);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pharmacy_order_details);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        if (intent != null) {
            order = (Order) intent.getSerializableExtra(AppConfig.INTENT_KEY);
            if (order != null) {
                init();
                updateUi(order);
                // toolbar
                setSupportActionBar(toolbar);
                Objects.requireNonNull(getSupportActionBar()).setTitle(getString(R.string.app_name));
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowHomeEnabled(true);
            } else {
                closeOnError();
            }
        } else {
            closeOnError();
        }
    }

    private void init() {
        // Firebase
        mAuth = FirebaseAuth.getInstance();
        mRef = FirebaseDatabase.getInstance().getReference();
    }

    private void updateUi(Order order) {
        orderUsernameTxt.setText(order.getUsername());
        orderNumberTxt.setText(order.getIndex());
        orderTxt.setText(order.getOrder());
        if (order.getState() == 0) {
            pendingStateRadioButton.setChecked(true);
        } else {
            deliveredStateRadioButton.setChecked(true);
        }
    }

    private void updateOrder(final Order order, final int state) {
        mRef.child(AppConfig.PHARMACY_ORDERS)
                .child(order.getPharmacyUid())
                .child(order.getUid())
                .child(AppConfig.STATE)
                .setValue(state)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        mRef.child(AppConfig.USER_ORDERS)
                                .child(order.getUserUid())
                                .child(order.getUid())
                                .child(AppConfig.STATE)
                                .setValue(state);
                        Toast.makeText(PharmacyOrderDetails.this, getString(R.string.order_is_updated), Toast.LENGTH_LONG).show();
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(PharmacyOrderDetails.this, getString(R.string.error), Toast.LENGTH_LONG).show();
            }
        });
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
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
