package com.lazyledger.app.ui;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.lazyledger.app.db.entity.Transaction;
import com.lazyledger.app.repository.TransactionRepository;
import java.util.List;

public class MainViewModel extends AndroidViewModel {
    private TransactionRepository repository;
    private LiveData<List<Transaction>> allTransactions;
    private LiveData<Double> totalExpense;
    private LiveData<Double> totalIncome;
    private LiveData<List<com.lazyledger.app.db.entity.CategorySum>> expenseByCategory;

    public MainViewModel(Application application) {
        super(application);
        repository = new TransactionRepository(application);
        allTransactions = repository.getAllTransactions();
        totalExpense = repository.getTotalExpense();
        totalIncome = repository.getTotalIncome();
        expenseByCategory = repository.getExpenseByCategory();
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

    public LiveData<List<com.lazyledger.app.db.entity.CategorySum>> getExpenseByCategory() {
        return expenseByCategory;
    }

    public void insert(Transaction transaction) {
        repository.insert(transaction);
    }

    public void deleteAll() {
        repository.deleteAll();
    }
}
