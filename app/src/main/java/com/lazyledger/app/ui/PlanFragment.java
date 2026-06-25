package com.lazyledger.app.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.lazyledger.app.databinding.FragmentPlanBinding;

public class PlanFragment extends Fragment {

    private FragmentPlanBinding binding;
    private MainViewModel mainViewModel;
    private SharedPreferences sharedPreferences;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentPlanBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        sharedPreferences = requireContext().getSharedPreferences("LazyLedgerPrefs", Context.MODE_PRIVATE);
        
        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        mainViewModel.getTotalExpense().observe(getViewLifecycleOwner(), this::updateBudgetUI);
    }
    
    @Override
    public void onResume() {
        super.onResume();
        // Update budget in case it was changed in Account page
        Double currentExpense = mainViewModel.getTotalExpense().getValue();
        updateBudgetUI(currentExpense);
    }

    private void updateBudgetUI(Double expense) {
        if (binding == null) return;
        
        double used = expense != null ? expense : 0.0;
        double budget = sharedPreferences.getFloat("monthly_budget", 3000f); // Default 3000
        
        binding.tvTotalBudget.setText(String.format("¥ %.2f", budget));
        binding.tvUsedBudget.setText(String.format("¥ %.2f", used));
        
        double remaining = budget - used;
        binding.tvRemainingBudget.setText(String.format("¥ %.2f", remaining));
        
        if (remaining < 0) {
            binding.tvRemainingBudget.setTextColor(getResources().getColor(com.lazyledger.app.R.color.colorAmountRed, null));
            binding.tvRemainingBudget.setText(String.format("- ¥ %.2f", Math.abs(remaining)));
        } else {
            binding.tvRemainingBudget.setTextColor(getResources().getColor(com.lazyledger.app.R.color.colorAmountGreen, null));
        }

        int progress = budget > 0 ? (int) ((used / budget) * 100) : 0;
        binding.progressBarBudget.setProgress(Math.min(progress, 100));
        binding.tvBudgetStatus.setText(progress + "%");
        
        // Change progress bar color to red if over budget
        // (For simplicity, we use the primary color and let it fill up)
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
