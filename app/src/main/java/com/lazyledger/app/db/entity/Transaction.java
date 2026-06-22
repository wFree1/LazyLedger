package com.lazyledger.app.db.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "transactions")
public class Transaction {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String merchantName;
    public double amount;
    public long timestamp;
    public String platform; // "WeChat" or "Alipay"
    
    public int type; // 0 for Expense (支出), 1 for Income (收入)
    public String category; // "餐饮", "红包", "转账"

    public Transaction(String merchantName, double amount, long timestamp, String platform, int type, String category) {
        this.merchantName = merchantName;
        this.amount = amount;
        this.timestamp = timestamp;
        this.platform = platform;
        this.type = type;
        this.category = category;
    }
}
