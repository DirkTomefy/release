package mg.bovit.release.dto;

import java.util.List;

public class InventairePayload {
    private String dateInventaire;
    private String libelle;
    private List<InventaireDetailPayload> details;

    public InventairePayload() {
    }

    public InventairePayload(String dateInventaire, String libelle, List<InventaireDetailPayload> details) {
        this.dateInventaire = dateInventaire;
        this.libelle = libelle;
        this.details = details;
    }

    public String getDateInventaire() {
        return dateInventaire;
    }

    public void setDateInventaire(String dateInventaire) {
        this.dateInventaire = dateInventaire;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public List<InventaireDetailPayload> getDetails() {
        return details;
    }

    public void setDetails(List<InventaireDetailPayload> details) {
        this.details = details;
    }
}
