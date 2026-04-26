package com.monprojet.boutiquejeux.dto.stats;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class StatsDto {
    public int ventesAujourdhui;
    public double caAujourdhui;
    public int reprisesAujourdhui;
    public int stocksBas;
    public long totalClients;
}
