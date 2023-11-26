package com.example.projetamio.utils;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import androidx.preference.DialogPreference;

public class TimePickerPreference extends DialogPreference {

    private int lastHour = 0;
    private int lastMinute = 0;

    public TimePickerPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setPositiveButtonText("Set");
        setNegativeButtonText("Cancel");
    }

    @Override
    protected void onClick() {
        // Show the time picker dialog when the preference is clicked
        int hour = lastHour;
        int minute = lastMinute;

        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                (view, hourOfDay, minute1) -> {
                    lastHour = hourOfDay;
                    lastMinute = minute1;
                    persistInt(lastHour * 60 + lastMinute);
                }, hour, minute, android.text.format.DateFormat.is24HourFormat(getContext()));
        timePickerDialog.show();
    }

    @Override
    protected void onSetInitialValue(Object defaultValue) {
        // Set the initial value of the preference
        int value;
        if (defaultValue != null) {
            value = (int) defaultValue;
        } else {
            // Provide a default value if none is set
            value = getPersistedInt(0);
        }

        lastHour = value / 60;
        lastMinute = value % 60;
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getInt(index, 0);
    }
}
