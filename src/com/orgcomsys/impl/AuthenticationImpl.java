/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.orgcomsys.impl;

import com.orgcomsys.inter.Authentication;
import com.orgcomsys.model.Company;
import com.orgcomsys.model.Response;
import com.orgcomsys.model.User;
import com.orgcomsys.utility.DatabaseConnection;
import java.rmi.RemoteException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Mallika
 */
public class AuthenticationImpl implements Authentication {
    @Override
    public Response signUp(User user, Company company) throws RemoteException {
        System.out.println(
            user.getFullName() + " | " + 
            user.getMobileNo() + " | " +
            user.getAddress() + " | " + 
            user.getUsername() + " | " +
            user.getEmailAddress() + " | " +
            user.getPassword() + " | " +
            company.getCompanyName() + " | " +
            company.getCompanyAddress() + " | " +
            company.getPhoneNo() + " | " +
            company.getPanNo() + " | " +
            company.getMailingAddress()
        );
        
        String status = insertIntoCompanyAndUser(company, user);
        Response response = new Response(200, status);
        return response;
    };
    
    private String insertIntoCompanyAndUser(Company company, User user) {
        String status = null;
        long companyId = 0;
        PreparedStatement statement = null;
        String sql = "insert into company(company_id, company_name, company_address, phone_no, pan_no, mailing_address) values (null, ?, ?, ?, ?, ?)";
        try {
            statement = DatabaseConnection.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, company.getCompanyName());
            statement.setString(2, company.getCompanyAddress());
            statement.setLong(3, company.getPhoneNo());
            statement.setLong(4, company.getPanNo());
            statement.setString(5, company.getMailingAddress());
            
            statement.executeUpdate();
            
            try (ResultSet resultSet = statement.getGeneratedKeys()) {
                if(resultSet.next()) {
                    System.out.println(resultSet.getLong(1));
                    companyId = resultSet.getLong(1);
                }
                resultSet.close();
            }
            if(companyId != 0) {
                long userId = setUser(user, companyId);
                if(userId != 0) {
                    status = "Ok";
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(AuthenticationImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return status;
    };
    
    private long setUser(User user, long companyId) {
        long userId = 0;
        PreparedStatement statement = null;
        String sql = "insert into user(user_id, full_name, mobile_no, address, username, email_address, password, company_id) values (null, ?, ?, ?, ?, ?, ?, ?)";
        try {
            statement = DatabaseConnection.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, user.getFullName());
            statement.setLong(2, user.getMobileNo());
            statement.setString(3, user.getAddress());
            statement.setString(4, user.getUsername());
            statement.setString(5, user.getEmailAddress());
            statement.setString(6, user.getPassword());
            statement.setInt(7, (int) companyId);
            
            statement.executeUpdate();
            
            try (ResultSet resultSet = statement.getGeneratedKeys()) {
                if(resultSet.next()) {
                    System.out.println(resultSet.getLong(1));
                    userId = resultSet.getLong(1); 
                }
                resultSet.close();
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(AuthenticationImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return userId;
    };

    @Override
    public Response signIn(String username, String password) throws RemoteException {
        int code = 0;
        String status = null;
        PreparedStatement statement = null;
        String sql = "select password from User where username = ?";
        try {
            statement = DatabaseConnection.getConnection().prepareStatement(sql);    
        
            statement.setString(1, username);
            
            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()){
                System.out.println(resultSet.getString(1));
                String passwordFromDb = resultSet.getString(1);
                if(password.equals(passwordFromDb)) {
                    code = 200;
                    status = "Ok";
                } else {
                    code = 404;
                    status = "Password didn't match";
                }
            }
            resultSet.close();
        } catch (SQLException ex) {
            Logger.getLogger(AuthenticationImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new Response(code, status);
    };
};
