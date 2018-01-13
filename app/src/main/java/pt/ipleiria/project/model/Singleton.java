package pt.ipleiria.project.model;

import com.google.android.gms.awareness.state.TimeIntervals;
import com.google.android.gms.location.DetectedActivity;

import java.util.List;

/**
 * Classe que guarda todas as variáveis do programa
 * contém também toStrings da AwarenessAPI (Passa ints para Strings)
 */
public class Singleton {
    private static final Singleton ourInstance = new Singleton();

    public static Singleton getInstance() {
        return ourInstance;
    }

    private Notes notes;
    private Checkboxes checkbox;
    private Awareness_Context awareness;
    private int updatedContext=0;


    private Singleton() {
        this.notes = new Notes();
        this.checkbox = new Checkboxes();
        this.awareness = new Awareness_Context();

    }

    public Notes getNotes(){
        return notes;
    }

    public void setNotes(Notes notes){
        this.notes = notes;
    }

    public Checkboxes getCheckboxes() {
        return checkbox;
    }

    public void setCheckboxes(Checkboxes checkboxes) {
        this.checkbox = checkboxes;
    }

    public Awareness_Context getAwareness() {
        return awareness;
    }

    public int getUpdatedContext() {
        return updatedContext;
    }

    public void setUpdatedContext() {
        this.updatedContext = 1;
    }

    public String conditionsToString(int []conditions) {
        StringBuilder condition= new StringBuilder();
        for (int condition1 : conditions) {
            switch (condition1) {
                case 0:
                    condition.append("Condition Unknown\n");
                    break;
                case 1:
                    condition.append("Clear Sky\n");
                    break;
                case 2:
                    condition.append("Cloudy Sky\n");
                    break;
                case 3:
                    condition.append("Foggy\n");
                    break;
                case 4:
                    condition.append("Haizy\n");
                    break;
                case 5:
                    condition.append("Icy\n");
                    break;
                case 6:
                    condition.append("Rainy\n");
                    break;
                case 7:
                    condition.append("Snowy\n");
                    break;
                case 8:
                    condition.append("Stormy\n");
                    break;
            }
        }
        return condition.toString();
    }

    String conditionsToString(int conditions) {
        String condition="";
            switch (conditions) {
                case 0:
                    condition += "Condition Unknown";
                    break;
                case 1:
                    condition += "Clear Sky\n";
                    break;
                case 2:
                    condition += "Cloudy Sky\n";
                    break;
                case 3:
                    condition += "Foggy\n";
                    break;
                case 4:
                    condition += "Haizy\n";
                    break;
                case 5:
                    condition += "Icy\n";
                    break;
                case 6:
                    condition += "Rainy\n";
                    break;
                case 7:
                    condition += "Snowy\n";
                    break;
                case 8:
                    condition += "Stormy\n";
                    break;
            }
        return condition;
    }

    public String activityToString(DetectedActivity mostProbableActivity){
        String activity_type;
        switch (mostProbableActivity.getType()){
            case 0:
                activity_type="In Vehicle";
                break;
            case 1:
                activity_type="On Bicycle";
                break;
            case 2:
                activity_type="On Foot";
                break;
            case 3:
                activity_type="Still";
                break;
            case 4:
                activity_type="Unknown";
                break;
            case 5:
                activity_type="Tilting";
                break;
            case 7:
                activity_type="Walking";
                break;
            case 8:
                activity_type="Running";
                break;
            default: activity_type="Unknown";
        }
        return activity_type;
    }

    public String placeTypesToString(List<Integer> placeTypes) {
        StringBuilder res = new StringBuilder();

        for (int placeType :
                placeTypes) {
            res.append(placeTypeToString(placeType)).append(" ");
        }

        return res.toString();
    }

