package pt.ipleiria.project.model;

import java.io.Serializable;

/**
 * Classe que contém todas as checkboxes
 */

public final class Checkboxes implements Serializable {

    private boolean [] checkedItens = new boolean[4];
    private boolean [] filterItens = new boolean[6];
    private boolean [] fenceItens = new boolean[4];

    private boolean filterByBody = checkedItens[0];
    private boolean filterByImage = checkedItens[1];
    private boolean filterByVideo = checkedItens[2];
    private boolean filterByContext = checkedItens[3];


    private boolean searchByID = filterItens[0];
    private boolean searchByTitle = filterItens[1];
    private boolean searchByDescription = filterItens[2];
    private boolean searchByKeywords = filterItens[3];
    private boolean searchByCreationDate = filterItens[4];
    private boolean searchByEditDate = filterItens[5];

    private boolean headphoneFence = fenceItens[0];
    private boolean locationFence = fenceItens[1];
    private boolean activityFence = fenceItens[2];
    private boolean timeFence = fenceItens[3];

    public boolean isHeadphoneFence() {
        return headphoneFence;
    }

    public void setHeadphoneFence(boolean headphoneFence) {
        this.headphoneFence = headphoneFence;
    }

    public boolean isLocationFence() {
        return locationFence;
    }

    public void setLocationFence(boolean locationFence) {
        this.locationFence = locationFence;
    }

    public boolean isActivityFence() {
        return activityFence;
    }

    public void setActivityFence(boolean activityFence) {
        this.activityFence = activityFence;
    }

    public boolean isTimeFence() {
        return timeFence;
    }

    public void setTimeFence(boolean timeFence) {
        this.timeFence = timeFence;
    }

    public boolean[] getCheckedItens() {
        return checkedItens;
    }

    public void setCheckedItens(boolean[] checkedItens) {
        this.checkedItens = checkedItens;
    }

    public boolean[] getFilterItens() {
        return filterItens;
    }

    public void setFilterItens(boolean[] filterItens) {
        this.filterItens = filterItens;
    }

    public void setFilterByBody(boolean filterByBody) {
        this.filterByBody = filterByBody;
    }

    public void setFilterByImage(boolean filterByImage) {
        this.filterByImage = filterByImage;
    }

    public void setFilterByVideo(boolean filterByVideo) {
        this.filterByVideo = filterByVideo;
    }

    public void setSearchByID(boolean searchByID) {
        this.searchByID = searchByID;
    }

    public void setSearchByTitle(boolean searchByTitle) {
        this.searchByTitle = searchByTitle;
    }

    public void setSearchByDescription(boolean searchByDescription) {
        this.searchByDescription = searchByDescription;
    }

    public void setSearchByKeywords(boolean searchByKeywords) {
        this.searchByKeywords = searchByKeywords;
    }

    public void setSearchByCreationDate(boolean searchByCreationDate) {
        this.searchByCreationDate = searchByCreationDate;
    }

    public void setSearchByEditDate(boolean searchByEditDate) {
        this.searchByEditDate = searchByEditDate;
    }

    public void setFilterByContext(boolean filterByContext) {
        this.filterByContext = filterByContext;
    }

    public boolean isFilterByBody() {
        return filterByBody;
    }

    public boolean isFilterByImage() {
        return filterByImage;
    }

    public boolean isFilterByVideo() {
        return filterByVideo;
    }

    public boolean isSearchByID() {
        return searchByID;
    }

    public boolean isSearchByTitle() {
        return searchByTitle;
    }

    public boolean isSearchByDescription() {
        return searchByDescription;
    }

    public boolean isSearchByKeywords() {
        return searchByKeywords;
    }

    public boolean isSearchByCreationDate() {
        return searchByCreationDate;
    }

    public boolean isSearchByEditDate() {
        return searchByEditDate;
    }

    public boolean isFilterByContext() {
        return filterByContext;
    }

    public boolean[] getFenceItens() {
        return fenceItens;
    }

    public void setFenceItens(boolean[] fenceItens) {
        this.fenceItens = fenceItens;
    }

}
