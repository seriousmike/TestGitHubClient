package ru.seriousmike.testgithubclient.activities;

import android.app.Fragment;
import android.content.Intent;
import android.util.Log;

import ru.seriousmike.testgithubclient.R;
import ru.seriousmike.testgithubclient.core.SingleFragmentActivity;
import ru.seriousmike.testgithubclient.fragments.AlertDialogFragment;
import ru.seriousmike.testgithubclient.fragments.RepositoryListFragment;
import ru.seriousmike.testgithubclient.ghservice.GitHubAPI;

/**
 * Created by SeriousM on 14.01.2015.
 */
public class RepositoryListActivity extends SingleFragmentActivity {

    private static final String TAG = "sm_RepositoryListFragment";

    @Override
    protected Fragment createFragment() {
        return new RepositoryListFragment();
    }

    @Override
    public void clickedPositive(int event_code) {
        switch(event_code) {
            case AlertDialogFragment.EVENT_LOGOUT:
                GitHubAPI.getInstance().logout();
                Intent i = new Intent(RepositoryListActivity.this, LoginActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                break;
            default:
                try {
                    ((RepositoryListFragment) getFragmentManager().findFragmentById(R.id.fragmentContainer)).retryRequest();
                } catch( NullPointerException e) {
                    Log.e(TAG, "No fragment? " + e.getLocalizedMessage());
                }
        }
    }

    @Override
    public void clickedNegative(int event_code) {
        ((RepositoryListFragment) getFragmentManager().findFragmentById(R.id.fragmentContainer)).cancelRefreshing();
        ((RepositoryListFragment) getFragmentManager().findFragmentById(R.id.fragmentContainer)).showRefreshFooter();
    }
}
