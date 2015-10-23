package com.instagram.instagram.service.endpoints;

import com.instagram.instagram.BuildConfig;
import com.instagram.instagram.service.model.RecentByTag;

import retrofit.Call;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

public class TagsEndpoint {

    public static interface TagsService {

        @GET("tags/{tag_name}/media/recent")
        Call<RecentByTag> getRecent(
                @Path("tag_name") String tagName,
                @Query("client_id") String clientId,
                @Query("min_id") String minId,
                @Query("max_id") String maxId
        );

        @GET("tags/{tag_name}/media/recent")
        Call<RecentByTag> getRecent(
                @Path("tag_name") String tagName,
                @Query("client_id") String clientId
        );
    }

    public final TagsService tagsService;

    public TagsEndpoint() {
        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        tagsService = retrofit.create(TagsService.class);
    }

    public Call<RecentByTag> getPopular(String search, String maxId) {
        return tagsService.getRecent(search, BuildConfig.CLIENT_ID, null, maxId);
    }

    public Call<RecentByTag> getPopular(String search) {
        return tagsService.getRecent(search, BuildConfig.CLIENT_ID);
    }

}
