package org.gooru.nucleus.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AssessmentImpl implements AssessmentInterface {

  static final Logger LOG = LoggerFactory.getLogger(AssessmentImpl.class);
  static final String url = "jdbc:postgresql://localhost/nucleus";
  static final String user = "nucleus";
  static final String password = "nucleus";


  @Override
  public HashMap<String, String> copyQuestionToAssessment(String question_id, String target_assessment_id, String logged_in_user) {
    // TODO Auto-generated method stub
    Connection con = null;
    PreparedStatement pst = null;
    Statement statement = null;
    ResultSet resultset = null;

    boolean bAssessmentFound = false; 
    int questionCount = -1;
    HashMap<String, String> returnHashmap= new HashMap<String, String>();
    
    try {
  
      bAssessmentFound = getAssessmentById(target_assessment_id);
      LOG.info("copyQuestionToAssessment Successful : bAssessmentFound - {}", questionCount);
      if ( bAssessmentFound ) {
        QuestionInterface qi = new QuestionImpl();
        String new_q_id  = qi.copyQuestion(question_id, logged_in_user);
        String new_generated_ass_item_id = UUID.randomUUID().toString();
        String seq_id = null;
        con = DriverManager.getConnection(url, user, password);
        
        questionCount = getMaxSeqIdOfQuestionsInAssessment(target_assessment_id);
        LOG.info("copyQuestionToAssessment Successful : questionCount - {}", questionCount);
        if (questionCount >= 0) {
          String stm = "INSERT INTO assessment_item (id, assessment_id, question_id, sequence_id, creator_id, created, modified, accessed, narration)" +  
                       "SELECT '" + new_generated_ass_item_id + "', '" + target_assessment_id  + "', '" + new_q_id + "', " + (questionCount + 1) + ", '" + logged_in_user + "', now(), now(), now(), null FROM assessment_item " + 
                       "WHERE assessment_id = '" + target_assessment_id + "'";
    
          LOG.info("copyQuestionToAssessment Successful : query - {}", stm);
          pst = con.prepareStatement(stm);
          
          pst.executeUpdate();
          LOG.info("copyQuestionToAssessment Successful : new id - {}", new_generated_ass_item_id);
          returnHashmap.put("new_question_id", new_q_id);
          
          String query = "SELECT id, sequence_id FROM assessment_item WHERE id = '" + new_generated_ass_item_id + "'";
          statement = con.createStatement();
          resultset = statement.executeQuery(query);
  
          if (resultset.next()) {
            seq_id = resultset.getString("sequence_id");
            returnHashmap.put("new_question_seq_id", seq_id);
            LOG.error("in copyQuestionToAssessment Seq : {} ", resultset.getString("sequence_id"));
          }
          
          return returnHashmap;
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
  public boolean getAssessmentById(String assessment_id) {
    Connection connection = null;
    Statement statement = null;
    ResultSet resultset = null;
    String query = null;

    try {

      query = "SELECT id FROM assessment WHERE id = '" + assessment_id + "'";

      connection = DriverManager.getConnection(url, user, password);
      statement = connection.createStatement();
      resultset = statement.executeQuery(query);

      if (resultset.next()) {
        // System.out.println();
        LOG.info("in getAssessmentById : {} ", resultset.getString("id"));
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

  @Override
  public String copyAssessment(String assessment_id, String logged_in_user) {
    // This is Copy Collection at global level
    Connection con = null;
    PreparedStatement pst = null;
    Statement statement = null;
    ResultSet resultset = null;

    boolean bAssessmentFound = false; 
    String new_generated_assessment_id = UUID.randomUUID().toString();
    HashMap<String, String> temp = new HashMap<String, String>();

    
    String query = "";
    try {
  
      bAssessmentFound = getAssessmentById(assessment_id);
      if ( bAssessmentFound ) {
        query = "INSERT INTO assessment(id, type, url, title, original_creator_id, creator_id, publish_date, created, modified, accessed, thumbnail, sharing, learning_objective, flag_report, audience, collaborator, metadata, login_required, settings) " +  
                "SELECT '" + new_generated_assessment_id + "', type, url, title, original_creator_id, '"+ logged_in_user + "', null, now(), now(), now(), thumbnail, sharing, learning_objective, flag_report, audience, collaborator, metadata, login_required, settings " +  
                "FROM assessment WHERE id = '" + assessment_id + "';";

        con = DriverManager.getConnection(url, user, password);
        pst = con.prepareStatement(query);
        pst.executeUpdate();
        LOG.info("copyAssessment Successful : new id - {}", new_generated_assessment_id);
        
        
        // get the list of questions associated with the assessment 
        query = "SELECT question_id FROM assessment_item WHERE assessment_id = '" + assessment_id + "'";
        statement = con.createStatement();
        resultset = statement.executeQuery(query);

        while (resultset.next()) {
          if ( resultset.getString("question_id") != null) {
            temp = copyQuestionToAssessment(resultset.getString("question_id"), new_generated_assessment_id, logged_in_user);
            LOG.info("copyAssessment qIds: {}" , resultset.getString("question_id"));
          }
         }
        
        return new_generated_assessment_id;
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

  public String copyAssessmentToLesson(String source_course_id, String source_unit_id, String target_course_id, String target_unit_id, String lesson_id, String assessment_id, String logged_in_user) {
    // This is Copy Collection at global level
       Connection con = null;
       PreparedStatement pst = null;
       Statement statement = null;
       ResultSet resultset = null;

       boolean bLessonFound = false; 
       boolean bAssessmentFound = false; 
       String new_generated_culca_id = UUID.randomUUID().toString();
       String new_gen_assessment_id = UUID.randomUUID().toString();
       int seqId = -1;
       String query = "";
       try {
     
         LessonInterface li = new LessonImpl();
         bLessonFound = li.getLessonByCourseIdAndUnitId(target_course_id, target_unit_id, lesson_id);
         bAssessmentFound = getAssessmentById(assessment_id);
         if ( bLessonFound && bAssessmentFound) {
           // copy collections newly added lesson 
           new_gen_assessment_id = copyAssessment(assessment_id, logged_in_user); 
           // then link those collections and assessments to culca table
           seqId = li.getMaxSeqIdOfElementsInLesson(target_unit_id, target_unit_id, lesson_id);
           if ( seqId >= 0) {
             query = "INSERT INTO course_unit_lesson_collection_assessment(id, course_id, unit_id, lesson_id, collection_id, assessment_id,sequence_id) " + 
                     "SELECT '" + new_generated_culca_id + "', '" + target_course_id + "', '" + target_unit_id + "', '" + lesson_id + "',null, '" + new_gen_assessment_id + "', " + (seqId + 1) + " FROM course_unit_lesson_collection_assessment " + 
                     " WHERE course_id = '" + source_course_id + "' AND unit_id = '" + source_unit_id + "' AND lesson_id ='" + lesson_id + "';";
  
             LOG.info("copyAssessmentToLesson Successful : query - \n{}", query);
             
             con = DriverManager.getConnection(url, user, password);
             pst = con.prepareStatement(query);
             pst.executeUpdate();
             LOG.info("copyAssessmentToLesson Successful : new id - {}", new_generated_culca_id);
             
              return new_gen_assessment_id;
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

  // returns -1 if not found 
  @Override
  public int getMaxSeqIdOfQuestionsInAssessment(String assessment_id) {
      
      Connection connection = null;
      Statement statement = null;
      ResultSet resultset = null;
      String query = null;

      try {

        query = "SELECT max(sequence_id) as questionCount FROM assessment_item WHERE assessment_id='" + assessment_id +  "';";

        LOG.error("in getMaxSeqIdOfQuestionsInAssessment query: {} ", query);

        connection = DriverManager.getConnection(url, user, password);
        statement = connection.createStatement();
        resultset = statement.executeQuery(query);

        if (resultset.next()) {
          // System.out.println();
          LOG.error("in getMaxSeqIdOfQuestionsInAssessment : {} ", resultset.getString("questionCount"));
          return resultset.getInt("questionCount");
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
