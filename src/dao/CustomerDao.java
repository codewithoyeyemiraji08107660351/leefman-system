/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import dbUtils.DbOperation;
import java.util.ArrayList;
import java.util.List;
import models.Customer;
import java.sql.*;

/**
 *
 * @author oyeyemi
 */
public class CustomerDao {

    public static void saveCustomer(Customer customer) {
        String saveQuery = "INSERT INTO customers(name,phone) VALUES('" + customer.getName() + "', '" + customer.getPhoneNo() + "')";
        DbOperation.saveUpdateDelete(saveQuery, "Customer saved successfully!");

    }

    public static List<Customer> allCustomers() {
        List<Customer> customerList = new ArrayList<>();
        ResultSet res = null;
        try {
            String allCustomers = "SELECT * FROM customers";
            res = DbOperation.getData(allCustomers);
            while (res.next()) {
                Customer custObj = new Customer();
                custObj.setCustomerId(res.getInt("customer_id"));
                custObj.setName(res.getString("name"));
                custObj.setPhoneNo(res.getString("phone"));

                customerList.add(custObj);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return customerList;
    }

    public static List<Customer> findCustomerByName(String name) {
        List<Customer> customerList = new ArrayList<>();
        ResultSet res = null;

        try {
            String customer = "SELECT * FROM customers WHERE name LIKE '%" + name + "%'";
            res = DbOperation.getData(customer);

            while (res.next()) {
                Customer custObj = new Customer();
                custObj.setCustomerId(res.getInt("customer_id"));
                custObj.setName(res.getString("name"));
                custObj.setPhoneNo(res.getString("phone"));

                customerList.add(custObj);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return customerList;
    }

    public static List<Customer> findCustomerByNameID() {
        List<Customer> customerList = new ArrayList<>();
        ResultSet res = null;

        try {
            String customer = "SELECT customer_id, name FROM customers";
            res = DbOperation.getData(customer);

            while (res.next()) {
                Customer custObj = new Customer();
                custObj.setCustomerId(res.getInt("customer_id"));
                custObj.setName(res.getString("name"));
                customerList.add(custObj);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return customerList;
    }

}
