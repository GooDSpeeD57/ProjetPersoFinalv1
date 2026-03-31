package fr.micromania.util;

public final class Regex {

    private Regex() {}

    public static final String PSEUDO =
            "^[A-Za-z0-9._-]{3,50}$";

    public static final String NOM_PRENOM =
            "^[A-Za-zÀ-ÖØ-öø-ÿ' -]{1,100}$";

    public static final String TELEPHONE_FR =
            "^(?:(?:\\+33|0)[1-9])(?:[ .-]?\\d{2}){4}$";

    public static final String MOT_DE_PASSE_FORT =
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z\\d]).{8,128}$";
}