package ru.seriousmike.testgithubclient.activities;

import android.app.Fragment;
import ru.seriousmike.testgithubclient.core.SingleFragmentActivity;
import ru.seriousmike.testgithubclient.fragments.LoginFragment;


/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends SingleFragmentActivity {

    private static final String TAG = "sm_LoginActivity";


    @Override
    protected Fragment createFragment() {
        return new LoginFragment();
    }

    @Override
    public void clickedPositive(int event_code) {
//TODO реакции на отсутствие инета и тд
    }

    @Override
    public void clickedNegative(int event_code) {

    }
}