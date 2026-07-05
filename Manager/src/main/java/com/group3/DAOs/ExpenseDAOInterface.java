package com.group3.DAOs;

import java.util.ArrayList;

import com.group3.models.Expense;

public interface ExpenseDAOInterface {

    // get by id
    Expense getExpenseById(int id);

    // get all expenses by userID
    ArrayList<Expense> getExpenseByUserId(int id);

    
}
