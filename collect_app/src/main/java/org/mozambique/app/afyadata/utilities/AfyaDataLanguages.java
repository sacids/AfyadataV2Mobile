package org.mozambique.app.afyadata.utilities;

/**
 * Created by Godluck Akyoo on 29/09/2017.
 */

public enum AfyaDataLanguages {
    FRENCH("fr"),
    ENGLISH("en"),
    PORTUGUESE("pt"),
    SWAHILI("sw"),
    THAI("th"),
    CHINESE("zh"),
    VIETNAMESE("vi"),
    KHMER("km");

    private String language;

    AfyaDataLanguages(String lang) {
        this.language = lang;
    }

    public String getLanguage() {
        return language;
    }
}
