package app.pharmacy.map.com.mappharmacyapp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;

import java.util.Objects;

import app.pharmacy.map.com.mappharmacyapp.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UserTypeActivity extends AppCompatActivity {

    public static int typeId;

    @OnClick(R.id.user_type_activity_pharmacy_type_img)
    void choosePharmacyType() {
        typeId = 0;
        Intent intent = new Intent(this, StartActivity.class);
        startActivity(intent);
//        finish();
    }

    @OnClick(R.id.user_type_activity_pharmacy_type_btn)
    void choosePharmacyType2() {
        typeId = 0;
        Intent intent = new Intent(this, StartActivity.class);
        startActivity(intent);
//        finish();
    }

    @OnClick(R.id.user_type_activity_customer_type_img)
    void chooseCustomerType() {
        typeId = 1;
        Intent intent = new Intent(this, StartActivity.class);
        startActivity(intent);
//        finish();
    }

    @OnClick(R.id.user_type_activity_customer_type_btn)
    void chooseCustomerType2() {
        typeId = 1;
        Intent intent = new Intent(this, StartActivity.class);
        startActivity(intent);
//        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_type);
        ButterKnife.bind(this);
    }
}
