package ru.seriousmike.testgithubclient.activities;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;

import ru.seriousmike.testgithubclient.R;
import ru.seriousmike.testgithubclient.core.AlerterInterfaceFragment;
import ru.seriousmike.testgithubclient.fragments.ErrorDialogFragment;

/**
 * Created by SeriousM on 04.01.2015.
 */
public abstract class SingleFragmentActivity extends Activity implements AlerterInterfaceFragment.AlertCaller, ErrorDialogFragment.DialogInteraction {
    protected abstract Fragment createFragment();

    @Override
    public void onCreate(Bundle savedInstanceState) {
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
    public void showAlertDialog(int error_code, boolean enableRepeatButton, boolean enableCancelButton, String customRepeatButtonTitle, String customCancelButtonTitle) {
        DialogFragment alertFragment = ErrorDialogFragment.newInstance(error_code,enableRepeatButton, customRepeatButtonTitle, enableCancelButton,customCancelButtonTitle);
        alertFragment.show(getFragmentManager(),"dialog");
    }

}
