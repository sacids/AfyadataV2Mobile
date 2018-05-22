package tz.org.sacids.afyadata.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import tz.org.sacids.afyadata.adapters.model.Campaign;
import tz.org.sacids.afyadata.adapters.model.Feedback;
import tz.org.sacids.afyadata.adapters.model.Form;
import tz.org.sacids.afyadata.adapters.model.FormDetails;
import tz.org.sacids.afyadata.adapters.model.Forms;
import tz.org.sacids.afyadata.adapters.model.SearchableData;
import tz.org.sacids.afyadata.adapters.model.Symptom;
import tz.org.sacids.afyadata.adapters.model.Tips;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by administrator on 13/09/2017.
 */

public class AfyaDataV2DB extends SQLiteOpenHelper {

    private static final String TAG = "AfyadataV2";

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 3;

    // Database Name
    private static final String DATABASE_NAME = "afyadata.db";

    // Feedback table
    private static final String TABLE_FEEDBACK = "feedback";
    private static final String KEY_FEEDBACK_ID = "id";
    private static final String KEY_FEEDBACK_FORM_ID = "form_id";
    private static final String KEY_INSTANCE_ID = "instance_id";
    private static final String KEY_FORM_TITLE = "title";
    private static final String KEY_MESSAGE = "message";
    private static final String KEY_SENDER = "sender";
    private static final String KEY_USER = "user";
    private static final String KEY_CHR_NAME = "chr_name";
    private static final String KEY_DATE_CREATED = "date_created";
    private static final String KEY_FEEDBACK_STATUS = "status";
    private static final String KEY_FEEDBACK_REPLY_BY = "reply_by";

    //Form details table
    private static final String TABLE_FORM_DETAILS = "form_details";
    private static final String KEY_FORM_DETAILS_ID = "id";
    private static final String KEY_FORM_DETAILS_LABEL = "label";
    private static final String KEY_FORM_DETAILS_TYPE = "type";
    private static final String KEY_FORM_DETAILS_VALUE = "value";
    private static final String KEY_FORM_DETAILS_INSTANCE_ID = "instance_id";

    //Campaign table
    private static final String TABLE_CAMPAIGN = "campaign";
    private static final String KEY_CAMPAIGN_ID = "id";
    private static final String KEY_CAMPAIGN_TITLE = "title";
    private static final String KEY_CAMPAIGN_TYPE = "type";
    private static final String KEY_CAMPAIGN_DESCRIPTION = "description";
    private static final String KEY_CAMPAIGN_ICON = "icon";
    private static final String KEY_CAMPAIGN_JR_FORM_ID = "jr_form_id";

    //OHKR tips
    private static final String TABLE_OHKR_TIPS = "ohkr_tips";
    private static final String KEY_TIP_ID = "id";
    private static final String KEY_TIP_TITLE = "title";
    private static final String KEY_TIP_DESCRIPTION = "description";

    //OHKR Symptoms List
    private static final String TABLE_OHKR_SYMPTOMS = "ohkr_symptoms";
    private static final String KEY_SYMPTOM_ID = "id";
    private static final String KEY_SYMPTOM_TITLE = "title";
    private static final String KEY_SYMPTOM_DESCRIPTION = "description";

    //searchable form
    private static final String TABLE_SEARCHABLE_FORM = "searchable_form";
    private static final String KEY_SEARCHABLE_FORM_ID = "id";
    private static final String KEY_SEARCHABLE_JR_FORM_ID = "jr_form_id";
    private static final String KEY_SEARCHABLE_FORM_TITLE = "title";

    //searchable form data
    private static final String TABLE_SEARCHABLE_DATA = "searchable_data";
    private static final String KEY_SEARCHABLE_DATA_ID = "id";
    private static final String KEY_SEARCHABLE_DATA_FORM_ID = "form_id";
    private static final String KEY_SEARCHABLE_DATA_LABEL = "label";
    private static final String KEY_SEARCHABLE_DATA_VALUE = "value";

    //forms
    private static final String TABLE_FORMS = "forms";
    private static final String KEY_FORMS_ID = "id";
    private static final String KEY_FORMS_FORM_ID = "form_id";
    private static final String KEY_FORMS_TITLE = "title";
    private static final String KEY_FORMS_DESCRIPTION = "description";
    private static final String KEY_FORMS_DOWNLOAD_URL = "download_url";


