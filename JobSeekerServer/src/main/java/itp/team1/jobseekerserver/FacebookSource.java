package itp.team1.jobseekerserver;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLEncoder;
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
    
    private static String[] pages = {"Dundee+Jobs+|+Scotland+|+UK"};
    
    public static List<Job> retrieveJobs(int n, String title, String location, int radius /* Job.Type type, Job.Industry industry, Job.Hours hours*/)
    {
        List<Job> jobs = new ArrayList<Job>(20);
        FacebookResponse jsonResponse;
        List<FacebookPost> listings = new ArrayList<FacebookPost>(n);
        
        // Aggregate all pages results
        for (String page : pages)
        {
            // Make RESTful GET request to Facebook's Graph API.  Returns JSON.
//            String uri = GRAPH_API_URL + "fql?q=" + URLEncoder.encode("SELECT source_id, created_time, message, place FROM stream WHERE source_id IN (SELECT page_id FROM page WHERE name =\"Dundee Jobs | Scotland | UK\") AND actor_id IN (SELECT page_id FROM page WHERE name =\"Dundee Jobs | Scotland | UK\") AND type = 46", "ISO-8859-1")
//                                    + "&access_token=" + FBAPP_ACCESS_TOKEN;
                
            HttpGet graphGetRequest = new HttpGet("https://graph.facebook.com/fql?q=SELECT+source_id%2C+created_time%2C+message%2C+place+FROM+stream+WHERE+source_id+IN+%28SELECT+page_id+FROM+page+WHERE+name+%3D%22Dundee+Jobs+%7C+Scotland+%7C+UK%22%29+AND+actor_id+IN+%28SELECT+page_id+FROM+page+WHERE+name+%3D%22Dundee+Jobs+%7C+Scotland+%7C+UK%22%29+AND+type+%3D+46&access_token=631489563562471%7CnuU6UcCwY9mL28PbnozLlvtvtxA");
                      
            try 
            {
                // Execute GET request
                HttpClient client = new DefaultHttpClient();
                HttpResponse response = client.execute(graphGetRequest);
                InputStreamReader streamReader = new InputStreamReader(response.getEntity().getContent());

//                JsonParser parser = new JsonParser();
//                JsonElement element = parser.parse(streamReader);
//                element.get
                // Parse JSON into FacebookPost object
                Gson gson = new Gson();
//                Type PostListings = new TypeToken<List<FacebookPost>>() {}.getType();
                jsonResponse = (FacebookResponse) gson.fromJson(streamReader, FacebookResponse.class);
                
                // Instantiate Job objects - Add to "jobs" list
                // Parse natural language in post
                for (FacebookPost post : jsonResponse.getData())
                {
                    // TODO: Do NLP and more parsing...
                    
                    Job job = new Job();
                    job.setDescription(post.getMessage());
                    jobs.add(job);
                }
            } 
            catch (IOException ex) 
            {
                Logger.getLogger(FacebookSource.class.getName()).log(Level.SEVERE, null, ex);
                return null;
            } 
        }
        return jobs;
    }
          
}
