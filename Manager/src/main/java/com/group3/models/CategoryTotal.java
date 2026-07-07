package com.group3.models;

// one row of the spending-by-category report
public record CategoryTotal(String category, int expenseCount, double totalAmount) {
}
