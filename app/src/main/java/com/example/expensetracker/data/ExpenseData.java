package com.example.expensetracker.data;

import com.example.expensetracker.model.Expense;

import java.util.ArrayList;
import java.util.List;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.example.expensetracker.model.Expense;

public final class ExpenseData {
    private static final List<Expense> EXPENSES = new ArrayList<>();

    static {
        // Dummy static data (id, amount, currency, date, category, remark)
        EXPENSES.add(new Expense(1, 5.00, "USD", "2025-10-20", "Coffee", "Latte"));
        EXPENSES.add(new Expense(2, 12.50, "USD", "2025-10-21", "Lunch", "Noodles"));
        EXPENSES.add(new Expense(3, 4.00, "KHR", "2025-10-22", "Transport", "Bus ticket"));
        EXPENSES.add(new Expense(4, 25.00, "USD", "2025-10-23", "Groceries", "Fruits & veg"));
        EXPENSES.add(new Expense(5, 7.75, "KHR", "2025-10-24", "Snacks", "Cookies"));
        EXPENSES.add(new Expense(6, 10.00, "USD", "2025-10-25", "Phone Top-up", "Data pack"));
        EXPENSES.add(new Expense(7, 3.00, "USD", "2025-10-26", "Coffee", "Espresso"));
        EXPENSES.add(new Expense(8, 9.90, "USD", "2025-10-27", "Transport", "Tuk-tuk"));
        EXPENSES.add(new Expense(9, 18.25, "USD", "2025-10-28", "Dinner", "BBQ"));
        EXPENSES.add(new Expense(10, 2.50, "USD", "2025-10-29", "Snacks", "Ice cream"));
    }

    private ExpenseData() {}

    public static List<Expense> getExpenses() {
        return Collections.unmodifiableList(EXPENSES);
    }
    public static void addExpense(Expense expense) {
        if (expense != null) {
            EXPENSES.add(expense);
        }
    }
    public static Expense findById(int id) {
        for (Expense e : EXPENSES) {
            if (e.getId() == id) return e;
        }
        return null;
    }
}
