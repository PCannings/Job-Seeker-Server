package itp.team1.jobseekerserver;

import itp.team1.jobseekerserver.sources.FacebookSource;
import itp.team1.jobseekerserver.sources.TwitterSource;

import com.google.gson.Gson;
import itp.team1.jobseekerserver.sources.GuardianSource;
import itp.team1.jobseekerserver.sources.IndeedSource;
import itp.team1.jobseekerserver.sources.YagaSource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Calum
 */
@WebServlet(name = "JobSeekerServer", urlPatterns = { "/search", "/search/*" })
public class JobSeekerServlet extends HttpServlet 
{
    private static final String SEARCH_ALL_STRING = "/search";
    private static final String SEARCH_SOCIAL_STRING = SEARCH_ALL_STRING + "/social";
    private static final String SEARCH_JOBSITES_STRING = SEARCH_ALL_STRING + "/jobsites";
    
    public JobSeekerServlet()
    {
        super();
    }
    
    /**
     * Handles the HTTP <code>GET</code> method.
     * -----RESTful Interface/API:-----
     * GET /search/social
     * GET /search/jobsites 
     * GET /search  (ALL)
     * Query Params: offset, limit, location,  (title, hours, type, industry)
     * ---------------------------------
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException
    {
        List<Job> allJobListings   = new ArrayList<Job>();
        List<Job> socialJobs       = new ArrayList<Job>();
        List<Job> conventionalJobs = new ArrayList<Job>();
        response.setContentType("application/json");
        
        // DB Instantiation
        ConcreteDBConnector database = new ConcreteDBConnector();
        
        // Get URI Details
        String contextPath = request.getContextPath();
        String requestURI = request.getRequestURI();
        
        // Parse query params - meta + Filters etc.
        int offset      = -1;
        int limit       = -1;
        String keyword    = "";
        String location = "";
        String hours    = "";
        String industry = "";
        String employer = "";
        String type     = "";
        String[] offsetParam    = request.getParameterValues("offset");
        if (offsetParam != null)    offset = Integer.parseInt(offsetParam[0]);
        String[] limitParam = request.getParameterValues("limit");
        if (limitParam != null)     limit = Integer.parseInt(limitParam[0]);
        String[] keywordParam    = request.getParameterValues("keyword");
        if (keywordParam != null)    keyword = (keywordParam[0] == null) ? "" : keywordParam[0];
        String[] locationParam = request.getParameterValues("location");
        if (locationParam != null) location = (locationParam[0] == null) ? "" : locationParam[0];
//        String[] hoursParams    = request.getParameterValues("hours");
//        if (hoursParams != null)    hours = hoursParams[0];
//        String[] industryParams = request.getParameterValues("industry");
//        if (industryParams != null) industry = industryParams[0];
//        String[] employerParams = request.getParameterValues("employer");
//        if (employerParams != null) employer = employerParams[0];
//        String[] typeParams     = request.getParameterValues("type");
//        if (typeParams != null)     type = typeParams[0];

        // Parse URI to implement RESTful interface...
        // TODO: Use Source superclass to iterate polymorphically over specific Source types
        if (requestURI.equals(contextPath + SEARCH_ALL_STRING))
        {                
            // Search ALL sites...
            
            if (offset == -1) // Daily "BIG" update - None specified
            {
                // 1. Get ALL results from ALL sources
                socialJobs.addAll(FacebookSource.retrieveAllJobs(location, limit)); // TODO: Get filters from query string
                socialJobs.addAll(TwitterSource.retrieveAllJobs( location, limit));
                conventionalJobs.addAll(IndeedSource.retrieveAllJobs(location, limit));
                conventionalJobs.addAll(GuardianSource.retrieveAllJobs(location, limit));
                conventionalJobs.addAll(YagaSource.retrieveAllJobs(location, limit));   // Only add Yaga for daily update.  Limited API calls.
                
                // 2. Add to DB (batch insert)
                database.insertSocialJobs(socialJobs);
                database.insertConventionalJobs(conventionalJobs);
                
//                allJobListings.addAll(socialJobs);
//                allJobListings.addAll(conventionalJobs);
                return; // Don't return any jobs to client
            }
            else if (offset == 0) // App refresh
            {
                // 1. Get new results from ALL sources (should be one location only)
                socialJobs.addAll(FacebookSource.retrieveAllJobs(location, limit));
                socialJobs.addAll(TwitterSource.retrieveAllJobs(location, limit));
                conventionalJobs.addAll(IndeedSource.retrieveAllJobs(location, limit));

                // 2. Insert to DB
                database.insertSocialJobs(socialJobs);
                database.insertConventionalJobs(conventionalJobs);

                // 3. Retrieve <limit> latest results
                allJobListings.addAll(database.getRecentSocialJobs(offset, limit, location, keyword));
                allJobListings.addAll(database.getRecentConventionalJobs(offset, limit, location, keyword));
            }
            else // Fetch older results - no fetch needed - database only
            {
                // 1. Retrieve latest <offset> - <limit> results from DB
                allJobListings.addAll(database.getRecentSocialJobs(offset, limit, location, keyword));
                allJobListings.addAll(database.getRecentConventionalJobs(offset, limit, location, keyword));

            }
        }
        else if (requestURI.equals(contextPath + SEARCH_SOCIAL_STRING))
        {
            // Search Social sites only (FB, Twitter, LI, GumTree)...
            
            if (offset == -1)    // Daily "BIG" update - add to DB only
            {
                // 1. Get ALL results from SOCIAL sources
                socialJobs.addAll(FacebookSource.retrieveAllJobs(location, limit)); // TODO: Get filters from query string
                socialJobs.addAll(TwitterSource.retrieveAllJobs(location, limit));
                
                // 2. Add to DB (batch insert)
                database.insertSocialJobs(socialJobs);
                
                // Don't return results - add to database only
//                allJobListings.addAll(socialJobs);
                return;
            }
            else if (offset == 0)   // App refresh - fetch new and get from db
            {
                // 1. Get new results from SOCIAL sources
                socialJobs.addAll(FacebookSource.retrieveAllJobs(location, limit));
                socialJobs.addAll(TwitterSource.retrieveAllJobs(location, limit));

                // 2. Insert to DB
                database.insertSocialJobs(socialJobs);
                
                // 3. Retrieve <limit> latest results
                allJobListings.addAll(database.getRecentSocialJobs(offset, limit, location, keyword));
            }
            else    // Old results - query DB only
            {
                // 1. Retrieve latest <offset> - <limit> results from DB
                allJobListings.addAll(database.getRecentSocialJobs(offset, limit, location, keyword));
            }
        }
        else if (requestURI.equals(contextPath + SEARCH_JOBSITES_STRING))
        {
            // Search Job Sites sites only (Indeed, Monster, S1jobs, )...
            
            if (offset == -1)    // Daily "BIG" update - add to DB only
            {
                // 1. Get ALL results from JOBSITE sources
                conventionalJobs.addAll(IndeedSource.retrieveAllJobs(location, limit)); // TODO: Get filters from query string
                conventionalJobs.addAll(GuardianSource.retrieveAllJobs(location, limit));
                conventionalJobs.addAll(YagaSource.retrieveAllJobs(location, limit));   // Only add Yaga for daily update.  Limited API calls.

                // 2. Add to DB (batch insert)
                database.insertConventionalJobs(conventionalJobs);
                
                // Don't return results - add to database only
//                allJobListings.addAll(conventionalJobs);
                return;
            }
            else if (offset == 0)   // App refresh - fetch new and add to db
            {
                // 1. Get new results from JOB sources
                conventionalJobs.addAll(IndeedSource.retrieveAllJobs(location, limit));

                // 2. Insert to DB
                database.insertConventionalJobs(conventionalJobs);
                
                // 3. Retrieve <limit> latest results
                allJobListings.addAll(database.getRecentConventionalJobs(offset, limit, location, keyword));
            }
            else    // Old jobs - query DB only
            {
                // 1. Retrieve latest <offset> - <limit> results from DB
                allJobListings.addAll(database.getRecentConventionalJobs(offset, limit, location, keyword));
            }        
        }
        else    // URL error
        {
            response.getWriter().print("{\"error\":{\"URL Malformed\"}");
            return;
        }
        
        // Serialise jobs to json to return
        // TODO: Add Status Message ?
        Gson gson = new Gson();
        String allJobsJson = "{\"jobs\":" + gson.toJson(allJobListings) + "}";
        response.getWriter().print(allJobsJson);
        System.gc();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException
    {
        response.setContentType("text/plain");
        response.getWriter().println("RESTful API:\n" +
                                    "---------------------------\n" +
                                    "GET /search/social (Get JSON array of jobs from social feeds [FB, Twitter]\n" +
                                    "GET /search/jobsites (Get JSON array of jobs from job sites [Indeed, Guardian, S1]\n" +
                                    "GET /search (Get JSON array of jobs from both sources)\n" +
                                    "----------------------------\n" +
                                    "Query Params:\n" +
                                    "?location=...&keyword=...&offset=...&limit=...");
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException 
    {
    }
   
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException 
    {
        // Calls SQL script to delete old jobs
        ConcreteDBConnector database = new ConcreteDBConnector();
        
        database.deleteOldJobs();
    }


}
