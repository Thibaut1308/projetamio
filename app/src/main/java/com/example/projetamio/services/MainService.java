package com.example.projetamio.services;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.example.projetamio.MainActivity;
import com.example.projetamio.objects.Light;
import com.example.projetamio.requests.GetLights;

import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainService extends Service {

    private Timer timer;

    public MainService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("MainService", "Le service a démarré en mode sticky.");
        this.createTimer();
        // Retourne START_STICKY pour redémarrer automatiquement le service en cas d'arrêt inattendu
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.stopTimer();
        Log.d("MainService", "Le service a été arrêté.");
    }

    public void createTimer() {
        this.timer = new Timer();

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Log.d("MainService", "Récupération des lumières.");
                String response = GetLights.last();
                if (response.contains("Erreur")) {
                    Intent intent = new Intent();
                    intent.setAction("light-response");
                    intent.putExtra("response", response);
                    sendBroadcast(intent);
                }else{
                    showToastOnUIThread("Lumières récupérées", Toast.LENGTH_SHORT);
                }
            }
        }, 0, 20000);
    }

    private void showToastOnUIThread(final String message, final int duration) {
        new Handler(Looper.getMainLooper()).post(() -> Toast.makeText(MainService.this, message, duration).show());
    }

    public void stopTimer() {
        this.timer.cancel();
    }

}