package mg.bovit.release.dto;

import java.util.List;

public class MortaliteInsertDto {

    // Date de la mortalité au format "yyyy-MM-dd" (input HTML type="date")
    private String date;

    // Identifiants des bovins concernés
    private List<Long> bovinIds;

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public List<Long> getBovinIds() { return bovinIds; }
    public void setBovinIds(List<Long> bovinIds) { this.bovinIds = bovinIds; }
}
