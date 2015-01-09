package ru.seriousmike.testgithubclient.ghservice.data;

/**
 * Created by SeriousM on 01.01.2015.
 * Класс результата авторизации https://api.github.com/authorizations/ для Retrofit
 */
public class AuthorizationResult {
    public long id;
    public String token;
    public String note;
    public String created_at;
    public String updated_at;
    public String[] scopes;
}
