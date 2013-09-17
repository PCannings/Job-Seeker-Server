
package itp.team1.jobseekerserver.test;

import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;
import com.meterware.servletunit.ServletRunner;
import com.meterware.servletunit.ServletUnitClient;
import itp.team1.jobseekerserver.JobSeekerServlet;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import junit.framework.TestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 *
 * @author Calum
 */
public class ServerTest extends TestCase
{

    
    JobSeekerServlet servlet;
    
    @Before
    public void setUp()
    {
        servlet = new JobSeekerServlet();
    }
    
    @After
    public void tearDown() 
    {
    }

     @Test //(expected=MalformedURLException.class/*, timeout=20*/)
     public void test_malformedURL() 
     {
         assertTrue(4 == 4);  
     }
     
     @Test //(expected=Exception.class, timeout=20)
     public void test_2() 
     {
         assertEquals(6, 2*3);
              
     }
     
     @Test
     public void test_servletRuns()  
     {
//        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
//        HttpServletResponse mockResponse = mock(HttpServletResponse.class);
//        ServletOutputStream mockOutput = mock(ServletOutputStream.class);
//
//        when(mockResponse.getOutputStream()).thenReturn(mockOutput);
//
//        new JobSeekerServlet().doGet(mockRequest, mockResponse);
//
//        verify(mockResponse).setContentType("plain/text");
//        verify(mockOutput).println("Hi Nemo!");


//        try {
//            WebConversation wc = new WebConversation();
//            WebResponse   resp = wc.getResponse("http://localhost:8080/JobSeekerServer/");
//            String text = resp.getText();
//            System.out.println(text);
////            ServletRunner sr = new ServletRunner();    
////            sr.registerServlet("jobSeekerServlet", JobSeekerServlet.class.getName());
////            ServletUnitClient client = sr.newClient();          
////        
////            WebRequest request = new GetMethodWebRequest("http://localhost:8080/JobSeekerServer/");
////            WebResponse response = client.getResponse(request);
////
////            assertNotNull("No response received", response);
////            assertEquals("content type", "application/json", response.getContentType());
//        } catch (Exception ex) {
//            System.out.println(ex.getMessage());
//            fail("Exception in test_servletRuns");
//        }
    }
}