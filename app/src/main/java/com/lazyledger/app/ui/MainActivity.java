package com.lazyledger.app.ui;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.lazyledger.app.databinding.ActivityMainBinding;
import com.lazyledger.app.R;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    
    private Fragment billFragment = new BillFragment();
    private Fragment reportFragment = new ReportFragment();
    private Fragment planFragment = new PlanFragment();
    private Fragment accountFragment = new AccountFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Default fragment
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, billFragment)
                .commit();
            updateNavSelection(binding.navWallet);
        }

        setupListeners();
    }

    private void setupListeners() {
        binding.navWallet.setOnClickListener(v -> {
            switchFragment(billFragment);
            updateNavSelection(binding.navWallet);
        });

        binding.navReport.setOnClickListener(v -> {
            switchFragment(reportFragment);
            updateNavSelection(binding.navReport);
        });

        binding.navPlan.setOnClickListener(v -> {
            switchFragment(planFragment);
            updateNavSelection(binding.navPlan);
        });

        binding.navAccount.setOnClickListener(v -> {
            switchFragment(accountFragment);
            updateNavSelection(binding.navAccount);
        });
        
        binding.fab.setOnClickListener(v -> {
            // Note: FAB might have specific action later
        });
    }

    private void switchFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit();
    }
    
    private void updateNavSelection(TextView selectedNav) {
        int colorPrimary = getResources().getColor(R.color.colorPrimary, getTheme());
        int colorGrey = getResources().getColor(R.color.colorTextGrey, getTheme());

        binding.navWallet.setTextColor(colorGrey);
        binding.navReport.setTextColor(colorGrey);
        binding.navPlan.setTextColor(colorGrey);
        binding.navAccount.setTextColor(colorGrey);

        selectedNav.setTextColor(colorPrimary);
    }
}
