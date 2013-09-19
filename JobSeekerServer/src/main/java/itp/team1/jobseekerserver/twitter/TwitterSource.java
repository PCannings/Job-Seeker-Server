package itp.team1.jobseekerserver.twitter;

import itp.team1.jobseekerserver.Job;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class encapsulating all logic interfacing with Twitter and its APIs
 * @author Calum
 */
public class TwitterSource 
{
	
    // IF you want a FULL UPDATE, location = "all"
    // OTHERWISE enter the name of the place.
    public static List<Job> retrieveAllJobs()
    {
        StringBuilder jsonBuilder = new StringBuilder("");
        try 
        {
            String query = "l=dundee";//+location;
                    
            // url should obviously be a local location
            URL twitterUrl = new URL("http://localhost:8080/JobSeekerServer/twitter.php?"+query);
            URLConnection tc = twitterUrl.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(tc.getInputStream()));
            String inputLine;

            
            while ((inputLine = in.readLine()) != null) 
                jsonBuilder.append(inputLine);
            in.close();
        } 
        catch (IOException ex) 
        {
            Logger.getLogger(TwitterSource.class.getName()).log(Level.SEVERE, null, ex);
        }
        //return jsonBuilder.toString();
        return null;
    }
}
