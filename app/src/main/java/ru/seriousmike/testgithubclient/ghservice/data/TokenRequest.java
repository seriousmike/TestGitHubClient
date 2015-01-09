package ru.seriousmike.testgithubclient.ghservice.data;

import ru.seriousmike.testgithubclient.ghservice.GHConfig;

/**
 * Created by SeriousM on 01.01.2015.
 * Объект для запроса разрешений, передаваемый на https://api.github.com/authorizations
 */
public class TokenRequest {

    public static final String SCOPE_REPO = "repo";

    public String[] scopes;
    public final String note = GHConfig.AUTH_NOTE;

    public TokenRequest(String[] setScopes) {
        scopes = setScopes;
    }
}
