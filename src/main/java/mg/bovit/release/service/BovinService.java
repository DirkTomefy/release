package mg.bovit.release.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.jpa.domain.Specification;

import mg.bovit.release.dto.MultiCriteriaFormBovinList;
import mg.bovit.release.model.Bovin;
import mg.bovit.release.model.Caisse;
import mg.bovit.release.model.MvtCaisse;
import mg.bovit.release.model.PeseBovin;
import mg.bovit.release.model.sqlview.BovinWithPoids;
import mg.bovit.release.repository.BovinRepository;
import mg.bovit.release.repository.BovinWithPoidsRepository;
import mg.bovit.release.repository.CauseCaisseRepository;
import mg.bovit.release.repository.MvtCaisseRepository;
import mg.bovit.release.repository.PeseBovinRepository;
import mg.bovit.release.specification.BovinSpecification;

@Service
public class BovinService {
    @Autowired
    private BovinRepository bovinRepository;
    @Autowired
    private CaisseService caisseService;

    @Autowired
    private BovinWithPoidsRepository bovinWithPoidsRepository;

    @Autowired
    private PeseBovinRepository peseRepository;

    @Autowired
    private MvtCaisseRepository mvtCaisseRepository;

    @Autowired
    private CauseCaisseRepository causeCaisseRepository;

    // Cause de caisse appliquée automatiquement aux sorties générées
    // par l'achat d'un ou plusieurs bovins.
    private static final String CAUSE_ACHAT_BOVIN = "ACHAT_BOVIN";

    // function to find bovin with status by id bovin
    public BovinWithPoids findBovinPoidsById(Long id_bovin) throws Exception {
        return bovinWithPoidsRepository.findById(id_bovin).orElseThrow();
    }

    // function to find bovin by id
    public Bovin findById(Long id_bovin) throws Exception {
        return bovinRepository.findById(id_bovin).orElseThrow();
    }

    @Transactional(rollbackFor = Exception.class)
    public void buyBovin(Bovin bovin, List<Caisse> caisses, int quantite, Double prixUnitaire) throws Exception {

        if (bovin == null) {
            throw new Exception("Les informations du bovin sont introuvables");
        }

        if (bovin.getPoids_achat() == null || bovin.getPoids_achat() <= 0) {
            throw new Exception("Le poids d'achat est obligatoire et doit être supérieur à 0");
        }

        if (quantite <= 0) {
            throw new Exception("La quantité ne doit pas être inférieure ou égale à 0");
        }

        if (prixUnitaire == null || prixUnitaire <= 0) {
            throw new Exception("Le prix unitaire doit être supérieur à 0");
        }

        // Calcul du prix total à partir du prix unitaire × quantité
        Double prixTotal = prixUnitaire * quantite;

        // Vérification et déduction des caisses
        Double totalPaiements = 0.0;
        for (int i = 0; i < caisses.size(); i++) {
            if (caisses.get(i).getMontant_actuelle() <= 0) {
                throw new Exception("Le montant de chaque paiement doit être supérieur à 0");
            }
            
            // Trouver la caisse
            Caisse temp_caisse = caisseService.findById(caisses.get(i).getId());
            
            // Vérifier que le montant à déduire ne dépasse pas le solde
            if (temp_caisse.getMontant_actuelle() < caisses.get(i).getMontant_actuelle()) {
                throw new Exception("Solde insuffisant dans la caisse: " + temp_caisse.getLibelle());
            }
            
            // Déduire le montant
            temp_caisse.setMontant_actuelle(temp_caisse.getMontant_actuelle() - caisses.get(i).getMontant_actuelle());
            temp_caisse = caisseService.save(temp_caisse);

            // Enregistrement de la sortie de caisse correspondante
            MvtCaisse mvtCaisse = new MvtCaisse();
            mvtCaisse.setCaisse(temp_caisse);
            mvtCaisse.setDate(bovin.getDate_achat());
            mvtCaisse.setMontant(-1 * caisses.get(i).getMontant_actuelle());
            mvtCaisse.setCauseCaisse(causeCaisseRepository.findByLibelleIgnoreCase(CAUSE_ACHAT_BOVIN)
                    .orElseThrow(() -> new Exception("Cause de caisse introuvable : " + CAUSE_ACHAT_BOVIN)));
            mvtCaisseRepository.save(mvtCaisse);

            totalPaiements += caisses.get(i).getMontant_actuelle();
        }

        // VÉRIFICATION CRITIQUE : Le total des paiements doit être égal au prix total
        if (Math.abs(totalPaiements - prixTotal) > 0.01) { // Tolérance de 0.01 pour les doubles
            throw new Exception(String.format(
                "Le total des paiements (%.2f) ne correspond pas au prix total calculé (%.2f = %.2f × %d)",
                totalPaiements, prixTotal, prixUnitaire, quantite
            ));
        }

        // Insertion des bovins
        for (int i = 0; i < quantite; i++) {
            Bovin newBovin = new Bovin();
            PeseBovin newPeseBovin = new PeseBovin();

            newBovin.setRace(bovin.getRace());
            newBovin.setPoids_achat(bovin.getPoids_achat());
            newBovin.setPoids_vente(bovin.getPoids_vente());
            newBovin.setDate_achat(bovin.getDate_achat());
            newBovin.setDate_vente(bovin.getDate_vente());
            newBovin.setPrix_achat(prixUnitaire); // Utiliser le prix unitaire

            bovinRepository.save(newBovin);

            newPeseBovin.setBovin(newBovin);
            newPeseBovin.setDate_pese(bovin.getDate_achat());
            newPeseBovin.setPoids_apres(bovin.getPoids_achat());

            peseRepository.save(newPeseBovin);
        }
    }

    // Nouvelle méthode de recherche paginée et filtrée (utilisant la vue)
    public Page<BovinWithPoids> searchBovinsWithPoids(MultiCriteriaFormBovinList form) throws Exception {
        // Construction du tri
        String sortField = "id";
        Sort.Direction direction = Sort.Direction.ASC;
        if (form.getSort() != null && !form.getSort().isEmpty()) {
            String[] parts = form.getSort().split(",");
            if (parts.length >= 1) {
                sortField = parts[0];
            }
            if (parts.length >= 2) {
                direction = Sort.Direction.fromString(parts[1]);
            }
        }
        Pageable pageable = PageRequest.of(
                form.getPage(),
                form.getSize(),
                Sort.by(direction, sortField)
        );

        Specification<BovinWithPoids> bovinSpecs = BovinSpecification.fromForm(form);

        return bovinWithPoidsRepository.findAll(bovinSpecs, pageable);
    }            

    public List<Bovin> findAll() {
        return bovinRepository.findAll();
    }
}
