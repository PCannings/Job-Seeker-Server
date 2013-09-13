package itp.team1.jobseekerserver;

import java.io.Serializable;

/**
 * Entity class encapsulating the information gathered (via FQL/Graph API) from a 
 * Facebook Post
 * @author Calum
 */
public class FacebookPost implements Serializable
{
    private String source_id;
    private String created_time;
    private String message;
    private String place;       // NB: Often null

    public String getSource_id() {
        return source_id;
    }

    public void setSource_id(String source_id) {
        this.source_id = source_id;
    }

    public String getCreated_time() {
        return created_time;
    }

    public void setCreated_time(String created_time) {
        this.created_time = created_time;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }
    
    
}
