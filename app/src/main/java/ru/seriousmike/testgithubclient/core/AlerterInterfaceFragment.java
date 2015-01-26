package ru.seriousmike.testgithubclient.core;

import android.app.Fragment;
import android.content.Intent;
import android.util.Log;

import ru.seriousmike.testgithubclient.R;
import ru.seriousmike.testgithubclient.activities.AuthorizationChooseActivity;
import ru.seriousmike.testgithubclient.activities.LoginActivity;
import ru.seriousmike.testgithubclient.activities.OauthWebFlowActivity;
import ru.seriousmike.testgithubclient.ghservice.GitHubAPI;

/**
 * Created by SeriousM on 07.01.2015.
 */
public abstract class AlerterInterfaceFragment extends Fragment {

    private static final String TAG = "sm_AlerterInterfaceFragment";

    public interface AlertCaller {
        public void showAlertDialog(int error_code, boolean enablePositiveButton, boolean enableNegativeButton, String customPositiveButtonTitle, String customNegativeButtonTitle);
    }

    protected final void defaultRequestFailureAction(int error_code) {
        Log.e(TAG, "!!! ERROR #" + error_code);
        if(error_code == GitHubAPI.ERR_CODE_UNAUTH) {
            defaultUnauthAction();
        } else {
            processRequestFailure(error_code);
        }
    }

    protected final void defaultUnauthAction() {
        Log.i(TAG, "starting login activity");
        Intent i = new Intent(getActivity(), AuthorizationChooseActivity.class);
        i.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK );
        startActivity(i);
        getActivity().overridePendingTransition(R.anim.activity_from_left_complex_motion_in, R.anim.activity_to_right_complex_motion_out);
    }

    protected void processRequestFailure(int error_code) {
        ((AlertCaller)getActivity()).showAlertDialog(error_code, true, false, null, null);
    }

}
