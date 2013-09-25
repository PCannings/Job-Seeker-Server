
package itp.team1.jobseekerserver;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Calum
 * Batch insert code adapted from http://viralpatel.net/blogs/batch-insert-in-java-jdbc/
 */
public class ConcreteDBConnector extends DatabaseConnector 
{
    private static final int DAY_AGE_TO_DELETE = 10;
    
    public ConcreteDBConnector()
    {
        super();
    }
    
    public void insertSocialJobs(List<Job> jobs)
    {

        try 
        {
            Connection connection = getConnection();
            String query = "INSERT INTO socialjobs (description, url, `source`, `timestamp`, city) VALUES(?, ?, ?, ?, ?)"
                         + "ON DUPLICATE KEY UPDATE url=url, id=id;";
            PreparedStatement prepStmt = connection.prepareStatement(query);
            for (Job job : jobs) 
            {
                prepStmt.setString(1, job.getDescription());
                prepStmt.setString(2, job.getURL());
                prepStmt.setString(3, job.getSource());
                prepStmt.setTimestamp(4, new Timestamp(job.getMillisTimestamp()));
                prepStmt.setString(5, job.getCity().toLowerCase());

                prepStmt.addBatch();
            }
            prepStmt.executeBatch();
            prepStmt.close();
            connection.close();
        }
        catch (SQLException ex) 
        {
            Logger.getLogger(ConcreteDBConnector.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void insertConventionalJobs(List<Job> jobs)
    {
       try 
        {
            Connection connection = getConnection();
            String query = "INSERT INTO jobs (employer, title, city, `timestamp`, url, `source`, description) VALUES(?, ?, ?, ?, ?, ?, ?) "
                         + "ON DUPLICATE KEY UPDATE url=url, id=id;";
            PreparedStatement prepStmt = connection.prepareStatement(query);
            
            for (Job job : jobs) 
            {
                prepStmt.setString(1, job.getEmployer());
                prepStmt.setString(2, job.getTitle());
                prepStmt.setString(3, job.getCity().toLowerCase());
                prepStmt.setTimestamp(4, new Timestamp(job.getMillisTimestamp()));
                prepStmt.setString(5, job.getURL());
                prepStmt.setString(6, job.getSource());
                prepStmt.setString(7, job.getDescription());

                prepStmt.addBatch();
            }
            prepStmt.executeBatch();
            prepStmt.close();
            connection.close();
        }
        catch (SQLException ex) 
        {
            Logger.getLogger(ConcreteDBConnector.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public List<Job> getRecentSocialJobs(int offset, int limit, String location, String keyword)
    {
        List<Job> recentJobs = new ArrayList<Job>(limit);
        try 
        {
            Connection connection = getConnection();
            String query = "";
            PreparedStatement prepStmt;
            String keywordWildcard = "%" + keyword + "%";

            // Specific locations or not
            if (location.equals(""))
            {
                // Search keyword or not
                if (keyword.equals(""))
                {
                    query = "SELECT * FROM socialjobs GROUP BY(url) ORDER BY `timestamp` DESC LIMIT ?,?;";
                    prepStmt = connection.prepareStatement(query);
                    prepStmt.setInt(1, offset);
                    prepStmt.setInt(2, limit);
                }
                else
                {
                    query = "SELECT * FROM socialjobs "
                          + "WHERE description LIKE ? GROUP BY(url) ORDER BY `timestamp` DESC LIMIT ?,?;";
                    prepStmt = connection.prepareStatement(query);
                    prepStmt.setString(1, keywordWildcard);
                    prepStmt.setInt(2, offset);
                    prepStmt.setInt(3, limit);
                }
            }
            else
            {
                if (keyword.equals(""))
                {
                    query = "SELECT * FROM socialjobs "
                          + "WHERE city = ? GROUP BY(url) ORDER BY `timestamp` DESC LIMIT ?,?;";
                    prepStmt = connection.prepareStatement(query);
                    prepStmt.setString(1, location);
                    prepStmt.setInt(2, offset);
                    prepStmt.setInt(3, limit);
                }
                else
                {
                    query = "SELECT * FROM socialjobs "
                          + "WHERE city = ? AND description LIKE ? "
                          + "GROUP BY(url) ORDER BY `timestamp` DESC LIMIT ?,?;";
                    prepStmt = connection.prepareStatement(query);
                    prepStmt.setString(1, location);
                    prepStmt.setString(2, keywordWildcard);
                    prepStmt.setInt(3, offset);
                    prepStmt.setInt(4, limit);
                }
            }
            
            // Execute
            ResultSet jobResults = prepStmt.executeQuery();
            
            while (jobResults.next())
            {
                // Instantiate job
                Job job = new Job();
                job.setId(jobResults.getInt("id"));
                job.setDescription(jobResults.getString("description"));
                job.setURL(jobResults.getString("url"));
                job.setSource(jobResults.getString("source"));
                job.setMillisTimestamp(jobResults.getTimestamp("timestamp").getTime());
                job.setCity(jobResults.getString("city"));
                
                // Add to list
                recentJobs.add(job);
            }

            prepStmt.close();
            connection.close();
        }
        catch (SQLException ex) 
        {
            Logger.getLogger(ConcreteDBConnector.class.getName()).log(Level.SEVERE, null, ex);
        }
        return recentJobs;
    }
    
    public List<Job> getRecentConventionalJobs(int offset, int limit, String location, String keyword)
    {
        List<Job> recentJobs = new ArrayList<Job>(limit);
        try 
        {
            Connection connection = getConnection();
            String query = "";
            PreparedStatement prepStmt;
            String keywordWildcard = "%" + keyword + "%";
            
            // Specific locations or not
            if (location.equals(""))
            {
                // Search keyword or not
                if (keyword.equals(""))
                {
                    query = "SELECT * FROM jobs GROUP BY(url) ORDER BY `timestamp` DESC LIMIT ?,?;";
                    prepStmt = connection.prepareStatement(query);
                    prepStmt.setInt(1, offset);
                    prepStmt.setInt(2, limit);
                }
                else
                {                    
                    query = "SELECT * FROM jobs "
                          + "WHERE description LIKE ? OR title LIKE ? OR employer LIKE ? "
                          + "GROUP BY(url) ORDER BY `timestamp` DESC LIMIT ?,?;";
                    prepStmt = connection.prepareStatement(query);
                    prepStmt.setString(1, keywordWildcard);
                    prepStmt.setString(2, keywordWildcard);
                    prepStmt.setString(3, keywordWildcard);
                    prepStmt.setInt(4, offset);
                    prepStmt.setInt(5, limit);
                }
            }
            else
            {    
                if (keyword.equals(""))
                {
                    query = "SELECT * FROM jobs WHERE city = ? GROUP BY(url) ORDER BY `timestamp` DESC LIMIT ?,?;";
                    prepStmt = connection.prepareStatement(query);
                    prepStmt.setString(1, location);
                    prepStmt.setInt(2, offset);
                    prepStmt.setInt(3, limit);
                }
                else
                {   
                    query = "SELECT * FROM jobs "
                          + "WHERE city = ? AND (description LIKE ? OR title LIKE ? OR employer LIKE ?) "
                          + "GROUP BY(url) ORDER BY `timestamp` DESC LIMIT ?,?;";
                    prepStmt = connection.prepareStatement(query);
                    prepStmt.setString(1, location);
                    prepStmt.setString(2, keywordWildcard);
                    prepStmt.setString(3, keywordWildcard);
                    prepStmt.setString(4, keywordWildcard);
                    prepStmt.setInt(5, offset);
                    prepStmt.setInt(6, limit);
                }
            }
            
            // Execute
            ResultSet jobResults = prepStmt.executeQuery();
            
            while (jobResults.next())
            {
                // Instantiate job
                Job job = new Job();
                job.setId(jobResults.getInt("id"));
                job.setTitle(jobResults.getString("title"));
                job.setEmployer(jobResults.getString("employer"));
                job.setDescription(jobResults.getString("description"));
                job.setURL(jobResults.getString("url"));
                job.setSource(jobResults.getString("source"));
                job.setMillisTimestamp(jobResults.getTimestamp("timestamp").getTime());
                job.setCity(jobResults.getString("city"));
                
                // Add to list
                recentJobs.add(job);
            }

            prepStmt.close();
            connection.close();
        }
        catch (SQLException ex) 
        {
            Logger.getLogger(ConcreteDBConnector.class.getName()).log(Level.SEVERE, null, ex);
        }
        return recentJobs;
    }
    
    public boolean deleteOldJobs()
    {
        try 
        {
            Connection connection = getConnection();
            String socialQuery       = "DELETE FROM jobseekerdb.socialjobs WHERE `timestamp` < (NOW() - INTERVAL ? DAY);";
            String conventionalQuery = "DELETE FROM jobseekerdb.jobs       WHERE `timestamp` < (NOW() - INTERVAL ? DAY);";

            PreparedStatement prepSocialStmt = connection.prepareStatement(socialQuery);
            PreparedStatement prepConvStmt   = connection.prepareStatement(conventionalQuery);

            prepSocialStmt.setInt(1, DAY_AGE_TO_DELETE);
            prepConvStmt.setInt(  1, DAY_AGE_TO_DELETE);

            boolean success = prepSocialStmt.execute() && prepConvStmt.execute();
            
            prepSocialStmt.close();
            prepConvStmt.close();
            connection.close();
            
            return success;
        }
        catch (SQLException ex) 
        {
            Logger.getLogger(ConcreteDBConnector.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
}
