package com.peeradon.android.musicovery;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * InfoFragment.java
 * A Fragment that shows a WebView pointing to Wikipedia
 *
 * Has a public method that the MainActivity can call to change the URL of the WebView
 */

public class InfoFragment extends Fragment {

    private String country;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_info, container, false);

        WebView webView = (WebView) v.findViewById(R.id.webView);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl("https://en.m.wikipedia.org/wiki/" + country);

        return v;
    }

    public void update(String country) {
        this.country = country;
    }
}