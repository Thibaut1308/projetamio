package com.example.projetamio;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;

import com.example.projetamio.objects.Light;
import com.example.projetamio.requests.GetLights;
import com.example.projetamio.services.MainService;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "save"; // Nom du fichier de préférences
    private static final String PREF_CHECKBOX_STATE = "checkbox_state"; // Clé pour sauvegarder l'état de la CheckBox
    private Intent mainServiceIntent;

    private Intent lightBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainServiceIntent = new Intent(this, MainService.class);
        setContentView(R.layout.activity_main);
        this.registerListener();
        this.registerBroadcastReceiver();

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
    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    private void registerBroadcastReceiver() {

        IntentFilter light_broadcast_filters = new IntentFilter(GetLights.LIGHT_BROADCAST_ACTION);
        this.lightBroadcastReceiver = this.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String response = intent.getStringExtra("response");

                try {
                    List<Light> lights = GetLights.parseJSONToLights(response);
                    for (Light l: lights) {
                        switch (l.getMote()) {
                            case "153.111":
                                TextView mote1result = findViewById(R.id.TVMOTE153111RESULT);
                                mote1result.setText((int) l.getValue());
                            case "81.77":
                                TextView mote2result = findViewById(R.id.TVMOTE8177RESULT);
                                mote2result.setText((int)l.getValue());
                            case "9.138":
                                TextView mote3result = findViewById(R.id.TVMOTE9138RESULT);
                                mote3result.setText((int)l.getValue());
                        }
                    }
                    // TODO Vérifier que ça change bien les valeurs
                } catch (IOException e) {
                    Toast.makeText(getApplicationContext(), "Erreur de parsing de la réponse", Toast.LENGTH_SHORT).show();
                }
            }
        }, light_broadcast_filters);
    }

}