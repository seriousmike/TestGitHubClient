package ru.seriousmike.testgithubclient.activities;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;

import ru.seriousmike.testgithubclient.R;
import ru.seriousmike.testgithubclient.core.AlerterInterfaceFragment;
import ru.seriousmike.testgithubclient.fragments.RepositoryFragment;
import ru.seriousmike.testgithubclient.ghservice.GitHubAPI;

/**
 * Created by SeriousM on 17.01.2015.
 */
public class RepositoryActivity extends SingleFragmentActivity {

    private static final String TAG = "sm_TagRepostioryActivity";

    public static final String EXTRA_REPO = "ru.seriousmike.testgithubclient.RepositoryActivity.repo";
    public static final String EXTRA_OWNER = "ru.seriousmike.testgithubclient.RepositoryActivity.owner";

    @Override
    protected Fragment createFragment() {
        return RepositoryFragment.getInstance( getIntent().getStringExtra(EXTRA_OWNER),getIntent().getStringExtra(EXTRA_REPO) );
    }

    @Override
    public void clickedPositive() {
        try {
            ((RepositoryFragment) getFragmentManager().findFragmentById(R.id.fragmentContainer)).refreshCommits();
        } catch( NullPointerException e) {
            Log.e(TAG,"No fragment? "+e.getLocalizedMessage());
        }
    }

    @Override
    public void clickedNegative() {
        Log.i(TAG,"user denied repeat");
        finish();
    }
}
