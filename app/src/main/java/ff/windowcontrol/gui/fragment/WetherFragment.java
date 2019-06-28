package ff.windowcontrol.gui.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import ff.windowcontrol.R;

/**
 * Created by Feng on 2018/3/21.
 */

public class WetherFragment extends Fragment {

    public WebView webView;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wether, container, false);
        setView(view, savedInstanceState);
        return view;
    }

    @SuppressLint("SetJavaScriptEnabled")
    public void setView(View view, Bundle savedInstanceState) {
        webView=(WebView)view.findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl("https://tianqi.moji.com/weather/china/guangxi/guilin");
        webView.setInitialScale(100);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }


}
