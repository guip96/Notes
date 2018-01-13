package pt.ipleiria.project.model;

import android.location.Location;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by guilh on 23/10/2017.
 */

public class Notes implements Serializable {
    private ArrayList<Note> notes;

    /**
     * Método Notes(): Construtor
     */
    public Notes() {
        this.notes = new ArrayList<Note>();
    }

    /**
     * Método addNote(): Adiciona uma nova nota
     *
     * @param note
     */
    public void addNote(Note note) {
        notes.add(note);
    }

    /**
     * Método removeNote(): Remove uma nota da lista de uma posição dada
     *
     * @param position
     */
    public void removeNote(int position) {
        notes.remove(position);
    }

    /**
     * Método editNote(): Edita uma nota da lista de uma posição dada
     *
     * @param position
     * @param n
     */
    public void editNote(int position, Note n) {
        notes.get(position).setId(n.getId());
        notes.get(position).setTitle(n.getTitle());
        notes.get(position).setBody(n.getBody());
        notes.get(position).setKeyword(n.getKeyword());
        notes.get(position).setEdit_date();
        notes.get(position).setImage(n.getImage());
        notes.get(position).setVideo(n.getVideo());
    }

    /**
     * Método searchNoteByEverything(): Procura uma nota que contenha uma string dada de acordo
     * com os parâmetros dados por selectedItens e retorna a uma lista de notas que contêm
     * essa string
     *
     * @param string
     * @param constant
     * @return
     */
    public ArrayList<Note> searchNoteByEverything(String string, Checkboxes constant) {
        ArrayList<Note> res = new ArrayList<>();
        for (Note n : notes) {
            ArrayList<String> keywordList = n.getKeyword();
            String keywords = "";
            for (int x = 0; x < keywordList.size(); x++) {
                keywords.concat(keywordList.get(x));
            }
            if ((n.getId().toLowerCase().contains(string.toLowerCase()) && constant.isSearchByID())) {
                res.add(n);
            } else if ((n.getTitle().toLowerCase().contains(string.toLowerCase())) && constant.isSearchByTitle()) {
                res.add(n);
            } else if ((n.getBody().toLowerCase().contains(string.toLowerCase()) && constant.isSearchByDescription())) {
                res.add(n);
            } else if ((keywords.contains(string.toLowerCase())) && constant.isSearchByKeywords()) {
                res.add(n);
            } else if ((n.getCreation_dateFormated().toLowerCase().contains(string.toLowerCase())) && constant.isSearchByCreationDate()) {
                res.add(n);
            } else if ((n.getEdit_dateFormated().toLowerCase().contains(string.toLowerCase())) && constant.isSearchByEditDate()) {
                res.add(n);
            }
        }
        return res;
    }


