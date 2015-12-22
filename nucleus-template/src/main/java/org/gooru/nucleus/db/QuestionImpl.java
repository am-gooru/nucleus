package org.gooru.nucleus.db;

import java.sql.*;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QuestionImpl implements QuestionInterface{

  static final Logger LOG = LoggerFactory.getLogger(QuestionImpl.class);
  static final String url = "jdbc:postgresql://localhost/nucleus";
  static final String user = "nucleus";
  static final String password = "nucleus";

  public String copyQuestion(String source_question_id, String logged_in_user_id) {
    Connection con = null;
    PreparedStatement pst = null;
    boolean bQuestionFound = false; 
    
    try {
  
      bQuestionFound = getQuestionById(source_question_id);
      if ( bQuestionFound ) {
        String new_generated_question_id = UUID.randomUUID().toString();
        con = DriverManager.getConnection(url, user, password);
          
        String stm = "INSERT INTO question (id, type, created, modified, accessed, original_creator_id, creator_id,publish_date, title, explanation, hint, detail, answer, metadata)" +  
        "SELECT '" + new_generated_question_id + "', type, now(), now(), now(), original_creator_id, '" + logged_in_user_id + "', null, title, explanation, hint, detail, answer, metadata FROM question " +  
        " WHERE id = '" + source_question_id + "'";
  
        pst = con.prepareStatement(stm);
        
        pst.executeUpdate();
        LOG.info("copyQuestion Successful : new id - {}", new_generated_question_id);
        
        return new_generated_question_id;
      }
      
  
    } catch(SQLException ex){
      LOG.warn("SEVERE", ex.getMessage(), ex);
    } finally {
  
      try {
        if (pst != null) {
          pst.close();
        }
        if (con != null) {
          con.close();
        }
  
      } catch (SQLException ex) {
        LOG.warn("SEVERE", ex.getMessage(), ex);
      }
    }
    return null;

  }
  
  public boolean getQuestionById(String question_id) {
    Connection connection = null;
    Statement statement = null;
    ResultSet resultset = null;
    String query = null;

    try {

      query = "SELECT id FROM question WHERE id = '" + question_id + "'";

      connection = DriverManager.getConnection(url, user, password);
      statement = connection.createStatement();
      resultset = statement.executeQuery(query);

      if (resultset.next()) {
        // System.out.println();
        LOG.error("in getQuestionById : {} ", resultset.getString("id"));
        return true;
      }

    } catch (SQLException ex) {

      LOG.error("SEVERE in Catch", ex.getMessage(), ex);

    } finally {
      try {
        if (resultset != null) {
          resultset.close();
        }
        if (statement != null) {
          statement.close();
        }
        if (connection != null) {
          connection.close();
        }

      } catch (SQLException ex) {
        LOG.warn("SEVERE In finally", ex.getMessage(), ex);
        return false;
      }
    }
    return false;
    
  }
}

