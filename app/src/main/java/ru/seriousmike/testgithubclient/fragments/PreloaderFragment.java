package ru.seriousmike.testgithubclient.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import ru.seriousmike.testgithubclient.R;
import ru.seriousmike.testgithubclient.activities.LoginActivity;
import ru.seriousmike.testgithubclient.activities.TestActivity;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View layout = inflater.inflate(R.layout.activity_preloader, container, false);

        checkAuthorization();

        return layout;
    }

    public void checkAuthorization() {
        GitHubAPI.getInstance(getActivity().getApplicationContext()).getBasicUserInfo(new RequestCallback<UserInfo>() {
            @Override
            public void onSuccess(UserInfo userInfo) {
                Log.i(TAG, "Success! " + userInfo.toString());
            }

            @Override
            public void onFailure(int error_code) {
                //TODO убрать по итогу
                Toast.makeText(getActivity(),"ERROR CODE #"+error_code, Toast.LENGTH_SHORT).show();
                Log.e(TAG,"!!! ERROR #"+error_code);
                if(error_code==GitHubAPI.ERR_CODE_UNAUTH) {
                    Log.i(TAG, "starting login activity");
                    Intent i = new Intent(getActivity(), LoginActivity.class);
                    startActivity(i);
                    getActivity().finish();
                } else {
                    ((AlertCaller)getActivity()).showAlertDialog(error_code, true, false, null, null);
                }
            }
        });
    }
}
