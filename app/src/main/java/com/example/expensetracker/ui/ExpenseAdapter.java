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

    public interface OnItemClickListener {
        void onItemClick(Expense expense);
    }

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
        Expense current = expenseList.get(position);
        holder.tvCategory.setText(current.getCategory());
        holder.tvRemark.setText(current.getRemark());
        holder.tvDate.setText(current.getDate());
        holder.tvAmount.setText(String.format(Locale.getDefault(), "%s %.2f", current.getCurrency(), current.getAmount()));
        holder.bind(current, clickListener);
    }

    @Override
    public int getItemCount() {
        return expenseList.size();
    }

    // Return stable id derived from expense id
    @Override
    public long getItemId(int position) {
        Expense e = expenseList.get(position);
        return e == null ? super.getItemId(position) : e.getId();
    }

    static class ExpenseViewHolder extends RecyclerView.ViewHolder {
        final TextView tvCategory, tvRemark, tvAmount, tvDate;
        ExpenseViewHolder(@NonNull View v) {
            super(v);
            tvCategory = v.findViewById(R.id.tv_item_category);
            tvRemark = v.findViewById(R.id.tv_item_remark);
            tvAmount = v.findViewById(R.id.tv_item_amount);
            tvDate = v.findViewById(R.id.tv_item_date);
        }
        void bind(final Expense expense, final OnItemClickListener listener) {
            itemView.setOnClickListener(v -> listener.onItemClick(expense));
        }
    }
}
