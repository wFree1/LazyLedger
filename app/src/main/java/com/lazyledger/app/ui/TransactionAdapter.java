package com.lazyledger.app.ui;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.lazyledger.app.R;
import com.lazyledger.app.db.entity.Transaction;
import com.lazyledger.app.utils.DateUtils;
import java.util.ArrayList;
import java.util.List;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {

    private List<Transaction> transactions = new ArrayList<>();

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_transaction, parent, false);
        return new TransactionViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        Transaction current = transactions.get(position);
        holder.tvMerchant.setText(current.merchantName);
        
        String timeStr = DateUtils.formatTimestamp(current.timestamp);
        holder.tvPlatformAndTime.setText(timeStr);

        // UI Logic for Pastel Theme
        if (current.type == 1) { // Income (Green)
            holder.tvAmount.setText(String.format("+ %.2f", current.amount));
            holder.tvAmount.setTextColor(Color.parseColor("#10C999")); // colorAmountGreen
            holder.flIconBg.setBackgroundResource(R.drawable.bg_icon_green);
            holder.tvCategoryIcon.setTextColor(Color.parseColor("#10C999"));
            holder.tvCategoryIcon.setText("收");
        } else { // Expense (Red/Yellow/Blue depending on category, but we default to Red or Blue)
            holder.tvAmount.setText(String.format("- %.2f", current.amount));
            holder.tvAmount.setTextColor(Color.parseColor("#FF6464")); // colorAmountRed
            
            if (current.merchantName.contains("外卖") || current.merchantName.contains("餐饮")) {
                holder.flIconBg.setBackgroundResource(R.drawable.bg_icon_red); // Reuse red for food
                holder.tvCategoryIcon.setTextColor(Color.parseColor("#FF6464"));
                holder.tvCategoryIcon.setText("吃");
            } else {
                // Default expense looks somewhat blue/yellow in pastel UI
                holder.flIconBg.setBackgroundResource(R.drawable.bg_icon_red);
                holder.tvCategoryIcon.setTextColor(Color.parseColor("#FF6464"));
                holder.tvCategoryIcon.setText("支");
            }
        }

        // Special handling for Red Packets
        if ("红包".equals(current.category)) {
            holder.flIconBg.setBackgroundResource(R.drawable.bg_icon_red);
            holder.tvCategoryIcon.setTextColor(Color.parseColor("#FF6464"));
            holder.tvCategoryIcon.setText("红");
        }
    }

    @Override
    public int getItemCount() {
        return transactions != null ? transactions.size() : 0;
    }

    class TransactionViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvMerchant;
        private final TextView tvAmount;
        private final TextView tvPlatformAndTime;
        private final TextView tvCategoryIcon;
        private final View flIconBg;

        private TransactionViewHolder(View itemView) {
            super(itemView);
            tvMerchant = itemView.findViewById(R.id.tvMerchant);
            tvAmount = itemView.findViewById(R.id.tvAmount);
            tvPlatformAndTime = itemView.findViewById(R.id.tvPlatformAndTime);
            tvCategoryIcon = itemView.findViewById(R.id.tvCategoryIcon);
            flIconBg = itemView.findViewById(R.id.flIconBg);
        }
    }
}
