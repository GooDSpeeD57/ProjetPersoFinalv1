package com.monprojet.boutiquejeux.dto.commande;

public class UpdateStatutCommandeDto {
    public String codeStatut;
    public String commentaire;

    public UpdateStatutCommandeDto(String codeStatut, String commentaire) {
        this.codeStatut  = codeStatut;
        this.commentaire = commentaire;
    }
}
