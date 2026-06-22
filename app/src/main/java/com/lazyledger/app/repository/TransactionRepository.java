package com.lazyledger.app.repository;

import android.app.Application;
import androidx.lifecycle.LiveData;
import com.lazyledger.app.db.AppDatabase;
import com.lazyledger.app.db.dao.TransactionDao;
import com.lazyledger.app.db.entity.Transaction;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TransactionRepository {
    private TransactionDao transactionDao;
    private LiveData<List<Transaction>> allTransactions;
    private LiveData<Double> totalExpense;
    private LiveData<Double> totalIncome;
    private static final ExecutorService executorService = Executors.newFixedThreadPool(4);

    public TransactionRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        transactionDao = db.transactionDao();
        allTransactions = transactionDao.getAllTransactions();
        totalExpense = transactionDao.getTotalExpense();
        totalIncome = transactionDao.getTotalIncome();
    }

    public LiveData<List<Transaction>> getAllTransactions() {
        return allTransactions;
    }

    public LiveData<Double> getTotalExpense() {
        return totalExpense;
    }

    public LiveData<Double> getTotalIncome() {
        return totalIncome;
    }

    public void insert(Transaction transaction) {
        executorService.execute(() -> transactionDao.insert(transaction));
    }
}
