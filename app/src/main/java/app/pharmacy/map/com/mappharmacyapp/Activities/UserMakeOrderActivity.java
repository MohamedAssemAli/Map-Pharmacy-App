package app.pharmacy.map.com.mappharmacyapp.Activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

import app.pharmacy.map.com.mappharmacyapp.App.AppConfig;
import app.pharmacy.map.com.mappharmacyapp.R;
import app.pharmacy.map.com.mappharmacyapp.Utils.Validation;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UserMakeOrderActivity extends AppCompatActivity {

    private final String TAG = UserMakeOrderActivity.class.getSimpleName();
    // Vars
    private String order;
    boolean isValid = false;
    // Firebase
    private FirebaseAuth mAuth;
    private DatabaseReference mRef;
    // Views
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
        init();
    }

    private void init() {
        // Firebase
        mAuth = FirebaseAuth.getInstance();
        mRef = FirebaseDatabase.getInstance().getReference();
    }

    private void sendOrder() {
        toggleLayout(false);
        mRef.child(AppConfig.TYPE).setValue("test").addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                toggleLayout(true);
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

    private void toggleLayout(boolean flag) {
        if (flag) {
            progressLayout.setVisibility(View.GONE);
            progressBar.hide();
        } else {
            progressLayout.setVisibility(View.VISIBLE);
            progressBar.show();
        }
    }
}

