package com.monprojet.boutiquejeux.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;
@Data @AllArgsConstructor
public class CartItem {
    private Long produitId;
    private String nom;
    private BigDecimal prix;
    private int quantite;
}
