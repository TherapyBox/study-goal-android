package com.studygoal.jisc.Fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatTextView;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.activeandroid.query.Select;
import com.studygoal.jisc.Adapters.ActivityTypeAdapter;
import com.studygoal.jisc.Adapters.ChooseActivityAdapter;
import com.studygoal.jisc.Adapters.GenericAdapter;
import com.studygoal.jisc.Managers.DataManager;
import com.studygoal.jisc.Managers.NetworkManager;
import com.studygoal.jisc.Managers.xApi.LogActivityEvent;
import com.studygoal.jisc.Managers.xApi.XApiManager;
import com.studygoal.jisc.Models.Module;
import com.studygoal.jisc.Models.Targets;
import com.studygoal.jisc.R;
import com.studygoal.jisc.Utils.Utils;
import com.studygoal.jisc.databinding.TargetAddTargetBinding;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddTarget extends Fragment {
    private static final String TAG = AddTarget.class.getSimpleName();

    public Boolean isInEditMode = false;
    public Targets item;

    private AppCompatTextView mActivityType;
    private AppCompatTextView mChooseActivity;
    private AppCompatTextView mEvery;
    private AppCompatTextView mIn;

    private EditText mHours;
    private EditText mMinutes;
    private EditText mBecause;
    private View mBody;

    private RelativeLayout mAddModuleLayout;

    private View mRoot = null;

    private TextWatcher mHoursWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            int maxValue = 8;

            if (mEvery != null) {
                if (mEvery.getText().toString().equals(getActivity().getString(R.string.day))) {
                    maxValue = 8;
                } else if (mEvery.getText().toString().equals(getActivity().getString(R.string.week))) {
                    maxValue = 40;
                } else if (mEvery.getText().toString().equals(getActivity().getString(R.string.month))) {
                    maxValue = 99;
                }
            }

            Log.e(TAG, "Max: " + maxValue);

            if (mHours != null && s.toString().length() != 0) {
                int value = Integer.parseInt(s.toString());

                if (value < 0) {
                    mHours.setText("0");
                }

                if (value > maxValue) {
                    mHours.setText("" + maxValue);
                }

                mHours.setSelection(mHours.getText().length());
            }
        }
    };

    private TextWatcher mMinutesWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (mMinutes != null && s.toString().length() != 0) {
                int value = Integer.parseInt(s.toString());

                if (value < 0 || value > 60) {
                    mMinutes.setText("");
                    mMinutes.setSelection(mMinutes.getText().length());
                }
            }
        }
    };

    private TargetAddTargetBinding mBinding = null;

    private boolean mIsRecurringTarget = true;

    public AddTarget() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.target_add_target, container, false);
        mRoot = mBinding.root;

        DataManager.getInstance().reload();
        applyTypeface();

        mBinding.targetSelector.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.target_recurring) {
                mIsRecurringTarget = true;
                mBinding.recurringLayout.setVisibility(View.VISIBLE);
                mBinding.singleLayout.setVisibility(View.GONE);
            } else {
                mIsRecurringTarget = false;
                mBinding.recurringLayout.setVisibility(View.GONE);
                mBinding.singleLayout.setVisibility(View.VISIBLE);
            }
        });

        mIsRecurringTarget = true;
        mBinding.recurringLayout.setVisibility(View.VISIBLE);
        mBinding.singleLayout.setVisibility(View.GONE);

        mRoot.findViewById(R.id.add_module_button_text).setOnClickListener(v -> onAddModule());
        mBody = mRoot.findViewById(R.id.addtarget_container);

        mActivityType = ((AppCompatTextView) mRoot.findViewById(R.id.addtarget_activityType_textView));
        mActivityType.setSupportBackgroundTintList(ColorStateList.valueOf(0xFF8a63cc));

        mChooseActivity = ((AppCompatTextView) mRoot.findViewById(R.id.addtarget_chooseActivity_textView));
        mChooseActivity.setSupportBackgroundTintList(ColorStateList.valueOf(0xFF8a63cc));

        mAddModuleLayout = (RelativeLayout) mRoot.findViewById(R.id.add_new_module_layout);
        mAddModuleLayout.setVisibility(View.GONE);

        mHours = ((EditText) mRoot.findViewById(R.id.addtarget_text_timer_1));
        mHours.addTextChangedListener(mHoursWatcher);

        mMinutes = ((EditText) mRoot.findViewById(R.id.addtarget_text_timer_3));
        mMinutes.addTextChangedListener(mMinutesWatcher);

        mEvery = ((AppCompatTextView) mRoot.findViewById(R.id.addtarget_every_textView));
        mEvery.setSupportBackgroundTintList(ColorStateList.valueOf(0xFF8a63cc));

        mIn = ((AppCompatTextView) mRoot.findViewById(R.id.addtarget_in_textView));
        mIn.setSupportBackgroundTintList(ColorStateList.valueOf(0xFF8a63cc));
        mBinding.addtargetInTextViewSingle.setSupportBackgroundTintList(ColorStateList.valueOf(0xFF8a63cc));

        mBecause = ((EditText) mRoot.findViewById(R.id.addtarget_edittext_because));
        mBecause.setOnTouchListener((view, event) -> {
            if (view.getId() == R.id.log_activity_edittext_note) {
                view.getParent().requestDisallowInterceptTouchEvent(true);

                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_UP:
                        view.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }
            }
            return false;
        });

        mBinding.addtargetTextDate.setOnClickListener(v -> onSelectDate());

        if (isInEditMode) {
            for (Map.Entry<String, String> entry : DataManager.getInstance().api_values.entrySet()) {
                if (entry.getValue().equals(item.activity_type))
                    mActivityType.setText(entry.getKey());
            }

            for (Map.Entry<String, String> entry : DataManager.getInstance().api_values.entrySet()) {
                if (entry.getValue().equals(item.activity))
                    mChooseActivity.setText(entry.getKey());
            }

            mHours.setText(Integer.parseInt(item.total_time) / 60 > 10 ? "" + Integer.parseInt(item.total_time) / 60 : "0" + Integer.parseInt(item.total_time) / 60);
            mMinutes.setText(Integer.parseInt(item.total_time) % 60 > 10 ? "" + Integer.parseInt(item.total_time) % 60 : "0" + Integer.parseInt(item.total_time) % 60);

            for (Map.Entry<String, String> entry : DataManager.getInstance().api_values.entrySet()) {
                if (entry.getValue().toLowerCase().equals(item.time_span.toLowerCase())) {
                    String value = entry.getKey();
                    value = value.substring(0, 1).toUpperCase() + value.substring(1, value.length());
                    mEvery.setText(value);
                }
            }

            if (item.module_id.equals("")) {
                mIn.setText(DataManager.getInstance().mainActivity.getString(R.string.any_module));
            } else {
                mIn.setText(((Module) (new Select().from(Module.class).where("module_id = ?", item.module_id).executeSingle())).name);
            }

            // TODO: need fetch saved date
            if (item.module_id.equals("")) {
                mBinding.addtargetInTextViewSingle.setText(DataManager.getInstance().mainActivity.getString(R.string.any_module));
            } else {
                mBinding.addtargetInTextViewSingle.setText(((Module) (new Select().from(Module.class).where("module_id = ?", item.module_id).executeSingle())).name);
            }

            mBecause.setText(item.because);

            // TODO: need fetch saved date
            Calendar calendar = Calendar.getInstance();
            //calendar.set(Integer.parseInt(item.activity_date.split("-")[0]), Integer.parseInt(item.activity_date.split("-")[1]) - 1, Integer.parseInt(item.activity_date.split("-")[2]));
            mBinding.addtargetTextDate.setText(Utils.formatDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)));
            mBinding.addtargetTextDate.setTag(calendar.get(Calendar.YEAR) + "-" + ((calendar.get(Calendar.MONTH) + 1) < 10 ? "0" + (calendar.get(Calendar.MONTH) + 1) : (calendar.get(Calendar.MONTH) + 1)) + "-" + ((calendar.get(Calendar.DAY_OF_MONTH)) < 10 ? "0" + calendar.get(Calendar.DAY_OF_MONTH) : calendar.get(Calendar.DAY_OF_MONTH)));
        } else {
            mActivityType.setText(DataManager.getInstance().activity_type.get(0));
            mChooseActivity.setText(DataManager.getInstance().choose_activity.get(DataManager.getInstance().activity_type.get(0)).get(0));
            mActivityType.setOnClickListener(v -> onAddTargetActivityType());
            mChooseActivity.setOnClickListener(v -> onAddTargetChooseActivity());

            mEvery.setText(DataManager.getInstance().period.get(0));
            mIn.setText(DataManager.getInstance().mainActivity.getString(R.string.any_module));
            mBinding.addtargetInTextViewSingle.setText(DataManager.getInstance().mainActivity.getString(R.string.any_module));

            Calendar calendar = Calendar.getInstance();
            mBinding.addtargetTextDate.setText(Utils.formatDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)));
            mBinding.addtargetTextDate.setTag(calendar.get(Calendar.YEAR) + "-" + ((calendar.get(Calendar.MONTH) + 1) < 10 ? "0" + (calendar.get(Calendar.MONTH) + 1) : (calendar.get(Calendar.MONTH) + 1)) + "-" + ((calendar.get(Calendar.DAY_OF_MONTH)) < 10 ? "0" + calendar.get(Calendar.DAY_OF_MONTH) : calendar.get(Calendar.DAY_OF_MONTH)));
        }

        mEvery.setOnClickListener(v -> onAddTargetEvery());
        mIn.setOnClickListener(v -> onAddTargetIn());
        mBinding.addtargetInTextViewSingle.setOnClickListener(v -> onAddTargetIn());
        mBinding.addtargetSaveBtn.setOnClickListener(v -> onAddTargetSave());
        mBinding.addtargetSaveBtnSingle.setOnClickListener(v -> onAddTargetSingleSave());

        final View contentView = container;
        container.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            private int mPreviousHeight;

            @Override
            public void onGlobalLayout() {
                int newHeight = contentView.getHeight();

                if (mPreviousHeight != 0) {
                    if (mPreviousHeight > newHeight) {
                        // Height decreased: keyboard was shown
                        mBinding.contentScroll.setPadding(0, 0, 0, 200);

                        if (mBecause.isFocused()) {
                            final Handler handler = new Handler();

                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    //Do something after 100ms
                                    mBinding.addtargetContainer.scrollTo(0, mBinding.contentScroll.getHeight());
                                }
                            }, 100);
                        }

                    } else if (mPreviousHeight < newHeight) {
                        mBinding.contentScroll.setPadding(0, 0, 0, 0);
                    } else {
                        // No change
                    }
                }
                mPreviousHeight = newHeight;
            }
        });

        return mRoot;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (isInEditMode) {
            DataManager.getInstance().mainActivity.setTitle(DataManager.getInstance().mainActivity.getString(R.string.edit_target));
        } else {
            DataManager.getInstance().mainActivity.setTitle(DataManager.getInstance().mainActivity.getString(R.string.add_target));
        }

        DataManager.getInstance().mainActivity.hideAllButtons();
        DataManager.getInstance().mainActivity.showCertainButtons(8);
        DataManager.getInstance().addTarget = 1;

        String module = (mBinding.addModuleEditText != null && mBinding.addModuleEditText.getText() != null) ? mBinding.addModuleEditText.getText().toString() : null;
        XApiManager.getInstance().sendLogActivityEvent(LogActivityEvent.AddTarget, module);
    }

    private void applyTypeface() {
        if (mRoot != null) {
            mBinding.targetRecurring.setTypeface(DataManager.getInstance().myriadpro_regular);
            mBinding.targetSingle.setTypeface(DataManager.getInstance().myriadpro_regular);
            mBinding.addtargetActivityTypeText.setTypeface(DataManager.getInstance().myriadpro_regular);
            mBinding.addtargetTextChoose.setTypeface(DataManager.getInstance().myriadpro_regular);
            mBinding.addtargetEveryTextView.setTypeface(DataManager.getInstance().myriadpro_regular);
            mBinding.addtargetActivityTypeText.setTypeface(DataManager.getInstance().myriadpro_regular);
            mBinding.addtargetChooseActivityTextView.setTypeface(DataManager.getInstance().myriadpro_regular);
            mBinding.addtargetEdittextBecause.setTypeface(DataManager.getInstance().myriadpro_regular);
            mBinding.addtargetEdittextBecauseSingle.setTypeface(DataManager.getInstance().myriadpro_regular);
            mBinding.addtargetEdittextMyGoalSingle.setTypeface(DataManager.getInstance().myriadpro_regular);
            mBinding.addtargetTextTimer1.setTypeface(DataManager.getInstance().myriadpro_regular);
            mBinding.addtargetTextTimer3.setTypeface(DataManager.getInstance().myriadpro_regular);
            mBinding.addtargetTextHours.setTypeface(DataManager.getInstance().myriadpro_regular);
            mBinding.addtargetTextMinutes.setTypeface(DataManager.getInstance().myriadpro_regular);
            mBinding.addModuleEditText.setTypeface(DataManager.getInstance().myriadpro_regular);
            mBinding.addModuleButtonText.setTypeface(DataManager.getInstance().myriadpro_regular);
            mBinding.addtargetTextFor.setTypeface(DataManager.getInstance().myriadpro_regular);
            mBinding.addtargetEveryText.setTypeface(DataManager.getInstance().myriadpro_regular);
            mBinding.addtargetInText.setTypeface(DataManager.getInstance().myriadpro_regular);
            mBinding.addtargetInText.setTypeface(DataManager.getInstance().myriadpro_regular);
            mBinding.addtargetInTextView.setTypeface(DataManager.getInstance().myriadpro_regular);
            mBinding.addtargetInTextSingle.setTypeface(DataManager.getInstance().myriadpro_regular);
            mBinding.addtargetInTextViewSingle.setTypeface(DataManager.getInstance().myriadpro_regular);
            mBinding.addtargetTextBecauseTitle.setTypeface(DataManager.getInstance().myriadpro_regular);
            mBinding.addtargetTextBecauseTitleSingle.setTypeface(DataManager.getInstance().myriadpro_regular);
            mBinding.addtargetTextDateTitle.setTypeface(DataManager.getInstance().myriadpro_regular);
            mBinding.addtargetTextDate.setTypeface(DataManager.getInstance().myriadpro_regular);
        }
    }

    private void onAddModule() {
        EditText add_module_edit_text = (EditText) mAddModuleLayout.findViewById(R.id.add_module_edit_text);
        final String moduleName = add_module_edit_text.getText().toString();
        if (moduleName.length() == 0) {
            Snackbar.make(mBody, R.string.module_name_invalid, Snackbar.LENGTH_LONG).show();
            return;
        }

        new Thread(() -> {
            final HashMap<String, String> params = new HashMap<>();
            params.put("student_id", DataManager.getInstance().user.id);
            params.put("module", moduleName);
            params.put("is_social", "yes");

            if (NetworkManager.getInstance().addModule(params)) {
                // TODO: need delete this cone if it not used
//                    DataManager.getInstance().mainActivity.runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                        }
//                    });
            } else {
                DataManager.getInstance().mainActivity.runOnUiThread(() -> {
                    (DataManager.getInstance().mainActivity).hideProgressBar();
                    Snackbar.make(mBody, R.string.something_went_wrong, Snackbar.LENGTH_LONG).show();
                });
            }
        }).start();

        mAddModuleLayout.setVisibility(View.GONE);
    }

    private void onAddTargetActivityType() {
        final Dialog dialog = new Dialog(DataManager.getInstance().mainActivity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_spinner_layout);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        if (DataManager.getInstance().mainActivity.isLandscape) {
            DisplayMetrics displaymetrics = new DisplayMetrics();
            DataManager.getInstance().mainActivity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
            int width = (int) (displaymetrics.widthPixels * 0.3);

            WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
            params.width = width;
            dialog.getWindow().setAttributes(params);
        }
        ((TextView) dialog.findViewById(R.id.dialog_title)).setTypeface(DataManager.getInstance().oratorstd_typeface);
        ((TextView) dialog.findViewById(R.id.dialog_title)).setText(R.string.choose_activity_type);

        final ListView listView = (ListView) dialog.findViewById(R.id.dialog_listview);
        listView.setAdapter(new ActivityTypeAdapter(DataManager.getInstance().mainActivity, mActivityType.getText().toString()));
        listView.setOnItemClickListener((parent, view, position, id) -> {
            mActivityType.setText(((TextView) view.findViewById(R.id.dialog_item_name)).getText().toString());
            mChooseActivity.setText(DataManager.getInstance().choose_activity.get(mActivityType.getText().toString()).get(0));
            dialog.dismiss();
        });

        dialog.show();
    }

    private void onAddTargetChooseActivity() {
        final Dialog dialog = new Dialog(DataManager.getInstance().mainActivity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_spinner_layout);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        if (DataManager.getInstance().mainActivity.isLandscape) {
            DisplayMetrics displaymetrics = new DisplayMetrics();
            DataManager.getInstance().mainActivity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
            int width = (int) (displaymetrics.widthPixels * 0.3);

            WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
            params.width = width;
            dialog.getWindow().setAttributes(params);
        }
        ((TextView) dialog.findViewById(R.id.dialog_title)).setTypeface(DataManager.getInstance().oratorstd_typeface);
        ((TextView) dialog.findViewById(R.id.dialog_title)).setText(R.string.choose_activity);

        final ListView listView = (ListView) dialog.findViewById(R.id.dialog_listview);
        listView.setAdapter(new ChooseActivityAdapter(DataManager.getInstance().mainActivity, mChooseActivity.getText().toString(), mActivityType.getText().toString()));
        listView.setOnItemClickListener((parent, view, position, id) -> {
            mChooseActivity.setText(((TextView) view.findViewById(R.id.dialog_item_name)).getText().toString());
            dialog.dismiss();
        });

        dialog.show();
    }

    private void onAddTargetCardView() {
        final Dialog dialog = new Dialog(DataManager.getInstance().mainActivity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.timespent_layout);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        if (DataManager.getInstance().mainActivity.isLandscape) {
            DisplayMetrics displaymetrics = new DisplayMetrics();
            DataManager.getInstance().mainActivity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
            int width = (int) (displaymetrics.widthPixels * 0.3);

            WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
            params.width = width;
            dialog.getWindow().setAttributes(params);
        }

        final NumberPicker hourPicker = (NumberPicker) dialog.findViewById(R.id.hour_picker);
        hourPicker.setMinValue(0);

        if (mEvery.getText().toString().equals(getString(R.string.daily))) {
            hourPicker.setMaxValue(23);
        } else {
            hourPicker.setMaxValue(71);
        }

        hourPicker.setValue(Integer.parseInt(mHours.getText().toString()));
        hourPicker.setFormatter(value -> String.format("%02d", value));

        final NumberPicker minutePicker = (NumberPicker) dialog.findViewById(R.id.minute_picker);
        minutePicker.setMinValue(0);
        minutePicker.setMaxValue(59);
        minutePicker.setValue(Integer.parseInt(mMinutes.getText().toString()));

        minutePicker.setFormatter(value -> {
            if (value < 10) {
                return "0" + value;
            } else {
                return value + "";
            }
        });

        ((TextView) dialog.findViewById(R.id.timespent_save_text)).setTypeface(DataManager.getInstance().myriadpro_regular);

        dialog.findViewById(R.id.timespent_save_btn).setOnClickListener(v -> {
            int hour = hourPicker.getValue();

            if (hour < 10) {
                mHours.setText("0" + hour);
            } else {
                mHours.setText("" + hour);
            }

            int minute = minutePicker.getValue();

            if (minute < 10) {
                mMinutes.setText("0" + minute);
            } else {
                mMinutes.setText("" + minute);
            }

            dialog.dismiss();
        });

        ((TextView) dialog.findViewById(R.id.dialog_title)).setTypeface(DataManager.getInstance().oratorstd_typeface);
        dialog.show();
    }

    private void onAddTargetEvery() {
        final Dialog dialog = new Dialog(DataManager.getInstance().mainActivity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_spinner_layout);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        if (DataManager.getInstance().mainActivity.isLandscape) {
            DisplayMetrics displaymetrics = new DisplayMetrics();
            DataManager.getInstance().mainActivity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
            int width = (int) (displaymetrics.widthPixels * 0.3);

            WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
            params.width = width;
            dialog.getWindow().setAttributes(params);
        }

        ((TextView) dialog.findViewById(R.id.dialog_title)).setTypeface(DataManager.getInstance().oratorstd_typeface);
        ((TextView) dialog.findViewById(R.id.dialog_title)).setText(R.string.choose_interval);

        final ListView listView = (ListView) dialog.findViewById(R.id.dialog_listview);
        listView.setAdapter(new GenericAdapter(DataManager.getInstance().mainActivity, mEvery.getText().toString(), DataManager.getInstance().period));
        listView.setOnItemClickListener((parent, view, position, id) -> {
            mEvery.setText(((TextView) view.findViewById(R.id.dialog_item_name)).getText().toString());
            mHours.setText(mHours.getText().toString());
            dialog.dismiss();
        });

        dialog.show();

    }

    private void onAddTargetIn() {
        final Dialog dialog = new Dialog(DataManager.getInstance().mainActivity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_spinner_layout);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        if (DataManager.getInstance().mainActivity.isLandscape) {
            DisplayMetrics displaymetrics = new DisplayMetrics();
            DataManager.getInstance().mainActivity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
            int width = (int) (displaymetrics.widthPixels * 0.3);

            WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
            params.width = width;
            dialog.getWindow().setAttributes(params);
        }

        ((TextView) dialog.findViewById(R.id.dialog_title)).setTypeface(DataManager.getInstance().oratorstd_typeface);
        ((TextView) dialog.findViewById(R.id.dialog_title)).setText(R.string.choose_module);

        final ListView listView = (ListView) dialog.findViewById(R.id.dialog_listview);

        final ArrayList<String> items = new ArrayList<>();
        items.add(DataManager.getInstance().mainActivity.getString(R.string.any_module));
        List<Module> modules = new Select().from(Module.class).execute();

        for (int i = 0; i < modules.size(); i++)
            items.add(modules.get(i).name);

        if (DataManager.getInstance().user.isSocial) {
            items.add(AddTarget.this.getActivity().getString(R.string.add_module));
        }

        listView.setAdapter(new GenericAdapter(DataManager.getInstance().mainActivity, mIn.getText().toString(), items));
        listView.setOnItemClickListener((parent, view, position, id) -> {
            if (DataManager.getInstance().user.isSocial
                    && position == items.size() - 1) {
                //add new module
                EditText add_module_edit_text = (EditText) mAddModuleLayout.findViewById(R.id.add_module_edit_text);
                add_module_edit_text.setText("");
                mAddModuleLayout.setVisibility(View.VISIBLE);
                dialog.dismiss();
            } else {
                mIn.setText(((TextView) view.findViewById(R.id.dialog_item_name)).getText().toString());
                dialog.dismiss();
            }
        });

        dialog.show();

    }

    private void onAddTargetSave() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        if (isInEditMode) {
            if (DataManager.getInstance().user.isDemo) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(AddTarget.this.getActivity());
                alertDialogBuilder.setTitle(Html.fromHtml("<font color='#3791ee'>" + getString(R.string.demo_mode_edittarget) + "</font>"));
                alertDialogBuilder.setNegativeButton("Ok", (dialog, which) -> dialog.dismiss());
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                return;
            }

            final int total_time = Integer.parseInt(mHours.getText().toString()) * 60 + Integer.parseInt(mMinutes.getText().toString());
            if (total_time == Integer.parseInt(item.total_time)
                    && mEvery.getText().toString().toLowerCase().equals(item.time_span.toLowerCase())
                    && (item.because.equals(mBecause.getText().toString()))
                    && (item.module_id.equals("") && mIn.getText().toString().equals(DataManager.getInstance().mainActivity.getString(R.string.any_module)))) {
                DataManager.getInstance().mainActivity.onBackPressed();
                return;
            }
            if (total_time == 0) {
                Snackbar.make(mBody, R.string.fail_to_edit_target_insuficient_time, Snackbar.LENGTH_LONG).show();
                return;
            } else {
                final HashMap<String, String> params = new HashMap<>();
                params.put("student_id", DataManager.getInstance().user.id);
                params.put("target_id", item.target_id);
                params.put("total_time", total_time + "");
                params.put("time_span", DataManager.getInstance().api_values.get(mEvery.getText().toString().toLowerCase()));
                if (!mIn.getText().toString().toLowerCase().equals(DataManager.getInstance().mainActivity.getString(R.string.any_module).toLowerCase()))
                    params.put("module", ((Module) (new Select().from(Module.class).where("module_name = ?", mIn.getText().toString()).executeSingle())).id);
                if (mBecause.getText().toString().length() > 0)
                    params.put("because", mBecause.getText().toString());
                System.out.println("EDIT_TARGET: " + params.toString());
                DataManager.getInstance().mainActivity.showProgressBar(null);
                Calendar calendar = Calendar.getInstance();
                String modified_date = "";
                modified_date += calendar.get(Calendar.YEAR) + "-";
                modified_date += (calendar.get(Calendar.MONTH) + 1) < 10 ? "0" + (calendar.get(Calendar.MONTH) + 1) + "-" : (calendar.get(Calendar.MONTH) + 1) + "-";
                modified_date += calendar.get(Calendar.DAY_OF_MONTH) < 10 ? "0" + calendar.get(Calendar.DAY_OF_MONTH) + " " : calendar.get(Calendar.DAY_OF_MONTH) + " ";
                modified_date += calendar.get(Calendar.HOUR_OF_DAY) < 10 ? "0" + calendar.get(Calendar.HOUR_OF_DAY) + ":" : calendar.get(Calendar.HOUR_OF_DAY) + ":";
                modified_date += calendar.get(Calendar.MINUTE) < 10 ? "0" + calendar.get(Calendar.MINUTE) + ":" : calendar.get(Calendar.MINUTE) + ":";
                modified_date += calendar.get(Calendar.SECOND) < 10 ? "0" + calendar.get(Calendar.SECOND) : calendar.get(Calendar.SECOND);

                final String finalModified_date = modified_date;

                new Thread(() -> {
                    if (NetworkManager.getInstance().editTarget(params)) {
                        DataManager.getInstance().mainActivity.runOnUiThread(() -> {
                            item.total_time = total_time + "";
                            item.time_span = DataManager.getInstance().api_values.get(mEvery.getText().toString().toLowerCase());
                            if (!mIn.getText().toString().toLowerCase().equals(DataManager.getInstance().mainActivity.getString(R.string.any_module).toLowerCase()))
                                item.module_id = ((Module) new Select().from(Module.class).where("module_name = ?", mIn.getText().toString()).executeSingle()).id;
                            else
                                item.module_id = "";
                            item.because = mBecause.getText().toString();
                            item.modified_date = finalModified_date;

                            DataManager.getInstance().mainActivity.hideProgressBar();
                            DataManager.getInstance().mainActivity.onBackPressed();
//                                            Snackbar.make(body, R.string.target_saved, Snackbar.LENGTH_LONG).show();
                        });
                    } else {
                        DataManager.getInstance().mainActivity.runOnUiThread(() -> {
                            DataManager.getInstance().mainActivity.hideProgressBar();
                            Snackbar.make(mBody, R.string.something_went_wrong, Snackbar.LENGTH_LONG).show();
                        });
                    }
                }).start();
            }
        } else {

            if (DataManager.getInstance().user.isDemo) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(AddTarget.this.getActivity());
                alertDialogBuilder.setTitle(Html.fromHtml("<font color='#3791ee'>" + getString(R.string.demo_mode_addtarget) + "</font>"));
                alertDialogBuilder.setNegativeButton("Ok", (dialog, which) -> dialog.dismiss());
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                return;
            }

            int total_time = Integer.parseInt(mHours.getText().toString()) * 60 + Integer.parseInt(mMinutes.getText().toString());
            if (total_time == 0) {
                Snackbar.make(mBody, R.string.fail_to_add_target_insufficient_time, Snackbar.LENGTH_LONG).show();
                return;
            } else {
                Module module = ((new Select().from(Module.class).where("module_name = ?", mIn.getText().toString()).executeSingle()));
                String id;
                if (module == null) id = "";
                else if (module.id == null) id = "";
                else id = module.id;
                if (new Select().from(Targets.class).where("activity = ?", mChooseActivity.getText().toString()).and("time_span = ?", mEvery.getText().toString()).and("module_id = ?", id).exists()) {
                    Snackbar.make(mBody, R.string.target_same_parameters, Snackbar.LENGTH_LONG).show();
                    return;
                }
                final HashMap<String, String> params = new HashMap<>();
                params.put("student_id", DataManager.getInstance().user.id);
                params.put("activity_type", DataManager.getInstance().api_values.get(mActivityType.getText().toString()));
                params.put("activity", DataManager.getInstance().api_values.get(mChooseActivity.getText().toString()));
                params.put("total_time", total_time + "");
                params.put("time_span", DataManager.getInstance().api_values.get(mEvery.getText().toString().toLowerCase()));
                if (!mIn.getText().toString().toLowerCase().equals(DataManager.getInstance().mainActivity.getString(R.string.any_module).toLowerCase()))
                    params.put("module", ((Module) (new Select().from(Module.class).where("module_name = ?", mIn.getText().toString()).executeSingle())).id);
                if (mBecause.getText().toString().length() > 0)
                    params.put("because", mBecause.getText().toString());

                System.out.println("ADD_TARGET: " + params.toString());
                DataManager.getInstance().mainActivity.showProgressBar(null);

                new Thread(() -> {
                    if (NetworkManager.getInstance().addTarget(params)) {
                        NetworkManager.getInstance().getTargets(DataManager.getInstance().user.id);
                        DataManager.getInstance().mainActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                DataManager.getInstance().mainActivity.hideProgressBar();
                                DataManager.getInstance().mainActivity.onBackPressed();
//                                            Snackbar.make(body, R.string.target_saved, Snackbar.LENGTH_LONG).show();
                            }
                        });
                    } else {
                        DataManager.getInstance().mainActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                (DataManager.getInstance().mainActivity).hideProgressBar();
                                Snackbar.make(mBody, R.string.something_went_wrong, Snackbar.LENGTH_LONG).show();
                            }
                        });
                    }
                }).start();
            }
        }
    }

    private void onAddTargetSingleSave() {
        // TODO: need implement saving of single target
//        View view = getActivity().getCurrentFocus();
//
//        if (view != null) {
//            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
//            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
//        }
//
//        if (isInEditMode) {
//            if (DataManager.getInstance().user.isDemo) {
//                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(AddTarget.this.getActivity());
//                alertDialogBuilder.setTitle(Html.fromHtml("<font color='#3791ee'>" + getString(R.string.demo_mode_edittarget) + "</font>"));
//                alertDialogBuilder.setNegativeButton("Ok", (dialog, which) -> dialog.dismiss());
//                AlertDialog alertDialog = alertDialogBuilder.create();
//                alertDialog.show();
//                return;
//            }
//
//            final int total_time = Integer.parseInt(mHours.getText().toString()) * 60 + Integer.parseInt(mMinutes.getText().toString());
//            if (total_time == Integer.parseInt(item.total_time)
//                    && mEvery.getText().toString().toLowerCase().equals(item.time_span.toLowerCase())
//                    && (item.because.equals(mBecause.getText().toString()))
//                    && (item.module_id.equals("") && mIn.getText().toString().equals(DataManager.getInstance().mainActivity.getString(R.string.any_module)))) {
//                DataManager.getInstance().mainActivity.onBackPressed();
//                return;
//            }
//            if (total_time == 0) {
//                Snackbar.make(mBody, R.string.fail_to_edit_target_insuficient_time, Snackbar.LENGTH_LONG).show();
//                return;
//            } else {
//                final HashMap<String, String> params = new HashMap<>();
//                params.put("student_id", DataManager.getInstance().user.id);
//                params.put("target_id", item.target_id);
//                params.put("total_time", total_time + "");
//                params.put("time_span", DataManager.getInstance().api_values.get(mEvery.getText().toString().toLowerCase()));
//                if (!mIn.getText().toString().toLowerCase().equals(DataManager.getInstance().mainActivity.getString(R.string.any_module).toLowerCase()))
//                    params.put("module", ((Module) (new Select().from(Module.class).where("module_name = ?", mIn.getText().toString()).executeSingle())).id);
//                if (mBecause.getText().toString().length() > 0)
//                    params.put("because", mBecause.getText().toString());
//                System.out.println("EDIT_TARGET: " + params.toString());
//                DataManager.getInstance().mainActivity.showProgressBar(null);
//                Calendar calendar = Calendar.getInstance();
//                String modified_date = "";
//                modified_date += calendar.get(Calendar.YEAR) + "-";
//                modified_date += (calendar.get(Calendar.MONTH) + 1) < 10 ? "0" + (calendar.get(Calendar.MONTH) + 1) + "-" : (calendar.get(Calendar.MONTH) + 1) + "-";
//                modified_date += calendar.get(Calendar.DAY_OF_MONTH) < 10 ? "0" + calendar.get(Calendar.DAY_OF_MONTH) + " " : calendar.get(Calendar.DAY_OF_MONTH) + " ";
//                modified_date += calendar.get(Calendar.HOUR_OF_DAY) < 10 ? "0" + calendar.get(Calendar.HOUR_OF_DAY) + ":" : calendar.get(Calendar.HOUR_OF_DAY) + ":";
//                modified_date += calendar.get(Calendar.MINUTE) < 10 ? "0" + calendar.get(Calendar.MINUTE) + ":" : calendar.get(Calendar.MINUTE) + ":";
//                modified_date += calendar.get(Calendar.SECOND) < 10 ? "0" + calendar.get(Calendar.SECOND) : calendar.get(Calendar.SECOND);
//
//                final String finalModified_date = modified_date;
//
//                new Thread(() -> {
//                    if (NetworkManager.getInstance().editTarget(params)) {
//                        DataManager.getInstance().mainActivity.runOnUiThread(() -> {
//                            item.total_time = total_time + "";
//                            item.time_span = DataManager.getInstance().api_values.get(mEvery.getText().toString().toLowerCase());
//                            if (!mIn.getText().toString().toLowerCase().equals(DataManager.getInstance().mainActivity.getString(R.string.any_module).toLowerCase()))
//                                item.module_id = ((Module) new Select().from(Module.class).where("module_name = ?", mIn.getText().toString()).executeSingle()).id;
//                            else
//                                item.module_id = "";
//                            item.because = mBecause.getText().toString();
//                            item.modified_date = finalModified_date;
//
//                            DataManager.getInstance().mainActivity.hideProgressBar();
//                            DataManager.getInstance().mainActivity.onBackPressed();
////                                            Snackbar.make(body, R.string.target_saved, Snackbar.LENGTH_LONG).show();
//                        });
//                    } else {
//                        DataManager.getInstance().mainActivity.runOnUiThread(() -> {
//                            DataManager.getInstance().mainActivity.hideProgressBar();
//                            Snackbar.make(mBody, R.string.something_went_wrong, Snackbar.LENGTH_LONG).show();
//                        });
//                    }
//                }).start();
//            }
//        } else {
//            if (DataManager.getInstance().user.isDemo) {
//                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(AddTarget.this.getActivity());
//                alertDialogBuilder.setTitle(Html.fromHtml("<font color='#3791ee'>" + getString(R.string.demo_mode_addtarget) + "</font>"));
//                alertDialogBuilder.setNegativeButton("Ok", (dialog, which) -> dialog.dismiss());
//                AlertDialog alertDialog = alertDialogBuilder.create();
//                alertDialog.show();
//                return;
//            }
//
//            int total_time = Integer.parseInt(mHours.getText().toString()) * 60 + Integer.parseInt(mMinutes.getText().toString());
//
//            if (total_time == 0) {
//                Snackbar.make(mBody, R.string.fail_to_add_target_insufficient_time, Snackbar.LENGTH_LONG).show();
//                return;
//            } else {
//                Module module = ((new Select().from(Module.class).where("module_name = ?", mIn.getText().toString()).executeSingle()));
//                String id;
//                if (module == null) id = "";
//                else if (module.id == null) id = "";
//                else id = module.id;
//                if (new Select().from(Targets.class).where("activity = ?", mChooseActivity.getText().toString()).and("time_span = ?", mEvery.getText().toString()).and("module_id = ?", id).exists()) {
//                    Snackbar.make(mBody, R.string.target_same_parameters, Snackbar.LENGTH_LONG).show();
//                    return;
//                }
//                final HashMap<String, String> params = new HashMap<>();
//                params.put("student_id", DataManager.getInstance().user.id);
//                params.put("activity_type", DataManager.getInstance().api_values.get(mActivityType.getText().toString()));
//                params.put("activity", DataManager.getInstance().api_values.get(mChooseActivity.getText().toString()));
//                params.put("total_time", total_time + "");
//                params.put("time_span", DataManager.getInstance().api_values.get(mEvery.getText().toString().toLowerCase()));
//                if (!mIn.getText().toString().toLowerCase().equals(DataManager.getInstance().mainActivity.getString(R.string.any_module).toLowerCase()))
//                    params.put("module", ((Module) (new Select().from(Module.class).where("module_name = ?", mIn.getText().toString()).executeSingle())).id);
//                if (mBecause.getText().toString().length() > 0)
//                    params.put("because", mBecause.getText().toString());
//
//                System.out.println("ADD_TARGET: " + params.toString());
//                DataManager.getInstance().mainActivity.showProgressBar(null);
//
//                new Thread(() -> {
//                    if (NetworkManager.getInstance().addTarget(params)) {
//                        NetworkManager.getInstance().getTargets(DataManager.getInstance().user.id);
//                        DataManager.getInstance().mainActivity.runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                DataManager.getInstance().mainActivity.hideProgressBar();
//                                DataManager.getInstance().mainActivity.onBackPressed();
////                                            Snackbar.make(body, R.string.target_saved, Snackbar.LENGTH_LONG).show();
//                            }
//                        });
//                    } else {
//                        DataManager.getInstance().mainActivity.runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                (DataManager.getInstance().mainActivity).hideProgressBar();
//                                Snackbar.make(mBody, R.string.something_went_wrong, Snackbar.LENGTH_LONG).show();
//                            }
//                        });
//                    }
//                }).start();
//            }
//        }
    }

    private void onSelectDate() {
        DatePickerFragment newFragment = new DatePickerFragment();
        newFragment.setListener((view, year, monthOfYear, dayOfMonth) -> {
            mBinding.addtargetTextDate.setText(Utils.formatDate(year, monthOfYear, dayOfMonth));
            mBinding.addtargetTextDate.setTag(year + "-" + ((monthOfYear + 1) < 10 ? "0" + (monthOfYear + 1) : (monthOfYear + 1)) + "-" + (dayOfMonth < 10 ? "0" + dayOfMonth : dayOfMonth));
        });

        newFragment.show(DataManager.getInstance().mainActivity.getSupportFragmentManager(), "datePicker");
    }
}
