package com.liskovsoft.smartyoutubetv2.tv.ui.dialogs.other;

import android.content.Context;
import android.util.AttributeSet;

import androidx.preference.ListPreference;
import androidx.preference.Preference;

public class LongClickListPreference extends ListPreference {
    private OnPreferenceLongClickListener mOnPreferenceLongClickListener;
    public LongClickListPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public LongClickListPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public LongClickListPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LongClickListPreference(Context context) {
        super(context);
    }

    public void callPreferenceLongClickListener(Object newValue) {
        if (mOnPreferenceLongClickListener == null) {
            return;
        }
        mOnPreferenceLongClickListener.onPreferenceLongClick(this, newValue);
    }

    public void setOnPreferenceLongClickListener(
            OnPreferenceLongClickListener onPreferenceLongClickListener) {
        mOnPreferenceLongClickListener = onPreferenceLongClickListener;
    }

    public interface OnPreferenceLongClickListener {
        void onPreferenceLongClick(Preference preference, Object newValue);
    }
}
