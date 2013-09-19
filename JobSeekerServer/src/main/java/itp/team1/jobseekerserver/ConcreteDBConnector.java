
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
    public ConcreteDBConnector()
    {
        super();
    }
    
    public void insertSocialJobs(List<Job> jobs)
    {
        try 
        {
            Connection connection = getConnection();
            String query = "INSERT INTO socialjobs (description, url, `source`, `timestamp`, city) VALUES(?, ?, ?, ?);";
            PreparedStatement prepStmt = connection.prepareStatement(query);
            
            for (Job job : jobs) 
            {
                prepStmt.setString(1, job.getDescription());
                prepStmt.setString(2, job.getURL());
                prepStmt.setString(3, job.getSource());
                prepStmt.setTimestamp(4, new Timestamp(job.getTimestamp()));
                prepStmt.setString(5, job.getCity());

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
            String query = "INSERT INTO jobs (employer, title, city, `timestamp`, url, `source`, description) VALUES(?, ?, ?, ?, ?, ?, ?);";
            PreparedStatement prepStmt = connection.prepareStatement(query);
            
            for (Job job : jobs) 
            {
                prepStmt.setString(1, job.getEmployer());
                prepStmt.setString(2, job.getTitle());
                prepStmt.setString(3, job.getCity());
                prepStmt.setTimestamp(4, new Timestamp(job.getTimestamp()));
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
    
    public List<Job> getRecentSocialJobs(int offset, int limit, String location, String title)
    {
        List<Job> recentJobs = new ArrayList<Job>(limit);
        try 
        {
            Connection connection = getConnection();
            String query = "SELECT * FROM socialjobs GROUP BY(url) ORDER BY `timestamp` DESC LIMIT ?;";
            PreparedStatement prepStmt = connection.prepareStatement(query);

            prepStmt.setInt(1, limit);
            prepStmt.setInt(2, limit);
            
            ResultSet jobResults = prepStmt.executeQuery();
            
            
            while (jobResults.next())
            {
                // Instantiate job
                Job job = new Job();
                job.setDescription(jobResults.getString("description"));
                job.setURL(jobResults.getString("url"));
                job.setSource(jobResults.getString("source"));
                job.setTimestamp(jobResults.getTimestamp("timestamp").getTime());
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
    
    public List<Job> getRecentConventionalJobs(int offset, int limit, String location, String title)
    {
        List<Job> recentJobs = new ArrayList<Job>(limit);
        try 
        {
            Connection connection = getConnection();
            String query = "SELECT * FROM jobs GROUP BY(url) ORDER BY `timestamp` DESC LIMIT ?;";
            PreparedStatement prepStmt = connection.prepareStatement(query);

            prepStmt.setInt(1, limit);
            prepStmt.setInt(2, limit);
            
            ResultSet jobResults = prepStmt.executeQuery();
            
            while (jobResults.next())
            {
                // Instantiate job
                Job job = new Job();
                job.setTitle(jobResults.getString("title"));
                job.setEmployer(jobResults.getString("employer"));
                job.setDescription(jobResults.getString("description"));
                job.setURL(jobResults.getString("url"));
                job.setSource(jobResults.getString("source"));
                job.setTimestamp(jobResults.getTimestamp("timestamp").getTime());
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
    
//    public void deleteOldJobs(int dayAge)
//    {
//        
//    }
    
}
