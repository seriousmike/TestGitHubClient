package ru.seriousmike.testgithubclient.activities;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import ru.seriousmike.testgithubclient.R;
import ru.seriousmike.testgithubclient.core.AlerterInterfaceFragment;
import ru.seriousmike.testgithubclient.fragments.PreloaderFragment;
import ru.seriousmike.testgithubclient.ghservice.GitHubAPI;
import ru.seriousmike.testgithubclient.ghservice.data.RequestCallback;

/**
 * Активность-загрузчик.
 */
public class PreloaderActivity extends SingleFragmentActivity {

    private static final String TAG = "sm_PreloaderActivity";

    @Override
    public Fragment createFragment() {
        return new PreloaderFragment();
    }

    @Override
    public void clickedPositive() {
        Log.i(TAG,"clicked repeat");
        ((PreloaderFragment)(getFragmentManager().findFragmentById(R.id.fragmentContainer))).checkAuthorization();
    }

    @Override
    public void clickedNegative() {
        finish();
    }
}
