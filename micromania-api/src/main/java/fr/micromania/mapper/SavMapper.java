package fr.micromania.mapper;

import fr.micromania.dto.sav.*;
import fr.micromania.entity.DossierSav;
import org.mapstruct.*;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.WARN)
public interface SavMapper {

    @Mapping(target = "statut",        source = "statutSav.code")
    @Mapping(target = "idVenteUnite",  source = "venteUnite.id")
    @Mapping(target = "numeroSerie",   source = "venteUnite.numeroSerie")
    @Mapping(target = "nomCommercial", source = "venteUnite.ligneFacture.variant.nomCommercial")
    @Mapping(target = "employe",       expression = "java(dossier.getEmploye() != null ? dossier.getEmploye().getPrenom() + ' ' + dossier.getEmploye().getNom() : null)")
    @Mapping(target = "sousGarantie",  expression = "java(dossier.getGarantie() != null && dossier.getGarantie().getDateFin().isAfter(java.time.LocalDate.now()))")
    DossierSavResponse toResponse(DossierSav dossier);

    List<DossierSavResponse> toResponseList(List<DossierSav> dossiers);
}
