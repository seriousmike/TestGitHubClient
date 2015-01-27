package ru.seriousmike.testgithubclient.core;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;

import ru.seriousmike.testgithubclient.R;
import ru.seriousmike.testgithubclient.fragments.AlertDialogFragment;

/**
 * Created by SeriousM on 04.01.2015.
 */
public abstract class SingleFragmentActivity extends ActionBarActivity implements AlerterInterfaceFragment.AlertCaller, AlertDialogFragment.DialogInteraction {
    protected abstract Fragment createFragment();

    private boolean mIsVisible = false;

    protected boolean isActivityVisible() {
        return mIsVisible;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mIsVisible = true;
    }

    @Override
    protected void onPause() {
        super.onResume();
        mIsVisible = false;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_fragment);
        initActionBar();

        FragmentManager fm = getFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragmentContainer);
        if(fragment == null) {
            fragment = createFragment();
            fm.beginTransaction().add(R.id.fragmentContainer, fragment).commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void showAlertDialog(int error_code, boolean enablePositiveButton, boolean enableNegativeButton, String customPositiveButtonTitle, String customNegativeButtonTitle) {
        if(isActivityVisible()) {
            DialogFragment alertFragment = AlertDialogFragment.newInstance(error_code, enablePositiveButton, customPositiveButtonTitle, enableNegativeButton, customNegativeButtonTitle);
            alertFragment.show(getFragmentManager(),"dialog");
        }
    }

    /**
     * настраивает ActionBar
     */
    protected abstract void initActionBar();
}
