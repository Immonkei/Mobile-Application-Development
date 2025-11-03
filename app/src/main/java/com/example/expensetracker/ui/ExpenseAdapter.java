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

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.VH> {

    private final List<Expense> list;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Expense e);
    }

    public ExpenseAdapter(List<Expense> list, OnItemClickListener listener) {
        this.list = list;
        this.listener = listener;
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VH(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_expense, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Expense e = list.get(position);

        // Use getters (Expense fields are private)
        String category = e.getCategory() != null ? e.getCategory() : "—";
        String currency = e.getCurrency() != null ? e.getCurrency() : "";
        double amountVal = e.getAmount();
        String remark = e.getRemark() != null ? e.getRemark() : "";

        holder.tvCategory.setText(category);
        holder.tvAmount.setText(String.format("%s %.2f", currency, amountVal));
        holder.tvRemark.setText(remark);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(e);
        });
    }

    @Override public int getItemCount() { return list == null ? 0 : list.size(); }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvCategory, tvAmount, tvRemark;
        VH(@NonNull View itemView) {
            super(itemView);
            tvCategory = itemView.findViewById(R.id.tv_category);
            tvAmount = itemView.findViewById(R.id.tv_amount);
            tvRemark = itemView.findViewById(R.id.tv_remark);
        }
    }
}
