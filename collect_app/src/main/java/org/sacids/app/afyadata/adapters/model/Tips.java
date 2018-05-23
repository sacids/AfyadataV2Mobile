package org.sacids.app.afyadata.adapters.model;

import org.parceler.Parcel;

/**
 * Created by administrator on 13/09/2017.
 */

@Parcel
public class Tips {
    long id;
    String title;
    String description;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
