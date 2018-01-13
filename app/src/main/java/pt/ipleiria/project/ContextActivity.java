package pt.ipleiria.project;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.awareness.FenceClient;
import com.google.android.gms.awareness.fence.AwarenessFence;
import com.google.android.gms.awareness.fence.DetectedActivityFence;
import com.google.android.gms.awareness.fence.FenceUpdateRequest;
import com.google.android.gms.awareness.fence.HeadphoneFence;
import com.google.android.gms.awareness.fence.LocationFence;
import com.google.android.gms.awareness.fence.TimeFence;
import com.google.android.gms.awareness.state.TimeIntervals;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import pt.ipleiria.project.model.Constants;
import pt.ipleiria.project.model.ContextTask;
import pt.ipleiria.project.model.Note;
import pt.ipleiria.project.model.Singleton;


public class ContextActivity extends AppCompatActivity {

    private static final int REQUEST_DESTINATION = 1;
    final ArrayList seletedItems = new ArrayList();

    private PendingIntent myPendingIntent;
    private MyFenceReceiver myFenceReceiver;
    private static final String FENCE_RECEIVER_ACTION = "FENCE_RECEIVER_ACTION";

    private static final String TAG = "TAG";
    private static final int MY_PERMISSION_LOCATION = 1;
    private GoogleApiClient mGoogleApiClient;
    TextView tv_headphone_state;
    TextView tv_weather_state;
    TextView tv_location_state;
    TextView tv_activity_state;
    TextView tv_nearbyPlace;
    TextView tv_timeIntervals;
    TextView tv_progress;
    ProgressBar progressBar;
    private LatLng originLatLng;
    private int activityType;
    private String currentAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_context);
        Toolbar myToolbar = findViewById(R.id.toolbar_context);
        setSupportActionBar(myToolbar);



        this.mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();
        this.mGoogleApiClient.connect();
        tv_headphone_state = findViewById(R.id.tv_headphone_state);
        tv_weather_state = findViewById(R.id.tv_weather_state);
        tv_location_state = findViewById(R.id.tv_location_state);
        tv_activity_state = findViewById(R.id.tv_activity_state);
        tv_nearbyPlace = findViewById(R.id.tv_places_state);
        tv_progress = findViewById(R.id.tv_progress);
        tv_timeIntervals = findViewById(R.id.tv_timeInterval_state);
        progressBar = findViewById(R.id.progressBar);
        tv_progress.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        if(Singleton.getInstance().getUpdatedContext()==1){
            updateContextText();
        }
    }

    @Override
    protected void onStart() {
        final Intent intent = new Intent(FENCE_RECEIVER_ACTION);
        myPendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
        myFenceReceiver = new MyFenceReceiver();

        registerReceiver(myFenceReceiver, new IntentFilter(FENCE_RECEIVER_ACTION));
        super.onStart();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_context, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case (R.id.action_update_context):
                Singleton.getInstance().setUpdatedContext();
                tv_progress.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                updateContext();

                break;
            case (R.id.action_maps):
                    Intent i4 = new Intent(this, MapsActivity.class);
                    i4.putExtra("NOTES", Singleton.getInstance().getNotes());
                    startActivity(i4);
                break;
            case (R.id.action_fence):
                AlertDialog dialog = new AlertDialog.Builder(this).setTitle("Fences").setMultiChoiceItems(Constants.fence_checkbox_Itens,
                        Singleton.getInstance().getCheckboxes().getFenceItens(), new DialogInterface.OnMultiChoiceClickListener() {
                            @SuppressLint("MissingPermission")
                            @Override
                            public void onClick(DialogInterface dialog, int index, boolean isChecked) {
                                if (isChecked) {
                                    seletedItems.add(index);
                                    switch (index) {
                                        case 0:
                                            Singleton.getInstance().getCheckboxes().setHeadphoneFence(true);
                                            registerFence("headphoneFenceKey", HeadphoneFence.pluggingIn());
                                            registerFence("headphoneFenceKey", HeadphoneFence.unplugging());
                                            break;
                                        case 1:
                                            Singleton.getInstance().getCheckboxes().setLocationFence(true);
                                            for (Note n : Singleton.getInstance().getNotes().getNotes()) {
                                                for(String keyword:n.getKeyword()){
                                                    if(keyword.startsWith("#Location:")){
                                                        String[] latlng = keyword.split(":");
                                                        String[] latlng_separated = latlng[1].split(",");
                                                        registerFence("location"+latlng[1],
                                                                LocationFence.in(Double.parseDouble(latlng_separated[0]),
                                                                        Double.parseDouble(latlng_separated[1]),
                                                                        100,
                                                                        10000));
                                                    }
                                                }
                                            }
                                            break;
                                        case 2:
                                            Singleton.getInstance().getCheckboxes().setActivityFence(true);
                                            registerFence("activityIn VehicleFenceKey", DetectedActivityFence.starting(DetectedActivityFence.IN_VEHICLE));
                                            registerFence("activityOn BicycleFenceKey", DetectedActivityFence.starting(DetectedActivityFence.ON_BICYCLE));
                                            registerFence("activityOn FootFenceKey", DetectedActivityFence.starting(DetectedActivityFence.ON_FOOT));
                                            registerFence("activityStillFenceKey", DetectedActivityFence.starting(DetectedActivityFence.STILL));
                                            registerFence("activityWalkingFenceKey", DetectedActivityFence.starting(DetectedActivityFence.WALKING));
                                            registerFence("activityRunningFenceKey", DetectedActivityFence.starting(DetectedActivityFence.RUNNING));
                                            break;
                                        case 3:
                                            Singleton.getInstance().getCheckboxes().setTimeFence(true);
                                            registerFence("timeWeekdayFenceKey", TimeFence.inTimeInterval(TimeFence.TIME_INTERVAL_WEEKDAY));
                                            registerFence("timeWeekendFenceKey", TimeFence.inTimeInterval(TimeFence.TIME_INTERVAL_WEEKEND));
                                            registerFence("timeHolidayFenceKey", TimeFence.inTimeInterval(TimeFence.TIME_INTERVAL_HOLIDAY));
                                            registerFence("timeMorningFenceKey", TimeFence.inTimeInterval(TimeFence.TIME_INTERVAL_MORNING));
                                            registerFence("timeAfternoonFenceKey", TimeFence.inTimeInterval(TimeFence.TIME_INTERVAL_AFTERNOON));
                                            registerFence("timeEveningFenceKey", TimeFence.inTimeInterval(TimeFence.TIME_INTERVAL_EVENING));
                                            registerFence("timeNightFenceKey", TimeFence.inTimeInterval(TimeFence.TIME_INTERVAL_NIGHT));
                                            break;
                                    }
                                } else if (seletedItems.contains(index)) {
                                    seletedItems.remove(Integer.valueOf(index));
                                    switch (index) {
                                        case 0:
                                            Singleton.getInstance().getCheckboxes().setHeadphoneFence(false);
                                            unregisterFence("headphoneFenceKey", HeadphoneFence.pluggingIn());
                                            unregisterFence("headphoneFenceKey", HeadphoneFence.unplugging());
                                            break;
                                        case 1:
                                            Singleton.getInstance().getCheckboxes().setLocationFence(false);
                                            for (Note n : Singleton.getInstance().getNotes().getNotes()) {
                                                for(String keyword:n.getKeyword()){
                                                    if(keyword.startsWith("#Location:")){
                                                        String[] latlng = keyword.split(":");
                                                        String[] latlng_separated = latlng[1].split(",");
                                                        unregisterFence("location"+latlng[1],
                                                                LocationFence.in(Double.parseDouble(latlng_separated[0]),
                                                                        Double.parseDouble(latlng_separated[1]),
                                                                        100,
                                                                        10000));
                                                    }
                                                }
                                            }
                                            break;
                                        case 2:
                                            Singleton.getInstance().getCheckboxes().setActivityFence(false);
                                            unregisterFence("activityIn VehicleFenceKey", DetectedActivityFence.starting(DetectedActivityFence.IN_VEHICLE));
                                            unregisterFence("activityOn BicycleFenceKey", DetectedActivityFence.starting(DetectedActivityFence.ON_BICYCLE));
                                            unregisterFence("activityOn FootFenceKey", DetectedActivityFence.starting(DetectedActivityFence.ON_FOOT));
                                            unregisterFence("activityStillFenceKey", DetectedActivityFence.starting(DetectedActivityFence.STILL));
                                            unregisterFence("activityWalkingFenceKey", DetectedActivityFence.starting(DetectedActivityFence.WALKING));
                                            unregisterFence("activityRunningFenceKey", DetectedActivityFence.starting(DetectedActivityFence.RUNNING));
                                            break;
                                        case 3:
                                            Singleton.getInstance().getCheckboxes().setTimeFence(false);
                                            unregisterFence("timeWeekdayFenceKey", TimeFence.inTimeInterval(TimeFence.TIME_INTERVAL_WEEKDAY));
                                            unregisterFence("timeWeekendFenceKey", TimeFence.inTimeInterval(TimeFence.TIME_INTERVAL_WEEKEND));
                                            unregisterFence("timeHolidayFenceKey", TimeFence.inTimeInterval(TimeFence.TIME_INTERVAL_HOLIDAY));
                                            unregisterFence("timeMorningFenceKey", TimeFence.inTimeInterval(TimeFence.TIME_INTERVAL_MORNING));
                                            unregisterFence("timeAfternoonFenceKey", TimeFence.inTimeInterval(TimeFence.TIME_INTERVAL_AFTERNOON));
                                            unregisterFence("timeEveningFenceKey", TimeFence.inTimeInterval(TimeFence.TIME_INTERVAL_EVENING));
                                            unregisterFence("timeNightFenceKey", TimeFence.inTimeInterval(TimeFence.TIME_INTERVAL_NIGHT));
                                            break;
                                    }
                                }
                            }
                        }).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                    }
                }).create();
                dialog.show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void onStop() {
        try {
            unregisterReceiver(myFenceReceiver);
        }catch(IllegalArgumentException e){
            e.printStackTrace();
        }

        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @SuppressLint("StaticFieldLeak")
    private void getDistance(final String destination){
        double lat = originLatLng.latitude;
        double lng = originLatLng.longitude;
        activityType=Singleton.getInstance().getAwareness().getMostProbableActivity().getType();

        final String str_originLatLng = String.valueOf(lat) + "," + String.valueOf(lng);

        new DistanceMatrixTask(str_originLatLng, destination, activityType, mGoogleApiClient){

            @Override
            protected void onPostExecute(String result) {
                if (!result.startsWith("ERROR")) {
                    String string_mode = null;
                    switch (activityType){
                        case 0:
                            string_mode="Driving";
                            break;
                        case 1:
                            string_mode="Bicycling";
                            break;
                        case 2:
                            string_mode="Walking";
                            break;
                        case 3:
                            string_mode="Walking";
                            break;
                        case 7:
                            string_mode="Walking";
                            break;
                        case 8:
                            string_mode="Walking";
                            break;
                    }

                    try {
                        JSONObject root = new JSONObject(result);
                        JSONArray rows = root.getJSONArray("rows");
                        JSONObject firstRow = rows.getJSONObject(0);
                        JSONObject firstElement = firstRow.getJSONArray("elements").getJSONObject(0);
                        JSONObject distance = firstElement.getJSONObject("distance");
                        JSONObject duration = firstElement.getJSONObject("duration");
                        String distanceText = distance.getString("text");
                        String durationText = duration.getString("text");
                        String res ="\n" + string_mode + " distance from " + currentAddress + " to " + destination + " is: \n" + distanceText + ", aprox. " + durationText + ".";
                        Singleton.getInstance().getAwareness().setDistanceToPlace(res);
                        tv_nearbyPlace.append(res);
                        progressBar.setProgress(progressBar.getProgress()+125);
                        tv_progress.setText("Progress: " + (progressBar.getProgress()/10) +"%");
                        if(progressBar.getProgress()==1000) {
                            tv_progress.setVisibility(View.GONE);
                            progressBar.setVisibility(View.GONE);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(ContextActivity.this, "ERROR: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(ContextActivity.this, result, Toast.LENGTH_LONG).show();
                }
            }
        }.execute();
    }

    @SuppressLint("StaticFieldLeak")
    private void getPlacePhoto(String placeId) {
        // Create a new AsyncTask that displays the bitmap and attribution once loaded.
        //new PhotoTask(mImageView.getWidth(), mImageView.getHeight()) {

        new PhotoTask(1000, 1000, mGoogleApiClient) {
            @Override
            protected void onPostExecute(AttributedPhoto attributedPhoto) {
                if (attributedPhoto != null) {
                    Singleton.getInstance().getAwareness().setImage(attributedPhoto.bitmap);
                }
            }
        }.execute(placeId);
    }

    @SuppressLint("StaticFieldLeak")
    private void getLocationAddress(Location location){
        new ReverseGeocodingTask(location,this){
            @Override
            protected void onPostExecute(String result) {
                currentAddress=result;
                Singleton.getInstance().getAwareness().setCurrentAddress(currentAddress);
                tv_location_state.append("\nAddress: " + Singleton.getInstance().getAwareness()
                        .getCurrentAddress());
                progressBar.setProgress(progressBar.getProgress()+125);
                tv_progress.setText("Progress: " + (progressBar.getProgress()/10) +"%");
                if(progressBar.getProgress()==1000) {
                    tv_progress.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                }
            }
        }.execute(location);
    }

    @SuppressLint("StaticFieldLeak")
    private void updateContext(){
        new ContextTask(ContextActivity.this, this){
            @Override
            protected void onPostExecute(Boolean aBoolean) {
                super.onPostExecute(aBoolean);
                updateContextText();
                if(progressBar.getProgress()==1000) {
                    tv_progress.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                }
            }
            @SuppressLint("SetTextI18n")
            @Override
            protected void onProgressUpdate(Integer... values) {

                progressBar.setProgress(progressBar.getProgress()+125);
                tv_progress.setText("Progress: " + (progressBar.getProgress()/10) +"%");
                if(values.length==2) {
                    if (values[1] == 1) {
                        Location location = Singleton.getInstance().getAwareness().getLocation();
                        originLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                        getLocationAddress(location);
                    }
                    if (values[1] == 2) {
                        getDistance(Singleton.getInstance().getAwareness().getPlaceLikelihoods()
                                .get(0).getPlace().getAddress().toString());
                        getPlacePhoto(Singleton.getInstance().getAwareness().getPlaceLikelihoods()
                                .get(0).getPlace().getId());
                    }
                }
            }
        }.execute(getApplicationContext());
    }

    protected void registerFence(final String fenceKey, final AwarenessFence fence) {
        FenceClient fenceClient = Awareness.getFenceClient(this);

        fenceClient.updateFences(new FenceUpdateRequest.Builder()
                .addFence(fenceKey, fence, myPendingIntent)
                .build()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(ContextActivity.this, "Fence " + fenceKey
                            + " was successfully registered.", Toast.LENGTH_SHORT).show();
                }else{
                    task.getException().printStackTrace();
                    Toast.makeText(ContextActivity.this, "Fence " + fenceKey
                            + " could not be registered: " + task.getResult().toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void unregisterFence(final String fenceKey, final AwarenessFence fence) {
        FenceClient fenceClient = Awareness.getFenceClient(this);

        fenceClient.updateFences(new FenceUpdateRequest.Builder()
                .removeFence(fenceKey)
                .build()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(ContextActivity.this, "Fence " + fenceKey
                            + " was successfully unregistered.", Toast.LENGTH_SHORT).show();
                }else{
                    task.getException().printStackTrace();
                    Toast.makeText(ContextActivity.this, "Fence " + fenceKey
                            + " could not be unregistered: " + task.getResult().toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updateContextText(){
        /////////////////////////HEADPHONES\\\\\\\\\\\\\\\\\\\\\\\\\\\\
        if(!(Singleton.getInstance().getAwareness().getHeadphoneState()==null)) {
            if (Singleton.getInstance().getAwareness().getHeadphoneState().getState() == 1) {
                Log.i(TAG, "Headphones are plugged in.\n");
                tv_headphone_state.setText(R.string.plugged_in);
            } else {
                Log.i(TAG, "Headphones are NOT plugged in.\n");
                tv_headphone_state.setText(R.string.unplugged);
            }
        }else{
            Toast.makeText(this, "Error headphones", Toast.LENGTH_SHORT).show();
        }
        //////////////////////////LOCATION\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
        if(!(Singleton.getInstance().getAwareness().getLocation()==null)) {
            String location_state = "Latitude: " +
                    Singleton.getInstance().getAwareness().getLocation().getLatitude()
                    + "\nLongitude: " + Singleton.getInstance().getAwareness().getLocation().getLongitude();
            tv_location_state.setText(location_state);
        }

        ////////////////////////WEATHER\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
        if(!(Singleton.getInstance().getAwareness().getWeather()==null)) {
            String condition;
            int[] conditions = Singleton.getInstance().getAwareness().getWeather().getConditions();
            condition = Singleton.getInstance().conditionsToString(conditions);
            @SuppressLint("DefaultLocale") String weather_state = "Temperature is " +
                    String.format("%.1f", Singleton.getInstance().getAwareness().getWeather().getTemperature(2))
                    + "ºC but feels like " +
                    String.format("%.1f", Singleton.getInstance().getAwareness().getWeather().getFeelsLikeTemperature(2))
                    + "ºC"
                    + "\nDew: " +
                    String.format("%.1f", Singleton.getInstance().getAwareness().getWeather().getDewPoint(2))
                    + "ºC"
                    + "\nHumidity: " +
                    Singleton.getInstance().getAwareness().getWeather().getHumidity()
                    + "\nCondition: " + condition;
            tv_weather_state.setText(weather_state);
        }

            ///////////////////ACTIVITY\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
        if(!(Singleton.getInstance().getAwareness().getMostProbableActivity()==null)) {
            String activity_type = Singleton.getInstance()
                    .activityToString(Singleton.getInstance().getAwareness()
                            .getMostProbableActivity());
            String activity_state = activity_type + " with " +
                    Singleton.getInstance().getAwareness().getMostProbableActivity().getConfidence()
                    + "% Confidence";
            tv_activity_state.setText(activity_state);
        }
            ////////////////NEARBY PLACES\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
        if(!(Singleton.getInstance().getAwareness().getPlaceLikelihoods()==null)) {
            PlaceLikelihood p = Singleton.getInstance().getAwareness().getPlaceLikelihoods().get(0);
            String nearbyPlace = "Nearest Place: " + p.getPlace().getName().toString()
                    + "\nLikelihood: " + p.getLikelihood()
                    + "\nAddress: " + p.getPlace().getAddress()
                    + "\nLocation: " + p.getPlace().getLatLng()
                    + "\nWebsite: " + p.getPlace().getWebsiteUri()
                    + "\nPlace Types:" + Singleton.getInstance().placeTypesToString(p.getPlace().getPlaceTypes());
            tv_nearbyPlace.setText(nearbyPlace);
        }

        /////////////////////////////TIME INTERVALS\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
        if(!(Singleton.getInstance().getAwareness().getTimeIntervals()==null)) {
            TimeIntervals timeIntervals = Singleton.getInstance().getAwareness().getTimeIntervals();
            String timeInterval = Singleton.getInstance().timeIntervalstoString(timeIntervals);
            tv_timeIntervals.setText(timeInterval);
        }
    }

}
