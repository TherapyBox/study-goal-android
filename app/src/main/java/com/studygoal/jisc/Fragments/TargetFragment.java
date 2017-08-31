package com.studygoal.jisc.Fragments;

import android.app.AlertDialog;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

import com.activeandroid.query.Select;
import com.studygoal.jisc.Adapters.TargetAdapter;
import com.studygoal.jisc.Adapters.ToDoTasksAdapter;
import com.studygoal.jisc.Managers.DataManager;
import com.studygoal.jisc.Managers.NetworkManager;
import com.studygoal.jisc.Managers.xApi.LogActivityEvent;
import com.studygoal.jisc.Managers.xApi.XApiManager;
import com.studygoal.jisc.Models.Targets;
import com.studygoal.jisc.Models.ToDoTasks;
import com.studygoal.jisc.R;
import com.studygoal.jisc.databinding.TargetFragmentBinding;

import java.util.HashMap;

public class TargetFragment extends BaseFragment {
    private static final String TAG = TargetFragment.class.getSimpleName();

    private TargetAdapter mAdapterTarget;
    private ToDoTasksAdapter mAdapterToDo;

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

        XApiManager.getInstance().sendLogActivityEvent(LogActivityEvent.NavigateTargetsMain);
        loadData(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.target_fragment, container, false);
        mRootView = mBinding.getRoot();

        mBinding.targetSelector.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.target_recurring) {
                mBinding.list.setVisibility(View.VISIBLE);
                mBinding.listTodo.setVisibility(View.GONE);
            } else {
                mBinding.list.setVisibility(View.GONE);
                mBinding.listTodo.setVisibility(View.VISIBLE);
            }

            updateTutorialMessage();
        });

        mTutorialMessage = mRootView.findViewById(R.id.tutorial_message);
        mLayout = (SwipeRefreshLayout) mRootView.findViewById(R.id.swipelayout);

        mAdapterTarget = new TargetAdapter(getActivity(), new TargetAdapter.TargetAdapterListener() {
            @Override
            public void onDelete(Targets target, int finalPosition) {
                deleteTarget(target, finalPosition);
            }

            @Override
            public void onEdit(Targets targets) {
                editTarget(targets);
            }
        });

        mAdapterToDo = new ToDoTasksAdapter(getActivity(), new ToDoTasksAdapter.ToDoTasksAdapterListener() {
            @Override
            public void onDelete(ToDoTasks target, int finalPosition) {
                deleteToDoTasks(target, finalPosition);
            }

            @Override
            public void onEdit(ToDoTasks targets) {
                editToDoTasks(targets);
            }
        });

        mBinding.list.setAdapter(mAdapterTarget);
        mBinding.listTodo.setAdapter(mAdapterToDo);

        mBinding.list.setOnItemClickListener((parent, v, position, id) -> {
            TargetDetails fragment = new TargetDetails();
            fragment.list = mAdapterTarget.list;
            fragment.position = position;
            DataManager.getInstance().mainActivity.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_fragment, fragment)
                    .addToBackStack(null)
                    .commit();
        });

        mBinding.listTodo.setOnItemClickListener((parent, v, position, id) -> {
            // no need any action
        });

        mBinding.list.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                int topRowVerticalPosition = (mBinding.list == null || mBinding.list.getChildCount() == 0) ? 0 : mBinding.list.getChildAt(0).getTop();
                mLayout.setEnabled(firstVisibleItem == 0 && topRowVerticalPosition >= 0);
            }
        });

        mBinding.listTodo.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                int topRowVerticalPosition = (mBinding.list == null || mBinding.list.getChildCount() == 0) ? 0 : mBinding.list.getChildAt(0).getTop();
                mLayout.setEnabled(firstVisibleItem == 0 && topRowVerticalPosition >= 0);
            }
        });

        mLayout.setColorSchemeResources(R.color.colorPrimary);
        mLayout.setOnRefreshListener(() -> loadData(false));

        return mRootView;
    }

    private void deleteTarget(final Targets target, final int finalPosition) {
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
                    mAdapterTarget.list.remove(finalPosition);
                    if (mAdapterTarget.list.size() == 0)
                        mTutorialMessage.setVisibility(View.VISIBLE);
                    else
                        mTutorialMessage.setVisibility(View.GONE);
                    mAdapterTarget.notifyDataSetChanged();
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

    private void deleteToDoTasks(final ToDoTasks task, final int finalPosition) {
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
        params.put("record_id", task.taskId);
        DataManager.getInstance().mainActivity.showProgressBar(null);

        new Thread(() -> {
            if (NetworkManager.getInstance().deleteToDoTask(params)) {
                DataManager.getInstance().mainActivity.runOnUiThread(() -> {
                    task.delete();
                    mAdapterToDo.deleteItem(finalPosition);
                    DataManager.getInstance().mainActivity.hideProgressBar();
                    updateTutorialMessage();
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

    private void editTarget(Targets item) {
        AddTarget fragment = new AddTarget();
        fragment.isInEditMode = true;
        fragment.isSingleTarget = false;
        fragment.item = item;

        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_fragment, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void editToDoTasks(ToDoTasks item) {
        AddTarget fragment = new AddTarget();
        fragment.isInEditMode = true;
        fragment.isSingleTarget = true;
        fragment.itemToDo = item;

        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_fragment, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void loadData(boolean showProgress) {
        if (showProgress) {
            runOnUiThread(() -> DataManager.getInstance().mainActivity.showProgressBar(null));
        }

        new Thread(() -> {
            NetworkManager.getInstance().getStretchTargets(DataManager.getInstance().user.id);
            NetworkManager.getInstance().getTargets(DataManager.getInstance().user.id);
            NetworkManager.getInstance().getToDoTasks(DataManager.getInstance().user.id);

            DataManager.getInstance().mainActivity.runOnUiThread(() -> {
                mAdapterTarget.list = new Select().from(Targets.class).execute();
                mAdapterTarget.notifyDataSetChanged();
                mAdapterToDo.updateList(new Select().from(ToDoTasks.class).execute());

                if (showProgress) {
                    DataManager.getInstance().mainActivity.hideProgressBar();
                } else {
                    mLayout.setRefreshing(false);
                }
            });

            updateTutorialMessage();
        }).start();
    }


    private void updateTutorialMessage() {
        runOnUiThread(() -> {
            if (mBinding.targetRecurring.isChecked()) {
                if (mAdapterTarget != null && mAdapterTarget.list.size() > 0) {
                    mTutorialMessage.setVisibility(View.GONE);
                } else {
                    mTutorialMessage.setVisibility(View.VISIBLE);
                }
            } else if (mBinding.targetSingle.isChecked()) {
                if (mAdapterToDo != null && mAdapterToDo.getCount() > 0) {
                    mTutorialMessage.setVisibility(View.GONE);
                } else {
                    mTutorialMessage.setVisibility(View.VISIBLE);
                }
            }
        });
    }
}
