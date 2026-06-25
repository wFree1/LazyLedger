package com.lazyledger.app.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.lazyledger.app.databinding.FragmentReportBinding;
import java.util.ArrayList;
import java.util.List;

public class ReportFragment extends Fragment {

    private FragmentReportBinding binding;
    private MainViewModel mainViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentReportBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        setupChart();
        
        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        mainViewModel.getExpenseByCategory().observe(getViewLifecycleOwner(), categorySums -> {
            if (categorySums != null && !categorySums.isEmpty()) {
                updateChart(categorySums);
            } else {
                binding.pieChart.clear();
            }
        });
    }
    
    private void setupChart() {
        PieChart pieChart = binding.pieChart;
        pieChart.getDescription().setEnabled(false);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.TRANSPARENT);
        pieChart.setTransparentCircleRadius(0f);
        pieChart.setCenterText("支出分类");
        pieChart.setCenterTextSize(18f);
        pieChart.setCenterTextColor(Color.parseColor("#2C304A"));
        pieChart.getLegend().setEnabled(true);
    }
    
    private void updateChart(List<com.lazyledger.app.db.entity.CategorySum> categorySums) {
        List<PieEntry> entries = new ArrayList<>();
        for (com.lazyledger.app.db.entity.CategorySum sum : categorySums) {
            entries.add(new PieEntry((float) sum.total, sum.category != null ? sum.category : "其他"));
        }

        PieDataSet dataSet = new PieDataSet(entries, "");
        
        // Define colors
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.parseColor("#71B2D9")); // Primary Blue
        colors.add(Color.parseColor("#86E0B9")); // Primary Green
        colors.add(Color.parseColor("#FF6464")); // Amount Red
        colors.add(Color.parseColor("#FFD54F")); // Yellow
        colors.add(Color.parseColor("#BA68C8")); // Purple
        colors.add(Color.parseColor("#FF8A65")); // Orange
        dataSet.setColors(colors);

        dataSet.setValueTextSize(14f);
        dataSet.setValueTextColor(Color.WHITE);

        PieData data = new PieData(dataSet);
        binding.pieChart.setData(data);
        binding.pieChart.invalidate(); // refresh
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
