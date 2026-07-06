package com.group3.models;

// one row of the spending-by-employee report
public record EmployeeSummary(String username, int totalExpenses, double totalAmount,
        int approved, int denied, int pending) {
}
