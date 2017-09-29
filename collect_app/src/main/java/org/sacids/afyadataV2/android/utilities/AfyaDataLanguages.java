package org.sacids.afyadataV2.android.utilities;

/**
 * Created by Godluck Akyoo on 29/09/2017.
 */

public enum AfyaDataLanguages {
    SWAHILI("sw"),
    ENGLISH("en"),
    FRENCH("fr"),
    PORTUGUESE("pt");

    private String language;

    AfyaDataLanguages(String lang) {
        this.language = lang;
    }

    public String getLanguage() {
        return language;
    }
}
