package ru.seriousmike.testgithubclient.activities;

import android.app.Fragment;
import android.util.Log;

import java.util.HashMap;

import ru.seriousmike.testgithubclient.R;
import ru.seriousmike.testgithubclient.core.SingleFragmentActivity;
import ru.seriousmike.testgithubclient.fragments.RepositoryFragment;

/**
 * Created by SeriousM on 17.01.2015.
 */
public class RepositoryActivity extends SingleFragmentActivity {

    private static final String TAG = "sm_TagRepostioryActivity";

    public static final String EXTRA_REPO = "ru.seriousmike.testgithubclient.RepositoryActivity.repo";
    public static final String EXTRA_OWNER = "ru.seriousmike.testgithubclient.RepositoryActivity.owner";
    public static final String EXTRA_RI_DESCR = "ru.seriousmike.testgithubclient.RepositoryActivity.repo_info.descr";
    public static final String EXTRA_RI_OWNER_NAME = "ru.seriousmike.testgithubclient.RepositoryActivity.repo_info.owner_name";
    public static final String EXTRA_RI_CREATED = "ru.seriousmike.testgithubclient.RepositoryActivity.repo_info.created_at";
    public static final String EXTRA_RI_PUSHED = "ru.seriousmike.testgithubclient.RepositoryActivity.repo_info.pushed_at";
    public static final String EXTRA_RI_ONWER_PIC = "ru.seriousmike.testgithubclient.RepositoryActivity.repo_info.avatar";

    @Override
    protected Fragment createFragment() {
        HashMap<String,String> repoInfo = new HashMap<>();
        repoInfo.put(RepositoryFragment.REPOINFO_CREATED, getIntent().getStringExtra(EXTRA_RI_CREATED));
        repoInfo.put(RepositoryFragment.REPOINFO_PUSHED, getIntent().getStringExtra(EXTRA_RI_PUSHED));
        repoInfo.put(RepositoryFragment.REPOINFO_OWNER_NAME, getIntent().getStringExtra(EXTRA_RI_OWNER_NAME));
        repoInfo.put(RepositoryFragment.REPOINFO_OWNER_PIC, getIntent().getStringExtra(EXTRA_RI_ONWER_PIC));
        repoInfo.put(RepositoryFragment.REPOINFO_DESCR, getIntent().getStringExtra(EXTRA_RI_DESCR));
        Log.i(TAG, repoInfo.toString() );

        return RepositoryFragment.getInstance( getIntent().getStringExtra(EXTRA_OWNER),getIntent().getStringExtra(EXTRA_REPO), repoInfo );
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
