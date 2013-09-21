package itp.team1.jobseekerserver.sources;

import java.io.Serializable;

/**
 * Entity class encapsulating the information gathered (via FQL/Graph API) from a 
 * Facebook Post
 * @author Calum
 */
public class FacebookPost implements Serializable
{
    private long created_time;
    private String message;
    private FacebookAttachment attachment;
    
    public long getCreated_time() {
        return created_time;
    }

    public void setCreated_time(long created_time) {
        this.created_time = created_time;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public FacebookAttachment getAttachment() {
        return attachment;
    }

    public void setAttachment(FacebookAttachment attachment) {
        this.attachment = attachment;
    }
    
    
}
