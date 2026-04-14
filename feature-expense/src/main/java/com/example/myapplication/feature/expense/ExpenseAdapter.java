package com.example.myapplication.feature.expense;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.core.data.entity.ExpenseEntity;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.Holder> {
    public interface OnExpenseClickListener { void onExpenseClick(ExpenseEntity expense); }
    private final List<ExpenseEntity> expenses = new ArrayList<>();
    private OnExpenseClickListener listener;

    public ExpenseAdapter(List<ExpenseEntity> initialData) {
        if (initialData != null) expenses.addAll(initialData);
    }

    public void setOnExpenseClickListener(OnExpenseClickListener listener) { this.listener = listener; }
    public void setExpenses(List<ExpenseEntity> data) {
        expenses.clear();
        if (data != null) expenses.addAll(data);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.expense_item, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        ExpenseEntity e = expenses.get(position);
        holder.title.setText(String.format("%s - %s", e.getCategory(), e.getProductName()));
        holder.details.setText(String.format(Locale.getDefault(), "%.2f %s x %.2f", e.getQuantity(), e.getUnit(), e.getUnitPrice()));
        holder.total.setText(String.format(Locale.getDefault(), "₱%.2f", e.getTotalCost()));
        holder.itemView.setOnClickListener(v -> { if (listener != null) listener.onExpenseClick(e); });
    }

    @Override
    public int getItemCount() { return expenses.size(); }

    static class Holder extends RecyclerView.ViewHolder {
        final TextView title, details, total;
        Holder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tv_expense_category_product);
            details = itemView.findViewById(R.id.tv_expense_details);
            total = itemView.findViewById(R.id.tv_expense_total);
        }
    }
}
