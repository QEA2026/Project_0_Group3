package com.group3.service;

import java.util.List;

import com.group3.DAOs.ApprovalDAOInterface;
import com.group3.DAOs.ExpenseDAOInterface;
import com.group3.models.Approval;
import com.group3.models.ExpenseWithApproval;

public class ExpenseApprovalsService {

    private final ApprovalDAOInterface approvalDAO;
    private final ExpenseDAOInterface expenseDAO;

    public ExpenseApprovalsService(ApprovalDAOInterface approvalDAO, ExpenseDAOInterface expenseDAO){
        this.approvalDAO = approvalDAO;
        this.expenseDAO = expenseDAO;
    }

    public List<ExpenseWithApproval> getPendingExpenses(){
        return approvalDAO.getExpenseApprovalsByStatus("pending");
    }

    public boolean approveOrDenyExpense(int expenseId, int managerId, String decision, String comment){

        if(!decision.equalsIgnoreCase("approved") && !decision.equalsIgnoreCase("denied")){
            throw new IllegalArgumentException("Decision must be 'approved' or 'denied'");
        }
        if (comment == null || comment.isBlank()){
            throw new IllegalArgumentException("A comment is required so the employee understands the decision");
        }

        ExpenseWithApproval ea = approvalDAO.getExpenseApprovalByExpenseId(expenseId);
        if (ea == null){
            throw new IllegalArgumentException("No expense found with id " + expenseId);
        }
        if (!"pending".equalsIgnoreCase(ea.approval().getStatus())){
            throw new IllegalArgumentException("Expense has already been reviewed");
        }

        Approval a = ea.approval();
        a.setStatus(decision.toLowerCase());
        a.setReviewer_id(managerId);
        a.setComment(comment.trim());
        a.setReview_date(java.time.LocalDateTime.now().toString());
        return approvalDAO.updateApprovalById(a);
    }

    // add or update the comment on an expense that has already been decided
    public boolean addCommentToExpenseDecision(int expenseId, int managerId, String comment){

        if (comment == null || comment.isBlank()){
            throw new IllegalArgumentException("Comment cannot be empty");
        }

        ExpenseWithApproval ea = approvalDAO.getExpenseApprovalByExpenseId(expenseId);
        if (ea == null || ea.approval() == null){
            throw new IllegalArgumentException("No expense found with id " + expenseId);
        }
        if ("pending".equalsIgnoreCase(ea.approval().getStatus())){
            throw new IllegalArgumentException("Expense " + expenseId + " is still pending; approve or deny it to leave a comment");
        }

        Approval a = ea.approval();
        a.setComment(comment.trim());
        a.setReviewer_id(managerId);
        return approvalDAO.updateApprovalById(a);
    }

}
