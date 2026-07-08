package mg.bovit.release.dto;

public class MultiCriteriaFormClientList {

    // Recherche globale multicritère (nom OU prenom OU contact)
    private String search;

    // Filtres spécifiques (optionnels, en plus de la recherche globale)
    private String nom;
    private String contact;

    // Pagination
    private int page = 0;
    private int size = 10;
    private String sort = "id,asc";

    public String getSearch() { return search; }
    public void setSearch(String search) { this.search = search; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }

    public int getPage() { return page; }
    public void setPage(int page) { this.page = page; }

    public int getSize() { return size; }
    public void setSize(int size) { this.size = size; }

    public String getSort() { return sort; }
    public void setSort(String sort) { this.sort = sort; }
}
