package ru.seriousmike.testgithubclient.fragments;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import ru.seriousmike.testgithubclient.R;
import ru.seriousmike.testgithubclient.activities.PreloaderActivity;
import ru.seriousmike.testgithubclient.core.AlerterInterfaceFragment;
import ru.seriousmike.testgithubclient.ghservice.GHConfig;
import ru.seriousmike.testgithubclient.ghservice.GitHubAPI;

/**
 * выводит WebFlow OAuth-авторизации
 */
public class OauthWebFlowFragment extends AlerterInterfaceFragment {

    private static final String TAG = "sm_OauthWebFlowFragment";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @SuppressWarnings("deprecation")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_oauth_webflow, parent, false);

        final ProgressBar progressBar = (ProgressBar) layout.findViewById(R.id.progressBar);
        progressBar.setMax(100);

        WebView webView = (WebView) layout.findViewById(R.id.webView);
        webView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.i(TAG, "got url: " + url);
                if (!url.contains(GHConfig.OAUTH_WEB_FLOW_SERVICE_BASE_URL) && !url.contains(GHConfig.OAUTH_WEB_FLOW_BASE_URL)) {
                    try {
                        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        startActivity(i);
                    } catch (ActivityNotFoundException e) {
                        Toast.makeText(getActivity(), getString(R.string.no_internet_browser_activity), Toast.LENGTH_SHORT).show();
                    }
                    return true;
                }

                if (url.contains(GHConfig.OAUTH_WEB_FLOW_BASE_URL + "?token=")) {
                    String token = url.replace(GHConfig.OAUTH_WEB_FLOW_BASE_URL + "?token=", "");
                    if (!token.equals("")) {
                        GitHubAPI.getInstance().setToken(token);
                        Intent i = new Intent(getActivity(), PreloaderActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        CookieManager cookieManager = CookieManager.getInstance();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            cookieManager.removeAllCookies(null);
                        } else {
                            cookieManager.removeAllCookie();
                        }
                        startActivity(i);
                        getActivity().overridePendingTransition(R.anim.activity_fade_in, R.anim.activity_fade_out);
                        getActivity().finish();
                    } else {
                        Toast.makeText(getActivity(), R.string.error_msg_unforseen, Toast.LENGTH_SHORT).show();
                    }

                }
                return false;
            }
        });

        webView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView webView, int progress) {
                if (progress == 100) {
                    progressBar.setVisibility(View.INVISIBLE);
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    progressBar.setProgress(progress);
                }
            }
        });
        webView.loadUrl(GHConfig.OAUTH_WEB_FLOW_URL);
        return layout;
    }


}
