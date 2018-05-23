package org.sacids.app.afyadata.adapters.model;

import org.parceler.Parcel;

/**
 * Created by administrator on 16/09/2017.
 */
@Parcel
public class FormDetails {
    long id;
    String label;
    String type;
    String value;
    String instanceId;

    public FormDetails() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }
}
