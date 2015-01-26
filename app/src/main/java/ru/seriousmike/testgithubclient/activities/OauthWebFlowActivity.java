package ru.seriousmike.testgithubclient.activities;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;


import ru.seriousmike.testgithubclient.R;
import ru.seriousmike.testgithubclient.core.SingleFragmentActivity;
import ru.seriousmike.testgithubclient.fragments.OauthWebFlowFragment;
import ru.seriousmike.testgithubclient.ghservice.GitHubAPI;

public class OauthWebFlowActivity extends SingleFragmentActivity {

    private static final String TAG = "sm_OauthWebFlowActivity";

    @Override
    protected Fragment createFragment() {
        return new OauthWebFlowFragment();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case(android.R.id.home):
                Log.i(TAG, "home pressed");
                finish();
                overridePendingTransition(R.anim.activity_step_in, R.anim.activity_to_top_right_out);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.activity_step_in, R.anim.activity_to_top_right_out);
    }


    @Override
    protected void initActionBar() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void clickedPositive(int event_code) {}

    @Override
    public void clickedNegative(int event_code) {}
}
