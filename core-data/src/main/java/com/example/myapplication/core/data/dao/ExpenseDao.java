package com.example.myapplication.core.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import com.example.myapplication.core.data.entity.ExpenseEntity;
import java.util.List;

@Dao
public interface ExpenseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(ExpenseEntity expense);

    @Update
    void update(ExpenseEntity expense);

    @Delete
    void delete(ExpenseEntity expense);

    @Query("SELECT * FROM expenses WHERE id = :id")
    ExpenseEntity getExpenseById(long id);

    @Query("SELECT * FROM expenses WHERE userId = :userId ORDER BY phase ASC, createdAt DESC")
    LiveData<List<ExpenseEntity>> getExpensesByUser(long userId);

    @Query("SELECT * FROM expenses WHERE userId = :userId AND phase = :phase ORDER BY createdAt DESC")
    LiveData<List<ExpenseEntity>> getExpensesByPhase(long userId, String phase);

    @Query("SELECT * FROM expenses WHERE userId = :userId AND activityId = :activityId ORDER BY createdAt DESC")
    LiveData<List<ExpenseEntity>> getExpensesByActivity(long userId, long activityId);

    @Query("SELECT * FROM expenses WHERE userId = :userId AND methodId = :methodId ORDER BY createdAt DESC")
    LiveData<List<ExpenseEntity>> getExpensesByMethod(long userId, long methodId);

    @Query("SELECT SUM(totalCost) FROM expenses WHERE userId = :userId")
    LiveData<Double> getTotalExpenses(long userId);

    @Query("SELECT SUM(implicitCost) FROM expenses WHERE userId = :userId")
    LiveData<Double> getTotalImplicitCosts(long userId);

    @Query("SELECT SUM(totalCost) FROM expenses WHERE userId = :userId AND phase = :phase")
    LiveData<Double> getTotalByPhase(long userId, String phase);

    @Query("SELECT SUM(totalCost) FROM expenses WHERE userId = :userId AND activityId = :activityId")
    LiveData<Double> getTotalByActivity(long userId, long activityId);

    @Query("SELECT SUM(totalCost) FROM expenses WHERE userId = :userId AND methodId = :methodId")
    LiveData<Double> getTotalByMethod(long userId, long methodId);

    @Query("SELECT * FROM expenses WHERE userId = :userId AND phase = :phase AND productName = :productName LIMIT 1")
    ExpenseEntity getExistingExpense(long userId, String phase, String productName);

    @Query("SELECT * FROM expenses WHERE batchId = :batchId")
    LiveData<List<ExpenseEntity>> getExpensesByBatch(long batchId);

    @Query("SELECT * FROM expenses WHERE batchId = :batchId")
    List<ExpenseEntity> getExpensesByBatchSync(long batchId);
}
