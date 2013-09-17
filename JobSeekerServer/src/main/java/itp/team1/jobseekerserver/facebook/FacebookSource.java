package itp.team1.jobseekerserver.facebook;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import itp.team1.jobseekerserver.Job;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    private static final String FBAPP_ACCESS_TOKEN = "631489563562471%7CnuU6UcCwY9mL28PbnozLlvtvtxA";
    
    // Graph API Resources
    private static final String GRAPH_API_URL = "https://graph.facebook.com/"; //...queries
    
    private static final String LINK_REGEX = "((([A-Za-z]{3,9}:(?:\\/\\/)?)(?:[-;:&=\\+\\$,\\w]+@)?[A-Za-z0-9.-]+|(?:www.|[-;:&=\\+\\$,\\w]+@)[A-Za-z0-9.-]+)((?:\\/[\\+~%\\/.\\w-_]*)?\\??(?:[-\\+=&;%@.\\w_]*)#?(?:[.\\!\\/\\\\w]*))?)";
    private static final String JOB_REGEX  = "job|vacanc|hiring";
    
    public static List<Job> retrieveJobs(int n, String title, String location, int radius /* Job.Type type, Job.Industry industry, Job.Hours hours*/)
    {
        List<Job> jobs = new ArrayList<Job>(n);
        
        // Find all relevant pages
        List<FacebookPage> pages = findRelevantFBPages(location, radius);
        
        // Aggregate all pages results - Iterate over each page
        ListIterator<FacebookPage> iter = pages.listIterator();
        while (iter.hasNext())
        {
            // Make RESTful GET request to Facebook's Graph API.  Returns JSON.
            
            String page = iter.next().getName();
            List<FacebookJobPost> fbPageJobLstings = findJobs(page);
            
            // Instantiate "Job" objects from "FacebookJobPost"s - Add to "jobs" list
            for (FacebookJobPost post : fbPageJobLstings)
            {
                // TODO: Do NLP and more parsing...
                // TODO: Extract employer, title, link 
                Job job = new Job();
                String postMessage = post.getMessage();

                // Check for words e.g. "job"
                if (!postMessage.toLowerCase().contains("job"))
                    continue;
                    
                // Extract link
                String link = "";
                Pattern pattern = Pattern.compile(LINK_REGEX);
                Matcher matcher = pattern.matcher(postMessage);

                while (matcher.find()) 
                    link = matcher.group(); 
                job.setDescription(post.getMessage());
                job.setExternalLink(link);
                job.setSource("facebook");
                jobs.add(job);
            }
            
        }
        return jobs;
    }

    private static List<FacebookPage> findRelevantFBPages(String location, int radius) 
    {
        String pagesUri = GRAPH_API_URL + "search?q=%22" + location + "+job%22%7C%22" + location + "+vacancy%22%7C%22" + location + "+hiring%22&type=page&center=-50%2C-2&distance=" + radius + "&access_token=" + FBAPP_ACCESS_TOKEN;
        FacebookPageResponse pageResponse;
        
        HttpGet getPagesRequest = new HttpGet(pagesUri);

        try 
        {
            // Execute GET request
            HttpClient client = new DefaultHttpClient();
            HttpResponse response = client.execute(getPagesRequest);
            InputStreamReader streamReader = new InputStreamReader(response.getEntity().getContent());
//            char[] json = new char[10000];
//            streamReader.read(json);
//            String jsonS = json.toString();
            // Parse JSON into FacebookPageResponse object
            Gson gson = new Gson();
            pageResponse = (FacebookPageResponse) gson.fromJson(streamReader, FacebookPageResponse.class);
            
        }
        catch (IOException ioe)
        {
            Logger.getLogger(FacebookSource.class.getName()).log(Level.SEVERE, null, ioe);
            return null;
        }
        
        // Return pages
        return pageResponse.getData();

    }
          
    private static List<FacebookJobPost> findJobs(String page)
    {
        FacebookJobResponse jobJsonResponse;
        List<FacebookJobPost> listings = new ArrayList<FacebookJobPost>();
        HttpGet graphGetRequest = null;
        try {
            String uri = GRAPH_API_URL + "fql?q=SELECT%20source_id%2C%20created_time%2C%20message%2C%20place%20FROM%20stream%20WHERE%20source_id%20IN%20(SELECT%20page_id%20FROM%20page%20WHERE%20name%20%3D%20%22" + URLEncoder.encode(page, "UTF-8") + "%22)%20AND%20actor_id%20IN%20(SELECT%20page_id%20FROM%20page%20WHERE%20name%20%3D%20%22" + URLEncoder.encode(page, "UTF-8") + "%22)%20AND%20type%20%3D%2046&access_token=" + FBAPP_ACCESS_TOKEN;
            graphGetRequest = new HttpGet(uri);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(FacebookSource.class.getName()).log(Level.SEVERE, null, ex);
        }

                    
        try 
        {
            // Execute GET request
            HttpClient client = new DefaultHttpClient();
            HttpResponse response = client.execute(graphGetRequest);
            InputStreamReader streamReader = new InputStreamReader(response.getEntity().getContent());

            // Parse JSON into FacebookJobPost object
            Gson gson = new Gson();

            jobJsonResponse = (FacebookJobResponse) gson.fromJson(streamReader, FacebookJobResponse.class);
        } 
        catch (IOException ex) 
        {
            Logger.getLogger(FacebookSource.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        } 
        // Return job listings for page
        return jobJsonResponse.getData();
    }
    
}
