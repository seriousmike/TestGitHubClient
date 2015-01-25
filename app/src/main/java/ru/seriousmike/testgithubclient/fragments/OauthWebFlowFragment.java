package ru.seriousmike.testgithubclient.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import ru.seriousmike.testgithubclient.R;
import ru.seriousmike.testgithubclient.activities.PreloaderActivity;
import ru.seriousmike.testgithubclient.activities.RepositoryListActivity;
import ru.seriousmike.testgithubclient.core.AlerterInterfaceFragment;
import ru.seriousmike.testgithubclient.ghservice.GHConfig;
import ru.seriousmike.testgithubclient.ghservice.GitHubAPI;

/**
 * выводит WebFlow OAuth-авторизации
 */
public class OauthWebFlowFragment extends AlerterInterfaceFragment {

    private static final String TAG = "sm_OauthWebFlowFragment";

    private WebView mWebView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_oauth_webflow, parent, false);
        mWebView = (WebView) layout.findViewById(R.id.webView);
        //TODO добавить хром-штуки, типа индикатора загрузки
        //mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setWebViewClient( new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.i(TAG,"got url: "+url);
                if(url.contains(GHConfig.OAUTH_WEB_FLOW_BASE_URL+"?token=")) {
                    String token = url.replace(GHConfig.OAUTH_WEB_FLOW_BASE_URL + "?token=", "");
                    if(!token.equals("")) {
                        GitHubAPI.getInstance().setToken(token);
                        Intent i = new Intent(getActivity(), PreloaderActivity.class);
                        i.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK );
                        startActivity(i);
                        getActivity().finish();
                    } else {
                        Toast.makeText(getActivity(), R.string.error_msg_unforseen, Toast.LENGTH_SHORT).show();
                    }

                }
                return false;
            }
        } );
        //TODO бомбануть проверку на отсутствие инета, и вообще бегания по ссылочкам
        mWebView.loadUrl(GHConfig.OAUTH_WEB_FLOW_URL);
        return layout;
    }


}
