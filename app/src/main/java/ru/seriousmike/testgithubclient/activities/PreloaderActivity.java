package ru.seriousmike.testgithubclient.activities;

import android.app.Fragment;
import android.util.Log;

import ru.seriousmike.testgithubclient.R;
import ru.seriousmike.testgithubclient.core.SingleFragmentActivity;
import ru.seriousmike.testgithubclient.fragments.PreloaderFragment;

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
    public void clickedPositive(int event_code) {
        Log.i(TAG,"clicked repeat");
        ((PreloaderFragment)(getFragmentManager().findFragmentById(R.id.fragmentContainer))).checkAuthorization();
    }

    @Override
    public void clickedNegative(int event_code) {
        finish();
    }
}
