package org.mozambique.app.afyadata.utilities;

/**
 * Created by Godluck Akyoo on 29/09/2017.
 */

public enum AfyaDataLanguages {
    PORTUGUESE("pt"),
    SWAHILI("sw"),
    ENGLISH("en"),
    FRENCH("fr"),
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
