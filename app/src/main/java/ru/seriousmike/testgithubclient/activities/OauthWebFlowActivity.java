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
    protected void initActionBar() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void clickedPositive(int event_code) {

    }

    @Override
    public void clickedNegative(int event_code) {

    }
}
