package com.example.projetamio;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.TextView;

public class MyBootBroadcastReceiver extends BroadcastReceiver {

    private static final String PREFS_NAME = "save";
    private static final String PREF_CHECKBOX_STATE = "checkbox_state";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() != null && intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            Log.d("MyBootReceiver", "Broadcast de démarrage reçu.");

            // Vérifier si l'utilisateur a coché l'option correspondante
            SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            boolean isChecked = prefs.getBoolean(PREF_CHECKBOX_STATE, false);

            if (isChecked) {
                Intent serviceIntent = new Intent(context, MainService.class);
                context.startService(serviceIntent);
                Log.d("MyBootReceiver", "Service démarré après le démarrage du système.");
            } else {
                Log.d("MyBootReceiver", "L'utilisateur n'a pas coché l'option. Le service n'est pas démarré.");
            }
        }
    }
}
