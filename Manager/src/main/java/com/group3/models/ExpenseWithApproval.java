package com.group3.models;

// creates an immutable class holding expense and approval
public record ExpenseWithApproval(Expense expense, Approval approval) {

    public ExpenseWithApproval(){
        this(null, null);
    }
    
};
