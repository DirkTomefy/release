package mg.bovit.release.service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import mg.bovit.release.dto.BuyBovinRequest.CaissePaymentDTO;
import mg.bovit.release.repository.*;
import mg.bovit.release.model.*;
import mg.bovit.release.dto.VenteInsertDto;
import mg.bovit.release.dto.VenteListItem;
import mg.bovit.release.dto.VenteSearchCriteria;
import mg.bovit.release.dto.VenteStatsDTO;

@Service
public class VenteService {
    @Autowired
    private VenteBovinRepository venteBovinRepository;

    @Autowired
    private VenteDetailRepository venteDetailRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private CaisseRepository caisseRepository;

    @Autowired
    private MvtCaisseRepository mvtCaisseRepository;

    @Autowired
    private FactureService factureService;

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

        if (dto.getPayments() == null || dto.getPayments().isEmpty()) {
            throw new Exception("Ajoutez au moins un paiement pour créditer la caisse");
        }

        Client client = clientRepository.findById(dto.getClientId())
                .orElseThrow(() -> new Exception("Client introuvable"));

        Date dateVente = dto.getDateVente() != null
                ? dto.getDateVente()
                : new Date(System.currentTimeMillis());

        Double totalVente = 0.0;

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

            totalVente += ligne.getPrixVente();

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

        registerPaiementsCaisse(dto.getPayments(), totalVente);

        return vente;
    }

    private void registerPaiementsCaisse(List<CaissePaymentDTO> payments, Double totalVente) throws Exception {
        double totalPaiements = 0.0;

        for (CaissePaymentDTO paiement : payments) {
            if (paiement == null || paiement.getCaisseId() == null) {
                throw new Exception("Une caisse de paiement est manquante");
            }

            if (paiement.getMontant() == null || paiement.getMontant() <= 0) {
                throw new Exception("Le montant de chaque paiement doit être supérieur à 0");
            }

            Caisse caisse = caisseRepository.findById(paiement.getCaisseId())
                    .orElseThrow(() -> new Exception("Caisse introuvable : " + paiement.getCaisseId()));

            caisse.setMontant_actuelle(caisse.getMontant_actuelle() + paiement.getMontant());
            caisse = caisseRepository.save(caisse);

            MvtCaisse mvt = new MvtCaisse();
            mvt.setCaisse(caisse);
            mvt.setDate(new Date(System.currentTimeMillis()));
            mvt.setMontant(paiement.getMontant());
            mvtCaisseRepository.save(mvt);

            totalPaiements += paiement.getMontant();
        }

        if (Math.abs(totalPaiements - totalVente) > 0.01) {
            throw new Exception(String.format(
                    "Le total des paiements (%.2f) ne correspond pas au total de la vente (%.2f)",
                    totalPaiements, totalVente));
        }
    }

    public VenteStatsDTO getVenteStats(LocalDate dateDebut, LocalDate dateFin, Long raceId) {
    Long totalVentes = venteBovinRepository.countVentesWithFilters(dateDebut, dateFin);

    List<Object[]> grouped = bovinRepository.findVenteStatsGroupedByMonth(dateDebut, dateFin, raceId);

    List<String> labels = new ArrayList<>();
    List<Double> montants = new ArrayList<>();
    List<Long> counts = new ArrayList<>();

    Double montantTotal = 0.0;
    Long totalBovins = 0L;

    for (Object[] row : grouped) {
        String mois = (String) row[0];        // déjà formaté en "YYYY-MM"
        Double montant = (Double) row[1];
        Long count = (Long) row[2];

        labels.add(mois);
        montants.add(montant);
        counts.add(count);

        montantTotal += montant;
        totalBovins += count;
    }

    VenteStatsDTO dto = new VenteStatsDTO();
    dto.setTotalVentes(totalVentes);
    dto.setTotalBovinsVendus(totalBovins);
    dto.setMontantTotal(montantTotal);
    dto.setLabels(labels);
    dto.setMontants(montants);
    dto.setCounts(counts);

    return dto;
}

    public Optional<VenteBovin> findById(Long id){
        return venteBovinRepository.findById(id);
    }

    public Page<VenteListItem> searchVentes(VenteSearchCriteria criteria, Pageable pageable) {
        Page<Object[]> page = venteBovinRepository.searchVentePage(
                criteria.getDateDebut(),
                criteria.getDateFin(),
                criteria.getClientId(),
                criteria.getRaceId(),
                pageable
        );

        return page.map(row -> {
            VenteListItem item = new VenteListItem();
            item.setId(((Number) row[0]).longValue());
            Object dateValue = row[1];
            if (dateValue instanceof java.sql.Date) {
                item.setDateVente(((java.sql.Date) dateValue).toLocalDate());
            } else if (dateValue instanceof java.time.LocalDate) {
                item.setDateVente((java.time.LocalDate) dateValue);
            }
            item.setClientNom((String) row[2]);
            item.setClientPrenom((String) row[3]);
            item.setMontantTotal(row[4] != null ? ((Number) row[4]).doubleValue() : 0.0);
            item.setNombreBovins(row[5] != null ? ((Number) row[5]).intValue() : 0);
            item.setCodeFacture((String) row[6]);
            item.setFactureId(row[7] != null ? ((Number) row[7]).longValue() : null);
            item.setFactureExistante(row[6] != null);
            return item;
        });
    }
}
