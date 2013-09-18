package itp.team1.jobseekerserver;

import itp.team1.jobseekerserver.facebook.FacebookSource;
import com.google.gson.Gson;
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
     * Query Params: title, location, radius, hours, type, industry, n (number)
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
        List<Job> allJobListings = new ArrayList<Job>();
        response.setContentType("application/json");
        
        // Get URI Details
        String contextPath = request.getContextPath();
        String requestURI = request.getRequestURI();
        
        // Parse Filters etc.
        int n = 20; 
        int radius = 10000;
        String title    = "";
        String location = "dundee";
        String hours    = "";
        String industry = "";
        String employer = "";
        String type     = "";
        String[] nParam = request.getParameterValues("n");
        if (nParam != null) n = Integer.parseInt(nParam[0]);
        String[] titleParams    = request.getParameterValues("title");
        if (titleParams != null) title = titleParams[0];
        String[] locationParams = request.getParameterValues("location");
        if (locationParams != null) location = locationParams[0];
        String[] hoursParams    = request.getParameterValues("hours");
        if (hoursParams != null) hours = hoursParams[0];
        String[] industryParams = request.getParameterValues("industry");
        if (industryParams != null) industry = industryParams[0];
        String[] employerParams = request.getParameterValues("employer");
        if (employerParams != null) employer = employerParams[0];
        String[] typeParams     = request.getParameterValues("type");
        if (typeParams != null) type = typeParams[0];

        // Parse URI to implement RESTful interface...
        // TODO: Use Source superclass to iterate polymorphically over specific Source types
        if (requestURI.equals(contextPath + SEARCH_ALL_STRING))
        {           
            // Search ALL sites...
            allJobListings.addAll(FacebookSource.retrieveJobs(n, title, location, radius)); // TODO: Get filters from query string

        }
        else if (requestURI.equals(contextPath + SEARCH_SOCIAL_STRING))
        {
            // Search Social sites only (FB, Twitter, LI, GumTree)...
            
            allJobListings.addAll(FacebookSource.retrieveJobs(n, title, location, radius)); // TODO: Get filters from query string
            // Twitter.retrieveJobs
            // LinkedIn.retrieveJobs
        }
        else if (requestURI.equals(contextPath + SEARCH_JOBSITES_STRING))
        {
            // Search Job Sites sites only (Indeed, Monster, S1jobs, )...
            
            // Indeed.retrieveJobs
        }
        else
            return;
        
        // Serialise jobs to json to return
        // TODO: Add Message
        Gson gson = new Gson();
        String allJobsJson = gson.toJson(allJobListings);
        response.getWriter().print(allJobsJson);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException
    {
        response.setContentType("text/plain");
        response.getWriter().println("RESTful API:\n" +
                                    "---------------------------\n" +
                                    "GET /search/social (Get JSON array of jobs from social feeds [FB, Twitter]\n" +
                                    "GET /search/jobsites (Get JSON array of jobs from job sites [Indeed]\n" +
                                    "GET /search (Get JSON array of jobs from both sources)\n" +
                                    "----------------------------\n" +
                                    "Query Params:\n" +
                                    "?q=...&location=...&hours=...&industry=...&type=...&employer=...");
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
    }

}
