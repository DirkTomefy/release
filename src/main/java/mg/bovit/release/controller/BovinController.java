package mg.bovit.release.controller;

import mg.bovit.release.dto.BuyBovinRequest;
import mg.bovit.release.model.Bovin;
import mg.bovit.release.model.Caisse;
import mg.bovit.release.model.Race;
import mg.bovit.release.service.BovinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bovins")
public class BovinController {

    @Autowired
    private BovinService bovinService;

    @PostMapping("/achat")
    public ResponseEntity<?> buyBovin(@RequestBody BuyBovinRequest request) {
        try {
            // 1. Préparation du modèle Bovin
            Bovin bovin = new Bovin();
            bovin.setDate_achat(Date.valueOf(request.getDateAchat()));
            
            Race race = new Race();
            race.setId(request.getRaceId());
            bovin.setRace(race);

            // 2. Mappage des lignes de paiement en liste de Caisses pour le service
            List<Caisse> caisses = new ArrayList<>();
            if (request.getPayments() != null) {
                for (BuyBovinRequest.CaissePaymentDTO pDto : request.getPayments()) {
                    Caisse caisse = new Caisse();
                    caisse.setId(pDto.getCaisseId());
                    caisse.setMontant_actuelle(pDto.getMontant()); // Utilisé pour stocker le montant à retirer dans le service
                    caisses.add(caisse);
                }
            }

            // 3. Appel au service transactionnel
            bovinService.buyBovin(bovin, caisses, request.getQuantite());

            return ResponseEntity.ok(Map.of("status", "success", "message", "Achat enregistré avec succès !"));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("status", "error", "message", e.getMessage()));
        }
    }
}