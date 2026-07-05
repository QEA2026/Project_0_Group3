package com.group3.models;

public class Expense {

    private int id;
    private float amount;
    private String description;
    private String expense_date;
    private int user_id_fk;

    public Expense() {
    }

    public Expense(int id, float amount, String description, String expense_date, int user_id_fk) {
        this.id = id;
        this.amount = amount;
        this.description = description;
        this.expense_date = expense_date;
        this.user_id_fk = user_id_fk;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public float getAmount() {
        return amount;
    }
    public void setAmount(float amount) {
        this.amount = amount;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getExpense_date() {
        return expense_date;
    }
    public void setExpense_date(String expense_date) {
        this.expense_date = expense_date;
    }
    public int getUser_id_fk() {
        return user_id_fk;
    }
    public void setUser_id_fk(int user_id_fk) {
        this.user_id_fk = user_id_fk;
    }

    @Override
    public String toString() {
        return "Expense [id=" + id + 
        ", amount=" + amount + 
        ", description=" + description + 
        ", expense_date="+ expense_date + 
        ", user_id_fk=" + user_id_fk + "]";
    }

}
