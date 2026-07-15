package mg.bovit.release.dto;

import java.util.List;

public class MortaliteInsertDto {

    // Identifiants des bovins concernés
    private List<Long> bovinIds;

    public List<Long> getBovinIds() { return bovinIds; }
    public void setBovinIds(List<Long> bovinIds) { this.bovinIds = bovinIds; }
}
