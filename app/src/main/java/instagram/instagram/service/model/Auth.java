package instagram.instagram.service.model;

/**
 * Created by semaspahi on 29/08/15.
 */
public class Auth {

    private String client_id;
    private String client_secret;
    private String grant_type;
    private String redirect_uri;
    private String code;

    public String getClient_id() {
        return client_id;
    }

    public String getClient_secret() {
        return client_secret;
    }

    public String getGrant_type() {
        return grant_type;
    }

    public String getRedirect_uri() {
        return redirect_uri;
    }

    public String getCode() {
        return code;
    }

    public Auth(String client_id, String client_secret, String grant_type, String redirect_uri, String code) {
        this.client_id = client_id;
        this.client_secret = client_secret;
        this.grant_type = grant_type;
        this.redirect_uri = redirect_uri;
        this.code = code;
    }
}
