package ru.seriousmike.testgithubclient.core;

import android.app.Fragment;

/**
 * Created by SeriousM on 07.01.2015.
 */
public abstract class AlerterInterfaceFragment extends Fragment {

    public interface AlertCaller {
        public void showAlertDialog(int error_code, boolean enableRepeatButton, boolean enableCancelButton, String customRepeatName, String customCancelName);
    }

}
