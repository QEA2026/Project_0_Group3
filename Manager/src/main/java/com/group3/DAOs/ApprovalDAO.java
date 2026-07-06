package com.group3.DAOs;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;



import com.group3.models.Approval;
import com.group3.models.Expense;
import com.group3.models.ExpenseWithApproval;
import com.group3.utils.ConnectionUtil;

public class ApprovalDAO implements ApprovalDAOInterface{

    @Override
    public ExpenseWithApproval getExpenseApprovalByExpenseId(int expense_id) {
        try(Connection conn = ConnectionUtil.getConnection()){
            
            String sql = """
                    SELECT e.id as expense_id, * FROM approvals a
                    INNER JOIN expenses e ON a.expense_id_fk = e.id
                    WHERE e.id = ?
                    ORDER BY e.id;
                    """;

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, expense_id);
            ResultSet rs = ps.executeQuery();
            
        
            if (rs.next()){
                ExpenseWithApproval ea = new ExpenseWithApproval(
                    new Expense(rs.getInt("expense_id"), rs.getFloat("amount"), rs.getString("description"), rs.getString("expense_date"), rs.getInt("user_id_fk")),
                    new Approval(rs.getInt("id"), rs.getInt("expense_id_fk"), rs.getString("status"), rs.getInt("reviewer"), rs.getString("comment"), rs.getString("review_date"))
                );
                return ea;
            }

        } catch (SQLException e){
            e.printStackTrace();
        }

        return new ExpenseWithApproval();
    }

    @Override
    public List<ExpenseWithApproval> getAllExpenseApprovals() {
        
        try(Connection conn = ConnectionUtil.getConnection()){

            String sql = """
                    SELECT a.status, a.id as approval_id, e.id as expense_id,* FROM approvals a
                    INNER JOIN expenses e ON a.expense_id_fk = e.id;
                    """;
            
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            List<ExpenseWithApproval> ea = new ArrayList<>();

            while(rs.next()){
                ea.add(new ExpenseWithApproval(
                    new Expense(rs.getInt("expense_id"),
                        rs.getFloat("amount"),
                        rs.getString("description"),
                        rs.getString("expense_date"),
                        rs.getInt("user_id_fk")),

                    new Approval(rs.getInt("approval_id"),
                        rs.getInt("expense_id_fk"),
                        rs.getString("status"),
                        rs.getInt("reviewer"), 
                        rs.getString("comment"),
                        rs.getString("review_date")))); 
            }

            return ea;
 
        } catch (SQLException e){
            e.printStackTrace();
        }
        return List.of(new ExpenseWithApproval());
    }

    @Override
    public List<ExpenseWithApproval> getExpenseApprovalByUserId(int user_id) {
        try(Connection conn = ConnectionUtil.getConnection()){
            
            String sql = 
                    """
                        SELECT a.status, a.id as approval_id, e.id as expense_id,* FROM approvals a
                        INNER JOIN expenses e ON a.expense_id_fk = e.id
                        WHERE e.user_id_fk = ?;
                    """;

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, user_id);
            ResultSet rs = ps.executeQuery();
            
            List<ExpenseWithApproval> ea = new ArrayList<>();
            while (rs.next()){
                    ea.add(new ExpenseWithApproval(
                        new Expense(rs.getInt("expense_id"),
                        rs.getFloat("amount"),
                        rs.getString("description"),
                        rs.getString("expense_date"),
                        rs.getInt("user_id_fk")),

                        new Approval(rs.getInt("id"),
                        rs.getInt("expense_id_fk"),
                        rs.getString("status"),
                        rs.getInt("reviewer"),
                        rs.getString("comment"),
                        rs.getString("review_date")))  
                    );
            }
            return ea;

            
        } catch (SQLException e){
            e.printStackTrace();
        }

        return List.of(new ExpenseWithApproval());
    }

    @Override
    public List<ExpenseWithApproval> getExpenseApprovalsByStatus(String status) {
        List<ExpenseWithApproval> ea = new ArrayList<>();

        try(Connection conn = ConnectionUtil.getConnection()){

            String sql = """
                    SELECT a.status, a.id as approval_id, e.id as expense_id,* FROM approvals a
                    INNER JOIN expenses e ON a.expense_id_fk = e.id
                    WHERE LOWER(a.status) = LOWER(?)
                    ORDER BY e.id;
                    """;

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, status);
            ResultSet rs = ps.executeQuery();

            while(rs.next()){
                ea.add(new ExpenseWithApproval(
                    new Expense(rs.getInt("expense_id"),
                        rs.getFloat("amount"),
                        rs.getString("description"),
                        rs.getString("expense_date"),
                        rs.getInt("user_id_fk")),

                    new Approval(rs.getInt("approval_id"),
                        rs.getInt("expense_id_fk"),
                        rs.getString("status"),
                        rs.getInt("reviewer"),
                        rs.getString("comment"),
                        rs.getString("review_date"))));
            }

        } catch (SQLException e){
            e.printStackTrace();
        }

        return ea;
    }

    @Override
    public boolean updateApprovalById(Approval approval) {
        
        try(Connection conn = ConnectionUtil.getConnection()){

            String sql = """
                    UPDATE approvals
                    SET status = ?, reviewer = ?, comment = ?, review_date = ? 
                    WHERE approvals.id = ?;
                    """;
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, approval.getStatus());
            ps.setInt(2, approval.getReviewer_id());
            ps.setString(3, approval.getComment());
            ps.setString(4, approval.getReview_date());
            ps.setInt(5, approval.getId());
            
            int rowsUpdated = ps.executeUpdate();
            return rowsUpdated > 0;

        } catch (SQLException e){
            e.printStackTrace();
        }

        return false;
    }

    
    
}