    private String placeTypeToString(int placeType) {
        switch (placeType) {
            case 0:
                return "OTHER";
            case 1:
                return "ACCOUNTING";
            case 2:
                return "AIRPORT";
            case 3:
                return "AMUSEMENT_PARK";
            case 4:
                return "AQUARIUM";
            case 5:
                return "ART_GALLERY";
            case 6:
                return "ATM";
            case 7:
                return "BAKERY";
            case 8:
                return "BANK";
            case 9:
                return "BAR";
            case 10:
                return "BEAUTY_SALON";
            case 11:
                return "BICYCLE_STORE";
            case 12:
                return "BOOK_STORE";
            case 13:
                return "BOWLING_ALLEY";
            case 14:
                return "BUS_STATION";
            case 15:
                return "CAFE";
            case 16:
                return "CAMPGROUND";
            case 17:
                return "CAR_DEALER";
            case 18:
                return "CAR_RENTAL";
            case 19:
                return "CAR_REPAIR";
            case 20:
                return "CAR_WASH";
            case 21:
                return "CASINO";
            case 22:
                return "CEMETERY";
            case 23:
                return "CHURCH";
            case 24:
                return "CITY_HALL";
            case 25:
                return "CLOTHING_STORE";
            case 26:
                return "CONVENIENCE_STORE";
            case 27:
                return "COURTHOUSE";
            case 28:
                return "DENTIST";
            case 29:
                return "DEPARTMENT_STORE";
            case 30:
                return "DOCTOR";
            case 31:
                return "ELECTRICIAN";
            case 32:
                return "ELECTRONICS_STORE";
            case 33:
                return "EMBASSY";
            case 34:
                return "ESTABLISHMENT";
            case 35:
                return "FINANCE";
            case 36:
                return "FIRE_STATION";
            case 37:
                return "FLORIST";
            case 38:
                return "FOOD";
            case 39:
                return "FUNERAL_HOME";
            case 40:
                return "FURNITURE_STORE";
            case 41:
                return "GAS_STATION";
            case 42:
                return "GENERAL_CONTRACTOR";
            case 43:
                return "GROCERY_OR_SUPERMARKET";
            case 44:
                return "GYM";
            case 45:
                return "HAIR_CARE";
            case 46:
                return "HARDWARE_STORE";
            case 47:
                return "HEALTH";
            case 48:
                return "HINDU_TEMPLE";
            case 49:
                return "HOME_GOODS_STORE";
            case 50:
                return "HOSPITAL";
            case 51:
                return "INSURANCE_AGENCY";
            case 52:
                return "JEWELRY_STORE";
            case 53:
                return "LAUNDRY";
            case 54:
                return "LAWYER";
            case 55:
                return "LIBRARY";
            case 56:
                return "LIQUOR_STORE";
            case 57:
                return "LOCAL_GOVERNMENT_OFFICE";
            case 58:
                return "LOCKSMITH";
            case 59:
                return "LODGING";
            case 60:
                return "MEAL_DELIVERY";
            case 61:
                return "MEAL_TAKEAWAY";
            case 62:
                return "MOSQUE";
            case 63:
                return "MOVIE_RENTAL";
            case 64:
                return "MOVIE_THEATER";
            case 65:
                return "MOVING_COMPANY";
            case 66:
                return "MUSEUM";
            case 67:
                return "NIGHT_CLUB";
            case 68:
                return "PAINTER";
            case 69:
                return "PARK";
            case 70:
                return "PARKING";
            case 71:
                return "PET_STORE";
            case 72:
                return "PHARMACY";
            case 73:
                return "PHYSIOTHERAPIST";
            case 74:
                return "PLACE_OF_WORSHIP";
            case 75:
                return "PLUMBER";
            case 76:
                return "POLICE";
            case 77:
                return "POST_OFFICE";
            case 78:
                return "REAL_ESTATE_AGENCY";
            case 79:
                return "RESTAURANT";
            case 80:
                return "ROOFING_CONTRACTOR";
            case 81:
                return "RV_PARK";
            case 82:
                return "SCHOOL";
            case 83:
                return "SHOE_STORE";
            case 84:
                return "SHOPPING_MALL";
            case 85:
                return "SPA";
            case 86:
                return "STADIUM";
            case 87:
                return "STORAGE";
            case 88:
                return "STORE";
            case 89:
                return "SUBWAY_STATION";
            case 90:
                return "SYNAGOGUE";
            case 91:
                return "TAXI_STAND";
            case 92:
                return "TRAIN_STATION";
            case 93:
                return "TRAVEL_AGENCY";
            case 94:
                return "UNIVERSITY";
            case 95:
                return "VETERINARY_CARE";
            case 96:
                return "ZOO";
            case 1001:
                return "ADMINISTRATIVE_AREA_LEVEL_1";
            case 1002:
                return "ADMINISTRATIVE_AREA_LEVEL_2";
            case 1003:
                return "ADMINISTRATIVE_AREA_LEVEL_3";
            case 1004:
                return "COLLOQUIAL_AREA";
            case 1005:
                return "COUNTRY";
            case 1006:
                return "FLOOR";
            case 1007:
                return "GEOCODE";
            case 1008:
                return "INTERSECTION";
            case 1009:
                return "LOCALITY";
            case 1010:
                return "NATURAL_FEATURE";
            case 1011:
                return "NEIGHBORHOOD";
            case 1012:
                return "POLITICAL";
            case 1013:
                return "POINT_OF_INTEREST";
            case 1014:
                return "POST_BOX";
            case 1015:
                return "POSTAL_CODE";
            case 1016:
                return "POSTAL_CODE_PREFIX";
            case 1017:
                return "POSTAL_TOWN";
            case 1018:
                return "PREMISE";
            case 1019:
                return "ROOM";
            case 1020:
                return "ROUTE";
            case 1021:
                return "STREET_ADDRESS";
            case 1022:
                return "SUBLOCALITY";
            case 1023:
                return "SUBLOCALITY_LEVEL_1";
            case 1024:
                return "SUBLOCALITY_LEVEL_2";
            case 1025:
                return "SUBLOCALITY_LEVEL_3";
            case 1026:
                return "SUBLOCALITY_LEVEL_4";
            case 1027:
                return "SUBLOCALITY_LEVEL_5";
            case 1028:
                return "SUBPREMISE";
            case 1029:
                return "SYNTHETIC_GEOCODE";
            case 1030:
                return "TRANSIT_STATION";
            default:
                return null;
        }
    }

    public String timeIntervalstoString(TimeIntervals timeIntervals){
        String timeInterval="";
        int[] timeIntervals1 = timeIntervals.getTimeIntervals();
        for (int time : timeIntervals.getTimeIntervals()) {
            switch (time){
                case 1:
                    timeInterval+="Weekday\n";
                    break;
                case 2:
                    timeInterval+="Weekend\n";
                    break;
                case 3:
                    timeInterval+="Holiday\n";
                    break;
                case 4:
                    timeInterval+="Morning\n";
                    break;
                case 5:
                    timeInterval+="Afternoon\n";
                    break;
                case 6:
                    timeInterval+="Evening\n";
                    break;
                case 7:
                    timeInterval+="Night\n";
                    break;
            }
        }
        return timeInterval;
    }

}
