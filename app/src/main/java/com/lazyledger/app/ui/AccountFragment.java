package com.lazyledger.app.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.lazyledger.app.databinding.FragmentAccountBinding;

public class AccountFragment extends Fragment {

    private FragmentAccountBinding binding;
    private MainViewModel mainViewModel;
    private SharedPreferences sharedPreferences;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAccountBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        sharedPreferences = requireContext().getSharedPreferences("LazyLedgerPrefs", Context.MODE_PRIVATE);

        setupListeners();
    }

    private void setupListeners() {
        binding.btnSetBudget.setOnClickListener(v -> showBudgetDialog());
        binding.btnClearData.setOnClickListener(v -> showClearDataDialog());
        binding.btnAbout.setOnClickListener(v -> showAboutDialog());
    }

    private void showBudgetDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("设置月度预算");

        final EditText input = new EditText(requireContext());
        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        float currentBudget = sharedPreferences.getFloat("monthly_budget", 3000f);
        input.setText(String.valueOf(currentBudget));
        builder.setView(input);

        builder.setPositiveButton("保存", (dialog, which) -> {
            String val = input.getText().toString();
            try {
                float newBudget = Float.parseFloat(val);
                sharedPreferences.edit().putFloat("monthly_budget", newBudget).apply();
                Toast.makeText(requireContext(), "预算已更新", Toast.LENGTH_SHORT).show();
            } catch (NumberFormatException e) {
                Toast.makeText(requireContext(), "无效的金额", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("取消", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void showClearDataDialog() {
        new AlertDialog.Builder(requireContext())
            .setTitle("警告")
            .setMessage("确定要清空所有的账单数据吗？此操作不可恢复。")
            .setPositiveButton("确定清空", (dialog, which) -> {
                mainViewModel.deleteAll();
                Toast.makeText(requireContext(), "数据已清空", Toast.LENGTH_SHORT).show();
            })
            .setNegativeButton("取消", null)
            .show();
    }

    private void showAboutDialog() {
        new AlertDialog.Builder(requireContext())
            .setTitle("关于 LazyLedger")
            .setMessage("懒大王记账 V1.0\n让记账变得更简单。\n图标已由设计师定制。")
            .setPositiveButton("确定", null)
            .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
