package org.mozambique.app.afyadata.adapters.model;

/**
 * Created by administrator on 13/09/2017.
 */

public class Tips {
    private long id;
    private String title;
    private String photo;
    private String description;

    public Tips() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
