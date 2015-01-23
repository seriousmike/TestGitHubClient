package ru.seriousmike.testgithubclient.ghservice;


import java.util.List;

import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;
import ru.seriousmike.testgithubclient.ghservice.data.Commit;
import ru.seriousmike.testgithubclient.ghservice.data.Contributor;
import ru.seriousmike.testgithubclient.ghservice.data.Repository;
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

    @GET("/user/repos")
    void getRepositoriesList(@Query("per_page") int perPage, @Query("page") int currentPage, Callback<List<Repository>> callback);

    @GET("/repos/{owner}/{repo}/commits")
    void getRepositoryCommits(@Path("owner") String owner, @Path("repo") String repo, @Query("per_page") int perPage, @Query("page") int currentPage, Callback<List<Commit>> cb);

}