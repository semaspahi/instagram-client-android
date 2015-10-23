package com.instagram.instagram.service.endpoints;

import com.instagram.instagram.BuildConfig;
import com.instagram.instagram.service.model.Popular;

import retrofit.Call;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.http.GET;
import retrofit.http.Query;

public class MediaEndpoint {

    public final MediaService mediaService;

    public MediaEndpoint() {

        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        mediaService = retrofit.create(MediaService.class);
    }

    public Call<Popular> getPopular() {
        return mediaService.getPopular(BuildConfig.CLIENT_ID);
    }

    private interface MediaService {

        @GET("media/popular")
        Call<Popular> getPopular(
                @Query("client_id") String accessToken);

    }

}
