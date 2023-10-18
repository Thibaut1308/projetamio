package com.example.projetamio;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
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
        // TODO: Return the communication channel to the service.
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
                Log.d("MainService", "Tâche périodique exécutée.");
            }
        }, 0, 10000);
    }

    public void createAsyncTask() {
        AsyncTask asyncTask = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {

                return null;
            }
        };
    }
    public void stopTimer() {
        this.timer.cancel();
    }

}