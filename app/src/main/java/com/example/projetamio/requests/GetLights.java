package com.example.projetamio.requests;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class GetLights extends AsyncTask<Void, Void, String> {
    @SuppressLint("StaticFieldLeak")
    private final Context mainActivity;

    public GetLights(Context context) {
        this.mainActivity = context;
    }

    @Override
    protected String doInBackground(Void... params) {
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
                int responseCode = urlConnection.getResponseCode();

                if (responseCode == 200) {
                    // La requête a réussi, lire la réponse
                    BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }

                    reader.close();

                    Log.d("GetLights", stringBuilder.toString());
                    return stringBuilder.toString();
                } else {
                    return "Erreur HTTP : " + responseCode;
                }
            } finally {
                // Fermer la connexion
                urlConnection.disconnect();
            }
        } catch (IOException e) {
            // Gérer l'échec de la requête
            e.printStackTrace();
            return "Échec de la requête";
        }
    }

    @Override
    protected void onPostExecute(String response) {
        if (response != null) {
            if (response.startsWith("Erreur HTTP")) {
                Toast.makeText(mainActivity, response, Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(mainActivity, "Échec de la requête", Toast.LENGTH_SHORT).show();
        }
    }
}
