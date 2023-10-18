package com.example.projetamio;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

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
        tb1.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                startService(mainServiceIntent);
                tv2.setText("En cours");
            } else {
                stopService(mainServiceIntent);
                tv2.setText("Arrêté");
            }
        });

        // Checkbox
        CheckBox cbStartAtBoot = findViewById(R.id.CBSTARTATBOOT);
        cbStartAtBoot.setOnCheckedChangeListener((checkBowView, isChecked) -> {
            if (isChecked) {

            } else {

            }
        });
    }


}