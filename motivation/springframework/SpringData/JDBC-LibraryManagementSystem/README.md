### Data persistence with JDBC
- One way to save an instance of the Musician class to a relational database would be to use the JDBC library. 
- JDBC is a layer of abstraction that lets an application issue SQL commands without thinking about the underlying database implementation.
```
Musician georgeHarrison = new Musician(0, "George Harrison");
      String myDriver = "org.gjt.mm.mysql.Driver";
      String myUrl = "jdbc:mysql://localhost/test";
      Class.forName(myDriver);
      Connection conn = DriverManager.getConnection(myUrl, "root", "");
      String query = " insert into users (id, name) values (?, ?)";
      PreparedStatement preparedStmt = conn.prepareStatement(query);
      preparedStmt.setInt     (1, 0);
      preparedStmt.setString (2, "George Harrison");
      preparedStmt.setString (2, "Rubble");
      preparedStmt.execute();
      conn.close();
// Error handling removed for brevity
```
- While JDBC provides the control that comes with manual configuration, it is cumbersome compared to JPA. 
- To modify the database, you first need to create an SQL query that maps from your Java object to the tables in a relational database. 
- You then have to modify the SQL whenever an object signature changes. 
- With JDBC, maintaining the SQL becomes a task in itself.

- SpringBootJDBC
  - Introduction to Springboot
  - Introduction to Springboot with crud