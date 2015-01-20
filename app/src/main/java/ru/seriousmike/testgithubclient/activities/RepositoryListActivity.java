package ru.seriousmike.testgithubclient.activities;

import android.app.Fragment;
import android.content.Intent;

import ru.seriousmike.testgithubclient.R;
import ru.seriousmike.testgithubclient.core.SingleFragmentActivity;
import ru.seriousmike.testgithubclient.fragments.AlertDialogFragment;
import ru.seriousmike.testgithubclient.fragments.RepositoryListFragment;
import ru.seriousmike.testgithubclient.ghservice.GitHubAPI;

/**
 * Created by SeriousM on 14.01.2015.
 */
public class RepositoryListActivity extends SingleFragmentActivity {
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
                startActivity(i);
                finish();
                break;
            default:
                //TODO перезагрузку данных фрагмента на случай коллапса с инетом и тд
                //((RepositoryListFragment)getFragmentManager().findFragmentById(R.id.fragmentContainer)).
        }
    }

    @Override
    public void clickedNegative(int event_code) {

    }
}
