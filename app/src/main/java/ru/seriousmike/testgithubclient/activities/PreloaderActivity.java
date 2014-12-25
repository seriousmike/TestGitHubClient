package ru.seriousmike.testgithubclient.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import ru.seriousmike.testgithubclient.R;
import ru.seriousmike.testgithubclient.data.Config;
import ru.seriousmike.testgithubclient.data.GitHubAPI;

/**
 * Активность-загрузчик.
 * Здесь проверяется, авторизован ли пользователь.
 * В зависимости от статуса происходит переход на нужную активность
 */
public class PreloaderActivity extends Activity {

    private static final String TAG = "sm_PreloaderActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preloader);

        String token = getSharedPreferences(Config.PREFERENCES, MODE_PRIVATE).getString(GitHubAPI.VAR_TOKEN, null);
        String lgn = getSharedPreferences(Config.PREFERENCES, MODE_PRIVATE).getString(GitHubAPI.VAR_LGN, null);
        String psw = getSharedPreferences(Config.PREFERENCES, MODE_PRIVATE).getString(GitHubAPI.VAR_PSW, null);
        if(token!=null) {
            //TODO авторизация по токену
        } else if(lgn!=null && psw!=null) {
            //TODO прямая аторизация
        } else {
            Log.i(TAG,"starting login activity");
            Intent i = new Intent(PreloaderActivity.this, LoginActivity.class);
            startActivity(i);
            finish();
        }

    }

}
