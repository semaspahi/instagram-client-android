package instagram.instagram.service.model;

import java.util.List;

public class Comments {

    private Integer count;
    private List<Comment> data;

    public int getCount() {
        return count;
    }

    public List<Comment> getComments() {
        return data;
    }

}
