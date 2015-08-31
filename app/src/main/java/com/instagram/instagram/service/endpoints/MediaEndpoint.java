package com.instagram.instagram.service.endpoints;

import com.instagram.instagram.BuildConfig;
import com.instagram.instagram.service.model.Popular;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.http.GET;
import retrofit.http.Query;

public class MediaEndpoint{

    public static interface MediaService {

        @GET("/media/popular")
        public void getPopular(
                @Query("access_token") String accessToken,
                Callback<Popular> popularCallback);

        @GET("/media/popular")
        public void getPopularPublic(
                @Query("client_id") String clientId,
                Callback<Popular> popularCallback);

    }

    public final MediaService mediaService;

    public MediaEndpoint() {
        final RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(BuildConfig.API_URL).build();
        mediaService = restAdapter.create(MediaService.class);
    }

}
