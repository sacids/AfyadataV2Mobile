package tz.org.sacids.afyadata.adapters.model;

import org.parceler.Parcel;

import java.util.Date;

/**
 * Created by administrator on 16/09/2017.
 */
@Parcel
public class Form {
    private long id;
    private String formId;
    private String instanceId;
    private String instanceName;
    private String title;
    private Date createdAt;
    private String jrFormId;
    private long feedback;
    private String user;

    public Form() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFormId() {
        return formId;
    }

    public void setFormId(String formId) {
        this.formId = formId;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public String getInstanceName() {
        return instanceName;
    }

    public void setInstanceName(String instanceName) {
        this.instanceName = instanceName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getJrFormId() {
        return jrFormId;
    }

    public void setJrFormId(String jrFormId) {
        this.jrFormId = jrFormId;
    }

    public long getFeedback() {
        return feedback;
    }

    public void setFeedback(long feedback) {
        this.feedback = feedback;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
