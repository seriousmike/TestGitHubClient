package ru.seriousmike.testgithubclient.core;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;

import ru.seriousmike.testgithubclient.R;
import ru.seriousmike.testgithubclient.fragments.AlertDialogFragment;

/**
 * Created by SeriousM on 04.01.2015.
 */
public abstract class SingleFragmentActivity extends Activity implements AlerterInterfaceFragment.AlertCaller, AlertDialogFragment.DialogInteraction {
    protected abstract Fragment createFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_fragment);

        FragmentManager fm = getFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragmentContainer);
        if(fragment == null) {
            fragment = createFragment();
            fm.beginTransaction().add(R.id.fragmentContainer, fragment).commit();
        }
    }

    @Override
    public void showAlertDialog(int error_code, boolean enablePositiveButton, boolean enableNegativeButton, String customPositiveButtonTitle, String customNegativeButtonTitle) {
        DialogFragment alertFragment = AlertDialogFragment.newInstance(error_code, enablePositiveButton, customPositiveButtonTitle, enableNegativeButton, customNegativeButtonTitle);
        alertFragment.show(getFragmentManager(),"dialog");
    }

}
