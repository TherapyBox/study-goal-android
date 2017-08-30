package com.studygoal.jisc.Fragments;

import android.app.AlertDialog;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.activeandroid.query.Select;
import com.studygoal.jisc.Adapters.TargetAdapter;
import com.studygoal.jisc.Managers.DataManager;
import com.studygoal.jisc.Managers.NetworkManager;
import com.studygoal.jisc.Managers.xApi.LogActivityEvent;
import com.studygoal.jisc.Managers.xApi.XApiManager;
import com.studygoal.jisc.Models.Targets;
import com.studygoal.jisc.R;
import com.studygoal.jisc.databinding.TargetFragmentBinding;

import java.util.HashMap;

public class TargetFragment extends Fragment {
    private static final String TAG = TargetFragment.class.getSimpleName();

    private ListView mList;
    private TargetAdapter mAdapter;
    private View mRootView;
    private View mTutorialMessage;
    private SwipeRefreshLayout mLayout;

    private TargetFragmentBinding mBinding = null;

    public TargetFragment() {
    }

    @Override
    public void onResume() {
        super.onResume();
        DataManager.getInstance().mainActivity.setTitle(DataManager.getInstance().mainActivity.getString(R.string.target));
        DataManager.getInstance().mainActivity.hideAllButtons();
        DataManager.getInstance().mainActivity.showCertainButtons(4);

        new Thread(() -> {
            DataManager.getInstance().mainActivity.runOnUiThread(() -> DataManager.getInstance().mainActivity.showProgressBar(null));
            NetworkManager.getInstance().getStretchTargets(DataManager.getInstance().user.id);
            NetworkManager.getInstance().getTargets(DataManager.getInstance().user.id);

            DataManager.getInstance().mainActivity.runOnUiThread(() -> {
                mAdapter.list = new Select().from(Targets.class).execute();
                mAdapter.notifyDataSetChanged();

                if (mAdapter.list.size() == 0) {
                    mTutorialMessage.setVisibility(View.VISIBLE);
                } else {
                    mTutorialMessage.setVisibility(View.GONE);
                }

                DataManager.getInstance().mainActivity.hideProgressBar();
            });

        }).start();

        XApiManager.getInstance().sendLogActivityEvent(LogActivityEvent.NavigateTargetsMain);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.target_fragment, container, false);
        mRootView = mBinding.getRoot();

        mTutorialMessage = mRootView.findViewById(R.id.tutorial_message);
        mLayout = (SwipeRefreshLayout) mRootView.findViewById(R.id.swipelayout);
        mList = (ListView) mRootView.findViewById(R.id.list);

        mAdapter = new TargetAdapter(this);
        mList.setAdapter(mAdapter);

        mList.setOnItemClickListener((parent, v, position, id) -> {
            TargetDetails fragment = new TargetDetails();
            fragment.list = mAdapter.list;
            fragment.position = position;
            DataManager.getInstance().mainActivity.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_fragment, fragment)
                    .addToBackStack(null)
                    .commit();
        });

        mLayout.setColorSchemeResources(R.color.colorPrimary);
        mLayout.setOnRefreshListener(() -> new Thread(() -> {
            NetworkManager.getInstance().getStretchTargets(DataManager.getInstance().user.id);
            if (NetworkManager.getInstance().getTargets(DataManager.getInstance().user.id)) {
                mAdapter.list = new Select().from(Targets.class).execute();

                DataManager.getInstance().mainActivity.runOnUiThread(() -> {
                    mAdapter.notifyDataSetChanged();
                    mLayout.setRefreshing(false);

                    if (mAdapter.list.size() == 0) {
                        mTutorialMessage.setVisibility(View.VISIBLE);
                    } else {
                        mTutorialMessage.setVisibility(View.GONE);
                    }
                });
            } else {
                getActivity().runOnUiThread(() -> mLayout.setRefreshing(false));
            }
        }).start());

        return mRootView;
    }

    public void deleteTarget(final Targets target, final int finalPosition) {
        if (DataManager.getInstance().user.email.equals("demouser@jisc.ac.uk")) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(TargetFragment.this.getActivity());
            alertDialogBuilder.setTitle(Html.fromHtml("<font color='#3791ee'>" + getString(R.string.demo_mode_deletetarget) + "</font>"));
            alertDialogBuilder.setNegativeButton("Ok", (dialog, which) -> dialog.dismiss());
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
            return;
        }

        final HashMap<String, String> params = new HashMap<>();
        params.put("student_id", DataManager.getInstance().user.id);
        params.put("target_id", target.target_id);
        DataManager.getInstance().mainActivity.showProgressBar(null);

        new Thread(() -> {
            if (NetworkManager.getInstance().deleteTarget(params)) {
                DataManager.getInstance().mainActivity.runOnUiThread(() -> {
                    target.delete();
                    mAdapter.list.remove(finalPosition);
                    if (mAdapter.list.size() == 0)
                        mTutorialMessage.setVisibility(View.VISIBLE);
                    else
                        mTutorialMessage.setVisibility(View.GONE);
                    mAdapter.notifyDataSetChanged();
                    DataManager.getInstance().mainActivity.hideProgressBar();
                    Snackbar.make(mRootView.findViewById(R.id.parent), R.string.target_deleted_successfully, Snackbar.LENGTH_LONG).show();
                });
            } else {
                DataManager.getInstance().mainActivity.runOnUiThread(() -> {
                    DataManager.getInstance().mainActivity.hideProgressBar();
                    Snackbar.make(mRootView.findViewById(R.id.parent), R.string.fail_to_delete_target_message, Snackbar.LENGTH_LONG).show();
                });
            }
        }).start();

    }
}
