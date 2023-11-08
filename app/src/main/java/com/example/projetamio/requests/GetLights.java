package com.example.projetamio.requests;

import android.util.JsonReader;

import com.example.projetamio.objects.Light;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class GetLights {

    public static final String LIGHT_BROADCAST_ACTION = "lightBroadcastAction";

    public GetLights() {
    }


    public static String last() {
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
            return "Erreur dans de la requête";
        }
    }

    /**
     * public void postExecution(String response) {
     * if (response != null) {
     * if (response.startsWith("Erreur HTTP")) {
     * Toast.makeText(mainActivity, response, Toast.LENGTH_SHORT).show();
     * }else{
     * List<Light> lights = null;
     * try {
     * lights = parseJSONToLights(response);
     * } catch (IOException e) {
     * Toast.makeText(mainActivity, "Erreur lors du parse de la réponse", Toast.LENGTH_SHORT).show();
     * throw new RuntimeException(e);
     * }
     * <p>
     * TextView tv = ((MainActivity)this.mainActivity).findViewById(R.id.TVMOTE153111);
     * tv.setText("Lights: dernière valeur "+ lights.get(0).getValue() + " mesurée à " + lights.get(0).getTimestamp());
     * }
     * } else {
     * Toast.makeText(mainActivity, "Échec de la requête", Toast.LENGTH_SHORT).show();
     * }
     * }
     */

    public static List<Light> parseJSONToLights(String jsonData) throws IOException {
        JsonReader reader = new JsonReader(new StringReader(jsonData));
        List<Light> lights = new ArrayList<>();
        reader.beginObject();

        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("data")) {
                reader.beginArray();

                while (reader.hasNext()) {
                    reader.beginObject();

                    Light light = new Light();

                    while (reader.hasNext()) {
                        String fieldName = reader.nextName();
                        switch (fieldName) {
                            case "label":
                                light.setLabel(reader.nextString());
                                break;
                            case "value":
                                light.setValue(reader.nextDouble());
                                break;
                            case "timestamp":
                                light.setTimestamp(reader.nextLong());
                                break;
                            case "mote":
                                light.setMote(reader.nextString());
                                break;
                            default:
                                reader.skipValue();
                                break;
                        }
                    }

                    lights.add(light);
                    reader.endObject();
                }

                reader.endArray();
            } else {
                reader.skipValue();
            }
        }

        reader.endObject();
        reader.close();
        return lights;
    }

}
