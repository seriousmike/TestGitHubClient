package ru.seriousmike.testgithubclient.ghservice;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Header;
import retrofit.client.Response;
import ru.seriousmike.testgithubclient.ghservice.data.Commit;
import ru.seriousmike.testgithubclient.ghservice.data.Repository;
import ru.seriousmike.testgithubclient.ghservice.data.RequestCallback;
import ru.seriousmike.testgithubclient.ghservice.data.TokenRequest;
import ru.seriousmike.testgithubclient.ghservice.data.AuthorizationResult;
import ru.seriousmike.testgithubclient.ghservice.data.UserInfo;

/**
 * Singleton-класс для общения с GitHub API
 */
public class GitHubAPI {

    private static final String TAG = "sm_GitHubAPI";

    public static final String VAR_TOKEN = "token";
    public static final String VAR_AUTH_ID = "authorization_id";

    public static final String API_URL = "https://api.github.com";


    private static final String HEADER_LINK = "Link";




    public static final int ERR_CODE_UNAUTH = 401;
    public static final int ERR_CODE_FORBIDDEN = 403;
    public static final int ERR_CODE_CONFLICT = 409;
    public static final int ERR_CODE_UNKONWN_ERROR = 666;
    public static final int ERR_CODE_NO_INTERNET = 665;

    private static final String ERR_MSG_NULL_CONTEXT = "NULL CONTEXT FOUND! Error accessing SharedPreferences";

    private static GitHubAPI sInstance;

    private Context mContext;
    private GitHub mService;
    private String mToken;
    private UserInfo mUserInfo;
    private Response mLastResponseHeaders;


    private GitHubAPI(Context context){

        mContext = context;

        RequestInterceptor requestInterceptor = new RequestInterceptor() {
            @Override
            public void intercept(RequestInterceptor.RequestFacade request) {
                request.addHeader("Cache-Control", "no-cache");
                request.addHeader("Accept", "application/vnd.github.v3.raw+json");
                request.addHeader("Accept-Encoding", "deflate"); // ответ в gzip идет с Transfer-Encoding: chunked и где-то там в библиотеке происходит краш
                request.addHeader("User-Agent", GHConfig.USER_AGENT);
                if(mToken !=null) {
                    request.addHeader("Authorization", "token " + mToken);
                }
            }
        };

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(GitHubAPI.API_URL)
                .setRequestInterceptor(requestInterceptor)
                .build();
        mService = restAdapter.create(GitHub.class);

        if(mContext !=null) {
            if (mToken == null) {
                mToken = mContext.getSharedPreferences(GHConfig.PREFERENCES, Context.MODE_PRIVATE).getString(VAR_TOKEN, null);
                Log.i(TAG,"Got from shared preferences token "+ mToken);
            }
        } else {
            Log.e(TAG, ERR_MSG_NULL_CONTEXT);
        }
    }

    public UserInfo getCurrentUser() {
        return mUserInfo;
    }


    public static GitHubAPI getInstance(Context context) {
        if(sInstance == null) {
            sInstance = new GitHubAPI(context);
        }
        return sInstance;
    }

    public static GitHubAPI getInstance() {
        return sInstance;
    }

    /**
     * получает инфы об авторизованном пользователе
     * @param cb
     */
    public void getBasicUserInfo(final RequestCallback<UserInfo> cb) {

        mService.getBasicUserInfo(new Callback<UserInfo>() {
            @Override
            public void success(UserInfo userInfo, Response response2) {
                mLastResponseHeaders = response2;
                mUserInfo = userInfo;
                //Log.i(TAG, "GOT USER BASIC INFO!");
                //logResponse(response2);
                cb.onSuccess(userInfo);
            }

            @Override
            public void failure(RetrofitError error) {
                cb.onFailure(defineError(error));
            }
        });
    }

    /**
     * получение
     */
    public void getRepositoryCommits(String owner, String repository, int perPage, int currentPage, final RequestCallback<List<Commit>> cb) {
        Log.i(TAG,"getRepositoryCommits "+owner+"/"+repository);
        mService.getRepositoryCommits(owner, repository, perPage, currentPage, new Callback<List<Commit>>() {
            @Override
            public void success(List<Commit> commitList, Response response2) {
                mLastResponseHeaders = response2;
                for(Commit commit : commitList) {
                    Log.i(TAG, commit.toString());
                }
                logResponse(response2);
                cb.onSuccess(commitList);
            }

//            @Override
//            public void success(Response commitList, Response response2) {
//                logResponse(commitList);
//                logBody(commitList);
//            }

            @Override
            public void failure(RetrofitError error) {
                cb.onFailure(defineError(error));
            }
        });
    }



