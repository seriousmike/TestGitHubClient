package ru.seriousmike.testgithubclient.ghservice;

import ru.seriousmike.testgithubclient.ghservice.data.TokenRequest;

/**
 * Created by SeriousM on 25.12.2014.
 */
public class GHConfig {
    public static final String PREFERENCES = "basepreffile";
    public static final String USER_AGENT = "SM-Simple-GitHub-Client-Test";
    public static final String AUTH_NOTE = "Simple GitHub Client Test App";
    public static final String[] SCOPES = new String[] {TokenRequest.SCOPE_REPO};
    public static final String OAUTH_WEB_FLOW_URL = "http://github.seriousmike.ru/oauth.php";
    public static final String OAUTH_WEB_FLOW_BASE_URL = "http://github.seriousmike.ru/";
    public static final String OAUTH_WEB_FLOW_SERVICE_BASE_URL = "https://github.com/login";
}
