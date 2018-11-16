package app.pharmacy.map.com.mappharmacyapp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import app.pharmacy.map.com.mappharmacyapp.App.AppConfig;
import app.pharmacy.map.com.mappharmacyapp.Models.Order;
import app.pharmacy.map.com.mappharmacyapp.Models.Pharmacy;
import app.pharmacy.map.com.mappharmacyapp.Models.User;
import app.pharmacy.map.com.mappharmacyapp.R;
import app.pharmacy.map.com.mappharmacyapp.Utils.Imageutility;
import app.pharmacy.map.com.mappharmacyapp.Utils.Validation;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UserMakeOrderActivity extends AppCompatActivity {

    private final String TAG = UserMakeOrderActivity.class.getSimpleName();
    // Vars
    private Pharmacy pharmacyObj;
    private String uid, username, order;
    boolean isValid = false;
    // Firebase
    private FirebaseAuth mAuth;
    private DatabaseReference mRef;
    // Views
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.user_make_order_activity_order_txt)
    EditText orderTxt;
    @BindView(R.id.user_make_order_activity_order_btn)
    Button orderBtn;
    @BindView(R.id.progress_bar)
    ContentLoadingProgressBar progressBar;
    @BindView(R.id.progress_layout)
    RelativeLayout progressLayout;

    @OnClick(R.id.user_make_order_activity_order_btn)
    void goToOrders() {
        CheckValidation();
        if (isValid)
            sendOrder();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_make_order);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        if (intent != null) {
            pharmacyObj = (Pharmacy) intent.getSerializableExtra(AppConfig.INTENT_KEY);
            if (pharmacyObj != null) {
                init();

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


    @Override
    protected void onResume() {
        super.onResume();
        getUserCredentials();
    }

    private void getUserCredentials() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            uid = currentUser.getUid();
            getUserdata(uid);
        }
    }

    private void sendOrder() {
        toggleLayout(false);
        final String key = mRef.child(AppConfig.USER_ORDERS).child(uid).push().getKey();
        final Order orderObj = new Order(key, order, 0, pharmacyObj.getUid(), pharmacyObj.getUsername(), uid, username);
        mRef.child(AppConfig.USER_ORDERS)
                .child(uid)
                .child(Objects.requireNonNull(key))
                .setValue(orderObj)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        mRef.child(AppConfig.PHARMACY_ORDERS)
                                .child(pharmacyObj.getUid())
                                .child(Objects.requireNonNull(key))
                                .setValue(orderObj).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                toggleLayout(true);
                            }
                        });
                        finish();

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UserMakeOrderActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                toggleLayout(true);
            }
        });
    }

    private void CheckValidation() {
        boolean orderFlag;
        orderFlag = validateInput(orderTxt, Validation.VALIDATE_NAME, getString(R.string.empty_order));
        if (orderFlag) {
            order = getInput(orderTxt);
            isValid = true;
        } else {
            isValid = false;
        }
    }

    private boolean validateInput(EditText editText, int funNum, String errorMsg) {
        String text = getInput(editText);
        Validation validation = new Validation();
        if (text.isEmpty()) {
            Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show();
            return false;
        } else return validation.validate(funNum, text);
    }

    private String getInput(EditText editText) {
        return Objects.requireNonNull(editText.getText()).toString();
    }

    private void getUserdata(String uid) {
        mRef.child(AppConfig.USERS).child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                username = Objects.requireNonNull(dataSnapshot.child(AppConfig.USERS_USERNAME).getValue()).toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "load user data:onCancelled", databaseError.toException());
            }
        });
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