    /**
     * получает список доступных репозиториев
     * @param cb
     */
    public void getRepositoriesList(int perPage, int currentPage, final RequestCallback<List<Repository>> cb) {
        mService.getRepositoriesList(perPage, currentPage, new Callback<List<Repository>>() {
            @Override
            public void success(List<Repository> repositoryList, Response response2) {
                mLastResponseHeaders = response2;
                cb.onSuccess(repositoryList);
                for(Repository repo : repositoryList) {
                    Log.i(TAG, repo.toString());
                }
            }

            @Override
            public void failure(RetrofitError error) {
                cb.onFailure(defineError(error));
            }
        });
    }


    /**
     * просмотр списка существующих авторизаций для данного пользователя
     * @param login логин/пароль нужны для базовой авторизации и получения токена, нигде храниться не будут
     * @param password логин/пароль нужны для базовой авторизации и получения токена, нигде храниться не будут
     * @param cb RequestCallback
     */
    public void getAuthorization(final String login, final String password, final RequestCallback<UserInfo> cb) {
        mToken = null;
        int page = 1;
        try {
            String lgnpsw = login+":"+password;
            final String encodedAuthString = "Basic "+new String( Base64.encode( lgnpsw.getBytes("UTF-8"), Base64.NO_WRAP), "UTF-8" );
            mService.checkAuth(encodedAuthString, page, new Callback<List<AuthorizationResult>>() {

                @Override
                public void success(List<AuthorizationResult> authorizationsList, Response response) {

                    logResponse(response);
                    mLastResponseHeaders = response;

                    if (authorizationsList != null && !authorizationsList.isEmpty()) { // здесь в цикле проверяем доступный список авторизаций на соответствие полей NOTE и SCOPES
                        for (AuthorizationResult auth : authorizationsList) {
                            Log.i(TAG, auth.note + " :: " + auth.id + " :: " + auth.token);
                            if (auth.note != null && auth.note.equals(GHConfig.AUTH_NOTE) && auth.scopes != null && auth.scopes.length == GHConfig.SCOPES.length) {
                                if (Arrays.equals(auth.scopes, GHConfig.SCOPES)) {
                                    Log.i(TAG, "Scopes equal " + Arrays.toString(GHConfig.SCOPES));
                                    mToken = auth.token;
                                    break;
                                } else {
                                    Log.i(TAG, "Scopes UNequal");
                                }
                            } else {
                                Log.i(TAG, "Scopes UNequal");
                            }
                        }
                    }

                    if (mToken != null) {
                        Log.i(TAG, "=== GOT TOKEN " + mToken);
                        writeToken();
                        getBasicUserInfo(cb);
                    } else if(hasLastResponseNextPage()) {
                        int nextPage = getNextPageFromLastResponse();
                        mService.checkAuth(encodedAuthString, nextPage, this);
                    } else {

                        createToken(login, password, new RequestCallback<Response>() {
                            @Override
                            public void onSuccess(Response response) {
                                getBasicUserInfo(cb);
                            }

                            @Override
                            public void onFailure(int error_code) {
                                cb.onFailure(error_code);
                            }
                        });
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    cb.onFailure(defineError(error));
                }
            });
        } catch(UnsupportedEncodingException e) {
            Log.e(TAG, e.getLocalizedMessage() );
        }
    }



    /**
     * Создание нового токена на основе базовой авторизации
     * @param login  логин/пароль нужны для базовой авторизации и получения токена, нигде храниться не будут
     * @param password  логин/пароль нужны для базовой авторизации и получения токена, нигде храниться не будут
     * @param cb RequestCallback
     */
    private void createToken(String login, String password, final RequestCallback<Response> cb) {
        try {
            String lgnpsw = login+":"+password;
            String encodedAuthString = "Basic "+new String( Base64.encode( lgnpsw.getBytes("UTF-8"), Base64.NO_WRAP), "UTF-8" );
            mService.createToken(encodedAuthString, new TokenRequest(new String[]{TokenRequest.SCOPE_REPO}), new Callback<AuthorizationResult>() {
                @Override
                public void success(AuthorizationResult authorizationResult, Response response) {


                    logResponse(response);
                    Log.i(TAG, "==== TOKEN =================== ");
                    Log.i(TAG, "TOKEN: " + authorizationResult.token);
                    Log.i(TAG, "CREATED: " + authorizationResult.created_at);
                    Log.i(TAG, "UPDATED: " + authorizationResult.updated_at);

                    mToken = authorizationResult.token;
                    writeToken();
                    cb.onSuccess(response);
                }

                @Override
                public void failure(RetrofitError error) {
                    cb.onFailure(defineError(error));
                }
            });
        } catch(UnsupportedEncodingException e) {
            Log.e(TAG, e.getLocalizedMessage() );
        }
    }

    /**
     * записывает полученный токен
     */
    private void writeToken() {
        if(mToken!=null) {
            Log.i(TAG,"writing to sharedpref token "+mToken);
            mContext.getSharedPreferences(GHConfig.PREFERENCES, Context.MODE_PRIVATE).edit().putString(VAR_TOKEN, mToken).commit();
        } else {
            Log.e(TAG,"WRITING NO TOKEN");
        }
    }

    /**
     * назначает новый токен
     * @param token - полученная строка токена
     */
    public void setToken(String token) {
        mToken = token;
        writeToken();
    }

    /**
     * логаутит текущего юзера
     */
    public void logout() {
        mUserInfo = null;
        mToken = null;
        mLastResponseHeaders = null;
        mContext.getSharedPreferences(GHConfig.PREFERENCES, Context.MODE_PRIVATE).edit().remove(VAR_TOKEN).commit();
    }

    /**
     * проверяет заголовки последнего запроса на наличие следующей страницы
     */
    public boolean hasLastResponseNextPage() {
        if(mLastResponseHeaders!=null) {
            List<Header> headerList = mLastResponseHeaders.getHeaders();
            for(Header header : headerList) {
                if(header.getName()!=null && header.getName().equals(HEADER_LINK)) {
                    String value = header.getValue();
                    return value.contains("rel=\"next\"");
                }
            }
        } else {
            Log.e(TAG,"Last Response Headers is NULL!");
        }
        Log.i(TAG,"Link header not found");
        return false;
    }

    /**
     * парсит заголовок Link и номер следующей страницы
     * @return номер следующей страницы из заголовка
     */
    public int getNextPageFromLastResponse() {
        if(mLastResponseHeaders!=null) {
            List<Header> headerList = mLastResponseHeaders.getHeaders();
            for(Header header : headerList) {
                if(header.getName()!=null && header.getName().equals(HEADER_LINK)) {
                    String value = header.getValue();
                    int pos = value.indexOf("rel=\"next\"");
                    if(pos>0) {
                        Pattern pattern = Pattern.compile("\\&page=(\\d)");
                        Matcher m = pattern.matcher(value.substring(0,pos));
                        if(m.find()) {
                            return Integer.parseInt(m.group(1));
                        }
                    }
                }
            }
        } else {
            Log.e(TAG,"Last Response Headers is NULL!");
        }
        Log.i(TAG,"Link header not found");
        return -1;
    }





    /**
     * Перехватывает и разбирает стандартные ошибки запросов и отдаёт адекватные сообщения на активность/фрагмент
     * @param error - стандартная ошибка RetrofitError
     */
    private int defineError(RetrofitError error) {
        Log.e(TAG, error.toString());

        if(error.getResponse()==null) {

            if(mContext != null) {
                ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetworkInfo = cm.getActiveNetworkInfo();
                if(activeNetworkInfo == null || !activeNetworkInfo.isConnected()) {
                    return ERR_CODE_NO_INTERNET;
                }
            } else {
                Log.e(TAG,ERR_MSG_NULL_CONTEXT);
            }
            return ERR_CODE_UNKONWN_ERROR;

        } else {

            JsonObject errorJson = (JsonObject)error.getBodyAs(JsonObject.class);
            logResponse(error.getResponse());

            try {

                StringBuilder errorBody = new StringBuilder();
                InputStream inputStream = error.getResponse().getBody().in();
                BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = br.readLine()) != null) {
                    errorBody.append(line);
                }

                Log.e(TAG, "ErrorBody: "+errorBody.toString());
                Toast.makeText(mContext, "#" + error.getResponse().getStatus()+": "+errorJson.get("message").getAsString(), Toast.LENGTH_SHORT).show();

            } catch (IOException e) {
                Log.e(TAG, e.getLocalizedMessage());
            } catch (NullPointerException e) {
                Log.e(TAG, "Probably no respone body. "+e.getLocalizedMessage());
            }
            return error.getResponse().getStatus();

        }

    }

    public void logResponse(Response response) {
        Log.i(TAG, "REASON: " + response.getReason());
        Log.i(TAG, "STATUS: " + response.getStatus());
        Log.i(TAG, "==== HEADERS =================== ");
        List<Header> headersList = response.getHeaders();
        for (Header header : headersList) {
            Log.i(TAG, header.toString());
        }

    }

    public void logBody(Response response) {
        Log.i(TAG, "==== BODY =================== ");
        StringBuilder html = new StringBuilder();
        try {
            InputStream inputStream = response.getBody().in();
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = br.readLine()) != null) {
                html.append(line);
            }
            Log.i(TAG, html.toString());
        } catch (IOException e) {
            Log.e(TAG, e.getLocalizedMessage());
        }
    }

}
