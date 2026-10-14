package com.example.callblock.ui.main;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.callblock.R;
import com.example.callblock.databinding.ActivityMainBinding;
import com.example.callblock.ui.brand.BrandListFragment;
import com.example.callblock.ui.calllog.CallLogFragment;
import com.example.callblock.ui.history.BlockedLogsFragment;
import com.example.callblock.ui.onboarding.OnboardingActivity;
import com.example.callblock.ui.settings.SettingsFragment;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if onboarding needs to be shown on first app launch
        SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
        boolean onboardingShown = prefs.getBoolean("onboarding_shown", false);
        if (!onboardingShown) {
            Intent intent = new Intent(this, OnboardingActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (savedInstanceState == null) {
            loadFragment(new CallLogFragment());
        }

        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_call_log) {
                loadFragment(new CallLogFragment());
                return true;
            } else if (itemId == R.id.nav_filter) {
                loadFragment(new BlockedLogsFragment());
                return true;
            } else if (itemId == R.id.nav_brand) {
                loadFragment(new BrandListFragment());
                return true;
            } else if (itemId == R.id.nav_settings) {
                loadFragment(new SettingsFragment());
                return true;
            }
            return false;
        });
    }

    public void navigateToBrandTab() {
        binding.bottomNavigation.setSelectedItemId(R.id.nav_brand);
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}
