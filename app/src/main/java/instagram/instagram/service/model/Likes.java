package instagram.instagram.service.model;

import java.util.List;

public class Likes {

    private Integer count;
    private List<User> data;

    public Integer getCount() {
        return count;
    }

    public List<User> getUsers() {
        return data;
    }

}