    public AfyaDataV2DB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {

        //Create feedback table
        String CREATE_FEEDBACK_TABLE = "CREATE TABLE "
                + TABLE_FEEDBACK + "("
                + KEY_FEEDBACK_ID + " INTEGER PRIMARY KEY,"
                + KEY_FEEDBACK_FORM_ID + " TEXT,"
                + KEY_INSTANCE_ID + " TEXT,"
                + KEY_FORM_TITLE + " TEXT,"
                + KEY_MESSAGE + " TEXT,"
                + KEY_SENDER + " TEXT,"
                + KEY_USER + " TEXT,"
                + KEY_CHR_NAME + " TEXT,"
                + KEY_DATE_CREATED + " TEXT,"
                + KEY_FEEDBACK_STATUS + " TEXT,"
                + KEY_FEEDBACK_REPLY_BY + " TEXT" + ")";

        //Create form_details table
        String CREATE_FORM_DETAILS_TABLE = "CREATE TABLE "
                + TABLE_FORM_DETAILS + "("
                + KEY_FORM_DETAILS_ID + " INTEGER PRIMARY KEY,"
                + KEY_FORM_DETAILS_LABEL + " TEXT,"
                + KEY_FORM_DETAILS_TYPE + " TEXT,"
                + KEY_FORM_DETAILS_VALUE + " TEXT,"
                + KEY_FORM_DETAILS_INSTANCE_ID + " TEXT" + ")";

        //Create campaign table
        String CREATE_CAMPAIGN_TABLE = "CREATE TABLE "
                + TABLE_CAMPAIGN + "("
                + KEY_CAMPAIGN_ID + " INTEGER PRIMARY KEY,"
                + KEY_CAMPAIGN_TITLE + " TEXT,"
                + KEY_CAMPAIGN_TYPE + " TEXT,"
                + KEY_CAMPAIGN_DESCRIPTION + " TEXT,"
                + KEY_CAMPAIGN_ICON + " TEXT,"
                + KEY_CAMPAIGN_JR_FORM_ID + " TEXT" + ")";

        //Create tips table
        String CREATE_TIPS_TABLE = "CREATE TABLE "
                + TABLE_OHKR_TIPS + "("
                + KEY_TIP_ID + " INTEGER PRIMARY KEY,"
                + KEY_TIP_TITLE + " TEXT,"
                + KEY_TIP_DESCRIPTION + " TEXT" + ")";

        //Create symptoms table
        String CREATE_SYMPTOMS_TABLE = "CREATE TABLE "
                + TABLE_OHKR_SYMPTOMS + "("
                + KEY_SYMPTOM_ID + " INTEGER PRIMARY KEY," // and auto increment will be handled with
                + KEY_SYMPTOM_TITLE + " TEXT,"
                + KEY_SYMPTOM_DESCRIPTION + " TEXT" + ")";

        //Create searchable_form table
        String CREATE_SEARCHABLE_FORM_TABLE = "CREATE TABLE "
                + TABLE_SEARCHABLE_FORM + "("
                + KEY_SEARCHABLE_FORM_ID + " INTEGER PRIMARY KEY,"
                + KEY_SEARCHABLE_JR_FORM_ID + " TEXT,"
                + KEY_SEARCHABLE_FORM_TITLE + " TEXT" + ")";

        //Create
        String CREATE_SEARCHABLE_DATA_TABLE = "CREATE TABLE "
                + TABLE_SEARCHABLE_DATA + "("
                + KEY_SEARCHABLE_DATA_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_SEARCHABLE_DATA_FORM_ID + " TEXT,"
                + KEY_SEARCHABLE_DATA_LABEL + " TEXT,"
                + KEY_SEARCHABLE_DATA_VALUE + " TEXT" + ")";

        //Create table forms
        String CREATE_FORMS_TABLE = "CREATE TABLE " + TABLE_FORMS + "("
                + KEY_FORMS_ID + " INTEGER PRIMARY KEY,"
                + KEY_FORMS_FORM_ID + " TEXT,"
                + KEY_FORMS_TITLE + " TEXT,"
                + KEY_FORMS_DESCRIPTION + " TEXT,"
                + KEY_FORMS_DOWNLOAD_URL + " TEXT" + ")";

        //execute
        db.execSQL(CREATE_FEEDBACK_TABLE);
        db.execSQL(CREATE_FORM_DETAILS_TABLE);
        db.execSQL(CREATE_CAMPAIGN_TABLE);
        db.execSQL(CREATE_TIPS_TABLE);
        db.execSQL(CREATE_SYMPTOMS_TABLE);
        db.execSQL(CREATE_SEARCHABLE_FORM_TABLE);
        db.execSQL(CREATE_SEARCHABLE_DATA_TABLE);
        db.execSQL(CREATE_FORMS_TABLE);

        //Log
        Log.d(TAG, "Database tables created");
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FEEDBACK);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FORM_DETAILS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CAMPAIGN);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_OHKR_TIPS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_OHKR_SYMPTOMS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SEARCHABLE_FORM);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SEARCHABLE_DATA);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FORMS);

        // Create tables again
        onCreate(db);
    }

    /*******************************************************************
     * All CRUD(Create, Read, Update, Delete) Operations for Feedback
     *******************************************************************/
    // Adding new Feedback
    public void addFeedback(Feedback feedback) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_FEEDBACK_ID, feedback.getId());
        values.put(KEY_FEEDBACK_FORM_ID, feedback.getFormId());
        values.put(KEY_INSTANCE_ID, feedback.getInstanceId());
        values.put(KEY_FORM_TITLE, feedback.getTitle());
        values.put(KEY_MESSAGE, feedback.getMessage());
        values.put(KEY_SENDER, feedback.getSender());
        values.put(KEY_USER, feedback.getUserName());
        values.put(KEY_CHR_NAME, feedback.getChrName());
        values.put(KEY_DATE_CREATED, feedback.getDateCreated());
        values.put(KEY_FEEDBACK_STATUS, feedback.getStatus());
        values.put(KEY_FEEDBACK_REPLY_BY, feedback.getReplyBy());

        // Inserting Row
        db.insert(TABLE_FEEDBACK, null, values);
        db.close(); // Closing database connection
    }

    // Getting Feedback List
    public List<Feedback> getFeedbackList() {

        List<Feedback> feedbackList = new ArrayList<Feedback>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_FEEDBACK
                + " GROUP BY " + KEY_INSTANCE_ID + " ORDER BY " + KEY_FEEDBACK_ID + " DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Feedback feedback = new Feedback();
                feedback.setId(Long.parseLong(cursor.getString(cursor.getColumnIndex(KEY_FEEDBACK_ID))));
                feedback.setFormId(cursor.getString(cursor.getColumnIndex(KEY_FEEDBACK_FORM_ID)));
                feedback.setInstanceId(cursor.getString(cursor.getColumnIndex(KEY_INSTANCE_ID)));
                feedback.setTitle(cursor.getString(cursor.getColumnIndex(KEY_FORM_TITLE)));
                feedback.setMessage(cursor.getString(cursor.getColumnIndex(KEY_MESSAGE)));
                feedback.setSender(cursor.getString(cursor.getColumnIndex(KEY_SENDER)));
                feedback.setUserName(cursor.getString(cursor.getColumnIndex(KEY_USER)));
                feedback.setChrName(cursor.getString(cursor.getColumnIndex(KEY_CHR_NAME)));
                feedback.setDateCreated(cursor.getString(cursor.getColumnIndex(KEY_DATE_CREATED)));
                feedback.setStatus(cursor.getString(cursor.getColumnIndex(KEY_FEEDBACK_STATUS)));
                feedback.setReplyBy(cursor.getString(cursor.getColumnIndex(KEY_FEEDBACK_REPLY_BY)));

                // Adding feedback to list
                feedbackList.add(feedback);
            } while (cursor.moveToNext());
        }

        // return feedback list
        return feedbackList;
    }

    // Getting Feedback by Instance
    public List<Feedback> getFeedbackByInstanceId(String instanceId) {

        List<Feedback> feedbackList = new ArrayList<Feedback>();
        // Select All Query based on instanceId
        String selectQuery = "SELECT * FROM " + TABLE_FEEDBACK + " WHERE " + KEY_INSTANCE_ID + " = ?";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[]{instanceId});

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Feedback feedback = new Feedback();
                feedback.setId(Long.parseLong(cursor.getString(cursor.getColumnIndex(KEY_FEEDBACK_ID))));
                feedback.setFormId(cursor.getString(cursor.getColumnIndex(KEY_FEEDBACK_FORM_ID)));
                feedback.setInstanceId(cursor.getString(cursor.getColumnIndex(KEY_INSTANCE_ID)));
                feedback.setTitle(cursor.getString(cursor.getColumnIndex(KEY_FORM_TITLE)));
                feedback.setMessage(cursor.getString(cursor.getColumnIndex(KEY_MESSAGE)));
                feedback.setSender(cursor.getString(cursor.getColumnIndex(KEY_SENDER)));
                feedback.setUserName(cursor.getString(cursor.getColumnIndex(KEY_USER)));
                feedback.setChrName(cursor.getString(cursor.getColumnIndex(KEY_CHR_NAME)));
                feedback.setDateCreated(cursor.getString(cursor.getColumnIndex(KEY_DATE_CREATED)));
                feedback.setStatus(cursor.getString(cursor.getColumnIndex(KEY_FEEDBACK_STATUS)));
                feedback.setReplyBy(cursor.getString(cursor.getColumnIndex(KEY_FEEDBACK_REPLY_BY)));

                // Adding feedback to list
                feedbackList.add(feedback);
            } while (cursor.moveToNext());
        }

        // return feedback list
        return feedbackList;
    }

    //check if feedback exists
    public boolean isFeedbackExist(Feedback feedback) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_FEEDBACK, new String[]{KEY_FEEDBACK_ID,
                        KEY_FEEDBACK_FORM_ID, KEY_INSTANCE_ID, KEY_FORM_TITLE, KEY_MESSAGE, KEY_SENDER, KEY_USER,
                        KEY_CHR_NAME, KEY_DATE_CREATED, KEY_FEEDBACK_STATUS, KEY_FEEDBACK_REPLY_BY},
                KEY_FEEDBACK_ID + "=?", new String[]{String.valueOf(feedback.getId())}, null, null, null, null);

        int count = cursor.getCount();
        cursor.close();
        return (count > 0) ? true : false;
    }

    // Updating feedback
    public int updateFeedback(Feedback feedback) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_FEEDBACK_FORM_ID, feedback.getFormId());
        values.put(KEY_INSTANCE_ID, feedback.getInstanceId());
        values.put(KEY_FORM_TITLE, feedback.getTitle());
        values.put(KEY_MESSAGE, feedback.getMessage());
        values.put(KEY_SENDER, feedback.getSender());
        values.put(KEY_USER, feedback.getUserName());
        values.put(KEY_CHR_NAME, feedback.getChrName());
        values.put(KEY_DATE_CREATED, feedback.getDateCreated());
        values.put(KEY_FEEDBACK_STATUS, feedback.getStatus());
        values.put(KEY_FEEDBACK_REPLY_BY, feedback.getReplyBy());

        // updating row
        return db.update(TABLE_FEEDBACK, values, KEY_FEEDBACK_ID + " = ?",
                new String[]{String.valueOf(feedback.getId())});
    }

    // Deleting feedback
    public void deleteFeedback(String instanceId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_FEEDBACK, KEY_INSTANCE_ID + " = ?",
                new String[]{instanceId});
        db.close();
    }

    //get Last Feedback
    public Feedback getLastFeedback() {
        String selectQuery = "SELECT  * FROM " + TABLE_FEEDBACK + " ORDER BY " + KEY_FEEDBACK_ID + " DESC LIMIT 1";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        Feedback feedback = null;

        //check if cursor not null
        if (cursor != null && cursor.moveToFirst()) {
            //feedback constructor
            feedback = new Feedback(
                    Long.parseLong(cursor.getString(cursor.getColumnIndex(KEY_FEEDBACK_ID))),
                    cursor.getString(cursor.getColumnIndex(KEY_FEEDBACK_FORM_ID)),
                    cursor.getString(cursor.getColumnIndex(KEY_INSTANCE_ID)),
                    cursor.getString(cursor.getColumnIndex(KEY_FORM_TITLE)),
                    cursor.getString(cursor.getColumnIndex(KEY_MESSAGE)),
                    cursor.getString(cursor.getColumnIndex(KEY_SENDER)),
                    cursor.getString(cursor.getColumnIndex(KEY_USER)),
                    cursor.getString(cursor.getColumnIndex(KEY_CHR_NAME)),
                    cursor.getString(cursor.getColumnIndex(KEY_DATE_CREATED)),
                    cursor.getString(cursor.getColumnIndex(KEY_FEEDBACK_STATUS)),
                    cursor.getString(cursor.getColumnIndex(KEY_FEEDBACK_REPLY_BY)));
        }

        // return feedback
        return feedback;
    }

    /*******************************************************************
     * All CRUD(Create, Read, Update, Delete) Operations for Feedback
     *******************************************************************/
    // Adding new form details
    public void addFormDetails(FormDetails forms) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_FORM_DETAILS_ID, forms.getId());
        values.put(KEY_FORM_DETAILS_LABEL, forms.getLabel());
        values.put(KEY_FORM_DETAILS_TYPE, forms.getType());
        values.put(KEY_FORM_DETAILS_VALUE, forms.getValue());
        values.put(KEY_FORM_DETAILS_INSTANCE_ID, forms.getInstanceId());

        // Inserting Row
        db.insert(TABLE_FORM_DETAILS, null, values);
        db.close();
    }


    //check if form details
    public boolean isFormDetailsExist(FormDetails forms) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_FORM_DETAILS, new String[]{KEY_FORM_DETAILS_ID,
                        KEY_FORM_DETAILS_LABEL, KEY_FORM_DETAILS_TYPE, KEY_FORM_DETAILS_VALUE, KEY_FORM_DETAILS_INSTANCE_ID},
                KEY_FORM_DETAILS_ID + "=?", new String[]{String.valueOf(forms.getId())}, null, null, null, null);

        int count = cursor.getCount();
        cursor.close();
        return (count > 0) ? true : false;
    }


    //Update form details
    public void updateFormDetails(FormDetails forms) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_FORM_DETAILS_ID, forms.getId());
        values.put(KEY_FORM_DETAILS_LABEL, forms.getLabel());
        values.put(KEY_FORM_DETAILS_TYPE, forms.getType());
        values.put(KEY_FORM_DETAILS_VALUE, forms.getValue());
        values.put(KEY_FORM_DETAILS_INSTANCE_ID, forms.getInstanceId());

        db.update(TABLE_FORM_DETAILS, values, KEY_FORM_DETAILS_ID + " =" + forms.getId(), null);
    }

    // Getting Form Details
    public List<FormDetails> getFormDetails(String instanceId) {

        List<FormDetails> formList = new ArrayList<FormDetails>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_FORM_DETAILS + " WHERE "
                + KEY_FORM_DETAILS_INSTANCE_ID + " = ?";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[]{instanceId});

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                FormDetails formDetails = new FormDetails();
                formDetails.setId(Long.parseLong(cursor.getString(cursor.getColumnIndex(KEY_FORM_DETAILS_ID))));
                formDetails.setLabel(cursor.getString(cursor.getColumnIndex(KEY_FORM_DETAILS_LABEL)));
                formDetails.setType(cursor.getString(cursor.getColumnIndex(KEY_FORM_DETAILS_TYPE)));
                formDetails.setValue(cursor.getString(cursor.getColumnIndex(KEY_FORM_DETAILS_VALUE)));
                formDetails.setInstanceId(cursor.getString(cursor.getColumnIndex(KEY_FORM_DETAILS_INSTANCE_ID)));

                // Adding formDetails to list
                formList.add(formDetails);
            } while (cursor.moveToNext());
        }

        // return formDetails list
        return formList;
    }


    /*******************************************************************
     * All CRUD(Create, Read, Update, Delete) Operations for Campaign
     *******************************************************************/
    //Add new campaign
    public void addCampaign(Campaign campaign) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_CAMPAIGN_ID, campaign.getId());
        values.put(KEY_CAMPAIGN_TITLE, campaign.getTitle());
        values.put(KEY_CAMPAIGN_TYPE, campaign.getType());
        values.put(KEY_CAMPAIGN_JR_FORM_ID, campaign.getJrFormId());
        values.put(KEY_CAMPAIGN_ICON, campaign.getIcon());
        values.put(KEY_CAMPAIGN_DESCRIPTION, campaign.getDescription());

        // Inserting Row
        db.insert(TABLE_CAMPAIGN, null, values);
        db.close();
    }

    // Getting campaignList
    public List<Campaign> getCampaignList() {

        List<Campaign> campaignList = new ArrayList<Campaign>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_CAMPAIGN + " ORDER BY " + KEY_CAMPAIGN_ID + " DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Campaign campaign = new Campaign();
                campaign.setId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_CAMPAIGN_ID))));
                campaign.setTitle(cursor.getString(cursor.getColumnIndex(KEY_CAMPAIGN_TITLE)));
                campaign.setType(cursor.getString(cursor.getColumnIndex(KEY_CAMPAIGN_TYPE)));
                campaign.setJrFormId(cursor.getString(cursor.getColumnIndex(KEY_CAMPAIGN_JR_FORM_ID)));
                campaign.setIcon(cursor.getString(cursor.getColumnIndex(KEY_CAMPAIGN_ICON)));
                campaign.setDescription(cursor.getString(cursor.getColumnIndex(KEY_CAMPAIGN_DESCRIPTION)));

                // Adding campaign to list
                campaignList.add(campaign);
            } while (cursor.moveToNext());
        }

        return campaignList;
    }

    //check if campaign exists
    public boolean isCampaignExist(Campaign campaign) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_CAMPAIGN, new String[]{KEY_CAMPAIGN_ID,
                        KEY_CAMPAIGN_TITLE, KEY_CAMPAIGN_TYPE, KEY_CAMPAIGN_JR_FORM_ID, KEY_CAMPAIGN_DESCRIPTION,
                        KEY_CAMPAIGN_ICON}, KEY_CAMPAIGN_ID + "=?",
                new String[]{String.valueOf(campaign.getId())}, null, null, null, null);

        int count = cursor.getCount();
        cursor.close();
        return (count > 0) ? true : false;
    }

    //updateCampaign
    public void updateCampaign(Campaign campaign) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_CAMPAIGN_TITLE, campaign.getTitle());
        values.put(KEY_CAMPAIGN_TYPE, campaign.getType());
        values.put(KEY_CAMPAIGN_JR_FORM_ID, campaign.getJrFormId());
        values.put(KEY_CAMPAIGN_ICON, campaign.getIcon());
        values.put(KEY_CAMPAIGN_DESCRIPTION, campaign.getDescription());

        db.update(TABLE_CAMPAIGN, values,
                KEY_CAMPAIGN_ID + " = " + campaign.getId(), null);
    }

    /*******************************************************************
     * All CRUD(Create, Read, Update, Delete) Operations for OHKR Tips
     *******************************************************************/
    // Adding new Tip
    public void addTips(Tips tp) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TIP_ID, tp.getId());
        values.put(KEY_TIP_TITLE, tp.getTitle());
        values.put(KEY_TIP_DESCRIPTION, tp.getDescription());

        // Inserting Row
        db.insert(TABLE_OHKR_TIPS, null, values);
        db.close();
    }


    // Getting Tips List
    public List<Tips> getTipsList() {

        List<Tips> tipsList = new ArrayList<Tips>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_OHKR_TIPS + " ORDER BY " + KEY_TIP_TITLE + " ASC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Tips tp = new Tips();
                tp.setId(Long.parseLong(cursor.getString(cursor.getColumnIndex(KEY_TIP_ID))));
                tp.setTitle(cursor.getString(cursor.getColumnIndex(KEY_TIP_TITLE)));
                tp.setDescription(cursor.getString(cursor.getColumnIndex(KEY_TIP_DESCRIPTION)));

                // Adding tip to list
                tipsList.add(tp);
            } while (cursor.moveToNext());
        }

        return tipsList;
    }

    //check if tip exists
    public boolean isTipExist(Tips tp) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_OHKR_TIPS, new String[]{KEY_TIP_ID,
                        KEY_TIP_TITLE, KEY_TIP_DESCRIPTION}, KEY_TIP_ID + "=?",
                new String[]{String.valueOf(tp.getId())}, null, null, null, null);

        int count = cursor.getCount();
        cursor.close();
        return (count > 0) ? true : false;
    }

    //update tip
    public void updateTip(Tips tp) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_TIP_TITLE, tp.getTitle());
        values.put(KEY_TIP_DESCRIPTION, tp.getDescription());

        db.update(TABLE_OHKR_TIPS, values,
                KEY_TIP_ID + " = " + tp.getId(), null);
    }

    /*******************************************************************
     * All CRUD(Create, Read, Update, Delete) Operations for Symptoms
     *******************************************************************/
    // Adding new symptom
    public void addSymptom(Symptom symptom) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_SYMPTOM_ID, symptom.getId());
        values.put(KEY_SYMPTOM_TITLE, symptom.getTitle());
        values.put(KEY_SYMPTOM_DESCRIPTION, symptom.getDescription());

        // Inserting Row
        db.insert(TABLE_OHKR_SYMPTOMS, null, values);
        db.close();
    }


    // Getting Symptoms List
    public List<Symptom> getSymptomsList() {

        List<Symptom> symptomsList = new ArrayList<Symptom>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_OHKR_SYMPTOMS + " ORDER BY " + KEY_SYMPTOM_TITLE + " ASC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Symptom symptom = new Symptom();
                symptom.setId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_SYMPTOM_ID))));
                symptom.setTitle(cursor.getString(cursor.getColumnIndex(KEY_SYMPTOM_TITLE)));
                symptom.setDescription(cursor.getString(cursor.getColumnIndex(KEY_SYMPTOM_DESCRIPTION)));

                // Adding glossary to list
                symptomsList.add(symptom);
            } while (cursor.moveToNext());
        }
        return symptomsList;
    }


    //check if symptom exists
    public boolean isGlossaryExist(Symptom symptom) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_OHKR_SYMPTOMS, new String[]{KEY_SYMPTOM_ID,
                        KEY_SYMPTOM_TITLE, KEY_SYMPTOM_DESCRIPTION}, KEY_SYMPTOM_ID + "=?",
                new String[]{String.valueOf(symptom.getId())}, null, null, null, null);

        int count = cursor.getCount();
        cursor.close();
        return (count > 0) ? true : false;
    }

    //update symptom
    public void updateSymptom(Symptom symptom) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_SYMPTOM_TITLE, symptom.getTitle());
        values.put(KEY_SYMPTOM_DESCRIPTION, symptom.getDescription());

        db.update(TABLE_OHKR_SYMPTOMS, values,
                KEY_SYMPTOM_ID + " = " + symptom.getId(), null);
    }

    /*******************************************************************
     * All CRUD(Create, Read, Update, Delete) Operations for Searchable Form
     *******************************************************************/
    //add searchable form
    public void addSearchableForm(Form form) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_SEARCHABLE_FORM_ID, form.getId());
        values.put(KEY_SEARCHABLE_FORM_TITLE, form.getTitle());
        values.put(KEY_SEARCHABLE_JR_FORM_ID, form.getJrFormId());

        // Inserting Row
        db.insert(TABLE_SEARCHABLE_FORM, null, values);
        db.close(); // Closing database connection
    }

    //getAllSearchable Form
    public List<Form> getSearchableFormsList() {

        List<Form> formList = new ArrayList<Form>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_SEARCHABLE_FORM + " ORDER BY " + KEY_SEARCHABLE_FORM_TITLE + " ASC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Form form = new Form();
                form.setId(cursor.getLong(cursor.getColumnIndex(KEY_SEARCHABLE_FORM_ID)));
                form.setJrFormId(cursor.getString(cursor.getColumnIndex(KEY_SEARCHABLE_JR_FORM_ID)));
                form.setTitle(cursor.getString(cursor.getColumnIndex(KEY_SEARCHABLE_FORM_TITLE)));

                // Adding form to list
                formList.add(form);
            } while (cursor.moveToNext());
        }
        // closing connection
        cursor.close();
        db.close();

        // return formList
        return formList;
    }

    //check if searchable exists
    public boolean isSearchableExist(Form form) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_SEARCHABLE_FORM, new String[]{KEY_SEARCHABLE_FORM_ID,
                        KEY_SEARCHABLE_FORM_TITLE, KEY_SEARCHABLE_JR_FORM_ID},
                KEY_SEARCHABLE_FORM_ID + "=?", new String[]{String.valueOf(form.getId())}, null, null, null, null);

        int count = cursor.getCount();
        cursor.close();
        return (count > 0) ? true : false;
    }


    // Updating Searchable Form
    public int updateSearchableForm(Form form) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_SEARCHABLE_FORM_ID, form.getId());
        values.put(KEY_SEARCHABLE_FORM_TITLE, form.getTitle());
        values.put(KEY_SEARCHABLE_JR_FORM_ID, form.getJrFormId());

        // updating row
        return db.update(TABLE_SEARCHABLE_FORM, values, KEY_SEARCHABLE_FORM_ID + " = ?",
                new String[]{String.valueOf(form.getId())});
    }

    /*******************************************************************
     * All CRUD(Create, Read, Update, Delete) Operations for Searchable data
     *******************************************************************/
    public void addSearchableData(SearchableData data) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_SEARCHABLE_DATA_FORM_ID, data.getFormId());
        values.put(KEY_SEARCHABLE_DATA_LABEL, data.getLabel());
        values.put(KEY_SEARCHABLE_DATA_VALUE, data.getValue());

        // Inserting Row
        db.insert(TABLE_SEARCHABLE_DATA, null, values);
        db.close(); // Closing database connection
    }

    //getAllSearchable Form
    public List<SearchableData> getSearchableDataList(long formId) {

        List<SearchableData> dataList = new ArrayList<SearchableData>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_SEARCHABLE_DATA + " WHERE " + KEY_SEARCHABLE_DATA_FORM_ID + " = ?";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[]{String.valueOf(formId)});

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                SearchableData data = new SearchableData();
                data.setLabel(cursor.getString(cursor.getColumnIndex(KEY_SEARCHABLE_DATA_LABEL)));
                data.setValue(cursor.getString(cursor.getColumnIndex(KEY_SEARCHABLE_DATA_VALUE)));
                data.setFormId(cursor.getLong(cursor.getColumnIndex(KEY_SEARCHABLE_DATA_FORM_ID)));

                // Adding form data to list
                dataList.add(data);
            } while (cursor.moveToNext());
        }
        // closing connection
        cursor.close();
        db.close();

        // return dataList
        return dataList;
    }

    //check if searchable data exists
    public boolean isSearchableDataExist(SearchableData data) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_SEARCHABLE_DATA, new String[]{KEY_SEARCHABLE_DATA_ID,
                        KEY_SEARCHABLE_DATA_FORM_ID, KEY_SEARCHABLE_DATA_LABEL, KEY_SEARCHABLE_DATA_VALUE},
                KEY_SEARCHABLE_DATA_FORM_ID + "=? AND " + KEY_SEARCHABLE_DATA_LABEL + "=?",
                new String[]{String.valueOf(data.getFormId()), data.getLabel()}, null, null, null, null);

        int count = cursor.getCount();
        cursor.close();
        return (count > 0) ? true : false;
    }


    // Updating Searchable data
    public int updateSearchableData(SearchableData data) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_SEARCHABLE_DATA_FORM_ID, data.getFormId());
        values.put(KEY_SEARCHABLE_DATA_LABEL, data.getLabel());
        values.put(KEY_SEARCHABLE_DATA_VALUE, data.getValue());

        // updating row
        return db.update(TABLE_SEARCHABLE_DATA, values, KEY_SEARCHABLE_DATA_FORM_ID + " = ? AND " +
                        KEY_SEARCHABLE_DATA_LABEL + " = ?",
                new String[]{String.valueOf(data.getFormId()), data.getValue()});
    }


    /*============================================================================
     CRUD OPERATIONS FOR FORMS TABLE
     ===========================================================================*/
    public void addForm(Forms forms) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_FORMS_ID, forms.getId());
        values.put(KEY_FORMS_FORM_ID, forms.getFormId());
        values.put(KEY_FORMS_TITLE, forms.getTitle());
        values.put(KEY_FORMS_DESCRIPTION, forms.getDescription());
        values.put(KEY_FORMS_DOWNLOAD_URL, forms.getDownloadUrl());

        // Inserting Row
        db.insert(TABLE_FORMS, null, values);
        db.close(); // Closing database connection
    }

    //get Forms
    public List<Forms> getFormsList() {

        List<Forms> formList = new ArrayList<Forms>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_FORMS + " ORDER BY " + KEY_FORMS_ID + " ASC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Forms form = new Forms();
                form.setId(cursor.getLong(cursor.getColumnIndex(KEY_FORMS_ID)));
                form.setFormId(cursor.getString(cursor.getColumnIndex(KEY_FORMS_FORM_ID)));
                form.setTitle(cursor.getString(cursor.getColumnIndex(KEY_FORMS_TITLE)));
                form.setDescription(cursor.getString(cursor.getColumnIndex(KEY_FORMS_DESCRIPTION)));
                form.setDownloadUrl(cursor.getString(cursor.getColumnIndex(KEY_FORMS_DOWNLOAD_URL)));

                // Adding form to list
                formList.add(form);
            } while (cursor.moveToNext());
        }
        // closing connection
        cursor.close();
        db.close();

        // return formList
        return formList;
    }

    //forms exists
    public boolean isFormExist(Forms forms) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_FORMS, new String[]{KEY_FORMS_ID,
                        KEY_FORMS_FORM_ID, KEY_FORMS_TITLE, KEY_FORMS_TITLE, KEY_FORMS_DESCRIPTION, KEY_FORMS_DOWNLOAD_URL},
                KEY_FORMS_ID + "=?", new String[]{String.valueOf(forms.getId())}, null, null, null, null);

        int count = cursor.getCount();
        cursor.close();
        return (count > 0) ? true : false;
    }


    // Updating Form
    public int updateForm(Forms forms) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_FORMS_FORM_ID, forms.getFormId());
        values.put(KEY_FORMS_TITLE, forms.getTitle());
        values.put(KEY_FORMS_DESCRIPTION, forms.getDescription());
        values.put(KEY_FORMS_DOWNLOAD_URL, forms.getDownloadUrl());

        // updating row
        return db.update(TABLE_FORMS, values, KEY_FORMS_ID + " = ?",
                new String[]{String.valueOf(forms.getId())});
    }

}
