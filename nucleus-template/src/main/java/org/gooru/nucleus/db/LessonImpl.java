package org.gooru.nucleus.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LessonImpl implements LessonInterface {

  static final Logger LOG = LoggerFactory.getLogger(LessonImpl.class);
  static final String url = "jdbc:postgresql://localhost/nucleus";
  static final String user = "nucleus";
  static final String password = "nucleus";


  //returns true or false if found or not
  public boolean getLessonByCourseIdAndUnitId(String course_id, String unit_id, String lesson_id) {
    // TODO Auto-generated method stub
    Connection connection = null;
    Statement statement = null;
    ResultSet resultset = null;
    String query = null;

    try {
      
      query = "SELECT course_id, unit_id, lesson_id  FROM course_unit_lesson WHERE course_id='" + course_id + "' AND unit_id = '" + unit_id + "' AND lesson_id = '" + lesson_id + "';";

      LOG.error("in getLessonById query: {} ", query);
      
      connection = DriverManager.getConnection(url, user, password);
      statement = connection.createStatement();
      resultset = statement.executeQuery(query);

      if (resultset.next()) {
        // System.out.println();
        LOG.error("in getLessonById : {} ", resultset.getString("lesson_id"));
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
  
  //  returns max of sequence id
  public int getMaxSeqIdOfLessonsByCourseIdAndUnitId(String course_id, String unit_id) {
    // TODO Auto-generated method stub
    Connection connection = null;
    Statement statement = null;
    ResultSet resultset = null;
    String query = null;

    try {
      
      query = "SELECT max(sequence_id) as lessonCount FROM course_unit_lesson WHERE course_id='" + course_id + "' AND unit_id = '" + unit_id + "';";

      LOG.error("in getMaxSeqIdOfLessonsByCourseIdAndUnitId query: {} ", query);
      
      connection = DriverManager.getConnection(url, user, password);
      statement = connection.createStatement();
      resultset = statement.executeQuery(query);

      if (resultset.next()) {
        // System.out.println();
        LOG.error("in getMaxSeqIdOfLessonsByCourseIdAndUnitId : {} ", resultset.getString("lessonCount"));
        return resultset.getInt("lessonCount");
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
  public String copyLessonToUnit(String source_course_id, String source_unit_id, String lesson_id, String target_course_id, String target_unit_id, String logged_in_user) {
 // This is Copy lesson to existing unit associated to some course
    Connection con = null;
    PreparedStatement pst = null;
    Statement statement = null;
    ResultSet resultset = null;
    ArrayList<String> aCollectionIds = new ArrayList<String>();
    ArrayList<String> aAssessmentIds = new ArrayList<String>();
    

    boolean bLessonFound = false; 
    String new_generated_CUL_id = UUID.randomUUID().toString();
    String new_lesson_id = UUID.randomUUID().toString();
    String result_id = null;
    int count = 0;
    int lessonSeqId = 0;
    String query = "";
    try {
  
      LessonInterface li = new LessonImpl();
      bLessonFound = li.getLessonByCourseIdAndUnitId(source_course_id, source_unit_id, lesson_id);
       if ( bLessonFound ) {
        // first copy the lesson to target course and unit in cul table
        // then look for associations in culca table, then 
        // copy collections and assessments to the newly added lesson 
        // then link those collections and assessments to culca table
         
        lessonSeqId =  li.getMaxSeqIdOfLessonsByCourseIdAndUnitId(target_course_id, target_unit_id);
        if (lessonSeqId >= 0) {
         
          query = "INSERT INTO course_unit_lesson(course_id, unit_id, lesson_id, creator_id, title, created, modified,accessed, sequence_id, metadata) " + 
                  "SELECT '" + target_course_id + "', '" + target_unit_id + "', '" + new_lesson_id + "', '" + logged_in_user + "', title, now(), now(), now(), " + (lessonSeqId + 1) + ", metadata FROM course_unit_lesson " + 
                  "WHERE course_id='" + source_course_id + "' AND unit_id = '" + source_unit_id + "' AND lesson_id = '" + lesson_id + "';";
  
          LOG.info("copyLessonToUnit Successful : insert query - {}", query);
          
          con = DriverManager.getConnection(url, user, password);
          pst = con.prepareStatement(query);
          pst.executeUpdate();
          LOG.info("copyLessonToUnit Successful : new id - {}", new_generated_CUL_id);
          
          
          // get the list of questions & resources associated with the collection 
          query = "SELECT collection_id, assessment_id FROM course_unit_lesson_collection_assessment WHERE course_id = '" + source_course_id + "' AND unit_id = '" + source_unit_id + "' AND lesson_id ='" + lesson_id + "';";
          LOG.info("copyLessonToUnit Successful : select query - {}", query);
          statement = con.createStatement();
          resultset = statement.executeQuery(query);
  
          while (resultset.next()) {
            if ( resultset.getString("collection_id") != null) {
              aCollectionIds.add(resultset.getString("collection_id"));
              LOG.info("copyLessonToUnit cIds: {}" , resultset.getString("collection_id"));
            }
            if ( resultset.getString("assessment_id") != null ) {
              aAssessmentIds.add(resultset.getString("assessment_id"));
              LOG.info("copyLessonToUnit aIds: {}" , resultset.getString("assessment_id"));
            }
          }
          
          if (aCollectionIds.size() > 0 ) {
            CollectionInterface ci = new CollectionImpl();
            LOG.info("copyLessonToUnit qIds size: {}" , aCollectionIds.size());
            for (count = 0 ; count < aCollectionIds.size() ; count++) {
              result_id = ci.copyCollectionToLesson(source_course_id, source_unit_id, target_course_id, target_unit_id, new_lesson_id, aCollectionIds.get(count), logged_in_user);
            }
          }
          if (aAssessmentIds.size() > 0 ) {
            AssessmentInterface ai = new AssessmentImpl();
            LOG.info("copyLessonToUnit aIds size: {}" , aAssessmentIds.size());
            for (count = 0 ; count < aAssessmentIds.size() ; count++) {
              result_id = ai.copyAssessmentToLesson(source_course_id, source_unit_id, target_course_id, target_unit_id, new_lesson_id, aAssessmentIds.get(count), logged_in_user);
            }
          }
       
          return new_lesson_id;
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

  @Override
  public int getMaxSeqIdOfElementsInLesson(String course_id, String unit_id, String lesson_id) {
    Connection connection = null;
    Statement statement = null;
    ResultSet resultset = null;
    String query = null;

    try {

      query = "SELECT max(sequence_id) as elementCount FROM course_unit_lesson_collection_assessment WHERE course_id='" + course_id +  "' AND unit_id = '" + unit_id + "' AND lesson_id = '" + lesson_id + "';";

      LOG.error("in getMaxSeqIdOfElementsInLesson query: {} ", query);

      connection = DriverManager.getConnection(url, user, password);
      statement = connection.createStatement();
      resultset = statement.executeQuery(query);

      if (resultset.next()) {
        // System.out.println();
        LOG.error("in getMaxSeqIdOfElementsInLesson : {} ", resultset.getString("elementCount"));
        return resultset.getInt("elementCount");
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
 
}
