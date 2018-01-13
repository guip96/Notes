package pt.ipleiria.project.model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.UUID;

/**
 * Created by guilh on 23/10/2017.
 */

public class Note implements Serializable{
    private String id;
    private String title;
    private String body;
    private ArrayList<String> keywords;
    private Calendar creation_date = new GregorianCalendar();
    private Calendar edit_date = new GregorianCalendar();
    private String image;
    private String video;

    /**
     * Construtor da classe Note
     * @param title
     * @param body
     * @param keywords
     * @param image
     * @param video
     */
    public Note(String title, String body, ArrayList<String> keywords, String image, String video) {
        this.id = UUID.randomUUID().toString();
        this.title = title;
        this.body = body;
        this.keywords = keywords;
        this.creation_date = Calendar.getInstance();
        this.edit_date = Calendar.getInstance();
        this.image = image;
        this.video = video;
    }

    /**
     * Set do atributo creation_date
     * @param creation_date
     */
    public void setCreation_date(Calendar creation_date) {
        this.creation_date = creation_date;
    }

    /**
     * Get do atributo creation_date
     * @return
     */
    public Calendar getCreation_date() {
        return creation_date;
    }

    /**
     * Get da creation_date formatada
     * @return
     */
    public String getCreation_dateFormated() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        return sdf.format(this.creation_date.getTime());
    }

    /**
     * Set do atributo id
     * @param id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Set do atributo title
     * @param title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Set do atributo body
     * @param body
     */
    public void setBody(String body) {
        this.body = body;
    }

    /**
     * Set do atributo keyword
     * @param keywords
     */
    public void setKeyword(ArrayList<String> keywords) {
        this.keywords = keywords;
    }

    /**
     * Get da edit_date formatada
     * @return
     */
    public String getEdit_dateFormated() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        return sdf.format(this.edit_date.getTime());
    }

    /**
     * Get do atributo title
     * @return
     */
    public String getTitle() {
        return title;
    }

    /**
     * Get do atributo id
     * @return
     */
    public String getId(){
        return id;
    }

    /**
     * Get do atributo body
     * @return
     */
    public String getBody() {
        return body;
    }

    /**
     * Get do atributo keyword
     * @return
     */
    public ArrayList<String> getKeyword() {
        return keywords;
    }

    /**
     * Get do atributo image
     * @return
     */
    public String getImage() {
        return image;
    }

    /**
     * Set do atributo image
     * @param image
     */
    public void setImage(String image) {
        this.image = image;
    }

    /**
     * Get do atributo video
     * @return
     */
    public String getVideo() {
        return video;
    }

    /**
     * Set do atributo video
     * @param video
     */
    public void setVideo(String video) {
        this.video = video;
    }

    /**
     * Método toString(): toString da classe Note, que mostra apenas o titulo e a
     * data de última edição
     * @return
     */
    @Override
    public String toString() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String edit_date_aux = sdf.format(this.edit_date.getTime());
        return "Title: " + title + "\n" + "Edit Date: " + edit_date_aux;
    }

    /**
     * Set da edit_date
     */
    public void setEdit_date() {
        this.edit_date = Calendar.getInstance();
    }

}
