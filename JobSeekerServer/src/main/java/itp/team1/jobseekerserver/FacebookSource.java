package itp.team1.jobseekerserver;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

/**
 * Class encapsulating all logic interfacing with Facebook and its APIs ("Graph API" and FQL)
 * @author Calum
 */
public class FacebookSource 
{
    // FB App Credentials
    private static final String APP_ID             = "631489563562471";
    private static final String APP_SECRET         = "ed5d59160530f4be7908fcfc5ef1f7bc";
    private static final String FBAPP_ACCESS_TOKEN = "631489563562471|nuU6UcCwY9mL28PbnozLlvtvtxA";
    
    // Graph API Resources
    private static final String GRAPH_API_URL = "http://graph.facebook.com/"; //...queries
    
    private String[] pages = {"Dundee Jobs | Scotland | UK"};
    
    public List<Job> retrieveJobs()
    {
        List<Job> jobs = new ArrayList<Job>(20);
        List<FacebookPost> listings = new ArrayList<FacebookPost>(20);
        
        // Aggregate all pages results
        for (String page : pages)
        {
            // Make RESTful GET request to Facebook's Graph API.  Returns JSON.
            String uri = GRAPH_API_URL + "fql?q=SELECT+source_id,+created_time,+message,+placeFROM+stream+WHERE+source_id+IN+(SELECT+page_id+FROM+page+WHERE+name+=\"Dundee+Jobs+|+Scotland+|+UK\")+AND+actor_id+IN+(SELECT+page_id+FROM+page+WHERE+name+=\"Dundee+Jobs+|+Scotland+|+UK\")+AND+type+=+46" +
                                         "&access_token=" + FBAPP_ACCESS_TOKEN;
            HttpGet graphGetRequest = new HttpGet(uri);
            
            try 
            {
                // Execute GET request
                HttpClient client = new DefaultHttpClient();
                HttpResponse response = client.execute(graphGetRequest);
                InputStreamReader streamReader = new InputStreamReader(response.getEntity().getContent());
                
                // Parse JSON into FacebookPost object
                Gson gson = new Gson();
                Type PostListings = new TypeToken<List<FacebookPost>>() {}.getType();
                listings = gson.fromJson(streamReader, PostListings);
                
                // Instantiate Job objects - Add to "jobs" list
                // Parse natural language in post
                for (FacebookPost post : listings)
                {
                    // TODO: Do NLP and more parsing...
                    
                    Job job = new Job();
                    
                }
                
            } 
            catch (IOException ex) 
            {
                Logger.getLogger(FacebookSource.class.getName()).log(Level.SEVERE, null, ex);
            } 
        }
        
        return null;
    }
          
}
