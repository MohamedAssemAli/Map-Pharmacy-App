package app.pharmacy.map.com.mappharmacyapp.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import app.pharmacy.map.com.mappharmacyapp.R;
import butterknife.ButterKnife;

public class PharmacyOrderDetails extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pharmacy_order_details);
        ButterKnife.bind(this);
    }
}