    /**
     * Método filterNote(): Procura todas as notas na lista que contêm os parametros
     * selecctionados pelo Array itens e retorna uma lista que contêm todas as notas que contêm
     * esses atributos
     *
     * @param constant
     * @return
     */
    public ArrayList<Note> filterNote(Checkboxes constant) {
        ArrayList<Note> res = new ArrayList<>();

        for (Note n : notes) {
            if (constant.isFilterByBody() && (n.getBody() != "" && n.getBody() != null)) {
                res.add(n);
            } else if (constant.isFilterByImage() && n.getImage() != null) {
                res.add(n);
            } else if (constant.isFilterByVideo() && n.getVideo() != null) {
                res.add(n);
            } else {
                if (constant.isFilterByContext()) {
                    if (Singleton.getInstance().getUpdatedContext() == 1) {
                        for (String keyword : n.getKeyword()) {
                            int state = Singleton.getInstance().getAwareness().getHeadphoneState().getState();
                            if ((keyword.contains("#Headphones:Plugged") && state == 1)
                                    || (keyword.contains("#Headphones:Unplugged") && state == 2)) {
                                res.add(n);
                                break;
                            }

                            if (keyword.startsWith("#Temperature")) {
                                if(!(Singleton.getInstance().getAwareness().getHeadphoneState()==null)) {
                                    float temperature = Singleton.getInstance().
                                            getAwareness().getWeather().getTemperature(2);
                                    float temp = Float.valueOf(keyword.replaceAll("[^\\d.]+|\\.(?!\\d)", ""));
                                    if ((keyword.contains("Greater") && (temp < temperature))
                                            || (keyword.contains("Equals") && (temp == temperature))
                                            || (keyword.contains("Lower") && (temp > temperature))) {
                                        res.add(n);
                                        break;
                                    }
                                }
                            }

                            if (keyword.startsWith("#Weather:")) {
                                if(!(Singleton.getInstance().getAwareness().getWeather()==null)) {
                                    int[] conditions = Singleton.getInstance().getAwareness().getWeather().getConditions();
                                    if (keyword.contains(Singleton.getInstance().conditionsToString(conditions[0]))) {
                                        res.add(n);
                                        break;
                                    }
                                }
                            }

                            if (keyword.contains("#Nearby:")) {
                                if(!(Singleton.getInstance().getAwareness().getPlaceLikelihoods()==null)) {
                                    String placeTypes = Singleton.getInstance().placeTypesToString(Singleton.getInstance().getAwareness().getPlaceLikelihoods()
                                            .get(0).getPlace().getPlaceTypes());
                                    String[] keyword_place = keyword.split(":");
                                    if (placeTypes.contains(keyword_place[1])) {
                                        res.add(n);
                                        break;
                                    }
                                }
                            }

                            if (keyword.startsWith("#Activity:")) {
                                if(!(Singleton.getInstance().getAwareness().getMostProbableActivity()==null)) {
                                    String activity = Singleton.getInstance().activityToString
                                            (Singleton.getInstance().getAwareness().getMostProbableActivity());
                                    String[] keyword_activity = keyword.split(":");
                                    if (activity.contains(keyword_activity[1])) {
                                        res.add(n);
                                        break;
                                    }
                                }
                            }

                            if (keyword.startsWith("#Time:")) {
                                if(!(Singleton.getInstance().getAwareness().getTimeIntervals()==null)) {
                                    String time = Singleton.getInstance().timeIntervalstoString(
                                            Singleton.getInstance().getAwareness().getTimeIntervals());
                                    String[] keyword_time = keyword.split(":");
                                    if (time.contains(keyword_time[1])) {
                                        res.add(n);
                                        break;
                                    }
                                }
                            }

                            if (keyword.startsWith("#Location:")) {
                                if(!(Singleton.getInstance().getAwareness().getLocation()==null)) {
                                    Location location = Singleton.getInstance().getAwareness().getLocation();
                                    String[] keyword_location = keyword.split(":");
                                    String[] latlng_split = keyword_location[1].split(",");
                                    float[] distance = new float[1];
                                    Location.distanceBetween(Double.parseDouble(latlng_split[0]),
                                            Double.parseDouble(latlng_split[1]),
                                            location.getLatitude(),
                                            location.getLongitude(),
                                            distance);
                                    if (distance[0] <= 100) {
                                        res.add(n);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return res;
    }

    /**
     * Método searchNoteByID(): Procura na lista de notas uma nota que tenha o ID igual à
     * string dada, se for igual retorna a posição da mesma
     *
     * @param string
     * @return
     */
    public int searchNoteByID(String string) {
        int position = 0;
        for (int i = 0; i < notes.size(); i++) {
            if (notes.get(i).getId().toLowerCase().contains(string.toLowerCase())) {
                position = i;
                break;
            }
        }
        return position;
    }

    /**
     * Método getNotes(): retorna a lista notes (principal)
     *
     * @return
     */
    public ArrayList<Note> getNotes() {
        return notes;
    }


    /**
     * Método toString(): Método que retorna o toString de todas as notas da lista principal
     *
     * @return
     */
    @Override
    public String toString() {
        String res = "";
        String keywords="";
        for (Note n : notes) {
            for(String keyword:n.getKeyword()){
                keywords+=keyword+";";
            }
            res += n.getTitle()+";"+n.getBody()+";"+keywords+"\n";
        }
        return res;
    }

}
