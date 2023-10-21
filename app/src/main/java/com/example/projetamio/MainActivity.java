package com.example.projetamio;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;

import com.example.projetamio.services.MainService;

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
        // Button btnRequete = findViewById(R.id.STATICBTN1);
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

        /** btnRequete.setOnClickListener(v -> {
         GetLights getLights = new GetLights(this);
         getLights.execute();
         });*/

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