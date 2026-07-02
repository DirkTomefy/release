package mg.bovit.release.service;

import java.sql.Date;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import mg.bovit.release.repository.*;
import mg.bovit.release.model.*;
import mg.bovit.release.dto.VenteInsertDto;

@Service
public class VenteService {
    @Autowired
    private VenteBovinRepository venteBovinRepository;

    @Autowired
    private VenteDetailRepository venteDetailRepository;

    @Autowired
    private ClientRepository clientRepository;

    // On réutilise le BovinRepository existant sans le modifier :
    // il possède déjà date_vente / prix_vente sur l'entité Bovin.
    @Autowired
    private BovinRepository bovinRepository;

    @Transactional(rollbackFor = Exception.class)
    public VenteBovin insertVente(VenteInsertDto dto) throws Exception {

        if (dto.getClientId() == null) {
            throw new Exception("Le client est obligatoire");
        }

        List<VenteInsertDto.LigneVenteDto> lignes = dto.getLignes();
        if (lignes == null || lignes.isEmpty()) {
            throw new Exception("Sélectionnez au moins un bovin à vendre");
        }

        Client client = clientRepository.findById(dto.getClientId())
                .orElseThrow(() -> new Exception("Client introuvable"));

        Date dateVente = dto.getDateVente() != null
                ? dto.getDateVente()
                : new Date(System.currentTimeMillis());

        // Création de l'entête de vente
        VenteBovin vente = new VenteBovin();
        vente.setClient(client);
        vente.setDescription(dto.getDescription());
        vente.setDate_vente(dateVente);
        vente = venteBovinRepository.save(vente);

        // Détail : un bovin ne peut être vendu qu'une seule fois
        for (VenteInsertDto.LigneVenteDto ligne : lignes) {
            if (ligne.getBovinId() == null) {
                throw new Exception("Un bovin de la ligne de vente est manquant");
            }

            Bovin bovin = bovinRepository.findById(ligne.getBovinId())
                    .orElseThrow(() -> new Exception("Bovin introuvable : " + ligne.getBovinId()));

            if (bovin.getDate_vente() != null) {
                throw new Exception("Le bovin #" + bovin.getId() + " a déjà été vendu");
            }

            if (ligne.getPrixVente() == null || ligne.getPrixVente() <= 0) {
                throw new Exception("Le prix de vente du bovin #" + bovin.getId() + " doit être supérieur à 0");
            }

            // Mise à jour du bovin (marqué comme vendu)
            bovin.setDate_vente(dateVente);
            bovin.setPrix_vente(ligne.getPrixVente());
            bovinRepository.save(bovin);

            // Ligne de détail de la vente
            VenteDetail detail = new VenteDetail();
            detail.setVenteBovin(vente);
            detail.setBovin(bovin);
            venteDetailRepository.save(detail);
        }

        return vente;
    }
}
