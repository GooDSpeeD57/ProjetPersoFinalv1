package com.monprojet.boutiquejeux.dto.avatar;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AvatarAdminDto {
    public Long    id;
    public String  nom;
    public String  url;
    public String  alt;
    public boolean decorative;
    public boolean actif;

    public AvatarAdminDto() {}
}
