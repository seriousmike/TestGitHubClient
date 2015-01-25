package ru.seriousmike.testgithubclient.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import ru.seriousmike.testgithubclient.R;
import ru.seriousmike.testgithubclient.activities.RepositoryListActivity;
import ru.seriousmike.testgithubclient.core.AlerterInterfaceFragment;
import ru.seriousmike.testgithubclient.ghservice.GitHubAPI;
import ru.seriousmike.testgithubclient.ghservice.data.RequestCallback;
import ru.seriousmike.testgithubclient.ghservice.data.UserInfo;

/**
 * создание персонального Токена через базовую авторизацию
 */
public class LoginFragment extends AlerterInterfaceFragment {

    private static final String TAG = "sm_LoginFragment";

    private EditText mLoginView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_login, parent, false);

        // Set up the login form.
        mLoginView = (EditText) layout.findViewById(R.id.login);

        mPasswordView = (EditText) layout.findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mLogInButton = (Button) layout.findViewById(R.id.log_in_button);
        mLogInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = layout.findViewById(R.id.login_form);
        mProgressView = layout.findViewById(R.id.login_progress);

        return layout;
    }




    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptLogin() {

        // Reset errors.
        mLoginView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mLoginView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;


        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mLoginView.setError(getString(R.string.error_field_required));
            focusView = mLoginView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            Log.i(TAG, "Checking " + mLoginView.getText().toString() + ":" + mPasswordView.getText().toString());
            GitHubAPI.getInstance().getAuthorization(mLoginView.getText().toString(), mPasswordView.getText().toString(), new RequestCallback<UserInfo>() {
                @Override
                public void onSuccess(UserInfo userInfo) {
                    Log.i(TAG, userInfo.name + "/" + userInfo.login);
                    Toast.makeText(getActivity(), userInfo.name + "/" + userInfo.login, Toast.LENGTH_SHORT).show();

                    Intent i = new Intent(getActivity(), RepositoryListActivity.class);
                    i.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK );
                    startActivity(i);
                    getActivity().finish();
                }

                @Override
                public void onFailure(int error_code) {
                    if(error_code==GitHubAPI.ERR_CODE_UNAUTH) {
                        showProgress(false);
                        mLoginView.setError(getString(R.string.error_msg_401_bad_credentials));
                        mLoginView.requestFocus();
                    } else {
                        ((AlertCaller)getActivity()).showAlertDialog(error_code, true, true, null, null);
                    }
                }
            });
        }
    }



    /**
     * Shows the progress UI and hides the login form.
     */
    public void showProgress(final boolean show) {
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mProgressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });

    }

}
