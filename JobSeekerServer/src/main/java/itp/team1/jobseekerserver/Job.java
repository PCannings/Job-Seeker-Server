package itp.team1.jobseekerserver;

import java.io.Serializable;

/**
 * POJO Entity class/bean POD, representing a "Job" listing.
 * @author Calum
 */
public class Job implements Serializable
{
    private String employer;
    private String title;
    private String location;
    private String openingDate;
    private String description;
    private String externalLink;
    private String source;
    //    private String closingDate;
    //    private String hours;
    //    private String industry;
    //    private String type
    //    private String
    
    // -----Getters & Setters------

    public String getEmployer() {
        return employer;
    }

    public void setEmployer(String employer) {
        this.employer = employer;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getOpeningDate() {
        return openingDate;
    }

    public void setOpeningDate(String openingDate) {
        this.openingDate = openingDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getExternalLink() {
        return externalLink;
    }

    public void setExternalLink(String externalLink) {
        this.externalLink = externalLink;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

}
