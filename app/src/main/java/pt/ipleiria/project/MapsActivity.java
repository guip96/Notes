package pt.ipleiria.project;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import pt.ipleiria.project.model.Note;
import pt.ipleiria.project.model.Notes;
import pt.ipleiria.project.model.Singleton;

/**
 * MapsActivity: Mostra todas as notas que contém localização numa keyword e permite ao
 * utilizador escolher a localização de uma nota.
 */
public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LatLng location;
    Notes notes;

    private PendingIntent myPendingIntent;
    private MyFenceReceiver myFenceReceiver;
    private static final String FENCE_RECEIVER_ACTION = "FENCE_RECEIVER_ACTION";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Intent i = getIntent();
        notes = (Notes) i.getSerializableExtra("NOTES");

    }


    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);
        Location location = Singleton.getInstance().getAwareness().getLocation();
        if (location != null) {
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(latLng, 15, 0, 0)));
            if (!(notes == null)) {
                //MOSTRA TODAS AS NOTAS
                for (Note n : notes.getNotes()) {
                    for (String keyword : n.getKeyword()) {
                        if (keyword.contains("#Location:")) {
                            String[] parts = keyword.split(":");
                            String[] latlng = parts[1].split(",");
                            LatLng location2 = new LatLng(Double.parseDouble(latlng[0]), Double.parseDouble(latlng[1]));
                            mMap.addMarker(new MarkerOptions().position(location2).title(n.getTitle()));
                            mMap.addCircle(new CircleOptions().center(location2).radius(100)
                                    .fillColor(ContextCompat.getColor(MapsActivity.this, R.color.md_red_200))
                                    .strokeColor(ContextCompat.getColor(MapsActivity.this, R.color.md_red_800)));
                        }
                    }
                }
            } else {
                //LOCALIZAÇÃO DA NOTA
                mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {
                        mMap.addMarker(new MarkerOptions().position(latLng).title("Note Location"));
                        Intent returnIntent = new Intent();
                        returnIntent.putExtra("Location", latLng);
                        setResult(RESULT_OK, returnIntent);
                        finish();
                    }
                });
            }

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

    protected void onStop() {
        try {
            unregisterReceiver(myFenceReceiver);
        }catch(IllegalArgumentException e){
            e.printStackTrace();
        }
        super.onStop();
    }
}
