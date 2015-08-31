package com.instagram.instagram.service.endpoints;

import com.instagram.instagram.BuildConfig;
import com.instagram.instagram.service.model.RecentByTag;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

public class TagsEndpoint {

    public static interface TagsService {

        @GET("/tags/{tag_name}/media/recent")
        public void getRecentMaxMinPublic(
                @Path("tag_name") String tagName,
                @Query("client_id") String clientId,
                @Query("min_id") String minId,
                @Query("max_id") String maxId,
                Callback<RecentByTag> recentByTagCallback

        );

        @GET("/tags/{tag_name}/media/recent")
        public void getRecentPublic(
                @Path("tag_name") String tagName,
                @Query("client_id") String clientId,
                Callback<RecentByTag> recentByTagCallback

        );
    }

    public final TagsService tagsService;

    public TagsEndpoint() {
        final RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(BuildConfig.API_URL).build();
        tagsService = restAdapter.create(TagsService.class);
    }

}
