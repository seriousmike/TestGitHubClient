package ru.seriousmike.testgithubclient.ghservice;


import java.util.List;

import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.POST;
import retrofit.http.Path;
import ru.seriousmike.testgithubclient.ghservice.data.Contributor;
import ru.seriousmike.testgithubclient.ghservice.data.TokenRequest;
import ru.seriousmike.testgithubclient.ghservice.data.AuthorizationResult;
import ru.seriousmike.testgithubclient.ghservice.data.UserInfo;

/**
 * Created by SeriousM on 30.12.2014.
 */
public interface GitHub {

    @GET("/repos/{owner}/{repo}/contributors")
    void contributors(
            @Path("owner") String owner,
            @Path("repo") String repo,
            Callback<List<Contributor>> cb
    );

    @POST("/authorizations")
    void createToken(@Header("Authorization") String authorization, @Body TokenRequest request, Callback<AuthorizationResult> callback);

    @GET("/authorizations")
    void checkAuth(@Header("Authorization") String authorization, Callback<List<AuthorizationResult>> callback);

    @GET("/user")
    void getBasicUserInfo(Callback<UserInfo> callback);

}