package com.studygoal.jisc.Fragments;

import android.support.v4.app.Fragment;

public abstract class BaseFragment extends Fragment {
    private static final String TAG = BaseFragment.class.getSimpleName();

    protected void runOnUiThread(Runnable action) {
        if (action != null && getActivity() != null) {
            getActivity().runOnUiThread(action);
        }
    }
}
