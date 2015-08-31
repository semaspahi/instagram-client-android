package instagram.instagram.service;


import java.net.URI;
import java.net.URISyntaxException;

import instagram.instagram.service.endpoints.MediaEndpoint;
import instagram.instagram.service.endpoints.TagsEndpoint;

public final class Instagram {
    private MediaEndpoint mediaEndpoint;
    private TagsEndpoint tagsEndpoint;

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
