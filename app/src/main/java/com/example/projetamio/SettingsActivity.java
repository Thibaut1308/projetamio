package com.example.projetamio;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.example.projetamio.utils.TimePickerPreference;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Objects;
import java.util.TimeZone;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

        /**
         * Méthode d'initialisation des valeurs actuelles des heures d'envoi des notifs/emails
         * @param savedInstanceState If the fragment is being re-created from a previous saved state,
         *                           this is the state.
         * @param rootKey
         */
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
            updatePreferenceSummary("weekday_notification_time_start");
            updatePreferenceSummary("weekday_notification_time_end");
            updatePreferenceSummary("weekend_notification_time_start");
            updatePreferenceSummary("weekend_notification_time_end");
            updatePreferenceSummary("weekday_email_time_start");
            updatePreferenceSummary("weekday_email_time_end");
            updatePreferenceSummary("weekend_email_time_start");
            updatePreferenceSummary("weekend_email_time_end");
        }

        /**
         * Méthode de mise à jour des valeurs actuelles des champs dans settings si modifiées
         * (listenner sur les préférences)
         * @param sharedPreferences
         * @param key
         */
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            updatePreferenceSummary("weekday_notification_time_start");
            updatePreferenceSummary("weekday_notification_time_end");
            updatePreferenceSummary("weekend_notification_time_start");
            updatePreferenceSummary("weekend_notification_time_end");
            updatePreferenceSummary("weekday_email_time_start");
            updatePreferenceSummary("weekday_email_time_end");
            updatePreferenceSummary("weekend_email_time_start");
            updatePreferenceSummary("weekend_email_time_end");
        }

        @Override
        public void onResume() {
            super.onResume();
            Objects.requireNonNull(getPreferenceManager().getSharedPreferences()).registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onPause() {
            Objects.requireNonNull(getPreferenceManager().getSharedPreferences()).unregisterOnSharedPreferenceChangeListener(this);
            super.onPause();
        }

        private void updatePreferenceSummary(String key) {
            Preference preference = findPreference(key);
            if (preference instanceof TimePickerPreference) {
                TimePickerPreference timePickerPreference = (TimePickerPreference) preference;

                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
                int storedTime = sharedPreferences.getInt(key, 0);
                int storedHour = storedTime / 60;
                int storedMinute = storedTime % 60;

                Calendar calendar = getTime(storedHour, storedMinute);

                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                sdf.setTimeZone(TimeZone.getTimeZone("Europe/Paris"));
                String formattedTime = sdf.format(calendar.getTime());

                timePickerPreference.setSummary(formattedTime);
            }
        }

        private Calendar getTime(int hour, int minute) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute);
            return calendar;
        }

    }

}