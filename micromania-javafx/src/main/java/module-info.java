module com.monprojet.boutiquejeux {
    // JavaFX 23
    requires javafx.base;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    // HTTP + JSON
    requires java.net.http;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.datatype.jsr310;

    // Lombok (compile-time only)
    requires static lombok;
    requires java.prefs;

    // Ouvrir les packages aux modules JavaFX / Jackson
    opens com.monprojet.boutiquejeux                   to javafx.fxml, javafx.graphics;
    opens com.monprojet.boutiquejeux.controller        to javafx.fxml;
    opens com.monprojet.boutiquejeux.model             to com.fasterxml.jackson.databind, javafx.base;
    opens com.monprojet.boutiquejeux.util              to javafx.fxml;
    opens com.monprojet.boutiquejeux.service           to javafx.fxml;
    opens com.monprojet.boutiquejeux.dto.auth          to com.fasterxml.jackson.databind;
    opens com.monprojet.boutiquejeux.dto.employe       to com.fasterxml.jackson.databind;
    opens com.monprojet.boutiquejeux.dto.client        to com.fasterxml.jackson.databind;
    opens com.monprojet.boutiquejeux.dto.produit       to com.fasterxml.jackson.databind;
    opens com.monprojet.boutiquejeux.dto.vente         to com.fasterxml.jackson.databind;
    opens com.monprojet.boutiquejeux.dto.stats         to com.fasterxml.jackson.databind;
    opens com.monprojet.boutiquejeux.dto.stock         to com.fasterxml.jackson.databind;
    opens com.monprojet.boutiquejeux.dto.garantie      to com.fasterxml.jackson.databind;
    opens com.monprojet.boutiquejeux.dto.planning      to com.fasterxml.jackson.databind;
    opens com.monprojet.boutiquejeux.dto.reprise       to com.fasterxml.jackson.databind;
    opens com.monprojet.boutiquejeux.dto               to com.fasterxml.jackson.databind;
    opens com.monprojet.boutiquejeux.dto.referentiel   to com.fasterxml.jackson.databind;
    opens com.monprojet.boutiquejeux.dto.magasin       to com.fasterxml.jackson.databind;
    opens com.monprojet.boutiquejeux.dto.depot         to com.fasterxml.jackson.databind;
    opens com.monprojet.boutiquejeux.dto.commande      to com.fasterxml.jackson.databind;
    opens com.monprojet.boutiquejeux.dto.entreprise    to com.fasterxml.jackson.databind;
    opens com.monprojet.boutiquejeux.dto.catalog       to com.fasterxml.jackson.databind;

    exports com.monprojet.boutiquejeux;
    exports com.monprojet.boutiquejeux.controller;
    exports com.monprojet.boutiquejeux.service;
    exports com.monprojet.boutiquejeux.model;
    exports com.monprojet.boutiquejeux.util;
    exports com.monprojet.boutiquejeux.exception;
    exports com.monprojet.boutiquejeux.dto.auth;
    exports com.monprojet.boutiquejeux.dto.employe;
    exports com.monprojet.boutiquejeux.dto.client;
    exports com.monprojet.boutiquejeux.dto.produit;
    exports com.monprojet.boutiquejeux.dto.vente;
    exports com.monprojet.boutiquejeux.dto.stats;
    exports com.monprojet.boutiquejeux.dto.stock;
    exports com.monprojet.boutiquejeux.dto.garantie;
    exports com.monprojet.boutiquejeux.dto.planning;
    exports com.monprojet.boutiquejeux.dto.reprise;
    exports com.monprojet.boutiquejeux.dto;
    exports com.monprojet.boutiquejeux.dto.referentiel;
    exports com.monprojet.boutiquejeux.dto.magasin;
    exports com.monprojet.boutiquejeux.dto.depot;
    exports com.monprojet.boutiquejeux.dto.commande;
    exports com.monprojet.boutiquejeux.dto.entreprise;
    exports com.monprojet.boutiquejeux.dto.catalog;
}
