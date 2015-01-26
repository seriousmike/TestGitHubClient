package ru.seriousmike.testgithubclient.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import ru.seriousmike.testgithubclient.R;
import ru.seriousmike.testgithubclient.activities.RepositoryListActivity;
import ru.seriousmike.testgithubclient.core.AlerterInterfaceFragment;
import ru.seriousmike.testgithubclient.ghservice.GitHubAPI;
import ru.seriousmike.testgithubclient.ghservice.data.RequestCallback;
import ru.seriousmike.testgithubclient.ghservice.data.UserInfo;

/**
 * Фрагмент-загрузчик
 * Здесь проверяется, авторизован ли пользователь.
 * В зависимости от статуса происходит переход на нужную активность
 */
public class PreloaderFragment extends AlerterInterfaceFragment {

    private static final String TAG = "sm_PreloaderFragment";

    public PreloaderFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View layout = inflater.inflate(R.layout.fragment_preloader, container, false);
        checkAuthorization();

        return layout;
    }

    public void checkAuthorization() {
        if(GitHubAPI.getInstance(getActivity().getApplicationContext()).isTokenSet()) {
            GitHubAPI.getInstance().getBasicUserInfo(new RequestCallback<UserInfo>() {
                @Override
                public void onSuccess(UserInfo userInfo) {
                    proceedAuthorized();
                }

                @Override
                public void onFailure(int error_code) {
                    Log.i(TAG, "error_code " + error_code);
                    proceedToAuthorization();
                }
            });
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    proceedToAuthorization();
                }
            }, 500);

        }

    }

    private void proceedAuthorized() {
        Intent i = new Intent(getActivity(), RepositoryListActivity.class);
        startActivity(i);
        getActivity().overridePendingTransition(R.anim.activity_fade_in, R.anim.activity_fade_out);
        getActivity().finish();
    }

    private void proceedToAuthorization() {
        defaultRequestFailureAction(GitHubAPI.ERR_CODE_UNAUTH);
        getActivity().overridePendingTransition(R.anim.activity_fade_in, R.anim.activity_fade_out);
    }
}
