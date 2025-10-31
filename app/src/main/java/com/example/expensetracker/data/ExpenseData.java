package com.example.expensetracker.data;

import com.example.expensetracker.model.Expense;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

        // More dummy expenses for scroll testing
        EXPENSES.add(new Expense(11, 14.00, "USD", "2025-10-30", "Lunch", "Fried rice"));
        EXPENSES.add(new Expense(12, 6.00, "KHR", "2025-10-31", "Coffee", "Cold brew"));
        EXPENSES.add(new Expense(13, 8.50, "USD", "2025-11-01", "Transport", "GrabBike"));
        EXPENSES.add(new Expense(14, 32.75, "USD", "2025-11-02", "Groceries", "Weekly market"));
        EXPENSES.add(new Expense(15, 4.25, "KHR", "2025-11-03", "Snacks", "Potato chips"));
        EXPENSES.add(new Expense(16, 10.00, "USD", "2025-11-04", "Phone Top-up", "Smart 5GB"));
        EXPENSES.add(new Expense(17, 20.00, "USD", "2025-11-05", "Dinner", "Pizza night"));
        EXPENSES.add(new Expense(18, 2.00, "KHR", "2025-11-06", "Coffee", "Iced Americano"));
        EXPENSES.add(new Expense(19, 15.00, "USD", "2025-11-07", "Lunch", "Khmer food"));
        EXPENSES.add(new Expense(20, 50.00, "USD", "2025-11-08", "Groceries", "Monthly shopping"));
        EXPENSES.add(new Expense(21, 3.75, "KHR", "2025-11-09", "Snacks", "Bread and jam"));
        EXPENSES.add(new Expense(22, 5.25, "USD", "2025-11-10", "Coffee", "Cappuccino"));
        EXPENSES.add(new Expense(23, 11.00, "USD", "2025-11-11", "Transport", "Bus pass"));
        EXPENSES.add(new Expense(24, 9.50, "USD", "2025-11-12", "Dinner", "Noodles soup"));
        EXPENSES.add(new Expense(25, 17.80, "USD", "2025-11-13", "Groceries", "Snacks & Drinks"));
        EXPENSES.add(new Expense(26, 8.40, "KHR", "2025-11-14", "Lunch", "Beef Lok Lak"));
        EXPENSES.add(new Expense(27, 3.20, "KHR", "2025-11-15", "Coffee", "Black Coffee"));
        EXPENSES.add(new Expense(28, 27.00, "USD", "2025-11-16", "Dinner", "Korean BBQ"));
        EXPENSES.add(new Expense(29, 19.99, "USD", "2025-11-17", "Transport", "Taxi"));
        EXPENSES.add(new Expense(30, 45.00, "USD", "2025-11-18", "Groceries", "Family shopping"));
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
