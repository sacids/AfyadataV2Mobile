package org.sacids.afyadata.adapters.model;

import org.parceler.Parcel;

/**
 * Created by administrator on 16/09/2017.
 */
@Parcel
public class SearchableForm {
    long id;
    String title;
    String jrFormId;

    public SearchableForm() {
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

    public String getJrFormId() {
        return jrFormId;
    }

    public void setJrFormId(String jrFormId) {
        this.jrFormId = jrFormId;
    }
}
