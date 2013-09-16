package itp.team1.jobseekerserver.facebook;

import java.util.List;

/**
 *
 * @author Calum
 */
public class FacebookPageResponse 
{
    private List<FacebookPage> data;
    private FacebookPaging paging;
    
    public List<FacebookPage> getData() {
        return data;
    }

    public void setData(List<FacebookPage> data) {
        this.data = data;
    }

    public FacebookPaging getPaging() {
        return paging;
    }

    public void setPaging(FacebookPaging paging) {
        this.paging = paging;
    }
    
    
}
