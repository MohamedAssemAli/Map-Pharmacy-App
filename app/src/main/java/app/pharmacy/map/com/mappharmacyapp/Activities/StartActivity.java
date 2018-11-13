package app.pharmacy.map.com.mappharmacyapp.Activities;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import app.pharmacy.map.com.mappharmacyapp.Adapters.ViewPagerAdapter;
import app.pharmacy.map.com.mappharmacyapp.Fragments.SignInFragment;
import app.pharmacy.map.com.mappharmacyapp.Fragments.SignUpFragment;
import app.pharmacy.map.com.mappharmacyapp.R;
import app.pharmacy.map.com.mappharmacyapp.Utils.ViewsUtils;
import butterknife.BindView;
import butterknife.ButterKnife;

public class StartActivity extends AppCompatActivity {

    // Views
    @BindView(R.id.tab_start)
    TabLayout tabLayout;
    @BindView(R.id.view_pager_start)
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragment(new SignInFragment(), getString(R.string.sign_in));
        viewPagerAdapter.addFragment(new SignUpFragment(), getString(R.string.sign_up));
        new ViewsUtils().setupTabLayout(viewPager, viewPagerAdapter, tabLayout, 0);
    }
}
