package com.studygoal.jisc.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.studygoal.jisc.Managers.DataManager;
import com.studygoal.jisc.R;
import com.studygoal.jisc.Activities.SettingsActivity;

public class PrivacyWebViewFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mainView = inflater.inflate(R.layout.privacy_webview_fragment, container, false);
        WebView webView = (WebView) mainView.findViewById(R.id.privacy_web_view);
        webView.loadUrl("https://github.com/jiscdev/learning-analytics/wiki/Privacy-Statement");
        return mainView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(DataManager.getInstance().mainActivity.isLandscape) {
            ((SettingsActivity)getActivity()).fragmentTitle.setText(DataManager.getInstance().mainActivity.getString(R.string.privacy_statement));
        } else {
            DataManager.getInstance().mainActivity.setTitle(DataManager.getInstance().mainActivity.getString(R.string.privacy_statement));
            DataManager.getInstance().mainActivity.hideAllButtons();
            DataManager.getInstance().mainActivity.showCertainButtons(7);
        }
    }


}
