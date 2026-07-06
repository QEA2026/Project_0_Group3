package com.group3.DAOs;

import java.util.List;

import com.group3.models.Approval;
import com.group3.models.ExpenseWithApproval;

public interface ApprovalDAOInterface {

    // returns list of a list containing approval + expense
    List<ExpenseWithApproval> getAllExpenseApprovals();

    // get an expenses by user_id
    List<ExpenseWithApproval> getExpenseApprovalByUserId(int user_id);

    // get expenses filtered by approval status (pending, approved, denied)
    List<ExpenseWithApproval> getExpenseApprovalsByStatus(String status);

    // get expense by id returns record(expense, approval)
    ExpenseWithApproval getExpenseApprovalByExpenseId(int expense_id);

    
     
    // update expenses
    boolean updateApprovalById(Approval a);  
} 