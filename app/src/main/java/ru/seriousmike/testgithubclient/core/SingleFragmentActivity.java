package ru.seriousmike.testgithubclient.core;

import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;

import ru.seriousmike.testgithubclient.R;
import ru.seriousmike.testgithubclient.fragments.AlertDialogFragment;

/**
 * Created by SeriousM on 04.01.2015.
 */
public abstract class SingleFragmentActivity extends ActionBarActivity implements AlerterInterfaceFragment.AlertCaller, AlertDialogFragment.DialogInteraction {
    private static final String TAG = "sm_SingleFragmentActivity";

    protected abstract Fragment createFragment();

    private boolean mIsStopped = false;

    protected boolean isActivityAfterOnStop() {
        Log.i(TAG, "Activity is " + (mIsStopped ?" after retaining  ":" not after ") );
        return mIsStopped;
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG,"onStart");
        mIsStopped = false;
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG,"onStop");
        mIsStopped = true;
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
        if(!isActivityAfterOnStop()) {
            DialogFragment alertFragment = AlertDialogFragment.newInstance(error_code, enablePositiveButton, customPositiveButtonTitle, enableNegativeButton, customNegativeButtonTitle);
            alertFragment.show(getFragmentManager(),"dialog");
        }
    }

    /**
     * настраивает ActionBar
     */
    protected abstract void initActionBar();
}
