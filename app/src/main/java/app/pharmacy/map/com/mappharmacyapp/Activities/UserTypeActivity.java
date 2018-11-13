package app.pharmacy.map.com.mappharmacyapp.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import app.pharmacy.map.com.mappharmacyapp.R;
import butterknife.ButterKnife;

public class UserTypeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_type);
        ButterKnife.bind(this);
    }
}
