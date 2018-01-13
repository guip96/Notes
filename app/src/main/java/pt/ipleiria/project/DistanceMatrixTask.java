package pt.ipleiria.project;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * DistanceMatrixTask Envia a localização do utilizador e a localização do
 * "Place" mais próximo e mostra a distância até ao mesmo em metros e o tempo que
 * demora a chegar ao mesmo se for a pé
 */
abstract class DistanceMatrixTask extends AsyncTask<String, Void, String> {
    private String origin;
    private String destination;
    private int mode;
    private String string_mode;
    private GoogleApiClient mGoogleApiClient;

    private static final String KEY = "AIzaSyCE0SDtBlux7XiRY857bZsEPZeXR201FIM";

    public DistanceMatrixTask(String origin, String destination, int mode, GoogleApiClient mGoogleApiClient) {
        this.origin = origin;
        this.destination = destination;
        this.mode = mode;
        this.mGoogleApiClient = mGoogleApiClient;
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            switch (mode){
                case 0:
                    string_mode="driving";
                    break;
                case 1:
                    string_mode="bicycling";
                    break;
                case 2:
                    string_mode="walking";
                    break;
                case 3:
                    string_mode="walking";
                    break;
                case 7:
                    string_mode="walking";
                    break;
                case 8:
                    string_mode="walking";
                    break;
            }

            String URL = "https://maps.googleapis.com/maps/api/distancematrix/json?"
                    + "origins=" + origin + "&"
                    + "destinations=" + destination + "&"
                    + "mode=" + string_mode + "&"
                    + "key=" + KEY;
            URL = URL.replaceAll(" ","%20");

            HttpURLConnection httpURLConnection = setupHttpURLConnection(URL, "GET");
            httpURLConnection.connect();

            int responseCode = httpURLConnection.getResponseCode();
            Log.i("TAG", "HTTP response code: " + responseCode);

            InputStream inputStream = httpURLConnection.getInputStream();
            String contentAsString = readStream(inputStream);
            inputStream.close();

            return contentAsString;

        } catch (IOException e) {
            e.printStackTrace();
            return "ERROR: " + e;
        }
    }

    private String readStream(InputStream is) {
        StringBuilder sb = new StringBuilder(512);
        try {
            Reader r = new InputStreamReader(is, "UTF-8");
            int c = 0;
            while ((c = r.read()) != -1) {
                sb.append((char) c);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return sb.toString();
    }

    @NonNull
    private HttpURLConnection setupHttpURLConnection(String string_URL, String requestMethod) throws IOException {
        URL url = new URL(string_URL);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setReadTimeout(10000);
        httpURLConnection.setConnectTimeout(15000);
        httpURLConnection.setRequestMethod(requestMethod);
        httpURLConnection.setRequestProperty("Content-Type", "application/json");
        httpURLConnection.setRequestProperty("Accept", "application/json");
        //      httpURLConnection.setDoInput(true);
        //      httpURLConnection.setDoOutput(false);

        return httpURLConnection;
    }
}



