package org.gooru.nucleus.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResourceImpl implements ResourceInterface {

  static final Logger LOG = LoggerFactory.getLogger(ResourceImpl.class);
  static final String url = "jdbc:postgresql://localhost/nucleus";
  static final String user = "nucleus";
  static final String password = "nucleus";

  @Override
  public boolean getResourceById(String resource_id) {
    // TODO Auto-generated method stub
    Connection connection = null;
    Statement statement = null;
    ResultSet resultset = null;
    String query = null;

    try {

      query = "SELECT id FROM resource WHERE id = '" + resource_id + "'";

      connection = DriverManager.getConnection(url, user, password);
      statement = connection.createStatement();
      resultset = statement.executeQuery(query);

      if (resultset.next()) {
        // System.out.println();
        LOG.error("in getResourceById : {} ", resultset.getString("id"));
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
