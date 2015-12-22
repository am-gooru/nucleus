package org.gooru.nucleus.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UnitImpl implements UnitInterface {

  static final Logger LOG = LoggerFactory.getLogger(AssessmentImpl.class);
  static final String url = "jdbc:postgresql://localhost/nucleus";
  static final String user = "nucleus";
  static final String password = "nucleus";


  @Override
  public boolean getUnitByCourseId(String course_id, String unit_id) {
 // TODO Auto-generated method stub
    Connection connection = null;
    Statement statement = null;
    ResultSet resultset = null;
    String query = null;

    try {
      
      query = "SELECT course_id, unit_id FROM course_unit WHERE course_id='" + course_id + "' AND unit_id = '" + unit_id + "'";

      connection = DriverManager.getConnection(url, user, password);
      statement = connection.createStatement();
      resultset = statement.executeQuery(query);

      if (resultset.next()) {
        // System.out.println();
        LOG.error("in getUnitByCourseId : {} ", resultset.getString("unit_id"));
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



//returns max of sequence id
  @Override
  public int getMaxSeqIdOfUnitsByCourseId(String course_id) {
    // TODO Auto-generated method stub
    Connection connection = null;
    Statement statement = null;
    ResultSet resultset = null;
    String query = null;

    try {

      query = "SELECT max(sequence_id) as unitCount FROM course_unit WHERE course_id='" + course_id +  "';";

      LOG.error("in getMaxSeqIdOfUnitsByCourseId query: {} ", query);

      connection = DriverManager.getConnection(url, user, password);
      statement = connection.createStatement();
      resultset = statement.executeQuery(query);

      if (resultset.next()) {
        // System.out.println();
        LOG.error("in getMaxSeqIdOfUnitsByCourseId : {} ", resultset.getString("unitCount"));
        return resultset.getInt("unitCount");
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
        return -1;
      }
    }
    return -1;

}


  @Override
  public String copyUnitToCourse(String source_course_id, String unit_id, String target_course_id, String logged_in_user) {
 // This is Copy Unit to existing course
    Connection con = null;
    PreparedStatement pst = null;
    Statement statement = null;
    ResultSet resultset = null;
  
    boolean bUnitFound = false; 
    String new_generated_CU_id = UUID.randomUUID().toString();
    String new_unit_id = UUID.randomUUID().toString();
    String result_id = null;
    int unitSeqId = 0;
    String query = "";
    String lesson_id = null;
    try {
  
      bUnitFound = getUnitByCourseId(source_course_id, unit_id);
       if ( bUnitFound ) {
        // first copy the unit to target course cu table
        // then look for lesson associations in cul table, then call copyUnitToLesson
         
        unitSeqId=  getMaxSeqIdOfUnitsByCourseId(target_course_id);
        if (unitSeqId >= 0) {
          query = "INSERT INTO course_unit(course_id, unit_id, title, creator_id, created, modified, accessed, big_ideas, essential_questions, metadata, sequence_id) " + 
                  "SELECT '" + target_course_id + "', '" + new_unit_id + "', title, '" + logged_in_user + "', now(), now(), now(),big_ideas, essential_questions, metadata, " + (unitSeqId + 1) + " FROM course_unit " +
                  "WHERE course_id = '" + source_course_id + "' AND unit_id = '" + unit_id + "';";
   
          LOG.info("copyUnitToCourse Successful : insert query - {}", query);
          
          con = DriverManager.getConnection(url, user, password);
          pst = con.prepareStatement(query);
          pst.executeUpdate();
          LOG.info("copyUnitToCourse Successful : new id - {}", new_generated_CU_id);
          
          
          // get the list of questions & resources associated with the collection 
          query = "SELECT lesson_id FROM course_unit_lesson WHERE course_id = '" + source_course_id + "' AND unit_id = '" + unit_id + "';";
          LOG.info("copyUnitToCourse Successful : select query - {}", query);
          statement = con.createStatement();
          resultset = statement.executeQuery(query);
          LessonInterface li = new LessonImpl();
          while (resultset.next()) {
            if ( resultset.getString("lesson_id") != null) {
              lesson_id = li.copyLessonToUnit(source_course_id, unit_id, resultset.getString("lesson_id"), target_course_id, new_unit_id, logged_in_user);
              LOG.info("copyUnitToCourse Successful : while query - {}", resultset.getString("lesson_id") );
              
            }
          }
          
          return new_unit_id;
        }
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
        if (resultset != null) {
          resultset.close();
        }
        if (statement != null) {
          statement.close();
        }
  
      } catch (SQLException ex) {
        LOG.warn("SEVERE", ex.getMessage(), ex);
      }
    }
    return null;

  }

}
