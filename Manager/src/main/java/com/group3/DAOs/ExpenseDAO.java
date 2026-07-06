package com.group3.DAOs;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.group3.models.Expense;
import com.group3.utils.ConnectionUtil;

public class ExpenseDAO implements ExpenseDAOInterface{

    @Override
    public Expense getExpenseByExpenseId(int id) {
        
        try(Connection conn = ConnectionUtil.getConnection()){

            String sql = "SELECT * FROM expenses WHERE id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            
            return new Expense(
                    rs.getInt("id"),
                    rs.getFloat("amount"),
                    rs.getString("description"),
                    rs.getString("expense_date"),
                    rs.getInt("user_id_fk"));
            

        } catch (SQLException e){
            e.printStackTrace();
        }

        return new Expense();
    }
}
