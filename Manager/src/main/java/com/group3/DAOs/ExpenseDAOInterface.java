package com.group3.DAOs;

import java.util.List;

import com.group3.models.CategoryTotal;
import com.group3.models.Expense;

public interface ExpenseDAOInterface {

    // get by id
    Expense getExpenseByExpenseId(int id);

    // set the category on an expense (manager assigns it during review)
    boolean updateCategory(int expenseId, String category);

    // spending report grouped by category
    List<CategoryTotal> getSpendingByCategory();
}
