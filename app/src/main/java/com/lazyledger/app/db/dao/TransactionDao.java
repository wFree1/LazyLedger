package com.lazyledger.app.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import com.lazyledger.app.db.entity.Transaction;
import java.util.List;

@Dao
public interface TransactionDao {
    @Insert
    void insert(Transaction transaction);

    @Query("SELECT * FROM transactions ORDER BY timestamp DESC")
    LiveData<List<Transaction>> getAllTransactions();

    @Query("SELECT SUM(amount) FROM transactions WHERE type = 0")
    LiveData<Double> getTotalExpense();

    @Query("SELECT SUM(amount) FROM transactions WHERE type = 1")
    LiveData<Double> getTotalIncome();
}
