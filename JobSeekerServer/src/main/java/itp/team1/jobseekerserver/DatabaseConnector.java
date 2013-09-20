
package itp.team1.jobseekerserver;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

/**
 *
 * @author 
 */
public class DatabaseConnector 
{

    private DataSource ds;
    protected Connection connect;
    private Statement statement;

    public DatabaseConnector() 
    {
        String dbName = "jdbc/jobseekerdb";

        try 
        {
            ds = (DataSource) new InitialContext().lookup("java:comp/env/" + dbName);
        } 
        catch (NamingException e) 
        {
            System.err.println(dbName + " is missing: " + e.toString());
        }
    }

    public Connection getConnection() 
    {
        try 
        {
            return ds.getConnection();
        } 
        catch (SQLException e) 
        {
            System.err.println("Error while connecting to database: " + e.toString());
        }
        return null;
    }

    /*
     * Connects to the database and issues the command
     * @param command which is executed by the database
     */
    public boolean execute(String command) 
    {
        try 
        {
            connect = getConnection();
            statement = connect.createStatement();
            statement.execute(command);
        } 
        catch (SQLException e) 
        {
            System.err.println("Error while executing SQL statement" + e.toString());
            return false;
        } 
        finally 
        {
            closeConnection();
        }
        return true;
    }

    public void closeConnection() 
    {
        try 
        {
            if (connect != null)
                connect.close();
        } 
        catch (SQLException ex) 
        {
            Logger.getLogger(DatabaseConnector.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
