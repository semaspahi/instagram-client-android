package com.instagram.instagram.service.model;

import java.util.List;

public class Recent {

    private Pagination pagination;
    private List<Media> data;

    public Pagination getPagination() {
        return pagination;
    }

    public List<Media> getMediaList() {
        return data;
    }

}
