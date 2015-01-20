package ru.seriousmike.testgithubclient.fragments;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.app.Fragment;

import ru.seriousmike.testgithubclient.R;
import ru.seriousmike.testgithubclient.ghservice.GitHubAPI;

/**
 * A simple {@link Fragment} subclass.
 */
public class ErrorDialogFragment extends DialogFragment {

    private static final String TAG = "sm_ErrorDialogFragment";

    private static final String VAR_ERR_CODE = "error_code";
    private static final String VAR_REPEAT_ENABLED = "repeat_button_enabled";
    private static final String VAR_CANCEL_ENABLED = "cancel_button_enabled";
    private static final String VAR_REPEAT_TITLE = "repeat_button_title";
    private static final String VAR_CANCEL_TITLE = "cancel_button_title";

    public ErrorDialogFragment() {}

    public static ErrorDialogFragment newInstance(int error_code, boolean enableRepeatButton, String customRepeatButtonTitle, boolean enableCancelButton, String customCancelButtonTitle) {
        ErrorDialogFragment dialogFragment = new ErrorDialogFragment();
        Bundle args = new Bundle();
        args.putInt(VAR_ERR_CODE,error_code);
        args.putBoolean(VAR_REPEAT_ENABLED, enableRepeatButton);
        args.putBoolean(VAR_CANCEL_ENABLED, enableCancelButton);
        args.putString(VAR_REPEAT_TITLE, customRepeatButtonTitle);
        args.putString(VAR_CANCEL_TITLE, customCancelButtonTitle);

        dialogFragment.setArguments(args);
        return dialogFragment;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int err_code = getArguments().getInt(VAR_ERR_CODE);
        boolean enableRepeatButton = getArguments().getBoolean(VAR_REPEAT_ENABLED);
        boolean enableCancelButton = getArguments().getBoolean(VAR_CANCEL_ENABLED);
        String titleRepeat = getArguments().getString(VAR_REPEAT_TITLE);
        String cancelRepeat = getArguments().getString(VAR_CANCEL_TITLE);

        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity());/*.setIcon(R.drawable.logo_github_violet_small)*/

        if(enableRepeatButton) {
            alertBuilder.setPositiveButton((titleRepeat!=null?titleRepeat:getString(R.string.retry)), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ((DialogInteraction)getActivity()).clickedPositive();
                }
            });
        }

        if(enableCancelButton) {
            alertBuilder.setNegativeButton((cancelRepeat!=null?cancelRepeat:getString(R.string.cancel)), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ((DialogInteraction)getActivity()).clickedNegative();
                }
            });
        }

        alertBuilder.setMessage(getErrorText(err_code));

        return alertBuilder.create();
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        ((DialogInteraction)getActivity()).clickedNegative();
    }


    public String getErrorText(int error_code) {
        String errtxt;
        switch(error_code) {
            case GitHubAPI.ERR_CODE_NO_INTERNET:
                errtxt = getString(R.string.error_msg_no_internet);
                break;
            case GitHubAPI.ERR_CODE_UNKONWN_ERROR:
                errtxt = getString(R.string.error_msg_unknown);
                break;
            case GitHubAPI.ERR_CODE_FORBIDDEN:
                errtxt = getString(R.string.error_msg_limit_reached);
                break;
            default:
                errtxt = getString(R.string.error_msg_unforseen)+" #"+error_code;
        }
        return errtxt;
    }



    /**
     * Интерфейс для обратного зваимодействия активности и фрагмента диалога
     */
    public interface DialogInteraction {
        public void clickedPositive();
        public void clickedNegative();
    }

}
