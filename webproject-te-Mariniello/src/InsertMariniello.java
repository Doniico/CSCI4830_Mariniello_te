import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/InsertMariniello")
public class InsertMariniello extends HttpServlet {
   private static final long serialVersionUID = 1L;

   public InsertMariniello() {
      super();
   }

   protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      //Receive and store request
	  String author = request.getParameter("author");
      String pubdate = request.getParameter("pubdate");
      String pubsource = request.getParameter("pubsource");
      String title = request.getParameter("title");
      String pubaddress = request.getParameter("pubaddress");
      String copyright = request.getParameter("copyright");
      String bias = search(pubsource, response);

      Connection connection = null;
      //Insert string for mysql
      String insertSql = "INSERT INTO myArticlesTableMariniello (id, AUTHOR, PUBDATE, PUBSOURCE, TITLE, PUBADDRESS, COPYRIGHT, BIAS) values (default, ?, ?, ?, ?, ?, ?, ?)";

      try {
    	 //Connection jdbc
         DBConnectionMariniello.getDBConnection(getServletContext());
         DBConnectionMariniello.getDBConnection();
         
         //Format statement with request info
         connection = DBConnectionMariniello.connection;
         PreparedStatement preparedStmt = connection.prepareStatement(insertSql);
         preparedStmt.setString(1, author);
         preparedStmt.setString(2, pubdate);
         preparedStmt.setString(3, pubsource);
         preparedStmt.setString(4, title);
         preparedStmt.setString(5, pubaddress);
         preparedStmt.setString(6, copyright);
         preparedStmt.setString(7, bias);

         //Send to server
         preparedStmt.execute();
         connection.close();
      } catch (Exception e) {
         e.printStackTrace();
      }

      // Set response content type
      response.setContentType("text/html");
      PrintWriter out = response.getWriter();
      String head = "Article Inserted";
      String docType = "<!doctype html public \"-//w3c//dtd html 4.0 " + "transitional//en\">\n";
      out.println(docType + //
            "<html>\n" + //
            "<head><title>" + head + "</title></head>\n" + //
            "<body bgcolor=\"#f0f0f0\">\n" + //
            "<h2 align=\"center\">" + head + "</h2>\n" + //
            "<ul>\n" + //

            "  <li><b>Author</b>: " + author + "\n" + //
            "  <li><b>Publication Date</b>: " + pubdate + "\n" + //
            "  <li><b>Publication Source</b>: " + pubsource + "\n" + //
            "  <li><b>Article Title</b>: " + title + "\n" + //
            "  <li><b>Publication Address</b>: " + pubaddress + "\n" + //
            "  <li><b>Copyright</b>: " + copyright + "\n" + //
            "  <li><b>Bias</b>: " + bias + "\n" + //

            
            "</ul>\n");
      
      out.println("<a href=/webproject-te-Mariniello/home.html>Try Another Article</a> <br>");
      out.println("</body></html>");
   }
   
   public String search(String keyword, HttpServletResponse response) throws IOException {
	     

	      Connection connection = null;
	      //One statement is created for each column, not sure how to do this more efficiently
	      PreparedStatement preparedStatementBias = null;
	      String ret = "Source not found in database";

	      try {
	         DBConnectionMariniello.getDBConnection(getServletContext());
	         connection = DBConnectionMariniello.connection;

	         if (keyword.isEmpty()) {
	            String selectSQL = "SELECT * FROM myNewsSources";
	            preparedStatementBias = connection.prepareStatement(selectSQL);
	         } else {
	        	//Check both users and phones
	            String selectSQLSources = "SELECT * FROM myNewsSources WHERE SOURCE LIKE ?";
	            String theKeyword = "%" + keyword + "%";
	            //Create statements for each column
	            preparedStatementBias = connection.prepareStatement(selectSQLSources);
	            preparedStatementBias.setString(1, theKeyword);
	         }
	         //Result set for each column, again not sure how to make this more efficient
	         ResultSet rs = preparedStatementBias.executeQuery();
	         //Output result set
	         while (rs.next()) {
	            String source = rs.getString("SOURCE").trim();
	            String bias = rs.getString("BIAS").trim();

	            if (source.contains(keyword)) {
	               ret = bias;
	            }
	         
	          }
	         rs.close();
	         preparedStatementBias.close();
	         connection.close();
	         return ret;
	      } catch (SQLException se) {
	         se.printStackTrace();
	         return ret;
	      } catch (Exception e) {
	         e.printStackTrace();
	         return ret;
	      } finally {
	         try {
	            if (preparedStatementBias != null)
	               preparedStatementBias.close();
	         } catch (SQLException se2) {
	         }
	         try {
	            if (connection != null)
	               connection.close();
	         } catch (SQLException se) {
	            se.printStackTrace();
	         }
	      }
	   }

   protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      doGet(request, response);
   }

}
