package tz.org.sacids.afyadata.adapters.model;

import org.parceler.Parcel;

/**
 * Created by administrator on 16/09/2017.
 */
@Parcel
public class Symptom {
    long id;
    String title;
    String description;

    public Symptom() {
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
