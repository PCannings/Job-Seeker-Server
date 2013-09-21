package itp.team1.jobseekerserver.sources;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import itp.team1.jobseekerserver.Job;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Calum
 */
public class IndeedSource 
{
    public static List<Job> retrieveAllJobs(String location, int limit)
    {
        List<Job> indeedJobs = new ArrayList<Job>();
        StringBuilder jsonBuilder = new StringBuilder("");
        try 
        {
            String query = "l=" + ((location.equals("")) ? "all" : location);
                    
            // url should obviously be a local location
            URL indeedUrl =  new URL("http://euanmorrison.co.uk/ac4/indeed/indeed.php?"+query);
            URLConnection tc = indeedUrl.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(tc.getInputStream()));
            String inputLine;

            // Read output of php
            while ((inputLine = in.readLine()) != null) 
                jsonBuilder.append(inputLine);
            in.close();
            
            // Deserialise JSON into List<Job>
            Gson gson = new Gson();
            Type JobListType = new TypeToken<List<Job>>(){}.getType();
            indeedJobs = gson.fromJson(jsonBuilder.toString(), JobListType);
        } 
        catch (IOException ex) 
        {
            Logger.getLogger(IndeedSource.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return indeedJobs;
    }
}
