package ru.seriousmike.testgithubclient.activities;

import android.app.Fragment;

import ru.seriousmike.testgithubclient.core.SingleFragmentActivity;
import ru.seriousmike.testgithubclient.fragments.RepositoryListFragment;

/**
 * Created by SeriousM on 14.01.2015.
 */
public class RepositoryListActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new RepositoryListFragment();
    }

    @Override
    public void clickedPositive() {

    }

    @Override
    public void clickedNegative() {

    }
}
