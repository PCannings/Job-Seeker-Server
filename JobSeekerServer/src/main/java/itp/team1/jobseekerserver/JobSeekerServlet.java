package itp.team1.jobseekerserver;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Calum
 */
@WebServlet(name = "JobSeekerServlet", urlPatterns = { "/search", "/search/*" })
public class JobSeekerServlet extends HttpServlet 
{
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
        // Get URI Details
        String contextPath = request.getContextPath();
        String requestURI = request.getRequestURI();
        String queryParams = request.getQueryString();  // Filters etc.
        response.getOutputStream().println("It Works!\nRequest URI: " + requestURI +
                                                  "\nQuery Params: " + queryParams);
    
        if (requestURI.equals(contextPath + "/search")
            || 
            requestURI.equals(contextPath + "/search/"))
	{           
            // Search ALL sites
            response.getOutputStream().println("ALL");
        }
        else if (requestURI.equals(contextPath + "/search/social")
                || 
                requestURI.equals(contextPath + "/search/social/"))
        {
            // Search Social sites only (FB, Twitter, LI, GumTree)
            // TODO: Use Source superclass to iterate polymorphically over specific Source types
            response.getOutputStream().println("SOCIAL");
            FacebookSource.retrieveJobs(0, "manager", "Dundee", 0); // TODO: Get filters from query string
            // Twitter.retrieveJobs
            // LinkedIn.retrieveJobs
        }
        else if (requestURI.equals(contextPath + "/search/jobsites")
                || 
                requestURI.equals(contextPath + "/search/jobsites/"))
        {
            // Search Job Sites sites only (Indeed, Monster, S1jobs, )
            response.getOutputStream().println("JOBSITES");
            // Indeed.retrieveJobs
        }
        else
            return;
        
        // TODO: Returns json array of n "Job" listings.

    
    }

    /**
     * Handles the HTTP
     * <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException
    {
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
