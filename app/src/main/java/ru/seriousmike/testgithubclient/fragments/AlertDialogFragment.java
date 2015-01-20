package ru.seriousmike.testgithubclient.fragments;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import ru.seriousmike.testgithubclient.R;
import ru.seriousmike.testgithubclient.ghservice.GitHubAPI;

/**
 * выводит сообщения по коду ошибки/события
 */
public class AlertDialogFragment extends DialogFragment {

    private static final String TAG = "sm_ErrorDialogFragment";

    // чтобы не конфликтовать с константами событий GitHubAPI значения местных констант будет отрицательным
    public static final int EVENT_LOGOUT = -333;

    private static final String VAR_ERR_CODE = "error_code";
    private static final String VAR_REPEAT_ENABLED = "repeat_button_enabled";
    private static final String VAR_CANCEL_ENABLED = "cancel_button_enabled";
    private static final String VAR_REPEAT_TITLE = "repeat_button_title";
    private static final String VAR_CANCEL_TITLE = "cancel_button_title";

    private int mEventCode;



    public AlertDialogFragment() {}

    public static AlertDialogFragment newInstance(int error_code, boolean enablePositiveButton, String customPositiveButtonTitle, boolean enableNegativeButton, String customNegativeButtonTitle) {
        AlertDialogFragment dialogFragment = new AlertDialogFragment();
        Bundle args = new Bundle();
        args.putInt(VAR_ERR_CODE,error_code);
        args.putBoolean(VAR_REPEAT_ENABLED, enablePositiveButton);
        args.putBoolean(VAR_CANCEL_ENABLED, enableNegativeButton);
        args.putString(VAR_REPEAT_TITLE, customPositiveButtonTitle);
        args.putString(VAR_CANCEL_TITLE, customNegativeButtonTitle);

        dialogFragment.setArguments(args);
        return dialogFragment;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mEventCode = getArguments().getInt(VAR_ERR_CODE);
        boolean enableRepeatButton = getArguments().getBoolean(VAR_REPEAT_ENABLED);
        boolean enableCancelButton = getArguments().getBoolean(VAR_CANCEL_ENABLED);
        String titleRepeat = getArguments().getString(VAR_REPEAT_TITLE);
        String cancelRepeat = getArguments().getString(VAR_CANCEL_TITLE);

        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity());/*.setIcon(R.drawable.logo_github_violet_small)*/

        if(enableRepeatButton) {
            alertBuilder.setPositiveButton((titleRepeat!=null?titleRepeat:getString(R.string.retry)), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ((DialogInteraction)getActivity()).clickedPositive(mEventCode);
                }
            });
        }

        if(enableCancelButton) {
            alertBuilder.setNegativeButton((cancelRepeat!=null?cancelRepeat:getString(R.string.cancel)), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ((DialogInteraction)getActivity()).clickedNegative(mEventCode);
                }
            });
        }

        alertBuilder.setMessage(getErrorText(mEventCode));

        return alertBuilder.create();
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        ((DialogInteraction)getActivity()).clickedNegative(mEventCode);
    }


    public String getErrorText(int event_code) {
        String errtxt;
        switch(event_code) {
            case GitHubAPI.ERR_CODE_NO_INTERNET:
                errtxt = getString(R.string.error_msg_no_internet);
                break;
            case GitHubAPI.ERR_CODE_UNKONWN_ERROR:
                errtxt = getString(R.string.error_msg_unknown);
                break;
            case GitHubAPI.ERR_CODE_FORBIDDEN:
                errtxt = getString(R.string.error_msg_limit_reached);
                break;
            case EVENT_LOGOUT:
                errtxt = getString(R.string.event_trying_to_logout)+" "+GitHubAPI.getInstance().getCurrentUser().login+"?";
                break;

            default:
                errtxt = getString(R.string.error_msg_unforseen)+" #"+event_code;
        }
        return errtxt;
    }



    /**
     * Интерфейс для обратного зваимодействия активности и фрагмента диалога
     */
    public interface DialogInteraction {
        public void clickedPositive(int event_code);
        public void clickedNegative(int event_code);
    }

}
