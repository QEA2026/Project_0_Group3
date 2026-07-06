package com.group3.DAOs;

import java.util.ArrayList;

import com.group3.models.Expense;

public interface ExpenseDAOInterface {

    // get by id
    Expense getExpenseByExpenseId(int id);
}
