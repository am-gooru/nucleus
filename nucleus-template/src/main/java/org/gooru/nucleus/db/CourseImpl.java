package org.gooru.nucleus.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CourseImpl implements CourseInterface {

  static final Logger LOG = LoggerFactory.getLogger(CourseImpl.class);
  static final String url = "jdbc:postgresql://localhost/nucleus";
  static final String user = "nucleus";
  static final String password = "nucleus";
 
  @Override
  public boolean getCourseById(String course_id) {
    Connection connection = null;
    Statement statement = null;
    ResultSet resultset = null;
    String query = null;

    try {
      
      query = "SELECT id FROM course WHERE id='" + course_id + "'";

      connection = DriverManager.getConnection(url, user, password);
      statement = connection.createStatement();
      resultset = statement.executeQuery(query);

      if (resultset.next()) {
        // System.out.println();
        LOG.error("in getCourseById : {} ", resultset.getString("id"));
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

  // returns new course id
  @Override
  public String copyCourse(String source_course_id, String logged_in_user) {
    // This is Copy Course
    Connection con = null;
    PreparedStatement pst = null;
    Statement statement = null;
    ResultSet resultset = null;

    boolean bCourseFound = false;
    String new_course_id = UUID.randomUUID().toString();
    String unit_id = null;
    String query = null;
    try {

      bCourseFound = getCourseById(source_course_id);
      if (bCourseFound) {
        // first copy the course in course table 
        // then look for units in course_unit table, then call copyUnitToCourse function 
       query = "INSERT INTO course(id, title, original_creator_id, creator_id, publish_date, created,modified, accessed, thumbnail, sharing, audience, metadata, collaborator,class_list) " +
               "SELECT '" + new_course_id + "', title, original_creator_id, '" + logged_in_user + "', null, now(), now(), now(), thumbnail, sharing, audience, metadata, collaborator, class_list FROM course  " + 
               "WHERE id = '" + source_course_id + "';";

        LOG.info("copyCourse Successful : insert query - {}", query);

        con = DriverManager.getConnection(url, user, password);
        pst = con.prepareStatement(query);
        pst.executeUpdate();
        LOG.info("copyCourse Successful : new id - {}", new_course_id);

        // get the list of questions & resources associated with the collection
        query = "SELECT unit_id FROM course_unit WHERE course_id = '" + source_course_id + "';";
        LOG.info("copyCourse Successful : select query - {}", query);
        statement = con.createStatement();
        resultset = statement.executeQuery(query);
        UnitInterface ui = new UnitImpl();
        while (resultset.next()) {
          if (resultset.getString("unit_id") != null) {
            unit_id = ui.copyUnitToCourse(source_course_id, resultset.getString("unit_id"), new_course_id, logged_in_user);
            LOG.info("copyCourse Successful : while unit id - {}", unit_id);

          }
        }

        return new_course_id;

      }

    } catch (SQLException ex) {
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
