package com.group3.service;

import java.util.ArrayList;
import java.util.List;

import com.group3.DAOs.ApprovalDAOInterface;
import com.group3.DAOs.ExpenseDAOInterface;
import com.group3.models.Approval;
import com.group3.models.CategoryTotal;
import com.group3.models.EmployeeSummary;
import com.group3.models.ExpenseWithApproval;

public class ExpenseApprovalsService {

    public static final List<String> CATEGORIES = List.of(
            "Travel", "Lodging", "Meals", "Office Supplies", "Software", "Training", "Other");

    private final ApprovalDAOInterface approvalDAO;
    private final ExpenseDAOInterface expenseDAO;

    public ExpenseApprovalsService(ApprovalDAOInterface approvalDAO, ExpenseDAOInterface expenseDAO){
        this.approvalDAO = approvalDAO;
        this.expenseDAO = expenseDAO;
    }

    public List<ExpenseWithApproval> getPendingExpenses(){
        return approvalDAO.getExpenseApprovalsByStatus("pending");
    }

    // everything that has already been decided, so a manager can revisit the comment
    public List<ExpenseWithApproval> getReviewedExpenses(){
        List<ExpenseWithApproval> reviewed = new ArrayList<>(approvalDAO.getExpenseApprovalsByStatus("approved"));
        reviewed.addAll(approvalDAO.getExpenseApprovalsByStatus("denied"));
        return reviewed;
    }

    public List<CategoryTotal> getSpendingByCategory(){
        return expenseDAO.getSpendingByCategory();
    }

    public List<EmployeeSummary> getSpendingByEmployee(){
        return approvalDAO.getSpendingByEmployee();
    }

    public List<ExpenseWithApproval> getExpensesBetweenDates(String startDate, String endDate){
        java.time.LocalDate start;
        java.time.LocalDate end;
        try {
            start = java.time.LocalDate.parse(startDate);
            end = java.time.LocalDate.parse(endDate);
        } catch (java.time.format.DateTimeParseException e){
            throw new IllegalArgumentException("Dates must be in YYYY-MM-DD format, e.g. 2026-07-06");
        }
        if (end.isBefore(start)){
            throw new IllegalArgumentException("End date must be on or after start date");
        }
        return approvalDAO.getExpenseApprovalsBetweenDates(start.toString(), end.toString());
    }

    public boolean approveOrDenyExpense(int expenseId, int managerId, String decision, String comment, String category){

        if(!decision.equalsIgnoreCase("approved") && !decision.equalsIgnoreCase("denied")){
            throw new IllegalArgumentException("Decision must be 'approved' or 'denied'");
        }
        if (comment == null || comment.isBlank()){
            throw new IllegalArgumentException("A comment is required so the employee understands the decision");
        }
        // validate before writing anything, so a bad category can't leave a half-done review
        String normalizedCategory = (category == null || category.isBlank())
                ? null
                : normalizeCategory(category);

        ExpenseWithApproval ea = approvalDAO.getExpenseApprovalByExpenseId(expenseId);
        if (ea == null || ea.approval() == null){
            throw new IllegalArgumentException("No expense found with id " + expenseId);
        }
        if (!"pending".equalsIgnoreCase(ea.approval().getStatus())){
            throw new IllegalArgumentException("Expense has already been reviewed");
        }

        if (normalizedCategory != null){
            expenseDAO.updateCategory(expenseId, normalizedCategory);
        }

        Approval a = ea.approval();
        a.setStatus(decision.toLowerCase());
        a.setReviewer_id(managerId);
        a.setComment(comment.trim());
        a.setReview_date(java.time.LocalDate.now().toString());
        return approvalDAO.updateApprovalById(a);
    }

    // matches ignoring case and returns the canonical spelling, so the DB stays consistent
    private String normalizeCategory(String input){
        for (String c : CATEGORIES){
            if (c.equalsIgnoreCase(input.trim())){
                return c;
            }
        }
        throw new IllegalArgumentException(
                "Unknown category '" + input.trim() + "'. Choose from: " + String.join(", ", CATEGORIES));
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
