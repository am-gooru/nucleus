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

public class CollectionImpl implements CollectionInterface {

  static final Logger LOG = LoggerFactory.getLogger(CollectionImpl.class);
  static final String url = "jdbc:postgresql://localhost/nucleus";
  static final String user = "nucleus";
  static final String password = "nucleus";


  @Override
  public HashMap<String, String> copyQuestionToCollection(String question_id, String target_collection_id, String logged_in_user) {
    
    Connection con = null;
    PreparedStatement pst = null;
    Statement statement = null;
    ResultSet resultset = null;

    boolean bCollectionFound = false; 
    HashMap<String, String> returnHashmap= new HashMap<String, String>();
    int seqId = -1;
    
    try {
  
      bCollectionFound = getCollectionById(target_collection_id);
      if ( bCollectionFound ) {
        QuestionInterface qi = new QuestionImpl();
        String new_q_id  = qi.copyQuestion(question_id, logged_in_user);
        String new_generated_col_item_id = UUID.randomUUID().toString();
        con = DriverManager.getConnection(url, user, password);
        
        seqId = getMaxSeqIdOfElementsInCollection(target_collection_id);
        if ( seqId >= 0) { 
          String stm = "INSERT INTO collection_item(id, collection_id, resource_id, question_id, sequence_id, creator_id, created, modified, accessed, narration, metadata) " +
                       "SELECT '" + new_generated_col_item_id + "', '" + target_collection_id + "', null,'" + new_q_id + "', " + (seqId + 1) + ", '" + logged_in_user + "', now(), now(), now(),null, null FROM collection_item " + 
                       "WHERE collection_id = '" + target_collection_id + "';";
  
          LOG.info("copyQuestionToCollection Successful : query - {}", stm);
                 
          pst = con.prepareStatement(stm);
           pst.executeUpdate();
          LOG.info("copyQuestionToCollection Successful : new id - {}", new_generated_col_item_id);
          returnHashmap.put("new_question_id", new_q_id);
          returnHashmap.put("new_question_seq_id", String.valueOf(seqId+1));
     
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
  public HashMap<String, String> copyResourceToCollection(String resource_id, String target_collection_id, String logged_in_user) {
    // TODO Auto-generated method stub
    Connection con = null;
    PreparedStatement pst = null;
    Statement statement = null;
    ResultSet resultset = null;

    boolean bCollectionFound = false; 
    boolean bResourceFound = false;
    HashMap<String, String> returnHashmap= new HashMap<String, String>();
    int seqId = -1;
    
    try {
  
      bCollectionFound = getCollectionById(target_collection_id);
      if ( bCollectionFound ) {
        ResourceInterface ri = new ResourceImpl();
        
        bResourceFound = ri.getResourceById(resource_id);
        LOG.error("in copyResourceToCollection res_id : {} ", bResourceFound);
        if (bResourceFound) {
          String new_generated_col_item_id = UUID.randomUUID().toString();
          String seq_id = null;
          con = DriverManager.getConnection(url, user, password);
          seqId = getMaxSeqIdOfElementsInCollection(target_collection_id);
          if ( seqId >= 0) { 
            String stm = "INSERT INTO collection_item(id, collection_id, resource_id, question_id, sequence_id, creator_id, created, modified, accessed, narration, metadata) " + 
                         "SELECT '" + new_generated_col_item_id + "', '" + target_collection_id + "','" + resource_id + "','null', " + (seqId + 1) + ", '" + logged_in_user + "', now(), now(), now(),null, null FROM collection_item " + 
                         "WHERE collection_id = '" + target_collection_id + "'";
               
            LOG.info("copyResourceToCollection Successful : insert query - {}", stm);
            pst = con.prepareStatement(stm);
            
            pst.executeUpdate();
            LOG.info("copyResourceToCollection Successful : new item_id - {}", new_generated_col_item_id);
            returnHashmap.put("new_generated_col_item_id", new_generated_col_item_id);
            returnHashmap.put("new_question_seq_id", String.valueOf(seqId+1));
                      
            return returnHashmap;
          }
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
  public boolean getCollectionById(String collection_id) {

      Connection connection = null;
      Statement statement = null;
      ResultSet resultset = null;
      String query = null;

      try {

        query = "SELECT id FROM collection WHERE id = '" + collection_id + "'";

        connection = DriverManager.getConnection(url, user, password);
        statement = connection.createStatement();
        resultset = statement.executeQuery(query);

        if (resultset.next()) {
          // System.out.println();
          LOG.info("in getCollectionById : {} ", resultset.getString("id"));
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
  public String copyCollection(String collection_id, String logged_in_user) {
    // This is Copy Collection at global level
    Connection con = null;
    PreparedStatement pst = null;
    Statement statement = null;
    ResultSet resultset = null;

    boolean bCollectionFound = false; 
    String new_generated_collection_id = UUID.randomUUID().toString();
    ArrayList<String> aQuestionsIds = new ArrayList<String>();
    ArrayList<String> aResourceIds = new ArrayList<String>();
    HashMap<String, String> temp = new HashMap<String, String>();
    int count = 0;
    
    String query = "";
    try {
  
      bCollectionFound = getCollectionById(collection_id);
      if ( bCollectionFound ) {
        query = "INSERT INTO collection(id, title, original_creator_id, creator_id, publish_date, created, modified, accessed, thumbnail, sharing, learning_objective, flag_report,comments_enabled, audience, metadata, collaborator) " +  
                "SELECT '" + new_generated_collection_id + "', title, original_creator_id, '" + logged_in_user + "', null, now(), now(), now(), thumbnail, sharing, learning_objective, flag_report, comments_enabled, audience, metadata, collaborator FROM collection " + 
                "WHERE id='" + collection_id + "'";

        LOG.info("copyCollection Successful : query - {}", query);
        con = DriverManager.getConnection(url, user, password);
        pst = con.prepareStatement(query);
        pst.executeUpdate();
        LOG.info("copyCollection Successful : new id - {}", new_generated_collection_id);
        
        
        // get the list of questions & resources associated with the collection 
        query = "SELECT question_id, resource_id FROM collection_item WHERE collection_id = '" + collection_id + "'";
        statement = con.createStatement();
        resultset = statement.executeQuery(query);

        while (resultset.next()) {
          if ( resultset.getString("question_id") != null) {
            aQuestionsIds.add(resultset.getString("question_id"));
            LOG.info("copyCollection qIds: {}" , resultset.getString("question_id"));
          }
          if ( resultset.getString("resource_id") != null ) {
            aResourceIds.add(resultset.getString("resource_id"));
            LOG.info("copyCollection rIds: {}" , resultset.getString("resource_id"));
          }
        }
        
        if (aQuestionsIds.size() > 0 ) {
          LOG.info("copyCollection qIds size: {}" , aQuestionsIds.size());
          for (count = 0 ; count < aQuestionsIds.size() ; count++) {
            temp = copyQuestionToCollection(aQuestionsIds.get(count), new_generated_collection_id, logged_in_user);
          }
        }
        if (aResourceIds.size() > 0 ) {
          LOG.info("copyCollection rIds size: {}" , aResourceIds.size());
          for (count = 0 ; count < aResourceIds.size() ; count++) {
            temp = copyResourceToCollection(aResourceIds.get(count), new_generated_collection_id, logged_in_user);
          }
        }
        
         return new_generated_collection_id;
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
  public String copyCollectionToLesson(String source_course_id, String source_unit_id, String target_course_id, String target_unit_id, String lesson_id, String collection_id, String logged_in_user) {
 // This is Copy Collection at global level
    Connection con = null;
    PreparedStatement pst = null;
    Statement statement = null;
    ResultSet resultset = null;

    boolean blessonFound = false; 
    boolean bCollectionFound = false; 
    String new_generated_culca_id = UUID.randomUUID().toString();
    String new_gen_collection_id = UUID.randomUUID().toString();
    int seqId = -1;
    String query = "";
    try {
  
      LessonInterface li = new LessonImpl();
      blessonFound = li.getLessonByCourseIdAndUnitId(target_course_id, target_unit_id, lesson_id);
      bCollectionFound = getCollectionById(collection_id);
      if (  blessonFound && bCollectionFound) {
        // copy collections newly added lesson 
        new_gen_collection_id = copyCollection(collection_id, logged_in_user); 
        // then link those collections and assessments to culca table
        seqId = li.getMaxSeqIdOfElementsInLesson(target_course_id, target_unit_id, lesson_id);
        if ( seqId >= 0 ) {
          query = "INSERT INTO course_unit_lesson_collection_assessment(id, course_id, unit_id, lesson_id, collection_id, assessment_id,sequence_id) " + 
                  "SELECT '" + new_generated_culca_id + "', '" + target_course_id + "', '" + target_unit_id + "', '" + lesson_id + "', '" + new_gen_collection_id + "', null, " + (seqId + 1) + " FROM course_unit_lesson_collection_assessment " + 
                  " WHERE course_id = '" + source_course_id + "' AND unit_id = '" + source_unit_id + "' AND lesson_id ='" + lesson_id + "';";
  
          LOG.info("copyCollectionToLesson Successful : insert query - {}", query);
          
          con = DriverManager.getConnection(url, user, password);
          pst = con.prepareStatement(query);
          pst.executeUpdate();
          LOG.info("copyCollectionToLesson Successful : new id - {}", new_generated_culca_id);
          
           return new_gen_collection_id;
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
  public int getMaxSeqIdOfElementsInCollection(String collection_id) {
    Connection connection = null;
    Statement statement = null;
    ResultSet resultset = null;
    String query = null;

    try {

      query = "SELECT max(sequence_id) as elementCount FROM collection_item WHERE collection_id='" + collection_id +  "';";

      LOG.error("in getMaxSeqIdOfElementsInCollection query: {} ", query);

      connection = DriverManager.getConnection(url, user, password);
      statement = connection.createStatement();
      resultset = statement.executeQuery(query);

      if (resultset.next()) {
        // System.out.println();
        LOG.error("in getMaxSeqIdOfQuestionsInAssessment : {} ", resultset.getString("elementCount"));
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
