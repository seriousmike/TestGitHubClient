package ru.seriousmike.testgithubclient.ghservice.data;

import android.util.Log;

/**
 * Created by SeriousM on 01.01.2015.
 * Коллбэк для запросов GitHubAPI
 * @param <K> generic object
 */
public abstract class RequestCallback<K> {
    public abstract void onSuccess(K k);
    public abstract void onFailure(int error_code);
}