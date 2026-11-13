/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import dbConnection.ConnectionProvider;
import dbUtils.DbOperation;
import java.util.ArrayList;
import java.util.List;
import models.Loss;
import java.sql.*;

/**
 *
 * @author oyeyemi
 */
public class LossDao {
    
    public static void saveExtras(Loss ex){
        String sql = "INSERT INTO extras(title, description, purpose, category, cost, profit_loss, responsible_person, event_date, notes, image_path)"
                + "VALUES('"+ex.getTitle()+"', '"+ex.getDescription()+"', '"+ex.getPurpose()+"','"+ex.getCategory()+"', '"+ex.getCost()+"', "
                + "'"+ex.getProfitLost()+"', '"+ex.getResponsiblePerson()+"', CURDATE(), '"+ex.getNotes()+"', '"+ex.getImagePath()+"')";
        DbOperation.saveUpdateDelete(sql, "Saved Successfully!!");
    }
    
    public static List<Loss> getAllExtras(){
        List<Loss> extraList = new ArrayList<>();
        
        try{
            String sql = "SELECT * FROM extras ORDER BY extra_id DESC";
            ResultSet res = DbOperation.getData(sql);
            while(res.next()){
                Loss exObj = new Loss();
                exObj.setExtra_id(res.getInt("extra_id"));
                exObj.setTitle(res.getString("title"));
                exObj.setDescription(res.getString("description"));
                exObj.setCategory(res.getString("category"));
                exObj.setPurpose(res.getString("purpose"));
                exObj.setCost(res.getDouble("cost"));
                exObj.setProfitLost(res.getDouble("profit_loss"));
                exObj.setResponsiblePerson(res.getString("responsible_person"));
                exObj.setNotes(res.getString("notes"));
                exObj.setImagePath(res.getString("image_path"));
                exObj.setExtraDate(res.getDate("event_date"));
                
                extraList.add(exObj);
            }
            
        }catch(SQLException ex){
            ex.printStackTrace();
        }
        return extraList;
    }
    
    public static List<Loss> getAllExtras(String searchText) {
        List<Loss> extraList = new ArrayList<>();
        String sql = "SELECT * FROM extras WHERE LOWER(title) LIKE ? OR LOWER(category) LIKE ? "
                   + "OR LOWER(purpose) LIKE ? OR LOWER(responsible_person) LIKE ? "
                   + "ORDER BY extra_id DESC";
        
        try (Connection con = ConnectionProvider.getCon();
             PreparedStatement pstmt = con.prepareStatement(sql)) {
            
            String searchPattern = "%" + (searchText != null ? searchText.toLowerCase() : "") + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            pstmt.setString(3, searchPattern);
            pstmt.setString(4, searchPattern);
            
            ResultSet res = pstmt.executeQuery();
            
            while (res.next()) {
                Loss exObj = new Loss();
                exObj.setExtra_id(res.getInt("extra_id"));
                exObj.setTitle(res.getString("title"));
                exObj.setDescription(res.getString("description"));
                exObj.setCategory(res.getString("category"));
                exObj.setPurpose(res.getString("purpose"));
                exObj.setCost(res.getDouble("cost"));
                exObj.setResponsiblePerson(res.getString("responsible_person"));
                exObj.setNotes(res.getString("notes"));
                exObj.setImagePath(res.getString("image_path"));
                exObj.setExtraDate(res.getDate("event_date"));
                
                extraList.add(exObj);
            }
            
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return extraList;
    }
}
