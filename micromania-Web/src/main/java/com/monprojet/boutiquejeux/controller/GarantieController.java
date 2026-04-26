package com.monprojet.boutiquejeux.controller;
import org.springframework.ui.Model;
import com.monprojet.boutiquejeux.dto.GarantieForm;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class GarantieController {

    @GetMapping("/garanties")
    public String pageGaranties(Model model) {
        model.addAttribute("garanties", List.of());
        model.addAttribute("produits", List.of());
        model.addAttribute("typesGarantie", List.of());
        model.addAttribute("garantieForm", new GarantieForm());
        return "garanties";
    }
}