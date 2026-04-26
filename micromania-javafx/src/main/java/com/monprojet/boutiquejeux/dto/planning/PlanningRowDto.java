package com.monprojet.boutiquejeux.dto.planning;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PlanningRowDto {
    public Long id;
    public String nomEmploye;
    public String role;
    public String lundi;
    public String mardi;
    public String mercredi;
    public String jeudi;
    public String vendredi;
}
