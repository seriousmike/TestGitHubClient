package ru.seriousmike.testgithubclient.activities;

import android.app.Fragment;
import android.util.Log;
import android.view.MenuItem;

import ru.seriousmike.testgithubclient.R;
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
    protected void initActionBar() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case(android.R.id.home):
                Log.i(TAG, "home pressed");
                finish();
                overridePendingTransition(R.anim.activity_step_in, R.anim.activity_to_bottom_right_out);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.activity_step_in, R.anim.activity_to_bottom_right_out);
    }

    @Override
    public void clickedPositive(int event_code) {
        ((LoginFragment)getFragmentManager().findFragmentById(R.id.fragmentContainer)).attemptLogin();
    }

    @Override
    public void clickedNegative(int event_code) {
        ((LoginFragment)getFragmentManager().findFragmentById(R.id.fragmentContainer)).showProgress(false);
    }
}