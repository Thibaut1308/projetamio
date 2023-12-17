package com.example.projetamio;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.preference.PreferenceManager;

import com.example.projetamio.objects.Light;
import com.example.projetamio.requests.GetLights;
import com.example.projetamio.services.MainService;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "save"; // Nom du fichier de préférences
    private static final String PREF_CHECKBOX_STATE = "checkbox_state"; // Clé pour sauvegarder l'état de la CheckBox
    private Intent mainServiceIntent;
    private Intent lightBroadcastReceiver;

    private final List<int[]> resultTextViewIds = new ArrayList<>(
            Arrays.asList(
                    new int[]{R.id.TVMOTE1, R.id.TVMOTE1RESULT},
                    new int[]{R.id.TVMOTE2, R.id.TVMOTE2RESULT},
                    new int[]{R.id.TVMOTE3, R.id.TVMOTE3RESULT},
                    new int[]{R.id.TVMOTE4, R.id.TVMOTE4RESULT},
                    new int[]{R.id.TVMOTE5, R.id.TVMOTE5RESULT},
                    new int[]{R.id.TVMOTE6, R.id.TVMOTE6RESULT},
                    new int[]{R.id.TVMOTE7, R.id.TVMOTE7RESULT},
                    new int[]{R.id.TVMOTE8, R.id.TVMOTE8RESULT},
                    new int[]{R.id.TVMOTE9, R.id.TVMOTE9RESULT},
                    new int[]{R.id.TVMOTE10, R.id.TVMOTE10RESULT}
            )
    );

    private int notifyId = 0;
    public Map<String, Double> previousLights = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainServiceIntent = new Intent(this, MainService.class);
        setContentView(R.layout.activity_main);
        NotificationChannel channel = new NotificationChannel("LIGHTS_CHANNEL", "Changement de luminosité", NotificationManager.IMPORTANCE_DEFAULT);
        channel.setDescription("Lumonisity channel");
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
        this.registerListener();
        this.registerBroadcastReceiver();
        findViewById(R.id.BTN_SETTINGS).setOnClickListener(view -> openSettings());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @SuppressLint("SetTextI18n")
    public void registerListener() {
        ToggleButton tb1 = findViewById(R.id.BTN1);
        TextView tv2 = findViewById(R.id.TV2);
        CheckBox cbStartAtBoot = findViewById(R.id.CBSTARTATBOOT);

        tb1.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                startService(mainServiceIntent);
                tv2.setText("En cours");
            } else {
                stopService(mainServiceIntent);
                tv2.setText("Arrêté");
            }
        });

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        boolean savedCheckBoxState = prefs.getBoolean(PREF_CHECKBOX_STATE, false);
        if (savedCheckBoxState) {
            startService(mainServiceIntent);
            tv2.setText("En cours");
        }

        cbStartAtBoot.setChecked(savedCheckBoxState);
        cbStartAtBoot.setOnCheckedChangeListener((checkBowView, isChecked) -> {
            editor.putBoolean(PREF_CHECKBOX_STATE, isChecked);
            editor.apply();
        });
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    private void registerBroadcastReceiver() {
        IntentFilter light_broadcast_filters = new IntentFilter(GetLights.LIGHT_BROADCAST_ACTION);
        this.lightBroadcastReceiver = this.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String response = intent.getStringExtra("response");
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                Calendar weekdayNotificationTimeStartMinutes = getCalendarFromMinutes(sharedPreferences.getInt("weekday_notification_time_start", 0), "Europe/Paris");
                Calendar weekdayNotificationTimeEndMinutes = getCalendarFromMinutes(sharedPreferences.getInt("weekday_notification_time_end", 0), "Europe/Paris");
                Calendar weekendNotificationTimeStartMinutes = getCalendarFromMinutes(sharedPreferences.getInt("weekend_notification_time_start", 0), "Europe/Paris");
                Calendar weekendNotificationTimeEndMinutes = getCalendarFromMinutes(sharedPreferences.getInt("weekend_notification_time_end", 0), "Europe/Paris");
                Calendar weekdayEmailTimeStartMinutes = getCalendarFromMinutes(sharedPreferences.getInt("weekday_email_time_start", 0), "Europe/Paris");
                Calendar weekdayEmailTimeEndMinutes = getCalendarFromMinutes(sharedPreferences.getInt("weekday_email_time_end", 0), "Europe/Paris");
                Calendar weekendEmailTimeStartMinutes = getCalendarFromMinutes(sharedPreferences.getInt("weekend_email_time_start", 0), "Europe/Paris");
                Calendar weekendEmailTimeEndMinutes = getCalendarFromMinutes(sharedPreferences.getInt("weekend_email_time_end", 0), "Europe/Paris");

                try {
                    List<Light> lights = GetLights.parseJSONToLights(response);
                    for (Light l : lights) {
                        // Enregistrement/comparatifs des valeurs précédentes
                        if (previousLights.containsKey(l.getMote())) {
                            double previousValue = previousLights.get(l.getMote());
                            if (Math.abs(l.getValue() - previousValue) > 50) {
                                if (((isWeekday() && isTimeBetween(weekdayNotificationTimeStartMinutes, weekdayNotificationTimeEndMinutes)) ||
                                        (isWeekend() && isTimeBetween(weekendNotificationTimeStartMinutes, weekendNotificationTimeEndMinutes))) && sharedPreferences.getBoolean("send_notifications", false)) {
                                    showNotification("Changement de luminosité", "La lumière du mote " + l.getMote() + " a changée de manière significative");
                                    // Mise à jour last alert
                                    setActualDate(R.id.TV6);
                                }
                                if (((isWeekend() && isTimeBetween(weekendEmailTimeStartMinutes, weekendEmailTimeEndMinutes)) ||
                                        (isWeekday() && isTimeBetween(weekdayEmailTimeStartMinutes, weekdayEmailTimeEndMinutes))) && sharedPreferences.getBoolean("send_email", false)) {
                                    sendEmail("Changement de luminosité", "La lumière du mote " + l.getMote() + " a changée de manière significative", context);
                                    // Mise à jour last alert
                                    setActualDate(R.id.TV6);
                                }
                            }
                        }
                        previousLights.put(l.getMote(), l.getValue());
                        int[] currentMote = resultTextViewIds.stream().filter(textViewId -> ((TextView) findViewById(textViewId[0])).getText().toString().equals("Mote " + l.getMote() + ":")).findFirst().orElse(null);
                        if (currentMote == null) {
                            int[] availableTextView = resultTextViewIds.stream().filter(textView -> ((TextView) findViewById(textView[0])).getText().toString().isEmpty()).findFirst().orElse(null);
                            if (availableTextView == null) {
                                Log.d("MainActivity", "Affichage limité à 10. Impossible d'afficher toutes les résultats");
                            } else {
                                ((TextView) findViewById(availableTextView[0])).setText("Mote " + l.getMote() + ":");
                                ((TextView) findViewById(availableTextView[1])).setText(l.getValue() + " lx - " + (((int) l.getValue()) > 250 ? "Allumé" : "Eteint"));
                            }
                        } else {
                            ((TextView) findViewById(currentMote[1])).setText(l.getValue() + " lx - " + (((int) l.getValue()) > 250 ? "Allumé" : "Eteint"));
                        }
                        // Mise à jour last result
                        setActualDate(R.id.TV4);
                    }
                } catch (IOException e) {
                    Toast.makeText(getApplicationContext(), "Erreur de parsing de la réponse", Toast.LENGTH_SHORT).show();
                }
            }
        }, light_broadcast_filters);
    }

    private void openSettings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    @SuppressLint("MissingPermission")
    private void showNotification(String title, String content) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "LIGHTS_CHANNEL")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(title)
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(++notifyId, builder.build());
    }

    @SuppressLint("IntentReset")
    public void sendEmail(String sujet, String corps, Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String destinataire = sharedPreferences.getString("email_adress", "");

        Intent intent = new Intent(Intent.ACTION_SEND);


        intent.setData(Uri.parse("mail:to"));
        intent.setType("text/plain");

        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{destinataire});
        intent.putExtra(Intent.EXTRA_SUBJECT, sujet);
        intent.putExtra(Intent.EXTRA_TEXT, corps);

        startActivity(Intent.createChooser(intent, "Send mail..."));
    }

    private boolean isWeekday() {
        Calendar calendar = Calendar.getInstance();
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        return (dayOfWeek >= Calendar.MONDAY && dayOfWeek <= Calendar.FRIDAY);
    }

    private boolean isWeekend() {
        Calendar calendar = Calendar.getInstance();
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        return (dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY);
    }

    private boolean isTimeBetween(Calendar startTime, Calendar endTime) {
        Calendar currentCalendar = Calendar.getInstance();
        return (currentCalendar.compareTo(startTime) >= 0 && currentCalendar.compareTo(endTime) <= 0);
    }

    private void setActualDate(int idTv) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.FRENCH);
        dateFormat.setTimeZone(TimeZone.getTimeZone("Europe/Paris"));
        ((TextView) findViewById(idTv)).setText(dateFormat.format(new Date()));
    }

    public static Calendar getCalendarFromMinutes(int totalMinutes, String timeZoneId) {
        int hours = totalMinutes / 60;
        int minutes = totalMinutes % 60;

        TimeZone timeZone = TimeZone.getTimeZone(timeZoneId);

        Calendar calendar = Calendar.getInstance(timeZone);
        calendar.set(Calendar.HOUR_OF_DAY, hours);
        calendar.set(Calendar.MINUTE, minutes);

        return calendar;
    }
}