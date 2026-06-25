package com.lazyledger.app.ui;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.app.DatePickerDialog;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.lazyledger.app.databinding.FragmentBillBinding;
import java.util.Calendar;

public class BillFragment extends Fragment {

    private FragmentBillBinding binding;
    private MainViewModel mainViewModel;
    private TransactionAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentBillBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupRecyclerView();
        setupViewModel();
        setupListeners();
    }

    @Override
    public void onResume() {
        super.onResume();
        checkAccessibilityService();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void checkAccessibilityService() {
        boolean enabled = isAccessibilityServiceEnabled(requireContext(), com.lazyledger.app.service.AutoLedgerService.class);
        if (enabled) {
            binding.tvServiceStatus.setVisibility(View.GONE);
        } else {
            binding.tvServiceStatus.setVisibility(View.VISIBLE);
        }
    }

    private boolean isAccessibilityServiceEnabled(Context context, Class<?> accessibilityService) {
        ComponentName expectedComponentName = new ComponentName(context, accessibilityService);
        String enabledServicesSetting = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
        if (enabledServicesSetting == null) return false;
        TextUtils.SimpleStringSplitter colonSplitter = new TextUtils.SimpleStringSplitter(':');
        colonSplitter.setString(enabledServicesSetting);
        while (colonSplitter.hasNext()) {
            String componentNameString = colonSplitter.next();
            ComponentName enabledService = ComponentName.unflattenFromString(componentNameString);
            if (enabledService != null && enabledService.equals(expectedComponentName)) {
                return true;
            }
        }
        return false;
    }

    private void setupRecyclerView() {
        adapter = new TransactionAdapter();
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerView.setAdapter(adapter);
    }

    private void setupViewModel() {
        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        
        mainViewModel.getAllTransactions().observe(getViewLifecycleOwner(), transactions -> {
            adapter.setTransactions(transactions);
        });

        mainViewModel.getTotalExpense().observe(getViewLifecycleOwner(), expense -> {
            double val = expense != null ? expense : 0.0;
            binding.tvTotalExpense.setText(String.format("- ¥%.2f", val));
            updateNetBalance();
        });

        mainViewModel.getTotalIncome().observe(getViewLifecycleOwner(), income -> {
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

        binding.llMonthSelector.setOnClickListener(v -> showMonthPicker());
    }

    private void showMonthPicker() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(
                requireContext(),
                android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                (view, year, month, dayOfMonth) -> {
                    String text = (year == Calendar.getInstance().get(Calendar.YEAR) && month == Calendar.getInstance().get(Calendar.MONTH)) 
                            ? "本月" : year + "年" + (month + 1) + "月";
                    binding.tvCurrentMonth.setText(text);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        try {
            int dayPickerId = getResources().getIdentifier("day", "id", "android");
            if(dayPickerId != 0) {
                View dayPicker = dialog.getDatePicker().findViewById(dayPickerId);
                if(dayPicker != null) {
                    dayPicker.setVisibility(View.GONE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        dialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT));
        }
        dialog.show();
    }
}
