package com.group3.DAOs;

import java.util.ArrayList;

import com.group3.models.Approval;
import com.group3.models.User;

public interface ApprovalDAOInterface {

    // get expense by id
    ArrayList<ArrayList<Object>> getApprovalByExpenseId(int id);

    // returns list of a list containing approval + expense
    ArrayList<ArrayList<Object>> getExpenseByIdWithStatus(int user_id);
    
    // update expense
    boolean update_status(int id);
} 