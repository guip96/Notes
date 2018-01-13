package pt.ipleiria.project.model;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.awareness.SnapshotClient;
import com.google.android.gms.awareness.snapshot.DetectedActivityResponse;
import com.google.android.gms.awareness.snapshot.HeadphoneStateResponse;
import com.google.android.gms.awareness.snapshot.LocationResponse;
import com.google.android.gms.awareness.snapshot.PlacesResponse;
import com.google.android.gms.awareness.snapshot.TimeIntervalsResponse;
import com.google.android.gms.awareness.snapshot.WeatherResponse;
import com.google.android.gms.awareness.state.HeadphoneState;
import com.google.android.gms.awareness.state.TimeIntervals;
import com.google.android.gms.awareness.state.Weather;
import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.List;

/**
 * Async Task que vai buscar os valores das variáveis da Awareness API
 * e só pára quando são adquiridos todos os valores da Awareness API
 */

public class ContextTask extends AsyncTask<Context, Integer , Boolean> {
    private static final String ERROR = "ERROR";
    private static final int MY_PERMISSION_LOCATION = 1;
    @SuppressLint("StaticFieldLeak")
    Context context;
    Activity activity;

    public ContextTask(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
    }

    @Override
    protected Boolean doInBackground(final Context... contexts) {
        final int[] done = {0, 0};
        done[0] = 0;
        done[1] = 0;

        SnapshotClient snapshotClient = Awareness.getSnapshotClient(contexts[0]);

        ////////////////////////////////////HEADPHONE\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
        Task<HeadphoneStateResponse> headphoneState = snapshotClient.getHeadphoneState();
        headphoneState.addOnCompleteListener(new OnCompleteListener<HeadphoneStateResponse>() {
            @Override
            public void onComplete(@NonNull Task<HeadphoneStateResponse> task) {
                if (task.isSuccessful()) {
                    HeadphoneStateResponse result = task.getResult();
                    HeadphoneState headphoneState = result.getHeadphoneState();
                    Singleton.getInstance().getAwareness().setHeadphoneState(headphoneState);
                    done[0] += 125;
                    publishProgress(done[0]);
                } else {
                    done[0] += 125;
                    publishProgress(done[0]);
                    Log.e(ERROR, "ERROR HEADPHONES");
                    return;
                }
            }
        });

        ////////////////////////////////////LOCATION\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
        checkFineLocationPermission();
        snapshotClient.getLocation().addOnCompleteListener(new OnCompleteListener<LocationResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationResponse> task) {
                if (task.isSuccessful()) {
                    LocationResponse result = task.getResult();
                    Location location = result.getLocation();
                    Singleton.getInstance().getAwareness().setLocation(location);
                    done[0] += 125;
                    done[1] = 1;
                    publishProgress(done[0], done[1]);
                } else {
                    done[0] += 125;
                    publishProgress(done[0]);
                    task.getException().printStackTrace();
                    Log.e(ERROR, "ERROR LOCATION");
                }
            }
        });

        //////////////////////////////////WEATHER\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
        checkFineLocationPermission();
        Task<WeatherResponse> weatherTask = snapshotClient.getWeather();

        weatherTask.addOnCompleteListener(new OnCompleteListener<WeatherResponse>() {
            @Override
            public void onComplete(@NonNull Task<WeatherResponse> task) {
                if (task.isSuccessful()) {
                    WeatherResponse result = task.getResult();
                    Weather weather = result.getWeather();
                    Singleton.getInstance().getAwareness().setWeather(weather);
                    done[0] += 125;
                    publishProgress(done[0]);
                } else {
                    done[0] += 125;
                    publishProgress(done[0]);
                    task.getException().printStackTrace();
                    Log.e(ERROR, "ERROR WEATHER");
                }
            }
        });

        //////////////////////////ACTIVITY\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
        Task<DetectedActivityResponse> detectedActivity = snapshotClient.getDetectedActivity();
        detectedActivity.addOnCompleteListener(new OnCompleteListener<DetectedActivityResponse>() {
            @Override
            public void onComplete(@NonNull Task<DetectedActivityResponse> task) {
                if (task.isSuccessful()) {
                    DetectedActivityResponse result = task.getResult();
                    ActivityRecognitionResult activityRecognitionResult = result.getActivityRecognitionResult();
                    DetectedActivity mostProbableActivity = activityRecognitionResult.getMostProbableActivity();
                    Singleton.getInstance().getAwareness().setMostProbableActivity(mostProbableActivity);
                    done[0] += 125;
                    publishProgress(done[0]);
                } else {
                    done[0] += 125;
                    publishProgress(done[0]);
                    Log.e(ERROR, "ERROR ACTIVITY");
                }
            }
        });

        ///////////////////////////////NEARBY_PLACES\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
        checkFineLocationPermission();
        Task<PlacesResponse> places = snapshotClient.getPlaces();
        places.addOnCompleteListener(new OnCompleteListener<PlacesResponse>() {
            @Override
            public void onComplete(@NonNull Task<PlacesResponse> task) {
                if (task.isSuccessful()) {
                    PlacesResponse result = task.getResult();
                    List<PlaceLikelihood> placeLikelihoods = result.getPlaceLikelihoods();
                    Singleton.getInstance().getAwareness().setPlaceLikelihoods(placeLikelihoods);
                    done[0] += 125;
                    done[1] = 2;
                    publishProgress(done[0], done[1]);
                } else {
                    done[0] += 125;
                    publishProgress(done[0]);
                    task.getException().printStackTrace();
                    Log.e(ERROR, "ERROR MEARBY PLACES");
                }
            }

        });

        ///////////////////////////TIME INTERVAL\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
        checkFineLocationPermission();
        Task<TimeIntervalsResponse> timeIntervals = snapshotClient.getTimeIntervals();
        timeIntervals.addOnCompleteListener(new OnCompleteListener<TimeIntervalsResponse>() {
            @Override
            public void onComplete(@NonNull Task<TimeIntervalsResponse> task) {
                if (task.isSuccessful()) {
                    TimeIntervalsResponse result = task.getResult();
                    TimeIntervals timeIntervals = result.getTimeIntervals();
                    Singleton.getInstance().getAwareness().setTimeIntervals(timeIntervals);
                    done[0] += 125;
                    publishProgress(done[0]);
                } else {
                    done[0] += 125;
                    publishProgress(done[0]);
                    task.getException().printStackTrace();
                    Log.e(ERROR, "Error Time Interval");
                }
            }

        });

        while(true){
            if(isCancelled()){
                return false;
            }else if(done[0] == 750){
                return true;
            }
        }
    }

///////////////////////PERMISSÕES\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
    private void checkFineLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                context.getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    activity,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSION_LOCATION
            );
        }
    }
}
