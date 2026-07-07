package com.group3.DAOs;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.group3.models.CategoryTotal;
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
                    rs.getInt("user_id_fk"),
                    rs.getString("category"));


        } catch (SQLException e){
            e.printStackTrace();
        }

        return new Expense();
    }

    @Override
    public boolean updateCategory(int expenseId, String category) {

        try(Connection conn = ConnectionUtil.getConnection()){

            String sql = "UPDATE expenses SET category = ? WHERE id = ?;";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, category);
            ps.setInt(2, expenseId);

            return ps.executeUpdate() > 0;

        } catch (SQLException e){
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public List<CategoryTotal> getSpendingByCategory() {
        List<CategoryTotal> report = new ArrayList<>();

        try(Connection conn = ConnectionUtil.getConnection()){

            String sql = """
                    SELECT category, COUNT(*) AS expense_count, SUM(amount) AS total_spent
                    FROM expenses
                    GROUP BY category
                    ORDER BY total_spent DESC;
                    """;

            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while(rs.next()){
                report.add(new CategoryTotal(
                        rs.getString("category"),
                        rs.getInt("expense_count"),
                        rs.getDouble("total_spent")));
            }

        } catch (SQLException e){
            e.printStackTrace();
        }

        return report;
    }
}
