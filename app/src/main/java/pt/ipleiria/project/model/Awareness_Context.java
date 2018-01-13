package pt.ipleiria.project.model;

import android.graphics.Bitmap;
import android.location.Location;

import com.google.android.gms.awareness.state.HeadphoneState;
import com.google.android.gms.awareness.state.TimeIntervals;
import com.google.android.gms.awareness.state.Weather;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.location.places.PlaceLikelihood;

import java.io.Serializable;
import java.util.List;

/**
 * Classe que contém todas as variáveis da Awareness API
 */

public class Awareness_Context implements Serializable {

    private HeadphoneState headphoneState;
    private Location location;
    private Weather weather;
    private DetectedActivity mostProbableActivity;
    private List<PlaceLikelihood> placeLikelihoods;
    private TimeIntervals timeIntervals;
    private String currentAddress;
    private String distanceToPlace;
    private String destination;

    private Bitmap image;


    public HeadphoneState getHeadphoneState() {
        return headphoneState;
    }

    public void setHeadphoneState(HeadphoneState headphones) {
        this.headphoneState = headphones;
    }


    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Weather getWeather() {
        return weather;
    }

    public void setWeather(Weather weather) {
        this.weather = weather;
    }

    public DetectedActivity getMostProbableActivity() {
        return mostProbableActivity;
    }

    public void setMostProbableActivity(DetectedActivity mostProbableActivity) {
        this.mostProbableActivity = mostProbableActivity;
    }

    public List<PlaceLikelihood> getPlaceLikelihoods() {
        return placeLikelihoods;
    }

    public void setPlaceLikelihoods(List<PlaceLikelihood> placeLikelihoods) {
        this.placeLikelihoods = placeLikelihoods;
    }

    public String getCurrentAddress() {
        return currentAddress;
    }

    public void setCurrentAddress(String currentAddress) {
        this.currentAddress = currentAddress;
    }

    public String getDistanceToPlace() {
        return distanceToPlace;
    }

    public void setDistanceToPlace(String distanceToPlace) {
        this.distanceToPlace = distanceToPlace;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public TimeIntervals getTimeIntervals() {
        return timeIntervals;
    }

    public void setTimeIntervals(TimeIntervals timeIntervals) {
        this.timeIntervals = timeIntervals;
    }
}
