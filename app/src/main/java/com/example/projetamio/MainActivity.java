package com.example.projetamio;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "save"; // Nom du fichier de préférences
    private static final String PREF_CHECKBOX_STATE = "checkbox_state"; // Clé pour sauvegarder l'état de la CheckBox
    private Intent mainServiceIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainServiceIntent = new Intent(this, MainService.class);
        setContentView(R.layout.activity_main);
        this.registerListener();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @SuppressLint("SetTextI18n")
    public void registerListener() {
        ToggleButton tb1 = findViewById(R.id.BTN1);
        TextView tv2 = findViewById(R.id.TV2);
        Button btnRequete = findViewById(R.id.STATICBTN1);
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

        btnRequete.setOnClickListener(v -> {
            // Appel de la méthode pour envoyer la requête GET
            new Thread(() -> {
                // URL de la requête GET
                String urlString = "http://iotlab.telecomnancy.eu:8080/iotlab/rest/data/1/light1/last";

                try {
                    // Convertir la chaîne URL en objet URL
                    URL url = new URL(urlString);

                    // Ouvrir une connexion HttpURLConnection
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                    try {
                        // Configurer la requête GET
                        urlConnection.setRequestMethod("GET");

                        // Lire la réponse de la requête
                        BufferedReader reader = new BufferedReader(new InputStreamReader(new BufferedInputStream(urlConnection.getInputStream())));
                        StringBuilder stringBuilder = new StringBuilder();
                        String line;

                        while ((line = reader.readLine()) != null) {
                            stringBuilder.append(line).append("\n");
                        }

                        reader.close();

                        // Retourner la réponse
                        Log.d("Main Activity", stringBuilder.toString());
                    } finally {
                        // Fermer la connexion
                        urlConnection.disconnect();
                    }
                } catch (IOException e) {
                    // Gérer l'échec de la requête
                    e.printStackTrace();
                }
            }).start();
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
}