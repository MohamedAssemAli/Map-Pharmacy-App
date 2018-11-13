package app.pharmacy.map.com.mappharmacyapp.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import app.pharmacy.map.com.mappharmacyapp.R;
import butterknife.ButterKnife;

public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        ButterKnife.bind(this);
    }
}
