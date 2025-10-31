// PASTE THIS ENTIRE CODE BLOCK INTO ExpenseAdapter.java

package com.example.expensetracker.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.expensetracker.R;
import com.example.expensetracker.model.Expense;
import java.util.List;
import java.util.Locale;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder> {

    private final List<Expense> expenseList;
    private final OnItemClickListener clickListener;

    // The interface that defines the click action
    public interface OnItemClickListener {
        void onItemClick(Expense expense);
    }

    // THIS IS THE CORRECT CONSTRUCTOR that your ExpenseListFragment needs.
    // It requires two arguments.
    public ExpenseAdapter(List<Expense> expenseList, OnItemClickListener clickListener) {
        this.expenseList = expenseList;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_expense, parent, false);
        return new ExpenseViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpenseViewHolder holder, int position) {
        Expense currentExpense = expenseList.get(position);

        // Bind the data to the views
        holder.tvCategory.setText(currentExpense.getCategory());
        holder.tvRemark.setText(currentExpense.getRemark());
        holder.tvDate.setText(currentExpense.getDate());
        String amountText = String.format(Locale.getDefault(), "%s %.2f", currentExpense.getCurrency(), currentExpense.getAmount());
        holder.tvAmountCurrency.setText(amountText);

        // Apply the click listener to the item view
        holder.bind(currentExpense, clickListener);
    }

    @Override
    public int getItemCount() {
        return expenseList.size();
    }

    public static class ExpenseViewHolder extends RecyclerView.ViewHolder {
        public final TextView tvCategory;
        public final TextView tvRemark;
        public final TextView tvAmountCurrency;
        public final TextView tvDate;

        public ExpenseViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCategory = itemView.findViewById(R.id.tv_item_category);
            tvRemark = itemView.findViewById(R.id.tv_item_remark);
            tvAmountCurrency = itemView.findViewById(R.id.tv_item_amount); // Use tv_item_amount
            tvDate = itemView.findViewById(R.id.tv_item_date);
        }

        // Helper method to set the click listener on the item
        public void bind(final Expense expense, final OnItemClickListener listener) {
            itemView.setOnClickListener(v -> listener.onItemClick(expense));
        }
    }
}
