package com.example.projetamio.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

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
                // GetLights getLights = new GetLights();
                // getLights.execute();
                // TODO
            }
        }, 0, 20000);
    }

    public void stopTimer() {
        this.timer.cancel();
    }

}