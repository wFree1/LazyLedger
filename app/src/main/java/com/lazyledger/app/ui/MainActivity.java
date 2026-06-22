package com.lazyledger.app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.lazyledger.app.databinding.ActivityMainBinding;
import com.lazyledger.app.db.entity.Transaction;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private MainViewModel mainViewModel;
    private TransactionAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupRecyclerView();
        setupViewModel();
        setupListeners();
    }

    private void setupRecyclerView() {
        adapter = new TransactionAdapter();
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(adapter);
    }

    private void setupViewModel() {
        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        
        mainViewModel.getAllTransactions().observe(this, transactions -> {
            adapter.setTransactions(transactions);
        });

        mainViewModel.getTotalExpense().observe(this, expense -> {
            double val = expense != null ? expense : 0.0;
            binding.tvTotalExpense.setText(String.format("- ¥%.2f", val));
            updateNetBalance();
        });

        mainViewModel.getTotalIncome().observe(this, income -> {
            double val = income != null ? income : 0.0;
            binding.tvTotalIncome.setText(String.format("+ ¥%.2f", val));
            updateNetBalance();
        });
    }

    private void updateNetBalance() {
        Double inc = mainViewModel.getTotalIncome().getValue();
        Double exp = mainViewModel.getTotalExpense().getValue();
        double incomeVal = inc != null ? inc : 0.0;
        double expVal = exp != null ? exp : 0.0;
        double net = incomeVal - expVal;
        binding.tvTotalBalance.setText(String.format("¥ %.2f", net));
    }

    private void setupListeners() {
        binding.tvServiceStatus.setOnClickListener(v -> {
            Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            startActivity(intent);
        });
    }
}
