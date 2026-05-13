package com.monprojet.boutiquejeux.dto.planning;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** Vue hebdomadaire : une ligne par employé, 7 colonnes de jours. */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PlanningRowDto {
    public Long   employeId;
    public String nomEmploye;
    public String role;
    public String lundi;
    public String mardi;
    public String mercredi;
    public String jeudi;
    public String vendredi;
    public String samedi;
    public String dimanche;

    // Getters pour PropertyValueFactory
    public Long   getEmployeId()  { return employeId; }
    public String getNomEmploye() { return s(nomEmploye); }
    public String getRole()       { return s(role);       }
    public String getLundi()      { return s(lundi);      }
    public String getMardi()      { return s(mardi);      }
    public String getMercredi()   { return s(mercredi);   }
    public String getJeudi()      { return s(jeudi);      }
    public String getVendredi()   { return s(vendredi);   }
    public String getSamedi()     { return s(samedi);     }
    public String getDimanche()   { return s(dimanche);   }

    private static String s(String v) { return v != null ? v : ""; }
}
