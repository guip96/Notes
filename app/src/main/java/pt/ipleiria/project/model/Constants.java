package pt.ipleiria.project.model;

/**
 * Classe que contém Constantes de texto para todos os dialogs do programa
 */

public interface Constants {

    String[] types = {"Headphones", "Temperature", "Weather", "Location", "Nearby", "Activity","Time"};
    String[] keywd_Head = {"Plugged", "Unplugged"};
    String[] keywd_Temp = {"Greater than", "Equal to", "Lower than"};
    String[] keywd_Cond = {"Clear Sky", "Cloudy Sky", "Foggy", "Haizy", "Icy", "Rainy", "Snowy", "Stormy"};
    String[] keywd_Loc = {"Insert Latitude/Longitude", "Get Location from Google Maps"};
    String[] keywd_Act = {"In Vehicle", "On Bicycle", "On Foot", "Still", "Walking", "Running"};
    String[] keywd_Time = {"Weekday","Weekend","Holiday","Morning","Afternoon","Evening","Night"};
    String[] keywd_NBP = {"OTHER","ACCOUNTING","AIRPORT","AMUSEMENT_PARK","AQUARIUM","ART_GALLERY","ATM","BAKERY","BANK","BAR","BEAUTY_SALON","BICYCLE_STORE","BOOK_STORE","BOWLING_ALLEY","BUS_STATION","CAFE","CAMPGROUND","CAR_DEALER","CAR_RENTAL","CAR_REPAIR","CAR_WASH","CASINO","CEMETERY","CHURCH","CITY_HALL","CLOTHING_STORE","CONVENIENCE_STORE","COURTHOUSE","DENTIST","DEPARTMENT_STORE","DOCTOR","ELECTRICIAN","ELECTRONICS_STORE","EMBASSY","ESTABLISHMENT","FINANCE","FIRE_STATION","FLORIST","FOOD","FUNERAL_HOME","FURNITURE_STORE","GAS_STATION","GENERAL_CONTRACTOR","GROCERY_OR_SUPERMARKET","GYM","HAIR_CARE","HARDWARE_STORE","HEALTH","HINDU_TEMPLE","HOME_GOODS_STORE","HOSPITAL","INSURANCE_AGENCY","JEWELRY_STORE","LAUNDRY","LAWYER","LIBRARY","LIQUOR_STORE","LOCAL_GOVERNMENT_OFFICE","LOCKSMITH","LODGING","MEAL_DELIVERY","MEAL_TAKEAWAY","MOSQUE","MOVIE_RENTAL","MOVIE_THEATER","MOVING_COMPANY","MUSEUM","NIGHT_CLUB","PAINTER","PARK","PARKING","PET_STORE","PHARMACY","PHYSIOTHERAPIST","PLACE_OF_WORSHIP","PLUMBER","POLICE","POST_OFFICE","REAL_ESTATE_AGENCY","RESTAURANT","ROOFING_CONTRACTOR","RV_PARK","SCHOOL","SHOE_STORE","SHOPPING_MALL","SPA","STADIUM","STORAGE","STORE","SUBWAY_STATION","SYNAGOGUE","TAXI_STAND","TRAIN_STATION","TRAVEL_AGENCY","UNIVERSITY","VETERINARY_CARE","ZOO","ADMINISTRATIVE_AREA_LEVEL_1","ADMINISTRATIVE_AREA_LEVEL_2","ADMINISTRATIVE_AREA_LEVEL_3","COLLOQUIAL_AREA","COUNTRY","FLOOR","GEOCODE","INTERSECTION","LOCALITY","NATURAL_FEATURE","NEIGHBORHOOD","POLITICAL","POINT_OF_INTEREST","POST_BOX","POSTAL_CODE","POSTAL_CODE_PREFIX","POSTAL_TOWN","PREMISE","ROOM","ROUTE","STREET_ADDRESS","SUBLOCALITY","SUBLOCALITY_LEVEL_1","SUBLOCALITY_LEVEL_2","SUBLOCALITY_LEVEL_3","SUBLOCALITY_LEVEL_4","SUBLOCALITY_LEVEL_5","SUBPREMISE","SYNTHETIC_GEOCODE","TRANSIT_STATION"};
    CharSequence[] searchby_checkbox_Items = {"ID", "Title", "Description", "Keywords", "Creation Date", "Edit Date"};
    CharSequence[] fence_checkbox_Itens = {"Headphone Fence", "Location Fence", "Activity Fence", "Time Fence"};

}
