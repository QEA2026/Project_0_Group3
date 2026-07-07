package com.group3.models;

public class Approval {

    private int id;
    private int expense_id_fk;
    private String status;
    // reviewer
    private int reviewer_id;
    private String comment;
    private String review_date;
    
    public Approval() {
    }
    
    public Approval(int id, int expense_id_fk, String status, int reviewer_id, String comment, String review_date) {
        this.id = id;
        this.expense_id_fk = expense_id_fk;
        this.status = status;
        this.reviewer_id = reviewer_id;
        this.comment = comment;
        this.review_date = review_date;
    }


    public Approval(String string) {
        //TODO Auto-generated constructor stub
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public int getExpense_id_fk() {
        return expense_id_fk;
    }
    public void setExpense_id_fk(int expense_id_fk) {
        this.expense_id_fk = expense_id_fk;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public int getReviewer_id() {
        return reviewer_id;
    }
    public void setReviewer_id(int reviewer_id) {
        this.reviewer_id = reviewer_id;
    }
    public String getComment() {
        return comment;
    }
    public void setComment(String comment) {
        this.comment = comment;
    }
    public String getReview_date() {
        return review_date;
    }
    public void setReview_date(String review_date) {
        this.review_date = review_date;
    }
    
    @Override
    public String toString() {
        return "Approval [id=" + id + ", expense_id_fk=" + expense_id_fk + ", status=" + status + ", reviewer_id="
                + reviewer_id + ", comment=" + comment + ", review_date=" + review_date + "]";
    }

}
