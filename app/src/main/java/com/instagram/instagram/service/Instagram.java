package com.instagram.instagram.service;


import com.instagram.instagram.service.endpoints.MediaEndpoint;
import com.instagram.instagram.service.endpoints.TagsEndpoint;

import java.net.URI;
import java.net.URISyntaxException;

public final class Instagram {

    private TagsEndpoint tagsEndpoint;
    private MediaEndpoint mediaEndpoint;

    public Instagram() {
    }

    public MediaEndpoint getMediaEndpoint() {
        if (mediaEndpoint == null) {
            mediaEndpoint = new MediaEndpoint();
        }
        return mediaEndpoint;
    }

    public TagsEndpoint getTagsEndpoint() {
        if (tagsEndpoint == null) {
            tagsEndpoint = new TagsEndpoint();
        }
        return tagsEndpoint;
    }

    public static String requestOAuthUrl(final String clientId, final String redirectUri) throws URISyntaxException {
        final StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append("response_type=").append("token");
        urlBuilder.append("&client_id=").append(clientId);
        urlBuilder.append("&redirect_uri=").append(redirectUri);

        return new URI("https", "instagram.com", "/oauth/authorize", urlBuilder.toString(), null).toString();
    }

    public static String requestOAuthUrlDisabledImplicit(final String clientId, final String redirectUri) throws URISyntaxException {
        final StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append("response_type=").append("code");
        urlBuilder.append("&client_id=").append(clientId);
        urlBuilder.append("&redirect_uri=").append(redirectUri);
        return new URI("https", "instagram.com", "/oauth/authorize", urlBuilder.toString(), null).toString();
    }

}
