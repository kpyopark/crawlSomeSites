package elevenquest.com.booking.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class BaseDao {
  static {
    try {
      Class.forName("org.postgresql.Driver");
    } catch (Exception e) {}
  }
  public Connection getConnection() throws SQLException {
    Connection con = null;
    con = DriverManager.getConnection("");
    return con;
  }
  
}