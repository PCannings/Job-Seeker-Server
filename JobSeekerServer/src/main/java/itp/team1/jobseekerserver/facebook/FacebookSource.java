package itp.team1.jobseekerserver.facebook;

import com.google.gson.Gson;
import itp.team1.jobseekerserver.Job;
import java.io.IOException;
import java.io.InputStreamReader;
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
    private static final String FBAPP_ACCESS_TOKEN = "631489563562471|nuU6UcCwY9mL28PbnozLlvtvtxA";
    
    // Graph API Resources
    private static final String GRAPH_API_URL = "https://graph.facebook.com/"; //...queries
    private static final String POSTS_FQL_STATEMENT = "SELECT message, created_time, attachment FROM stream WHERE source_id = 'page_id' AND (type = 46 OR type = 80);";
    private static final String PAGES_GRAPH_STATEMENT = "search?q=%22Dundee+job%22&type=page";

            
    // Regex/Parsing
    private static final String LINK_REGEX = "((([A-Za-z]{3,9}:(?:\\/\\/)?)(?:[-;:&=\\+\\$,\\w]+@)?[A-Za-z0-9.-]+|(?:www.|[-;:&=\\+\\$,\\w]+@)[A-Za-z0-9.-]+)((?:\\/[\\+~%\\/.\\w-_]*)?\\??(?:[-\\+=&;%@.\\w_]*)#?(?:[.\\!\\/\\\\w]*))?)";
    private static final String JOB_REGEX  = "job|vacanc|hiring|experience|work|full-time|part-time|er|tor|cian|tant|ist|tive";
    
    // Static method to encapsulate process of returning list of jobs to servlet
    // Delegates to "findRelevantFBPages" and "findPostsInPage"
    public static List<Job> retrieveJobs(int n, String title, String location, int radius /* Job.Type type, Job.Industry industry, Job.Hours hours*/)
    {
        List<Job> jobs = new ArrayList<Job>(n);
        
        // Find all relevant pages
        List<FacebookPage> pages = findRelevantFBPages(location, radius);
        
        // Aggregate all pages results - Iterate over each page
        ListIterator<FacebookPage> iter = pages.listIterator();
        while (iter.hasNext())
        {
            // Make RESTful GET request to Facebook's Graph API.  Returns JSON posts in each page...
            FacebookPage page = iter.next();
            List<FacebookJobPost> fbPageJobLstings = findPostsInPage(page);
            
            // Instantiate "Job" objects from "FacebookJobPost"s - Add to "jobs" list
            for (FacebookJobPost post : fbPageJobLstings)
            {
                // TODO: Do NLP and more parsing...
                // TODO: Extract employer, title, link 
                Job job = new Job();
                
                String attachmentName = post.getAttachment().getName();
                String postMessage = (attachmentName == null) ? post.getMessage() : attachmentName;

                // Check for words e.g. "job"
                if (!matchesPattern(Pattern.compile(JOB_REGEX), postMessage) ||
                        postMessage.contains("Hi") || postMessage.contains("hi"))
                    continue;
                    
                // Extract link
                String link = "";
                String attachmentLink = post.getAttachment().getHref();
                Pattern pattern = Pattern.compile(LINK_REGEX);
                Matcher matcher = pattern.matcher(postMessage);
                while (matcher.find()) 
                    link = matcher.group(); 
                link = (attachmentLink == null) ? link : attachmentLink;
                
                if (link.equals(""))
                    continue;
                
                // Create job
                job.setDescription(postMessage);
                job.setURL(link);
                job.setSource("facebook");
                job.setTimestamp(post.getCreated_time());
                jobs.add(job);
            }
            
        }
        return jobs;
    }

    // Get all PAGES in "location" e.g. Dundee
    private static List<FacebookPage> findRelevantFBPages(String location, int radius) 
    {
        FacebookPageResponse pageResponse = null;

        try 
        {
            String pagesUri = GRAPH_API_URL + "search?q=%22" + location + "+job%22&type=page&access_token=" + URLEncoder.encode(FBAPP_ACCESS_TOKEN, "UTF-8");

            HttpGet getPagesRequest = new HttpGet(pagesUri);

            // Execute GET request
            HttpClient client = new DefaultHttpClient();
            HttpResponse response = client.execute(getPagesRequest);
            InputStreamReader streamReader = new InputStreamReader(response.getEntity().getContent());
            
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
        
    // Find job posts within the page name
    private static List<FacebookJobPost> findPostsInPage(FacebookPage page)
    {
        FacebookJobResponse jobJsonResponse;
        List<FacebookJobPost> listings = new ArrayList<FacebookJobPost>();
     
        try 
        {
            String uri = GRAPH_API_URL + "fql?q=" + URLEncoder.encode("SELECT message, created_time, attachment FROM stream WHERE source_id = " + page.getId() + " AND (type = 46 OR type = 80)", "UTF-8") + "&access_token=" + URLEncoder.encode(FBAPP_ACCESS_TOKEN, "UTF-8");
            HttpGet graphGetRequest = new HttpGet(uri);

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
    
    // Checks if sentence matches a regex p.  
    // Adapted from http://stackoverflow.com/questions/9515635/using-java-regex-how-to-check-if-a-string-contains-any-of-the-words-in-a-set
    private static boolean matchesPattern(Pattern p, String sentence) 
    {
        return p.matcher(sentence).find();
    }
    
}
