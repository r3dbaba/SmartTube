package com.liskovsoft.smartyoutubetv2.tv.ui.dialogs.other;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;

public class LongClickPreference extends Preference {
    private OnPreferenceLongClickListener mOnPreferenceLongClickListener;

    public LongClickPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public LongClickPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public LongClickPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LongClickPreference(Context context) {
        super(context);
    }

    @Override
    public void onBindViewHolder(@NonNull PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);
        holder.itemView.setOnLongClickListener(v -> {
            callPreferenceLongClickListener();
            return true;
        });
    }

    public void callPreferenceLongClickListener() {
        if (mOnPreferenceLongClickListener != null) {
            mOnPreferenceLongClickListener.onPreferenceLongClick(this);
        }
    }

    public void setOnPreferenceLongClickListener(OnPreferenceLongClickListener listener) {
        mOnPreferenceLongClickListener = listener;
    }

    public interface OnPreferenceLongClickListener {
        void onPreferenceLongClick(Preference preference);
    }
}
