package pt.ipleiria.project;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * ReverseGeocodingTask: Recebe a localização do utilizador em LatLng
 * e converte essa localização numa morada
 */
abstract class ReverseGeocodingTask extends AsyncTask<Location, Void, String> {

    Context mContext;
    Location loc;

    public ReverseGeocodingTask(Location loc, Context context) {
        super();
        this.loc = loc;
        this.mContext=context;
    }

    List<Address> addresses = null;
    @Override
    protected String doInBackground(Location... params) {
        Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
        Location loc = params[0];
        try {
            // Call the synchronous getFromLocation() method by passing in the lat/long values.
            addresses = geocoder.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
        } catch (IOException e) {
            e.printStackTrace();
            // Update UI field with the exception.
        }
        if (addresses != null && addresses.size() > 0) {
            Address address = addresses.get(0);
            // Format the first line of address (if available), city, and country name.
            String addressText = String.format("%s",
                    address.getAddressLine(0));
            return addressText;
        }else{
            return "Error";
        }
    }

}

