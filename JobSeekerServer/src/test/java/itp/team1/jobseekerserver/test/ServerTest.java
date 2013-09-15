
package itp.team1.jobseekerserver.test;

import static junit.framework.Assert.assertTrue;
import junit.framework.TestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Calum
 */
public class ServerTest extends TestCase
{
    
    @Before
    public void setUp()
    {
    }
    
    @After
    public void tearDown() 
    {
    }

     @Test //(expected=Exception.class, timeout=20)
     public void test_1() 
     {
         assertTrue(4 == 4);
              
     }
     
     @Test //(expected=Exception.class, timeout=20)
     public void test_2() 
     {
         assertEquals(6, 2*3);
              
     }
}