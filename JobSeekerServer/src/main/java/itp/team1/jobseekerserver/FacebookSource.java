package itp.team1.jobseekerserver;

import java.util.List;

/**
 * Class encapsulating all logic interfacing with Facebook and its APIs ("Graph API" and FQL)
 * @author Calum
 */
public class FacebookSource 
{
    // FB App Credentials
    public static final String APP_ID             = "631489563562471";
    public static final String APP_SECRET         = "ed5d59160530f4be7908fcfc5ef1f7bc";
    public static final String FBAPP_ACCESS_TOKEN = "631489563562471|nuU6UcCwY9mL28PbnozLlvtvtxA";
    
    // Graph API Resources
    public static final String GRAPH_API_URL = "http://graph.facebook.com/"; //...queries
    
    
    public List<Job> retrieveJobs()
    {
        return null;
    }
}
