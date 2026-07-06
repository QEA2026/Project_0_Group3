package com.group3.DAOs;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.group3.models.User;
import com.group3.utils.ConnectionUtil;

public class UserDAO implements UserDAOInterface{

    @Override
    public User createUser(User user) {
        
        try(Connection conn = ConnectionUtil.getConnection()){

            // expects encrpyted password
            String sql = "INSERT INTO users (username, password, role) VALUES (?,?,?);";
            PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getRole());
            ps.executeUpdate();

            // gets key of created user
            try(ResultSet lastRowId = ps.getGeneratedKeys()){
                
                if (lastRowId.next()){
                    user.setId(lastRowId.getInt(1));
                }
            }
            

        } catch(SQLException e){
            e.printStackTrace();
        }
        return user;
    }


    @Override
    public Optional<List<User>> getUsers() {
        List<User> userList = new ArrayList<>();
        try(Connection conn = ConnectionUtil.getConnection()){

            String sql = "SELECT * FROM users;";

            // create prepared statement object 
            Statement s = conn.createStatement();
            ResultSet rs = s.executeQuery(sql);

            while(rs.next()){
                User u = new User(
                    rs.getInt("id"),
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getString("role")
                );
                userList.add(u);
            }
            return Optional.of(userList);

        } catch (SQLException e){
            e.printStackTrace();
        }
        return Optional.empty();
    }


    /** *
     * 
     * Gets user by username
     * 
     * @param username to search
     * @return the user
     * @throws SQLException if search fails
     *  
    */
    @Override
    public User getUser(String username) {

        try(Connection conn = ConnectionUtil.getConnection()){

            String sql = "SELECT * FROM users WHERE username = ?;";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            ResultSet result = ps.executeQuery();
            
            if (result.next()){
                return new User(
                    result.getInt("id"),
                    result.getString("username"),
                    result.getString("password"),
                    result.getString("role")
                );
            }
           

            return null;

        } catch (SQLException e){
            e.printStackTrace();
        }

        throw new IllegalArgumentException("Username not found");
    }

    @Override
    public User getUserById(int id) {
        try(Connection conn = ConnectionUtil.getConnection()){

            String sql = "SELECT * FROM users WHERE id = ?;";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            
            User u = new User(
                rs.getInt(1),
                rs.getString("username"),
                rs.getString("password"), 
                rs.getString("role")
            );
            return u;

        } catch (SQLException e){
            e.printStackTrace();
        }

        throw new IllegalArgumentException("User id not found");
    }
}
